package a.slelin.work.task.management.rest;

import a.slelin.work.task.management.dto.TaskRD;
import a.slelin.work.task.management.dto.TaskWD;
import a.slelin.work.task.management.service.TaskService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.List;

@Path("/tasks")
@ApplicationScoped
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class TaskController {

    @Inject
    private TaskService service;

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

    @PUT
    @Path("/{id}")
    public TaskRD updateTask(@PathParam("id") Long id, TaskWD task) {
        return service.update(id, task);
    }

    @PATCH
    @Path("/{id}")
    public TaskRD patchTask(@PathParam("id") Long id, TaskWD dto) {
        return service.patch(id, dto);
    }

    @PUT
    @Path("/{id}/project/{projectId}")
    public TaskRD setProject(@PathParam("id") Long id,
                             @PathParam("projectId") Long projectId) {
        return service.drawToProject(id, projectId);
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
