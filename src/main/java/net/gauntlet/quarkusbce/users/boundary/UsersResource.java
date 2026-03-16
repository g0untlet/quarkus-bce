//==============================================================================

// Copyright (c) 2026 net.gauntlet. All rights reserved.

//==============================================================================

package net.gauntlet.quarkusbce.users.boundary;

import jakarta.inject.Inject;
import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.enterprise.context.ApplicationScoped;
import net.gauntlet.quarkusbce.users.control.UsersControl;
import net.gauntlet.quarkusbce.users.entity.User;
import org.eclipse.microprofile.metrics.annotation.Metered;

import java.util.List;

@ApplicationScoped
@Path("/users")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Metered(absolute = true)
public class UsersResource {

    private static final System.Logger LOG = System.getLogger(UsersResource.class.getName());

    @Inject
    UsersControl usersControl;

    @POST
    public Response create(UserRequest request) {
        if (request.name == null || request.name.isBlank()) {
            throw new BadRequestException("name is required");
        }
        if (request.email == null || request.email.isBlank()) {
            throw new BadRequestException("email is required");
        }
        LOG.log(System.Logger.Level.INFO, "Creating user: {0}", request.name);
        User user = usersControl.create(request.name, request.email);
        return Response.status(Response.Status.CREATED)
                .entity(user)
                .build();
    }

    @GET
    @Path("/{id}")
    public User getById(@PathParam("id") Long id) {
        return usersControl.getById(id)
                .orElseThrow(() -> new NotFoundException("User not found"));
    }

    @GET
    public List<User> getAll() {
        return usersControl.listAll();
    }

    @PUT
    @Path("/{id}")
    public User update(@PathParam("id") Long id, UserRequest request) {
        LOG.log(System.Logger.Level.INFO, "Updating user with id: {0}", id);
        return usersControl.update(id, request.name, request.email)
                .orElseThrow(() -> new NotFoundException("User not found"));
    }

    @DELETE
    @Path("/{id}")
    public Response delete(@PathParam("id") Long id) {
        LOG.log(System.Logger.Level.INFO, "Deleting user with id: {0}", id);
        if (!usersControl.delete(id)) {
            throw new NotFoundException("User not found");
        }
        return Response.noContent().build();
    }

    public static class UserRequest {
        public String name;
        public String email;

        public UserRequest() {
        }

        public UserRequest(String name, String email) {
            this.name = name;
            this.email = email;
        }
    }
}
