package a.slelin.work.task.management.entity;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter
public class StatusConverter implements AttributeConverter<Status, String> {

    @Override
    public String convertToDatabaseColumn(Status status) {
        return status.getDisplayName();
    }

    @Override
    public Status convertToEntityAttribute(String s) {
        return Status.of(s);
    }
}
