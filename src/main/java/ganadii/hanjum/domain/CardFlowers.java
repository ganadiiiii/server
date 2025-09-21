package ganadii.hanjum.domain;

import ganadii.hanjum.domain.enums.FlowerType;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "card_flowers")
public class CardFlowers {

    @EmbeddedId
    private CardFlowersId id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @MapsId("cardId")
    @JoinColumn(name = "card_id", nullable = false)
    private FlowerCards flowerCards;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @MapsId("flowerId")
    @JoinColumn(name = "flower_id", nullable = false)
    private Flowers flowers;

    @Enumerated(EnumType.STRING)
    @Column(name = "flower_type", nullable = false)
    private FlowerType flowerType;
}

