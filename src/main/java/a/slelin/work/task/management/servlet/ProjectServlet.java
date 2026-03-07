package a.slelin.work.task.management.servlet;

import a.slelin.work.task.management.dto.ProjectRD;
import a.slelin.work.task.management.dto.ProjectWD;
import a.slelin.work.task.management.service.ProjectService;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@WebServlet("/projects/*")
public class ProjectServlet extends HttpServlet {

    private final static ObjectMapper mapper = new ObjectMapper();

    private final static ProjectService service = ProjectService.getInstance();

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
                Long id = Long.parseLong(path.substring(1));
                resp.getWriter().write(mapper.writeValueAsString(service.getById(id)));
            }
        } catch (Exception e) {
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Internal server error: " + e.getMessage());
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            resp.setContentType("application/json");
            resp.setCharacterEncoding(StandardCharsets.UTF_8.name());
            resp.setStatus(HttpServletResponse.SC_CREATED);

            ProjectWD project = mapper.readValue(req.getReader(), ProjectWD.class);
            ProjectRD savedProject = service.create(project);
            resp.getWriter().write(mapper.writeValueAsString(savedProject));

            resp.setHeader("Location", "http://localhost:8080/TaskManagementSystem/projects/" +
                    savedProject.id().toString());
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

            Long id = Long.parseLong(req.getPathInfo().substring(1));

            ProjectWD project = mapper.readValue(req.getReader(), ProjectWD.class);
            ProjectRD savedProject = service.update(id, project);

            resp.getWriter().write(mapper.writeValueAsString(savedProject));
        } catch (Exception e) {
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Internal server error: " + e.getMessage());
        }
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            resp.setStatus(HttpServletResponse.SC_NO_CONTENT);

            Long id = Long.parseLong(req.getPathInfo().substring(1));

            service.delete(id);
        } catch (Exception e) {
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Internal server error: " + e.getMessage());
        }
    }
}
