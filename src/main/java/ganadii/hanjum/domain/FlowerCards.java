package ganadii.hanjum.domain;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.Instant;
import java.util.List;
import ganadii.hanjum.domain.enums.WhoType;
import ganadii.hanjum.domain.enums.WhenType;
import ganadii.hanjum.domain.enums.EmotionType;
// store enum names in DB for stability and i18n-friendly labels at UI
import ganadii.hanjum.domain.enums.BouquetSize;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "flowercards")
public class FlowerCards {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "card_id", nullable = false, updatable = false)
    private Long cardId;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "creator_id", nullable = false)
    private User creator;


    @Column(name = "title")
    private String title;

    @Column(name = "image_url")
    private String imageUrl;

    @Column(name = "floriography")
    private String floriography;


    @Enumerated(EnumType.STRING)
    @Column(name = "who_type")
    private WhoType whoType;

    @Enumerated(EnumType.STRING)
    @Column(name = "when_type")
    private WhenType whenType;

    @Enumerated(EnumType.STRING)
    @Column(name = "emotion_type")
    private EmotionType emotionType;

    @Enumerated(EnumType.STRING)
    @Column(name = "bouquet_size")
    private BouquetSize bouquetSize;

    @Column(name = "price")
    private Integer price;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "background_colors", columnDefinition = "json")
    private List<String> backgroundColors;

    @Builder.Default
    @Column(name = "is_ordered", nullable = false)
    private boolean isOrdered = false;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

}
