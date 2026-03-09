package a.slelin.work.task.management.entity;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter
public class GenderConverter implements AttributeConverter<Gender, String> {

    @Override
    public String convertToDatabaseColumn(Gender gender) {
        return gender.getDisplayName();
    }

    @Override
    public Gender convertToEntityAttribute(String s) {
        return Gender.of(s);
    }
}
