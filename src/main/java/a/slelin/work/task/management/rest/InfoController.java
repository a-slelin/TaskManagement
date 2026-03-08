package a.slelin.work.task.management.rest;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.time.LocalDateTime;
import java.util.Map;

@Path("/")
@Produces(MediaType.APPLICATION_JSON)
public class InfoController {

    public static final Map<String, Object> info = Map.of(
            "name", "Task Management System",
            "version", "4.0",
            "description", "REST API for managing tasks and projects",
            "timestamp", LocalDateTime.now().toString(),
            "links", Map.of(
                    "users", "/api/users",
                    "projects", "/api/projects",
                    "tasks", "/api/tasks"
            ));

    @GET
    public Response getInfo() {
        return Response.ok(info).build();
    }

    @GET
    @Path("/help")
    public Response getInfo2() {
        return Response.ok(info).build();
    }

    @GET
    @Path("/info")
    public Response getInfo3() {
        return Response.ok(info).build();
    }
}
