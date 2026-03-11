package a.slelin.work.task.management.util.filter;

import tools.jackson.core.JacksonException;
import tools.jackson.core.JsonGenerator;
import tools.jackson.databind.SerializationContext;
import tools.jackson.databind.ValueSerializer;

public class OperationSerializer extends ValueSerializer<Operation> {

    @Override
    public void serialize(Operation value, JsonGenerator gen, SerializationContext ctxt)
            throws JacksonException {
        gen.writeString(value.getDisplayName());
    }
}
