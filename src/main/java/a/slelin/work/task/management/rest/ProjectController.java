package a.slelin.work.task.management.rest;

import a.slelin.work.task.management.dto.ProjectRD;
import a.slelin.work.task.management.dto.ProjectWD;
import a.slelin.work.task.management.dto.TaskRD;
import a.slelin.work.task.management.dto.TaskWD;
import a.slelin.work.task.management.service.ProjectService;
import a.slelin.work.task.management.service.TaskService;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;

import java.net.URI;
import java.util.List;

@Path("/projects")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class ProjectController {

    private final static TaskService taskService = TaskService.getInstance();

    private final static ProjectService service = ProjectService.getInstance();

    @GET
    @Consumes(MediaType.WILDCARD)
    public List<ProjectRD> getProjects(@QueryParam("tasks") @DefaultValue("false") boolean tasks) {
        return service.getAll(tasks);
    }

    @GET
    @Path("/{id}")
    public ProjectRD getProject(@PathParam("id") Long id,
                                @QueryParam("tasks") @DefaultValue("false") boolean tasks) {
        return service.getById(id, tasks);
    }

    @GET
    @Path("/{id}/tasks")
    public List<TaskRD> getTasks(@PathParam("id") Long id) {
        return service.getProjectTasks(id);
    }

    @POST
    public Response createProject(ProjectWD project, @Context UriInfo uriInfo) {
        ProjectRD savedProject = service.create(project);
        URI location = uriInfo.getAbsolutePathBuilder()
                .path(savedProject.id().toString())
                .build();
        return Response.created(location)
                .entity(savedProject)
                .build();
    }

    @POST
    @Path("/{id}/tasks")
    public Response createTask(@PathParam("id") Long id,
                               TaskWD task,
                               @Context UriInfo uriInfo) {
        TaskWD newTask = TaskWD.intercept(task)
                .project(id)
                .build();

        TaskRD savedTask = taskService.create(newTask);
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
        return service.update(id, project);
    }

    @DELETE
    @Path("/{id}")
    @Consumes(MediaType.WILDCARD)
    @Produces(MediaType.WILDCARD)
    public Response deleteProject(@PathParam("id") Long id) {
        service.delete(id);
        return Response.noContent().build();
    }

    @DELETE
    @Path("/{id}/tasks")
    @Consumes(MediaType.WILDCARD)
    @Produces(MediaType.WILDCARD)
    public Response deleteTask(@PathParam("id") Long id) {
        service.deleteTasks(id);
        return Response.noContent().build();
    }
}
