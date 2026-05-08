package a.slelin.work.task.management.e2e.test;

import a.slelin.work.task.management.core.dto.SheetDto;
import a.slelin.work.task.management.core.dto.api.ProjectRD;
import a.slelin.work.task.management.core.dto.api.ProjectWD;
import a.slelin.work.task.management.core.dto.api.TaskRD;
import a.slelin.work.task.management.core.dto.api.TaskWD;
import a.slelin.work.task.management.core.dto.auth.JwtResponse;
import a.slelin.work.task.management.core.dto.auth.LoginRequest;
import a.slelin.work.task.management.core.dto.auth.UserWD;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.postgresql.PostgreSQLContainer;

import java.net.URI;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = Config.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DisplayName("Тестируем взаимодействие микросервисов auth & api")
public class AuthApiInteractionIT {

    @Autowired
    private ApplicationHolder holder;

    @Autowired
    private RestTemplate rest;

    @Autowired
    @Qualifier("apiDb")
    private PostgreSQLContainer apiDb;

    @Autowired
    @Qualifier("api")
    private GenericContainer<?> api;

    @Autowired
    @Qualifier("authDb")
    private PostgreSQLContainer authDb;

    @Autowired
    @Qualifier("auth")
    private GenericContainer<?> auth;

    private String apiUrl;

    private String authUrl;

    @BeforeEach
    @SuppressWarnings("HttpUrlsUsage")
    void setup() {
        apiDb.start();
        api.start();
        authDb.start();
        auth.start();

        apiUrl = "http://%s:%d".formatted(api.getHost(), api.getMappedPort(holder.apiPort()));
        authUrl = "http://%s:%d".formatted(auth.getHost(), auth.getMappedPort(holder.authPort()));
    }

    @AfterEach
    void teardown() {
        api.stop();
        apiDb.stop();
        auth.stop();
        authDb.stop();
    }

    @Test
    @Order(1)
    @DisplayName("Регистрация -> Работа с API -> Выход из системы")
    public void test1() {

        /*
         * Регистрируем нового пользователя и получаем к нему необходимые токены.
         * */

        UserWD user = UserWD.builder()
                .username("test_user")
                .password("test_password")
                .gender("male")
                .phone("+78964536363")
                .email("test.email@gmail.com")
                .build();

        ResponseEntity<JwtResponse> response = rest.exchange(
                authUrl + "/auth/register",
                HttpMethod.POST,
                new HttpEntity<>(user),
                JwtResponse.class);
        assertNotNull(response);
        assertNotNull(response.getStatusCode());
        assertEquals(HttpStatus.OK, response.getStatusCode());

        JwtResponse jwtResponse = response.getBody();
        assertNotNull(jwtResponse);
        assertNotNull(jwtResponse.accessToken());
        assertNotNull(jwtResponse.refreshToken());

        /*
         * Прочитаем текущие наши проекты.
         * Должно быть пусто.
         * */

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(jwtResponse.accessToken());

        ResponseEntity<SheetDto<ProjectRD>> response2 = rest.exchange(
                apiUrl + "/api/projects",
                HttpMethod.GET,
                new HttpEntity<>(headers),
                new ParameterizedTypeReference<>() {
                });
        assertNotNull(response2);
        assertNotNull(response2.getStatusCode());
        assertEquals(HttpStatus.OK, response2.getStatusCode());

        SheetDto<ProjectRD> sheet = response2.getBody();
        assertNotNull(sheet);
        assertNotNull(sheet.page());

        List<ProjectRD> projects = sheet.content();
        assertNotNull(projects);
        assertTrue(projects.isEmpty());

        /*
         * Создаем новый проект.
         * */

        ProjectWD project = new ProjectWD("test_project", "test_description");

        ResponseEntity<ProjectRD> response3 = rest.exchange(
                apiUrl + "/api/projects",
                HttpMethod.POST,
                new HttpEntity<>(project, headers),
                ProjectRD.class);
        assertNotNull(response3);
        assertNotNull(response3.getStatusCode());
        assertEquals(HttpStatus.CREATED, response3.getStatusCode());

        HttpHeaders headers2 = response3.getHeaders();
        assertNotNull(headers2);
        List<String> locations = headers2.get("Location");
        assertNotNull(locations);
        String locationStr = locations.getFirst();
        assertNotNull(locationStr);
        URI location = URI.create(locationStr);
        assertNotNull(location);

        ProjectRD savedProject = response3.getBody();
        assertNotNull(savedProject);
        assertNotNull(savedProject.id());
        assertNotNull(savedProject.name());
        assertEquals(project.name(), savedProject.name());
        assertNotNull(savedProject.description());
        assertEquals(project.description(), savedProject.description());

        /*
         * Прочитаем задачи только что созданного проекта.
         * Должно быть пусто.
         * */

        ResponseEntity<SheetDto<TaskRD>> response4 = rest.exchange(
                apiUrl + "/api/projects/{id}/tasks",
                HttpMethod.GET,
                new HttpEntity<>(headers),
                new ParameterizedTypeReference<>() {
                },
                savedProject.id());
        assertNotNull(response4);
        assertNotNull(response4.getStatusCode());
        assertEquals(HttpStatus.OK, response4.getStatusCode());

        SheetDto<TaskRD> sheet2 = response4.getBody();
        assertNotNull(sheet2);
        assertNotNull(sheet2.page());

        List<TaskRD> tasks = sheet2.content();
        assertNotNull(tasks);
        assertTrue(tasks.isEmpty());

        /*
         * Создаём новую задачу в проекте.
         * */

        TaskWD task = new TaskWD("test_task", "begin", "test_description");

        ResponseEntity<TaskRD> response5 = rest.exchange(
                apiUrl + "/api/projects/{id}/tasks",
                HttpMethod.POST,
                new HttpEntity<>(task, headers),
                TaskRD.class,
                savedProject.id());
        assertNotNull(response5);
        assertNotNull(response5.getStatusCode());
        assertEquals(HttpStatus.CREATED, response5.getStatusCode());

        assertNotNull(response5.getHeaders());
        URI uri2 = response5.getHeaders().getLocation();
        assertNotNull(uri2);

        TaskRD savedTask = response5.getBody();
        assertNotNull(savedTask);
        assertNotNull(savedTask.id());
        assertNotNull(savedTask.title());
        assertEquals(task.title(), savedTask.title());
        assertNotNull(savedTask.description());
        assertEquals(task.description(), savedTask.description());
        assertNotNull(savedTask.status());
        assertEquals(task.status(), savedTask.status());

        /*
         * Выходим из системы.
         * */

        HttpHeaders headers3 = new HttpHeaders();
        headers3.setBearerAuth(jwtResponse.refreshToken());

        ResponseEntity<Void> response6 = rest.exchange(
                authUrl + "/auth/logout",
                HttpMethod.GET,
                new HttpEntity<>(headers3),
                Void.class);
        assertNotNull(response6);
        assertNotNull(response6.getStatusCode());
        assertEquals(HttpStatus.NO_CONTENT, response6.getStatusCode());
    }

