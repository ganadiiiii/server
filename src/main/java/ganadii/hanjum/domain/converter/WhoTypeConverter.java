package ganadii.hanjum.domain.converter;

import ganadii.hanjum.domain.enums.WhoType;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = false)
public class WhoTypeConverter implements AttributeConverter<WhoType, String> {
    @Override
    public String convertToDatabaseColumn(WhoType attribute) {
        return attribute == null ? null : attribute.getLabel();
    }

    @Override
    public WhoType convertToEntityAttribute(String dbData) {
        return WhoType.fromLabel(dbData);
    }
}

