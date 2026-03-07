package a.slelin.work.task.management.rest;

import a.slelin.work.task.management.dto.TaskRD;
import a.slelin.work.task.management.dto.TaskWD;
import a.slelin.work.task.management.service.TaskService;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;

import java.net.URI;
import java.util.List;

@Path("/tasks")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class TaskController {

    private final static TaskService service = TaskService.getInstance();

    @GET
    @Consumes(MediaType.WILDCARD)
    public List<TaskRD> getTasks() {
        return service.getAll();
    }

    @GET
    @Path("/{id}")
    @Consumes(MediaType.WILDCARD)
    public TaskRD getTask(@PathParam("id") Long id) {
        return service.getById(id);
    }

    @POST
    public Response createTask(TaskWD task, @Context UriInfo uriInfo) {
        TaskRD savedTask = service.create(task);
        URI location = uriInfo.getAbsolutePathBuilder()
                .path(savedTask.id().toString())
                .build();
        return Response.created(location)
                .entity(savedTask)
                .build();
    }

    @PUT
    @Path("/{id}")
    public TaskRD updateTask(@PathParam("id") Long id, TaskWD task) {
        return service.update(id, task);
    }

    @DELETE
    @Path("/{id}")
    @Consumes(MediaType.WILDCARD)
    @Produces(MediaType.WILDCARD)
    public Response deleteTask(@PathParam("id") Long id) {
        service.delete(id);
        return Response.noContent().build();
    }
}
