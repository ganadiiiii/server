package ganadii.hanjum.domain.converter;

import ganadii.hanjum.domain.enums.WhenType;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = false)
public class WhenTypeConverter implements AttributeConverter<WhenType, String> {
    @Override
    public String convertToDatabaseColumn(WhenType attribute) {
        return attribute == null ? null : attribute.getLabel();
    }

    @Override
    public WhenType convertToEntityAttribute(String dbData) {
        return WhenType.fromLabel(dbData);
    }
}

