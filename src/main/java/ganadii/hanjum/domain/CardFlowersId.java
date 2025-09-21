package ganadii.hanjum.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@Embeddable
public class CardFlowersId implements Serializable {

    @Column(name = "card_id")
    private Long cardId;

    @Column(name = "flower_id")
    private Long flowerId;
}

