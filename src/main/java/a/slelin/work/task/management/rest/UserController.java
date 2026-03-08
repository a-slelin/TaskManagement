package a.slelin.work.task.management.rest;

import a.slelin.work.task.management.dto.ProjectRD;
import a.slelin.work.task.management.dto.ProjectWD;
import a.slelin.work.task.management.dto.UserRD;
import a.slelin.work.task.management.dto.UserWD;
import a.slelin.work.task.management.service.ProjectService;
import a.slelin.work.task.management.service.UserService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@Path("/users")
@ApplicationScoped
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class UserController {

    @Inject
    private ProjectService projectService;

    @Inject
    private UserService userService;

    @GET
    @Consumes(MediaType.WILDCARD)
    public List<UserRD> getUsers(@QueryParam("projects") @DefaultValue("false") boolean projects,
                                 @QueryParam("tasks") @DefaultValue("false") boolean tasks) {
        return userService.getAll(projects, tasks);
    }

    @GET
    @Path("/{id}")
    @Consumes(MediaType.WILDCARD)
    public UserRD getUser(@PathParam("id") UUID id,
                          @QueryParam("projects") @DefaultValue("false") boolean projects,
                          @QueryParam("tasks") @DefaultValue("false") boolean tasks) {
        return userService.getById(id, projects, tasks);
    }

    @GET
    @Path("/{id}/projects")
    @Consumes(MediaType.WILDCARD)
    public List<ProjectRD> getProjects(@PathParam("id") UUID id,
                                       @QueryParam("tasks") @DefaultValue("false") boolean tasks) {
        return userService.getUserProjects(id, tasks);
    }

    @POST
    public Response createUser(UserWD user, @Context UriInfo uriInfo) {
        UserRD savedUser = userService.create(user);
        URI location = uriInfo.getAbsolutePathBuilder()
                .path(savedUser.id())
                .build();
        return Response.created(location)
                .entity(savedUser)
                .build();
    }

    @POST
    @Path("/{id}/projects")
    public Response createProject(@PathParam("id") UUID id,
                                  ProjectWD project,
                                  @Context UriInfo uriInfo) {
        ProjectRD savedProject = projectService.create(id, project);
        URI location = uriInfo.getBaseUriBuilder()
                .path(ProjectController.class)
                .path(savedProject.id().toString())
                .build();
        return Response.created(location)
                .entity(savedProject)
                .build();
    }

    @PUT
    @Path("/{id}")
    public UserRD updateUser(@PathParam("id") UUID id, UserWD user) {
        return userService.update(id, user);
    }


    @DELETE
    @Path("/{id}")
    @Consumes(MediaType.WILDCARD)
    @Produces(MediaType.WILDCARD)
    public Response deleteUser(@PathParam("id") UUID id) {
        userService.delete(id);
        return Response.noContent().build();
    }

    @DELETE
    @Path("/{id}/projects")
    @Consumes(MediaType.WILDCARD)
    @Produces(MediaType.WILDCARD)
    public Response deleteProject(@PathParam("id") UUID id) {
        userService.deleteUserProjects(id);
        return Response.noContent().build();
    }
}
