package ganadii.hanjum.service;

import ganadii.hanjum.domain.FlowerCards;
import ganadii.hanjum.domain.Shares;
import ganadii.hanjum.domain.User;
import ganadii.hanjum.repository.FlowerCardsRepository;
import ganadii.hanjum.repository.FriendshipsRepository;
import ganadii.hanjum.repository.SharesRepository;
import ganadii.hanjum.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ShareService {

    private final SharesRepository sharesRepository;
    private final UserRepository userRepository;
    private final FlowerCardsRepository flowerCardsRepository;
    private final FriendshipsRepository friendshipsRepository;

    @Transactional
    public Shares sendToSelf(UUID senderId, Long cardId, String idempotencyKey) {
        User sender = userRepository.findById(senderId)
                .orElseThrow(() -> new IllegalArgumentException("Sender not found"));
        FlowerCards card = flowerCardsRepository.findById(cardId)
                .orElseThrow(() -> new IllegalArgumentException("Card not found"));

        // 카드 소유권 검증
        if (!card.getCreator().getUserId().equals(senderId)) {
            throw new IllegalArgumentException("You can only send your own cards");
        }

        // a card can be sent only once
        sharesRepository.findFirstByFlowerCards_CardId(cardId).ifPresent(s -> {
            throw new IllegalStateException("Card already sent");
        });

        Shares share = Shares.builder()
                .flowerCards(card)
                .sender(sender)
                .receiver(sender)
                .toName(resolveName(null, sender.getDisplayName()))
                .fromName(resolveName(null, sender.getDisplayName()))
                .isRead(true)
                .idempotencyKey(trimToNull(idempotencyKey))
                .build();

        return sharesRepository.save(share);
    }

    @Transactional
    public Shares sendToFriend(UUID senderId, Long cardId, UUID receiverId,
                               String toName, String fromName, String note, String idempotencyKey) {
        User sender = userRepository.findById(senderId)
                .orElseThrow(() -> new IllegalArgumentException("Sender not found"));
        User receiver = userRepository.findById(receiverId)
                .orElseThrow(() -> new IllegalArgumentException("Receiver not found"));
        FlowerCards card = flowerCardsRepository.findById(cardId)
                .orElseThrow(() -> new IllegalArgumentException("Card not found"));

        // 카드 소유권 검증
        if (!card.getCreator().getUserId().equals(senderId)) {
            throw new IllegalArgumentException("You can only send your own cards");
        }

        // 친구 관계 검증
        if (!friendshipsRepository.existsByUser_UserIdAndFriend_UserId(senderId, receiverId)) {
            throw new IllegalArgumentException("You can only send cards to friends");
        }

        // 친구에게는 1번만 보낼 수 있음 (sender ≠ receiver인 Share 체크)
        sharesRepository.findFriendShareByCardAndSender(cardId, senderId).ifPresent(s -> {
            throw new IllegalStateException("Card already sent to a friend");
        });

        String finalTo = resolveName(toName, receiver.getDisplayName());
        String finalFrom = resolveName(fromName, sender.getDisplayName());
        String finalNote = trimToNull(note);
        if (finalNote != null && finalNote.length() > 200) {
            throw new IllegalArgumentException("note must be <= 200 characters");
        }

        // 1. 친구에게 Share 생성
        Shares friendShare = Shares.builder()
                .flowerCards(card)
                .sender(sender)
                .receiver(receiver)
                .toName(finalTo)
                .fromName(finalFrom)
                .note(finalNote)
                .isRead(false)
                .idempotencyKey(trimToNull(idempotencyKey))
                .build();
        sharesRepository.save(friendShare);

        // 2. 나에게도 Share 생성 (내 Archive용)
        Shares myArchiveShare = Shares.builder()
                .flowerCards(card)
                .sender(sender)
                .receiver(sender)  // receiver = sender
                .toName(finalTo)
                .fromName(finalFrom)
                .note(finalNote)
                .isRead(true)  // 내가 만든 카드이므로 이미 본 것으로
                .build();
        sharesRepository.save(myArchiveShare);

        return friendShare;
    }

    private static String resolveName(String candidate, String fallbackName) {
        String v = trimToNull(candidate);
        String result = (v == null) ? safeName(fallbackName) : v;
        validateName(result);
        return result;
    }

    private static String trimToNull(String s) {
        if (s == null) return null;
        String t = s.trim();
        return t.isEmpty() ? null : t;
    }

    private static String safeName(String raw) {
        String v = raw == null ? "" : raw.trim();
        if (v.isEmpty()) v = "-";
        return v.length() > 20 ? v.substring(0, 20) : v;
    }

    private static void validateName(String name) {
        int len = name.length();
        if (len < 1 || len > 20) {
            throw new IllegalArgumentException("Name must be 1-20 characters");
        }
    }
}
