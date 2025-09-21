package ganadii.hanjum.domain.enums;

import lombok.Getter;

@Getter
public enum WhenType {
    BANQUET("연회"),
    CONFESSION("고백"),
    BIRTHDAY("생일"),
    ANNIVERSARY("기념일"),
    MEMORIAL("추모"),
    OPENING("개업");

    private final String label;

    WhenType(String label) { this.label = label; }

    public static WhenType fromLabel(String label) {
        if (label == null) return null;
        for (WhenType v : values()) if (v.label.equals(label)) return v;
        throw new IllegalArgumentException("Unknown WhenType label: " + label);
    }
}

