package com.ganadi.palmful.service;

import com.ganadi.palmful.dto.FriendRequestDto;
import com.ganadi.palmful.dto.FriendResponse;
import com.ganadi.palmful.dto.UserResponse;
import com.ganadi.palmful.entity.FriendRequest;
import com.ganadi.palmful.entity.Friendship;
import com.ganadi.palmful.entity.User;
import com.ganadi.palmful.repository.FriendRequestRepository;
import com.ganadi.palmful.repository.FriendshipRepository;
import com.ganadi.palmful.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class FriendService {

    private final FriendRequestRepository friendRequestRepository;
    private final FriendshipRepository friendshipRepository;
    private final UserRepository userRepository;

    @Autowired
    public FriendService(FriendRequestRepository friendRequestRepository,
                         FriendshipRepository friendshipRepository,
                         UserRepository userRepository) {
        this.friendRequestRepository = friendRequestRepository;
        this.friendshipRepository = friendshipRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public void sendRequest(Long requesterId, Long addresseeId, String message) {
        if (requesterId.equals(addresseeId)) {
            throw new IllegalArgumentException("자기 자신에게 요청할 수 없습니다.");
        }

        User requester = userRepository.findById(requesterId)
                .orElseThrow(() -> new IllegalArgumentException("요청자를 찾을 수 없습니다: " + requesterId));
        User addressee = userRepository.findById(addresseeId)
                .orElseThrow(() -> new IllegalArgumentException("대상 사용자를 찾을 수 없습니다: " + addresseeId));

        boolean alreadyRequested = friendRequestRepository
                .existsByRequester_IdAndAddressee_IdAndStatusIn(requesterId, addresseeId, Set.of("pending", "accepted"));
        boolean alreadyFriends = friendshipRepository.existsByUser_IdAndFriend_Id(requesterId, addresseeId)
                || friendshipRepository.existsByUser_IdAndFriend_Id(addresseeId, requesterId);
        if (alreadyRequested || alreadyFriends) {
            throw new IllegalStateException("이미 요청했거나 친구입니다.");
        }

        FriendRequest req = new FriendRequest(requester, addressee, message);
        req.setStatus("pending");
        friendRequestRepository.save(req);
    }

    @Transactional
    public void acceptRequest(Long requestId, Long userId) {
        FriendRequest req = friendRequestRepository.findById(requestId)
                .orElseThrow(() -> new IllegalArgumentException("요청을 찾을 수 없습니다: " + requestId));
        
        // 권한 확인: 요청을 받은 사람만 수락할 수 있음
        if (!req.getAddressee().getId().equals(userId)) {
            throw new IllegalArgumentException("이 요청을 수락할 권한이 없습니다.");
        }
        
        if (!"pending".equals(req.getStatus())) {
            throw new IllegalStateException("이미 처리된 요청입니다.");
        }

        req.setStatus("accepted");
        req.setRespondedAt(LocalDateTime.now());
        friendRequestRepository.save(req);

        // 양방향 친구 생성
        Friendship f1 = new Friendship(req.getRequester(), req.getAddressee());
        Friendship f2 = new Friendship(req.getAddressee(), req.getRequester());
        friendshipRepository.save(f1);
        friendshipRepository.save(f2);
    }

    @Transactional
    public void rejectRequest(Long requestId, Long userId) {
        FriendRequest req = friendRequestRepository.findById(requestId)
                .orElseThrow(() -> new IllegalArgumentException("요청을 찾을 수 없습니다: " + requestId));
        
        // 권한 확인: 요청을 받은 사람만 거절할 수 있음
        if (!req.getAddressee().getId().equals(userId)) {
            throw new IllegalArgumentException("이 요청을 거절할 권한이 없습니다.");
        }
        
        if (!"pending".equals(req.getStatus())) {
            throw new IllegalStateException("이미 처리된 요청입니다.");
        }
        req.setStatus("rejected");
        req.setRespondedAt(LocalDateTime.now());
        friendRequestRepository.save(req);
    }

    @Transactional(readOnly = true)
    public List<FriendResponse> getFriends(Long userId) {
        List<Friendship> asUser = friendshipRepository.findByUser_Id(userId);
        return asUser.stream().map(fs -> {
            User f = fs.getFriend();
            UserResponse friendUser = new UserResponse(
                    f.getId(), f.getEmail(), f.getFirstName(), f.getLastName(), f.getProvider(), f.getCreatedAt()
            );
            return new FriendResponse(friendUser, fs.getCreatedAt());
        }).collect(Collectors.toList());
    }

    @Transactional
    public void removeFriend(Long userId, Long friendId) {
        if (!friendshipRepository.existsByUser_IdAndFriend_Id(userId, friendId) &&
            !friendshipRepository.existsByUser_IdAndFriend_Id(friendId, userId)) {
            throw new IllegalArgumentException("친구 관계가 아닙니다.");
        }
        friendshipRepository.deleteByUser_IdAndFriend_Id(userId, friendId);
        friendshipRepository.deleteByUser_IdAndFriend_Id(friendId, userId);
    }
    
    @Transactional(readOnly = true)
    public List<FriendRequestDto> getReceivedRequests(Long userId) {
        List<FriendRequest> requests = friendRequestRepository.findByAddressee_IdAndStatusOrderByCreatedAtDesc(userId, "pending");
        return requests.stream().map(this::convertToFriendRequestDto).collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public List<FriendRequestDto> getSentRequests(Long userId) {
        List<FriendRequest> requests = friendRequestRepository.findByRequester_IdOrderByCreatedAtDesc(userId);
        return requests.stream().map(this::convertToFriendRequestDto).collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public List<FriendResponse> searchFriends(Long userId, String query) {
        // 현재 사용자와 친구인 사용자들 중에서 검색
        List<Friendship> friendships = friendshipRepository.findByUser_Id(userId);
        Set<Long> friendIds = friendships.stream()
                .map(fs -> fs.getFriend().getId())
                .collect(Collectors.toSet());
        
        if (friendIds.isEmpty()) {
            return List.of();
        }
        
        // 이름이나 이메일로 검색
        List<User> users = userRepository.findByIdInAndFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCaseOrEmailContainingIgnoreCase(
                friendIds, query, query, query);
        
        return users.stream().map(user -> {
            UserResponse userResponse = new UserResponse(
                    user.getId(), user.getEmail(), user.getFirstName(), 
                    user.getLastName(), user.getProvider(), user.getCreatedAt()
            );
            // 친구가 된 날짜 찾기
            LocalDateTime friendshipDate = friendships.stream()
                    .filter(fs -> fs.getFriend().getId().equals(user.getId()))
                    .findFirst()
                    .map(Friendship::getCreatedAt)
                    .orElse(LocalDateTime.now());
            return new FriendResponse(userResponse, friendshipDate);
        }).collect(Collectors.toList());
    }
    
    private FriendRequestDto convertToFriendRequestDto(FriendRequest request) {
        FriendRequestDto dto = new FriendRequestDto();
        dto.setId(request.getId());
        dto.setRequesterId(request.getRequester().getId());
        dto.setAddresseeId(request.getAddressee().getId());
        dto.setMessage(request.getMessage());
        dto.setStatus(request.getStatus());
        dto.setCreatedAt(request.getCreatedAt());
        dto.setRespondedAt(request.getRespondedAt());
        return dto;
    }
}


