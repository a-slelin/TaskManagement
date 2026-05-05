package a.slelin.work.task.management.api.test;

import a.slelin.work.task.management.core.dto.*;
import a.slelin.work.task.management.core.dto.api.ProjectRD;
import a.slelin.work.task.management.core.dto.api.ProjectWD;
import a.slelin.work.task.management.core.dto.api.TaskRD;
import a.slelin.work.task.management.core.dto.api.TaskWD;
import a.slelin.work.task.management.core.util.filter.Filter;
import a.slelin.work.task.management.core.util.filter.FilterChain;
import a.slelin.work.task.management.core.util.filter.Operation;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test")
@DisplayName("Тест контроллера проектов")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ProjectControllerTest {

    @Autowired
    private RestTemplate rest;

    @Autowired
    @Qualifier("alexToken")
    private String accessToken;

    @LocalServerPort
    private int port;

    private String projectUrl;

    @BeforeEach
    void beforeEach() {
        projectUrl = "http://localhost:%d/api/projects".formatted(port);
    }

    @Test
    @Order(1)
    @DisplayName("Тестируем получение всех проектов")
    public void getAllProjects() {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);
        HttpEntity<Void> httpEntity = new HttpEntity<>(headers);

        ResponseEntity<SheetDto<ProjectRD>> response = rest.exchange(
                projectUrl,
                HttpMethod.GET,
                httpEntity,
                new ParameterizedTypeReference<>() {
                });
        assertNotNull(response);
        assertNotNull(response.getStatusCode());
        assertEquals(HttpStatus.OK, response.getStatusCode());

        SheetDto<ProjectRD> sheet = response.getBody();
        assertNotNull(sheet);
        assertNotNull(sheet.page());
        assertNotNull(sheet.content());

        List<ProjectRD> projects = sheet.content();
        assertNotNull(projects);

        projects.forEach(project -> {
            assertNotNull(project);
            assertNotNull(project.id());
        });
    }

    @Test
    @Order(2)
    @DisplayName("Тестируем получение проекта")
    public void getProject() {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);
        HttpEntity<Void> httpEntity = new HttpEntity<>(headers);

        ResponseEntity<SheetDto<ProjectRD>> response = rest.exchange(
                projectUrl,
                HttpMethod.GET,
                httpEntity,
                new ParameterizedTypeReference<>() {
                });
        assertNotNull(response);
        assertNotNull(response.getStatusCode());
        assertEquals(HttpStatus.OK, response.getStatusCode());

        SheetDto<ProjectRD> sheet = response.getBody();
        assertNotNull(sheet);
        assertNotNull(sheet.page());
        assertNotNull(sheet.content());

        List<ProjectRD> projects = sheet.content();
        assertNotNull(projects);
        assertFalse(projects.isEmpty());

        long id = projects.getFirst().id();

        ResponseEntity<ProjectRD> response2 = rest.exchange(
                projectUrl + "/{id}",
                HttpMethod.GET,
                httpEntity,
                new ParameterizedTypeReference<>() {
                }, id);
        assertNotNull(response2);
        assertNotNull(response2.getStatusCode());
        assertEquals(HttpStatus.OK, response2.getStatusCode());

        ProjectRD project = response2.getBody();
        assertNotNull(project);
        assertNotNull(project.id());
        assertEquals(id, project.id());

        assertEquals(projects.getFirst(), project);
    }

    @Test
    @Order(3)
    @DisplayName("Тестируем получение задач проекта")
    public void getProjectTasks() {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);
        HttpEntity<Void> httpEntity = new HttpEntity<>(headers);

        ResponseEntity<SheetDto<ProjectRD>> response = rest.exchange(
                projectUrl,
                HttpMethod.GET,
                httpEntity,
                new ParameterizedTypeReference<>() {
                });
        assertNotNull(response);
        assertNotNull(response.getStatusCode());
        assertEquals(HttpStatus.OK, response.getStatusCode());

        SheetDto<ProjectRD> sheet = response.getBody();
        assertNotNull(sheet);
        assertNotNull(sheet.page());
        assertNotNull(sheet.content());

        List<ProjectRD> projects = sheet.content();
        assertNotNull(projects);
        assertFalse(projects.isEmpty());

        long id = projects.getFirst().id();

        ResponseEntity<SheetDto<TaskRD>> response2 = rest.exchange(
                projectUrl + "/{id}/tasks",
                HttpMethod.GET,
                httpEntity,
                new ParameterizedTypeReference<>() {
                }, id);
        assertNotNull(response2);
        assertNotNull(response2.getStatusCode());
        assertEquals(HttpStatus.OK, response2.getStatusCode());

        SheetDto<TaskRD> sheet2 = response2.getBody();
        assertNotNull(sheet2);
        assertNotNull(sheet2.page());
        assertNotNull(sheet2.content());

        List<TaskRD> tasks = sheet2.content();
        assertNotNull(tasks);
        assertFalse(tasks.isEmpty());

        tasks.forEach(task -> {
            assertNotNull(task);
            assertNotNull(task.id());
        });
    }

    @Test
    @Order(4)
    @DisplayName("Тестируем создание новой задачи у проекта с id")
    public void createTasks() {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);
        HttpEntity<Void> httpEntity = new HttpEntity<>(headers);

        ResponseEntity<SheetDto<ProjectRD>> response = rest.exchange(
                projectUrl,
                HttpMethod.GET,
                httpEntity,
                new ParameterizedTypeReference<>() {
                });
        assertNotNull(response);
        assertNotNull(response.getStatusCode());
        assertEquals(HttpStatus.OK, response.getStatusCode());

        SheetDto<ProjectRD> sheet = response.getBody();
        assertNotNull(sheet);
        assertNotNull(sheet.page());
        assertNotNull(sheet.content());

        List<ProjectRD> projects = sheet.content();
        assertNotNull(projects);
        assertFalse(projects.isEmpty());

        long projectId = projects.getFirst().id();

        TaskWD task = TaskWD.builder()
                .title("Title")
                .description("Description")
                .status("begin")
                .build();

        ResponseEntity<TaskRD> response2 = rest.exchange(
                projectUrl + "/{projectId}/tasks",
                HttpMethod.POST,
                new HttpEntity<>(task, headers),
                TaskRD.class, projectId);
        assertNotNull(response2);
        assertNotNull(response2.getStatusCode());
        assertEquals(HttpStatus.CREATED, response2.getStatusCode());

        HttpHeaders headers2 = response2.getHeaders();
        assertNotNull(headers2);
        List<String> locations = headers2.get("Location");
        assertNotNull(locations);
        String locationStr = locations.getFirst();
        assertNotNull(locationStr);
        URI location = URI.create(locationStr);

        TaskRD savedTask = response2.getBody();
        assertNotNull(savedTask);
        assertNotNull(savedTask.id());
        assertEquals(task.title(), savedTask.title());
        assertEquals(task.description(), savedTask.description());
        assertEquals(task.status(), savedTask.status());

        assertTrue(locationStr.contains(savedTask.id().toString()));

        TaskRD taskById = rest.exchange(
                location,
                HttpMethod.GET,
                httpEntity,
                TaskRD.class).getBody();
        assertNotNull(taskById);
        assertEquals(savedTask, taskById);
    }

    @Test
    @Order(5)
    @DisplayName("Тестируем обновление проекта")
    public void updateProject() {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);
        HttpEntity<Void> httpEntity = new HttpEntity<>(headers);

        ResponseEntity<SheetDto<ProjectRD>> response = rest.exchange(
                projectUrl,
                HttpMethod.GET,
                httpEntity,
                new ParameterizedTypeReference<>() {
                });
        assertNotNull(response);
        assertNotNull(response.getStatusCode());
        assertEquals(HttpStatus.OK, response.getStatusCode());

        SheetDto<ProjectRD> sheet = response.getBody();
        assertNotNull(sheet);
        assertNotNull(sheet.page());
        assertNotNull(sheet.content());

        List<ProjectRD> projects = sheet.content();
        assertNotNull(projects);
        assertFalse(projects.isEmpty());

        long id = projects.getFirst().id();

        ProjectRD project = rest.exchange(
                projectUrl + "/{id}",
                HttpMethod.GET,
                httpEntity,
                ProjectRD.class,
                id).getBody();
        assertNotNull(project);
        assertNotNull(project.id());
        assertEquals(id, project.id());

        String name = "updatedName";
        String description = "updatedDescription";
        ProjectWD newProject = new ProjectWD(name, description);

        ResponseEntity<ProjectRD> response2 = rest.exchange(
                projectUrl + "/{id}",
                HttpMethod.PUT,
                new HttpEntity<>(newProject, headers),
                ProjectRD.class, id);
        assertNotNull(response2);
        assertNotNull(response2.getStatusCode());
        assertEquals(HttpStatus.OK, response2.getStatusCode());

        ProjectRD updatedProject = response2.getBody();
        assertNotNull(updatedProject);
        assertNotNull(updatedProject.id());
        assertEquals(id, updatedProject.id());
        assertNotNull(updatedProject.name());
        assertEquals(name, updatedProject.name());
        assertNotNull(updatedProject.description());
        assertEquals(description, updatedProject.description());
    }

    @Test
    @Order(6)
    @DisplayName("Тестируем патчинг проекта")
    public void patchProject() {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);
        HttpEntity<Void> httpEntity = new HttpEntity<>(headers);

        ResponseEntity<SheetDto<ProjectRD>> response = rest.exchange(
                projectUrl,
                HttpMethod.GET,
                httpEntity,
                new ParameterizedTypeReference<>() {
                });
        assertNotNull(response);
        assertNotNull(response.getStatusCode());
        assertEquals(HttpStatus.OK, response.getStatusCode());

        SheetDto<ProjectRD> sheet = response.getBody();
        assertNotNull(sheet);
        assertNotNull(sheet.page());
        assertNotNull(sheet.content());

        List<ProjectRD> projects = sheet.content();
        assertNotNull(projects);
        assertFalse(projects.isEmpty());

        long id = projects.getFirst().id();

        ProjectRD project = rest.exchange(
                projectUrl + "/{id}",
                HttpMethod.GET,
                httpEntity,
                ProjectRD.class,
                id).getBody();
        assertNotNull(project);
        assertNotNull(project.id());
        assertEquals(id, project.id());

        String name = "updatedName";
        ProjectWD newProject = ProjectWD.builder()
                .name(name)
                .build();

        ResponseEntity<ProjectRD> response2 = rest.exchange(
                projectUrl + "/{id}",
                HttpMethod.PATCH,
                new HttpEntity<>(newProject, headers),
                ProjectRD.class, id);
        assertNotNull(response2);
        assertNotNull(response2.getStatusCode());
        assertEquals(HttpStatus.OK, response2.getStatusCode());

        ProjectRD updatedProject = response2.getBody();
        assertNotNull(updatedProject);
        assertNotNull(updatedProject.id());
        assertEquals(id, updatedProject.id());
        assertNotNull(updatedProject.name());
        assertEquals(name, updatedProject.name());
        assertNotNull(updatedProject.description());
        assertEquals(project.description(), updatedProject.description());
    }

    @Test
    @Order(7)
    @DisplayName("Тестируем удаление задач проекта")
    public void deleteProjectTasks() {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);
        HttpEntity<Void> httpEntity = new HttpEntity<>(headers);

        ResponseEntity<SheetDto<ProjectRD>> response = rest.exchange(
                projectUrl,
                HttpMethod.GET,
                httpEntity,
                new ParameterizedTypeReference<>() {
                });
        assertNotNull(response);
        assertNotNull(response.getStatusCode());
        assertEquals(HttpStatus.OK, response.getStatusCode());

        SheetDto<ProjectRD> sheet = response.getBody();
        assertNotNull(sheet);
        assertNotNull(sheet.page());
        assertNotNull(sheet.content());

        List<ProjectRD> projects = sheet.content();
        assertNotNull(projects);
        assertFalse(projects.isEmpty());

        long id = projects.getFirst().id();

        ProjectRD project = rest.exchange(
                projectUrl + "/{id}",
                HttpMethod.GET,
                httpEntity,
                ProjectRD.class,
                id).getBody();
        assertNotNull(project);
        assertNotNull(project.id());
        assertEquals(id, project.id());

        ResponseEntity<Void> response2 = rest.exchange(
                projectUrl + "/{id}/tasks",
                HttpMethod.DELETE,
                httpEntity,
                Void.class, id);
        assertNotNull(response2);
        assertNotNull(response2.getStatusCode());
        assertEquals(HttpStatus.NO_CONTENT, response2.getStatusCode());
    }

    @Test
    @Order(8)
    @DisplayName("Тестируем удаление проекта)")
    public void deleteProject() {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);
        HttpEntity<Void> httpEntity = new HttpEntity<>(headers);

        ResponseEntity<SheetDto<ProjectRD>> response = rest.exchange(
                projectUrl,
                HttpMethod.GET,
                httpEntity,
                new ParameterizedTypeReference<>() {
                });
        assertNotNull(response);
        assertNotNull(response.getStatusCode());
        assertEquals(HttpStatus.OK, response.getStatusCode());

        SheetDto<ProjectRD> sheet = response.getBody();
        assertNotNull(sheet);
        assertNotNull(sheet.page());
        assertNotNull(sheet.content());

        List<ProjectRD> projects = sheet.content();
        assertNotNull(projects);
        assertFalse(projects.isEmpty());

        long id = projects.getFirst().id();

        ProjectRD project = rest.exchange(
                projectUrl + "/{id}",
                HttpMethod.GET,
                httpEntity,
                ProjectRD.class,
                id).getBody();
        assertNotNull(project);
        assertNotNull(project.id());
        assertEquals(id, project.id());

        ResponseEntity<Void> response2 = rest.exchange(
                projectUrl + "/{id}",
                HttpMethod.DELETE,
                httpEntity,
                Void.class, id);
        assertNotNull(response2);
        assertNotNull(response2.getStatusCode());
        assertEquals(HttpStatus.NO_CONTENT, response2.getStatusCode());

        assertThrows(HttpClientErrorException.NotFound.class, () ->
                rest.exchange(projectUrl + "/{id}", HttpMethod.GET,
                        httpEntity, ProjectRD.class, id));
    }

    @Test
    @Order(9)
    @DisplayName("Тестируем получение проектов по фильтру")
    public void getProjectsByFilter() {
        FilterChain filters = FilterChain.empty()
                .add(Filter.of("description", Operation.IS_NOT_NULL));

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);
        ResponseEntity<SheetDto<ProjectRD>> response = rest.exchange(
                projectUrl + "/search",
                HttpMethod.POST,
                new HttpEntity<>(filters, headers),
                new ParameterizedTypeReference<>() {
                });
        assertNotNull(response);
        assertNotNull(response.getStatusCode());
        assertEquals(HttpStatus.OK, response.getStatusCode());

        SheetDto<ProjectRD> sheet = response.getBody();
        assertNotNull(sheet);
        assertNotNull(sheet.page());
        assertNotNull(sheet.content());

        List<ProjectRD> projects = sheet.content();
        assertNotNull(projects);

        projects.forEach(project -> {
            assertNotNull(project);
            assertNotNull(project.id());
            assertNotNull(project.description());
        });
    }

    @Test
    @Order(10)
    @DisplayName("Тестируем удаление всех проектов")
    public void deleteAllProject() {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);
        HttpEntity<Void> httpEntity = new HttpEntity<>(headers);

        ResponseEntity<Void> response = rest.exchange(
                projectUrl,
                HttpMethod.DELETE,
                httpEntity,
                Void.class
        );
        assertNotNull(response);
        assertNotNull(response.getStatusCode());
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());

        ResponseEntity<SheetDto<ProjectRD>> response2 = rest.exchange(
                projectUrl,
                HttpMethod.GET,
                httpEntity,
                new ParameterizedTypeReference<>() {
                });
        assertNotNull(response2);
        assertNotNull(response2.getStatusCode());
        assertEquals(HttpStatus.OK, response2.getStatusCode());

        SheetDto<ProjectRD> sheet = response2.getBody();
        assertNotNull(sheet);
        assertNotNull(sheet.page());
        assertNotNull(sheet.content());

        List<ProjectRD> projects = sheet.content();
        assertNotNull(projects);
        assertTrue(projects.isEmpty());
    }

    @Test
    @Order(11)
    @DisplayName("Тестируем создание нового проекта")
    public void createProject() {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);

        ProjectWD project = new ProjectWD("name", "description");
        HttpEntity<ProjectWD> httpEntity = new HttpEntity<>(project, headers);

        ResponseEntity<ProjectRD> response = rest.exchange(
                projectUrl,
                HttpMethod.POST,
                httpEntity,
                ProjectRD.class
        );
        assertNotNull(response);
        assertNotNull(response.getStatusCode());
        assertEquals(HttpStatus.CREATED, response.getStatusCode());

        ProjectRD savedProject = response.getBody();
        assertNotNull(savedProject);
        assertNotNull(savedProject.id());
        assertNotNull(savedProject.name());
        assertEquals(project.name(), savedProject.name());
        assertNotNull(savedProject.description());
        assertEquals(project.description(), savedProject.description());

        HttpHeaders headers2 = response.getHeaders();
        assertNotNull(headers2);
        List<String> locations = headers2.get("Location");
        assertNotNull(locations);
        String locationStr = locations.getFirst();
        assertNotNull(locationStr);
        URI location = URI.create(locationStr);

        assertTrue(locationStr.contains(savedProject.id().toString()));

        ProjectRD projectById = rest.exchange(
                location,
                HttpMethod.GET,
                new HttpEntity<>(headers),
                ProjectRD.class).getBody();
        assertNotNull(projectById);
        assertEquals(savedProject, projectById);
    }
}