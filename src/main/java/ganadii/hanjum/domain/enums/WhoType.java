package ganadii.hanjum.domain.enums;

import lombok.Getter;

@Getter
public enum WhoType {
    TEACHER("스승"),
    LOVER("연인"),
    SELF("본인"),
    FAMILY("가족"),
    FRIEND("친구"),
    COLLEAGUE("동료");

    private final String label;

    WhoType(String label) { this.label = label; }

    public static WhoType fromLabel(String label) {
        if (label == null) return null;
        for (WhoType v : values()) if (v.label.equals(label)) return v;
        throw new IllegalArgumentException("Unknown WhoType label: " + label);
    }
}

