package ganadii.hanjum.domain.enums;

public enum BouquetSize {
    S("S"), M("M"), L("L");

    private final String label;

    BouquetSize(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