    @Test
    @Order(2)
    @DisplayName("Логин -> Работа с API -> Выход из системы")
    public void test2() {

        /*
         * Регистрируем нового пользователя.
         * */

        UserWD user = UserWD.builder()
                .username("test_user")
                .password("test_password")
                .gender("male")
                .phone("+78964536363")
                .email("test.email@gmail.com")
                .build();

        ResponseEntity<JwtResponse> response = rest.exchange(
                authUrl + "/auth/register",
                HttpMethod.POST,
                new HttpEntity<>(user),
                JwtResponse.class);
        assertNotNull(response);
        assertNotNull(response.getStatusCode());
        assertEquals(HttpStatus.OK, response.getStatusCode());

        JwtResponse jwtResponse = response.getBody();
        assertNotNull(jwtResponse);
        assertNotNull(jwtResponse.accessToken());
        assertNotNull(jwtResponse.refreshToken());

        /*
         * Логинимся под этим пользователем.
         * */

        LoginRequest login = new LoginRequest("test_user", "test_password");
        ResponseEntity<JwtResponse> response2 = rest.exchange(
                authUrl + "/auth/login",
                HttpMethod.POST,
                new HttpEntity<>(login),
                JwtResponse.class);
        assertNotNull(response2);
        assertNotNull(response2.getStatusCode());
        assertEquals(HttpStatus.OK, response2.getStatusCode());

        JwtResponse jwtResponse2 = response2.getBody();
        assertNotNull(jwtResponse2);
        assertNotNull(jwtResponse2.accessToken());
        assertNotNull(jwtResponse2.refreshToken());

        /*
         * Создаем новый проект.
         * */

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(jwtResponse2.accessToken());

        ProjectWD project = new ProjectWD("test_project", "test_project");
        ResponseEntity<ProjectRD> response3 = rest.exchange(
                apiUrl + "/api/projects",
                HttpMethod.POST,
                new HttpEntity<>(project, headers),
                ProjectRD.class);
        assertNotNull(response3);
        assertNotNull(response3.getStatusCode());
        assertEquals(HttpStatus.CREATED, response3.getStatusCode());

        HttpHeaders headers2 = response3.getHeaders();
        assertNotNull(headers2);
        List<String> locations = headers2.get("Location");
        assertNotNull(locations);
        String locationStr = locations.getFirst();
        assertNotNull(locationStr);
        URI location = URI.create(locationStr);
        assertNotNull(location);

        /*
         * Меняем проекту название.
         * */

        ProjectWD project2 = new ProjectWD("test1_project");
        ResponseEntity<ProjectRD> response4 = rest.exchange(
                location,
                HttpMethod.PATCH,
                new HttpEntity<>(project2, headers),
                ProjectRD.class);
        assertNotNull(response4);
        assertNotNull(response4.getStatusCode());
        assertEquals(HttpStatus.OK, response4.getStatusCode());

        /*
         * Выходим из системы.
         * */
        HttpHeaders headers3 = new HttpHeaders();
        headers3.setBearerAuth(jwtResponse.refreshToken());

        ResponseEntity<Void> response5 = rest.exchange(
                authUrl + "/auth/logout/all",
                HttpMethod.GET,
                new HttpEntity<>(headers3),
                Void.class);
        assertNotNull(response5);
        assertNotNull(response5.getStatusCode());
        assertEquals(HttpStatus.NO_CONTENT, response5.getStatusCode());
    }
}
