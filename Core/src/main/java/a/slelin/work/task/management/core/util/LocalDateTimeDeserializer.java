package a.slelin.work.task.management.core.util;

import tools.jackson.core.JacksonException;
import tools.jackson.core.JsonParser;
import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.ValueDeserializer;

import java.time.LocalDateTime;

import static a.slelin.work.task.management.core.util.DateTimeUtil.UNIVERSE_DATETIME_FORMATTER;

public class LocalDateTimeDeserializer extends ValueDeserializer<LocalDateTime> {

    @Override
    public LocalDateTime deserialize(JsonParser p,
                                     DeserializationContext ctxt) throws JacksonException {
        String str = p.getValueAsString();
        return LocalDateTime.parse(str, UNIVERSE_DATETIME_FORMATTER);
    }
}
