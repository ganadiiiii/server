package ganadii.hanjum.domain;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.Instant;
import java.util.List;

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

    @Column(name = "message")
    private String message;

    @Column(name = "who_type")
    private String whoType;

    @Column(name = "when_type")
    private String whenType;

    @Column(name = "emotion_type")
    private String emotionType;

    @Column(name = "bouquet_size")
    private String bouquetSize;

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
