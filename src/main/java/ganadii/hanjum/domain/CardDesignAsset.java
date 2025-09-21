package ganadii.hanjum.domain;

import ganadii.hanjum.domain.enums.BouquetSize;
import ganadii.hanjum.domain.enums.CardImageSource;
import ganadii.hanjum.domain.enums.EmotionType;
import ganadii.hanjum.domain.enums.WhenType;
import ganadii.hanjum.domain.enums.WhoType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "card_design_assets",
        uniqueConstraints = @UniqueConstraint(name = "uk_card_design_assets_combo",
                columnNames = {"main_flower_id", "who_type", "when_type", "emotion_type", "bouquet_size"}))
public class CardDesignAsset {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "asset_id", updatable = false, nullable = false)
    private Long assetId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "main_flower_id", nullable = false)
    private Flowers mainFlower;

    @Enumerated(EnumType.STRING)
    @Column(name = "who_type", nullable = false)
    private WhoType whoType;

    @Enumerated(EnumType.STRING)
    @Column(name = "when_type", nullable = false)
    private WhenType whenType;

    @Enumerated(EnumType.STRING)
    @Column(name = "emotion_type", nullable = false)
    private EmotionType emotionType;

    @Enumerated(EnumType.STRING)
    @Column(name = "bouquet_size")
    private BouquetSize bouquetSize;

    @Enumerated(EnumType.STRING)
    @Column(name = "source", nullable = false)
    private CardImageSource source;

    @Column(name = "image_url", nullable = false)
    private String imageUrl;

    @Column(name = "storage_key")
    private String storageKey;

    @Column(name = "checksum")
    private String checksum;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private Instant updatedAt;
}
