package a.slelin.work.task.management.rest;

import a.slelin.work.task.management.exception.GlobalExceptionMapper;
import jakarta.ws.rs.ApplicationPath;
import org.glassfish.jersey.server.ResourceConfig;

@ApplicationPath("/")
public class JerseyConfig extends ResourceConfig {

    public JerseyConfig() {
        packages("a.slelin.work.task.management.rest");
        register(GlobalExceptionMapper.class);
    }
}
