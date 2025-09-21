package ganadii.hanjum.service;

import ganadii.hanjum.domain.FriendRequest;
import ganadii.hanjum.domain.Friendships;
import ganadii.hanjum.domain.User;
import ganadii.hanjum.domain.enums.FriendRequestStatus;
import ganadii.hanjum.repository.FriendRequestRepository;
import ganadii.hanjum.repository.FriendshipsRepository;
import ganadii.hanjum.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FriendRequestService {

    private final FriendRequestRepository friendRequestRepository;
    private final FriendshipsRepository friendshipsRepository;
    private final UserRepository userRepository;

    @Transactional
    public FriendRequest create(UUID senderId, UUID receiverId) {
        if (senderId.equals(receiverId)) {
            throw new IllegalArgumentException("Cannot send request to self");
        }
        User sender = userRepository.findById(senderId).orElseThrow(() -> new IllegalArgumentException("Sender not found"));
        User receiver = userRepository.findById(receiverId).orElseThrow(() -> new IllegalArgumentException("Receiver not found"));

        // Already friends?
        if (friendshipsRepository.existsByUser_UserIdAndFriend_UserId(senderId, receiverId) ||
                friendshipsRepository.existsByUser_UserIdAndFriend_UserId(receiverId, senderId)) {
            throw new IllegalStateException("Already friends");
        }
        // Existing pending either direction
        boolean dup = friendRequestRepository.existsBySender_UserIdAndReceiver_UserIdAndStatus(senderId, receiverId, FriendRequestStatus.PENDING)
                || friendRequestRepository.existsBySender_UserIdAndReceiver_UserIdAndStatus(receiverId, senderId, FriendRequestStatus.PENDING);
        if (dup) {
            throw new IllegalStateException("Request already pending");
        }

        FriendRequest req = FriendRequest.builder()
                .sender(sender)
                .receiver(receiver)
                .status(FriendRequestStatus.PENDING)
                .build();
        return friendRequestRepository.save(req);
    }

    @Transactional(readOnly = true)
    public List<FriendRequest> list(UUID userId, String type) {
        if ("sent".equalsIgnoreCase(type)) {
            return friendRequestRepository.findBySender_UserId(userId);
        }
        // default received
        return friendRequestRepository.findByReceiver_UserId(userId);
    }

    @Transactional
    public FriendRequest accept(Long requestId, UUID receiverId) {
        FriendRequest req = friendRequestRepository.findByIdAndReceiver_UserId(requestId, receiverId)
                .orElseThrow(() -> new IllegalArgumentException("Request not found"));
        if (req.getStatus() != FriendRequestStatus.PENDING) {
            throw new IllegalStateException("Request is not pending");
        }
        // Create friendships bidirectionally if not exist
        UUID a = req.getSender().getUserId();
        UUID b = req.getReceiver().getUserId();
        if (!friendshipsRepository.existsByUser_UserIdAndFriend_UserId(a, b)) {
            friendshipsRepository.save(Friendships.builder().user(req.getSender()).friend(req.getReceiver()).build());
        }
        if (!friendshipsRepository.existsByUser_UserIdAndFriend_UserId(b, a)) {
            friendshipsRepository.save(Friendships.builder().user(req.getReceiver()).friend(req.getSender()).build());
        }
        req.setStatus(FriendRequestStatus.ACCEPTED);
        req.setRespondedAt(Instant.now());
        return req;
    }

    @Transactional
    public FriendRequest reject(Long requestId, UUID receiverId) {
        FriendRequest req = friendRequestRepository.findByIdAndReceiver_UserId(requestId, receiverId)
                .orElseThrow(() -> new IllegalArgumentException("Request not found"));
        if (req.getStatus() != FriendRequestStatus.PENDING) {
            throw new IllegalStateException("Request is not pending");
        }
        req.setStatus(FriendRequestStatus.REJECTED);
        req.setRespondedAt(Instant.now());
        return req;
    }
}

