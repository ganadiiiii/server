package com.ganadi.palmful.service;

import com.ganadi.palmful.dto.GiftResponse;
import com.ganadi.palmful.dto.UserResponse;
import com.ganadi.palmful.entity.Bouquet;
import com.ganadi.palmful.entity.Gift;
import com.ganadi.palmful.entity.User;
import com.ganadi.palmful.repository.BouquetRepository;
import com.ganadi.palmful.repository.GiftRepository;
import com.ganadi.palmful.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class GiftService {

    private final GiftRepository giftRepository;
    private final BouquetRepository bouquetRepository;
    private final UserRepository userRepository;

    @Autowired
    public GiftService(GiftRepository giftRepository, BouquetRepository bouquetRepository, UserRepository userRepository) {
        this.giftRepository = giftRepository;
        this.bouquetRepository = bouquetRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public GiftResponse sendGift(Long senderId, Long bouquetId, Long receiverId, String message) {
        User sender = userRepository.findById(senderId)
                .orElseThrow(() -> new IllegalArgumentException("보낸 사용자를 찾을 수 없습니다: " + senderId));
        User receiver = userRepository.findById(receiverId)
                .orElseThrow(() -> new IllegalArgumentException("받는 사용자를 찾을 수 없습니다: " + receiverId));
        Bouquet bouquet = bouquetRepository.findById(bouquetId)
                .orElseThrow(() -> new IllegalArgumentException("부케를 찾을 수 없습니다: " + bouquetId));

        Gift gift = new Gift(bouquet, sender, receiver, message);
        gift.setStatus("sent");
        Gift saved = giftRepository.save(gift);
        return toResponse(saved);
    }

    @Transactional(readOnly = true)
    public List<GiftResponse> getReceivedGifts(Long userId) {
        return giftRepository.findByReceiver_IdOrderBySentAtDesc(userId)
                .stream().map(this::toResponse).collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public List<GiftResponse> getSentGifts(Long userId) {
        return giftRepository.findBySender_IdOrderBySentAtDesc(userId)
                .stream().map(this::toResponse).collect(Collectors.toList());
    }

    @Transactional
    public void markAsRead(Long giftId) {
        Gift gift = giftRepository.findById(giftId)
                .orElseThrow(() -> new IllegalArgumentException("선물을 찾을 수 없습니다: " + giftId));
        gift.setStatus("read");
        giftRepository.save(gift);
    }

    private GiftResponse toResponse(Gift gift) {
        User s = gift.getSender();
        User r = gift.getReceiver();
        UserResponse sender = new UserResponse(s.getId(), s.getEmail(), s.getFirstName(), s.getLastName(), s.getProvider(), s.getCreatedAt());
        UserResponse receiver = new UserResponse(r.getId(), r.getEmail(), r.getFirstName(), r.getLastName(), r.getProvider(), r.getCreatedAt());
        return new GiftResponse(
                gift.getId(), sender, receiver,
                gift.getBouquet().getId(), gift.getMessage(), gift.getStatus(), gift.getSentAt()
        );
    }
}



