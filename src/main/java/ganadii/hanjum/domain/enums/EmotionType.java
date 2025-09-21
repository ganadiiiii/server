package ganadii.hanjum.domain.enums;

import lombok.Getter;

@Getter
public enum EmotionType {
    COURAGE("용기"),
    ENCOURAGEMENT("격려"),
    EXPECTATION("기대"),
    CELEBRATION("축하"),
    RESPECT("존경"),
    FRIENDSHIP("우정"),
    RESOLUTION("다짐"),
    APOLOGY("사과"),
    MOURNING("애도");

    private final String label;

    EmotionType(String label) { this.label = label; }

    public static EmotionType fromLabel(String label) {
        if (label == null) return null;
        for (EmotionType v : values()) if (v.label.equals(label)) return v;
        throw new IllegalArgumentException("Unknown EmotionType label: " + label);
    }
}

