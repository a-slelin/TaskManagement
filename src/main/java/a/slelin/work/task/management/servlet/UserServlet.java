package a.slelin.work.task.management.servlet;

import a.slelin.work.task.management.dto.UserDto;
import a.slelin.work.task.management.service.UserService;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

@WebServlet("/users/*")
public class UserServlet extends HttpServlet {

    private final static ObjectMapper mapper = new ObjectMapper();

    private final static UserService service = UserService.getInstance();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            resp.setContentType("application/json");
            resp.setCharacterEncoding(StandardCharsets.UTF_8.name());
            resp.setStatus(HttpServletResponse.SC_OK);

            String path = req.getPathInfo();
            if (path == null || path.equals("/")) {
                resp.getWriter().write(mapper.writeValueAsString(service.getAll()));
            } else {
                UUID id = UUID.fromString(path.substring(1));
                resp.getWriter().write(mapper.writeValueAsString(service.getById(id)));
            }
        } catch (Exception e) {
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Internal server error: " + e.getMessage());
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            resp.setStatus(HttpServletResponse.SC_CREATED);

            UserDto user = mapper.readValue(req.getReader(), UserDto.class);
            UUID id = service.create(user);

            resp.setHeader("Location", "http://localhost:8080/TaskManagementSystem/users/" + id.toString());
        } catch (Exception e) {
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Internal server error: " + e.getMessage());
        }
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            resp.setContentType("application/json");
            resp.setCharacterEncoding(StandardCharsets.UTF_8.name());
            resp.setStatus(HttpServletResponse.SC_OK);

            UserDto user = mapper.readValue(req.getReader(), UserDto.class);
            user = service.update(user);

            resp.getWriter().write(mapper.writeValueAsString(user));
        } catch (Exception e) {
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Internal server error: " + e.getMessage());
        }
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            resp.setStatus(HttpServletResponse.SC_NO_CONTENT);

            UUID id = UUID.fromString(req.getPathInfo().substring(1));
            service.delete(id);
        } catch (Exception e) {
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Internal server error: " + e.getMessage());
        }
    }
}
