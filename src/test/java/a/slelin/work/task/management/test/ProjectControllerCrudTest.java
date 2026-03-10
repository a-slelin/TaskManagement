package a.slelin.work.task.management.test;

import a.slelin.work.task.management.dto.ProjectRD;
import a.slelin.work.task.management.dto.ProjectWD;
import a.slelin.work.task.management.dto.TaskRD;
import a.slelin.work.task.management.dto.TaskWD;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.util.List;

import static a.slelin.work.task.management.test.TestConfig.PROJECT_URL;
import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test")
@DisplayName("Тест контроллера проектов")
@TestClassOrder(ClassOrderer.OrderAnnotation.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class ProjectControllerCrudTest {

    @Autowired
    private RestTemplate rest;

    @Test
    @Order(1)
    @DisplayName("Тестируем получение всех проектов без задач")
    public void getAllProjects() {
        ResponseEntity<List<ProjectRD>> response = rest.exchange(
                PROJECT_URL,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {
                });
        assertNotNull(response);
        assertNotNull(response.getStatusCode());
        assertEquals(HttpStatus.OK, response.getStatusCode());

        List<ProjectRD> projects = response.getBody();
        assertNotNull(projects);

        projects.forEach(project -> {
            assertNotNull(project);
            assertNotNull(project.id());
            assertNotNull(project.user());
            assertNull(project.tasks());
        });
    }

    @Test
    @Order(2)
    @DisplayName("Тестируем получение всех проектов с задачами")
    public void getAllProjectsWithTasks() {
        ResponseEntity<List<ProjectRD>> response = rest.exchange(
                PROJECT_URL + "?tasks",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {
                });
        assertNotNull(response);
        assertNotNull(response.getStatusCode());
        assertEquals(HttpStatus.OK, response.getStatusCode());

        List<ProjectRD> projects = response.getBody();
        assertNotNull(projects);

        projects.forEach(project -> {
            assertNotNull(project);
            assertNotNull(project.id());
            assertNotNull(project.user());
            assertNotNull(project.tasks());
        });
    }

    @Test
    @Order(3)
    @DisplayName("Тестируем получение проекта по id = 1 без задач")
    public void getProjectById() {
        long id = 1;

        ResponseEntity<ProjectRD> response = rest.exchange(
                PROJECT_URL + "/" + id,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {
                });
        assertNotNull(response);
        assertNotNull(response.getStatusCode());
        assertEquals(HttpStatus.OK, response.getStatusCode());

        ProjectRD project = response.getBody();
        assertNotNull(project);
        assertNotNull(project.id());
        assertEquals(id, project.id());
        assertNotNull(project.user());

        assertNull(project.tasks());
    }

    @Test
    @Order(4)
    @DisplayName("Тестируем получение проекта по id = 1 с задачами")
    public void getProjectByIdWithTasks() {
        long id = 1;

        ResponseEntity<ProjectRD> response = rest.exchange(
                PROJECT_URL + "/" + id + "?tasks",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {
                });
        assertNotNull(response);
        assertNotNull(response.getStatusCode());
        assertEquals(HttpStatus.OK, response.getStatusCode());

        ProjectRD project = response.getBody();
        assertNotNull(project);
        assertNotNull(project.id());
        assertEquals(id, project.id());
        assertNotNull(project.user());

        assertNotNull(project.tasks());
    }

    @Test
    @Order(5)
    @DisplayName("Тестируем получение задач проекта с id = 1")
    public void getProjectTasks() {
        long id = 1;

        ResponseEntity<List<TaskRD>> response = rest.exchange(
                PROJECT_URL + "/" + id + "/tasks",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {
                });
        assertNotNull(response);
        assertNotNull(response.getStatusCode());
        assertEquals(HttpStatus.OK, response.getStatusCode());

        List<TaskRD> tasks = response.getBody();
        assertNotNull(tasks);

        tasks.forEach(task -> {
            assertNotNull(task);
            assertNotNull(task.id());
            assertNotNull(task.user());
            assertNotNull(task.project());
            assertEquals(id, task.project());
        });
    }

    @Test
    @Order(6)
    @DisplayName("Тестируем создание новой задачи у проекта с id = 1")
    public void createTasks() {
        long projectId = 1;

        TaskWD task = TaskWD.builder()
                .title("Title")
                .description("Description")
                .status("begin")
                .build();

        ResponseEntity<TaskRD> response = rest.exchange(
                PROJECT_URL + "/" + projectId + "/tasks",
                HttpMethod.POST,
                new HttpEntity<>(task),
                TaskRD.class);
        assertNotNull(response);
        assertNotNull(response.getStatusCode());
        assertEquals(HttpStatus.CREATED, response.getStatusCode());

        HttpHeaders headers = response.getHeaders();
        assertNotNull(headers);
        List<String> locations = headers.get("Location");
        assertNotNull(locations);
        String locationStr = locations.getFirst();
        assertNotNull(locationStr);
        URI location = URI.create(locationStr);

        TaskRD savedTask = response.getBody();
        assertNotNull(savedTask);
        assertNotNull(savedTask.id());
        assertEquals(projectId, savedTask.project());
        assertEquals(task.title(), savedTask.title());
        assertEquals(task.description(), savedTask.description());
        assertEquals(task.status(), savedTask.status());

        assertTrue(locationStr.contains(savedTask.id().toString()));

        TaskRD taskById = rest.getForObject(location, TaskRD.class);
        assertNotNull(taskById);
        assertEquals(savedTask, taskById);
    }

    @Test
    @Order(7)
    @DisplayName("Тестируем обновление проекта (id = 1)")
    public void updateProject() {
        long id = 1;

        ProjectRD project = rest.getForObject(PROJECT_URL + "/" + id, ProjectRD.class);
        assertNotNull(project);
        assertNotNull(project.id());
        assertEquals(id, project.id());

        String name = "updatedName";
        String description = "updatedDescription";
        ProjectWD newProject = new ProjectWD(name, description);

        ResponseEntity<ProjectRD> response = rest.exchange(
                PROJECT_URL + "/" + id,
                HttpMethod.PUT,
                new HttpEntity<>(newProject),
                ProjectRD.class);
        assertNotNull(response);
        assertNotNull(response.getStatusCode());
        assertEquals(HttpStatus.OK, response.getStatusCode());

        ProjectRD updatedProject = response.getBody();
        assertNotNull(updatedProject);
        assertNotNull(updatedProject.id());
        assertEquals(id, updatedProject.id());
        assertNotNull(updatedProject.name());
        assertEquals(name, updatedProject.name());
        assertNotNull(updatedProject.description());
        assertEquals(description, updatedProject.description());
    }

    @Test
    @Order(8)
    @DisplayName("Тестируем патчинг проекта (id = 1)")
    public void patchProject() {
        long id = 1;

        ProjectRD project = rest.getForObject(PROJECT_URL + "/" + id, ProjectRD.class);
        assertNotNull(project);
        assertNotNull(project.id());
        assertEquals(id, project.id());

        String name = "updatedName";
        ProjectWD newProject = ProjectWD.builder()
                .name(name)
                .build();

        ResponseEntity<ProjectRD> response = rest.exchange(
                PROJECT_URL + "/" + id,
                HttpMethod.PATCH,
                new HttpEntity<>(newProject),
                ProjectRD.class);
        assertNotNull(response);
        assertNotNull(response.getStatusCode());
        assertEquals(HttpStatus.OK, response.getStatusCode());

        ProjectRD updatedProject = response.getBody();
        assertNotNull(updatedProject);
        assertNotNull(updatedProject.id());
        assertEquals(id, updatedProject.id());
        assertNotNull(updatedProject.name());
        assertEquals(name, updatedProject.name());
        assertNotNull(updatedProject.description());
        assertEquals(project.description(), updatedProject.description());
    }

    @Test
    @Order(9)
    @DisplayName("Тестируем удаление задач проекта (id = 1)")
    public void deleteProjectTasks() {
        long id = 1;

        ProjectRD project = rest.getForObject(PROJECT_URL + "/" + id + "?tasks", ProjectRD.class);
        assertNotNull(project);
        assertNotNull(project.id());
        assertEquals(id, project.id());
        assertNotNull(project.tasks());

        ResponseEntity<Void> response = rest.exchange(
                PROJECT_URL + "/" + id + "/tasks",
                HttpMethod.DELETE,
                null,
                Void.class);
        assertNotNull(response);
        assertNotNull(response.getStatusCode());
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());

        ProjectRD updatedProject = rest.getForObject(PROJECT_URL + "/" + id + "?tasks", ProjectRD.class);
        assertNotNull(updatedProject);
        assertNotNull(updatedProject.id());
        assertEquals(id, updatedProject.id());
        assertNotNull(updatedProject.tasks());
        assertTrue(updatedProject.tasks().isEmpty());
    }

    @Test
    @Order(10)
    @DisplayName("Тестируем удаление проекта (id = 1)")
    public void deleteProject() {
        long id = 1;

        @SuppressWarnings("unchecked")
        List<ProjectRD> projects = rest.getForObject(PROJECT_URL, List.class);
        assertNotNull(projects);
        assertFalse(projects.isEmpty());

        ProjectRD project = rest.getForObject(PROJECT_URL + "/" + id, ProjectRD.class);
        assertNotNull(project);
        assertNotNull(project.id());
        assertEquals(id, project.id());

        ResponseEntity<Void> response = rest.exchange(
                PROJECT_URL + "/" + id,
                HttpMethod.DELETE,
                null,
                Void.class);
        assertNotNull(response);
        assertNotNull(response.getStatusCode());
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());

        assertThrows(HttpClientErrorException.NotFound.class, () ->
                rest.getForObject(PROJECT_URL + "/" + id, ProjectRD.class));

        @SuppressWarnings("unchecked")
        List<ProjectRD> updatedProjects = rest.getForObject(PROJECT_URL, List.class);
        assertNotNull(updatedProjects);

        assertEquals(projects.size() - 1, updatedProjects.size());
    }
}
