package a.slelin.work.task.management.core.util;

import tools.jackson.core.JacksonException;
import tools.jackson.core.JsonGenerator;
import tools.jackson.databind.SerializationContext;
import tools.jackson.databind.ValueSerializer;

import java.time.LocalDateTime;

import static a.slelin.work.task.management.core.util.DateTimeUtil.UNIVERSE_DATETIME_FORMATTER;

public class LocalDateTimeSerializer extends ValueSerializer<LocalDateTime> {

    @Override
    public void serialize(LocalDateTime value,
                          JsonGenerator gen,
                          SerializationContext ctxt) throws JacksonException {
        gen.writeString(value.format(UNIVERSE_DATETIME_FORMATTER));
    }
}
