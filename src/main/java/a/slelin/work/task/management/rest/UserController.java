package a.slelin.work.task.management.rest;

import a.slelin.work.task.management.dto.UserRD;
import a.slelin.work.task.management.dto.UserWD;
import a.slelin.work.task.management.service.UserService;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@Path("/users")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class UserController {

    private final static UserService service = UserService.getInstance();

    @GET
    @Consumes(MediaType.WILDCARD)
    public List<UserRD> getUsers() {
        return service.getAll();
    }

    @GET
    @Path("/{id}")
    @Consumes(MediaType.WILDCARD)
    public UserRD getUser(@PathParam("id") UUID id) {
        return service.getById(id);
    }

    @POST
    public Response createUser(UserWD user, @Context UriInfo uriInfo) {
        UserRD savedUser = service.create(user);
        URI location = uriInfo.getAbsolutePathBuilder()
                .path(savedUser.id())
                .build();
        return Response.created(location)
                .entity(savedUser)
                .build();
    }

    @PUT
    @Path("/{id}")
    public UserRD updateUser(@PathParam("id") UUID id, UserWD user) {
        return service.update(id, user);
    }


    @DELETE
    @Path("/{id}")
    @Consumes(MediaType.WILDCARD)
    @Produces(MediaType.WILDCARD)
    public Response deleteUser(@PathParam("id") UUID id) {
        service.delete(id);
        return Response.noContent().build();
    }
}
