package a.slelin.work.task.management.rest;

import a.slelin.work.task.management.dto.ProjectRD;
import a.slelin.work.task.management.dto.ProjectWD;
import a.slelin.work.task.management.service.ProjectService;
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

    private final static ProjectService service = ProjectService.getInstance();

    @GET
    @Consumes(MediaType.WILDCARD)
    public List<ProjectRD> getProjects() {
        return service.getAll();
    }

    @GET
    @Path("/{id}")
    public ProjectRD getProject(@PathParam("id") Long id) {
        return service.getById(id);
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
}
