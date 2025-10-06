package ganadii.hanjum.domain.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum WrappingType {
    KRAFT_PAPER("크래프트지"),
    COLOR_PAPER("컬러 종이"),
    CLEAR_VINYL("투명 비닐");

    private final String label;

    public static WrappingType fromLabel(String label) {
        if (label == null) {
            return null;
        }
        for (WrappingType type : values()) {
            if (type.label.equals(label)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown wrapping type label: " + label);
    }
}
