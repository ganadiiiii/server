package ganadii.hanjum.domain.converter;

import ganadii.hanjum.domain.enums.EmotionType;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = false)
public class EmotionTypeConverter implements AttributeConverter<EmotionType, String> {
    @Override
    public String convertToDatabaseColumn(EmotionType attribute) {
        return attribute == null ? null : attribute.getLabel();
    }

    @Override
    public EmotionType convertToEntityAttribute(String dbData) {
        return EmotionType.fromLabel(dbData);
    }
}

