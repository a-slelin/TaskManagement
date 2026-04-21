package a.slelin.work.task.management.api.util;

import org.springframework.http.HttpMethod;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.SerializationContext;
import tools.jackson.databind.ValueSerializer;

public class HttpMethodSerializer extends ValueSerializer<HttpMethod> {

    @Override
    public void serialize(HttpMethod value,
                          tools.jackson.core.JsonGenerator gen,
                          SerializationContext ctxt) throws JacksonException {
        gen.writeString(value.toString());
    }
}