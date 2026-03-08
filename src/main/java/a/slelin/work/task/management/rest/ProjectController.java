package a.slelin.work.task.management.rest;

import a.slelin.work.task.management.dto.ProjectRD;
import a.slelin.work.task.management.dto.ProjectWD;
import a.slelin.work.task.management.dto.TaskRD;
import a.slelin.work.task.management.dto.TaskWD;
import a.slelin.work.task.management.service.ProjectService;
import a.slelin.work.task.management.service.TaskService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;

import java.net.URI;
import java.util.List;

@Path("/projects")
@ApplicationScoped
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class ProjectController {

    @Inject
    private TaskService taskService;

    @Inject
    private ProjectService projectService;

    @GET
    @Consumes(MediaType.WILDCARD)
    public List<ProjectRD> getProjects(@QueryParam("tasks") @DefaultValue("false") boolean tasks) {
        return projectService.getAll(tasks);
    }

    @GET
    @Path("/{id}")
    public ProjectRD getProject(@PathParam("id") Long id,
                                @QueryParam("tasks") @DefaultValue("false") boolean tasks) {
        return projectService.getById(id, tasks);
    }

    @GET
    @Path("/{id}/tasks")
    public List<TaskRD> getTasks(@PathParam("id") Long id) {
        return projectService.getProjectTasks(id);
    }

    @POST
    @Path("/{id}/tasks")
    public Response createTask(@PathParam("id") Long id,
                               TaskWD task,
                               @Context UriInfo uriInfo) {
        TaskRD savedTask = taskService.create(id, task);
        URI location = uriInfo.getBaseUriBuilder()
                .path(TaskController.class)
                .path(savedTask.id().toString())
                .build();

        return Response.created(location)
                .entity(savedTask)
                .build();
    }

    @PUT
    @Path("/{id}")
    public ProjectRD updateProject(@PathParam("id") Long id, ProjectWD project) {
        return projectService.update(id, project);
    }

    @DELETE
    @Path("/{id}")
    @Consumes(MediaType.WILDCARD)
    @Produces(MediaType.WILDCARD)
    public Response deleteProject(@PathParam("id") Long id) {
        projectService.delete(id);
        return Response.noContent().build();
    }

    @DELETE
    @Path("/{id}/tasks")
    @Consumes(MediaType.WILDCARD)
    @Produces(MediaType.WILDCARD)
    public Response deleteTask(@PathParam("id") Long id) {
        projectService.deleteTasks(id);
        return Response.noContent().build();
    }
}
