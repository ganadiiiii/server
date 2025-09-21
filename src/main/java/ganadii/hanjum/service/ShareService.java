package ganadii.hanjum.service;

import ganadii.hanjum.domain.FlowerCards;
import ganadii.hanjum.domain.Shares;
import ganadii.hanjum.domain.User;
import ganadii.hanjum.repository.FlowerCardsRepository;
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

    @Transactional
    public Shares sendToSelf(UUID senderId, Long cardId) {
        User sender = userRepository.findById(senderId)
                .orElseThrow(() -> new IllegalArgumentException("Sender not found"));
        FlowerCards card = flowerCardsRepository.findById(cardId)
                .orElseThrow(() -> new IllegalArgumentException("Card not found"));

        // a card can be sent only once
        sharesRepository.findFirstByFlowerCards_CardId(cardId).ifPresent(s -> {
            throw new IllegalStateException("Card already sent");
        });

        Shares share = Shares.builder()
                .flowerCards(card)
                .sender(sender)
                .receiver(sender)
                .toName(resolveName(null, sender.getNickname()))
                .fromName(resolveName(null, sender.getNickname()))
                .isRead(true)
                .build();

        return sharesRepository.save(share);
    }

    @Transactional
    public Shares sendToFriend(UUID senderId, Long cardId, UUID receiverId,
                               String toName, String fromName, String note) {
        User sender = userRepository.findById(senderId)
                .orElseThrow(() -> new IllegalArgumentException("Sender not found"));
        User receiver = userRepository.findById(receiverId)
                .orElseThrow(() -> new IllegalArgumentException("Receiver not found"));
        FlowerCards card = flowerCardsRepository.findById(cardId)
                .orElseThrow(() -> new IllegalArgumentException("Card not found"));

        // a card can be sent only once
        sharesRepository.findFirstByFlowerCards_CardId(cardId).ifPresent(s -> {
            throw new IllegalStateException("Card already sent");
        });

        String finalTo = resolveName(toName, receiver.getNickname());
        String finalFrom = resolveName(fromName, sender.getNickname());
        String finalNote = trimToNull(note);
        if (finalNote != null && finalNote.length() > 200) {
            throw new IllegalArgumentException("note must be <= 200 characters");
        }

        Shares share = Shares.builder()
                .flowerCards(card)
                .sender(sender)
                .receiver(receiver)
                .toName(finalTo)
                .fromName(finalFrom)
                .note(finalNote)
                .isRead(false)
                .build();

        return sharesRepository.save(share);
    }

    private static String resolveName(String candidate, String fallbackNickname) {
        String v = trimToNull(candidate);
        String result = (v == null) ? safeName(fallbackNickname) : v;
        validateName(result);
        return result;
    }

    private static String trimToNull(String s) {
        if (s == null) return null;
        String t = s.trim();
        return t.isEmpty() ? null : t;
    }

    private static String safeName(String nickname) {
        String v = nickname == null ? "" : nickname.trim();
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
