package a.slelin.work.task.management.util.filter;

import tools.jackson.core.JacksonException;
import tools.jackson.databind.ValueDeserializer;

public class OperationDeserializer extends ValueDeserializer<Operation> {

    @Override
    public Operation deserialize(tools.jackson.core.JsonParser p,
                                 tools.jackson.databind.DeserializationContext ctxt)
            throws JacksonException {
        String value = p.getValueAsString().trim();
        return Operation.of(value);
    }
}
