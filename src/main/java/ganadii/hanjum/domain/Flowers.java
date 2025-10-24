package ganadii.hanjum.domain;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.List;
import ganadii.hanjum.domain.model.MeaningDetail;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "flowers")
public class Flowers {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "flower_id", nullable = false, updatable = false)
    private Long flowerId;

    @Column(name = "korean_name", nullable = false)
    private String koreanName;

    @Column(name = "english_name", nullable = false)
    private String englishName;

    @Column(name = "image_url")
    private String imageUrl;

    @Column(name = "default_floriography", length = 500)
    private String defaultFloriography;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "details", columnDefinition = "jsonb")
    private List<MeaningDetail> details;
}
