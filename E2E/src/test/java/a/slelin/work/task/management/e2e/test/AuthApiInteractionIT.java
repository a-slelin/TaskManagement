package a.slelin.work.task.management.e2e.test;

import a.slelin.work.task.management.core.dto.SheetDto;
import a.slelin.work.task.management.core.dto.api.ProjectRD;
import a.slelin.work.task.management.core.dto.api.ProjectWD;
import a.slelin.work.task.management.core.dto.api.TaskRD;
import a.slelin.work.task.management.core.dto.api.TaskWD;
import a.slelin.work.task.management.core.dto.auth.JwtResponse;
import a.slelin.work.task.management.core.dto.auth.UserWD;
import a.slelin.work.task.management.core.exception.ErrorResponse;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.postgresql.PostgreSQLContainer;

import java.net.URI;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = Config.class)
@TestClassOrder(ClassOrderer.OrderAnnotation.class)
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
    @DisplayName("Проверяем защищён ли API")
    public void test1() {

        /*
         * Проверяем можем ли мы получить что-то от API,
         * без токена-доступа.
         * */

        try {
            rest.exchange(
                    apiUrl + "/api/projects",
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<>() {
                    }
            );
            fail("Should throw HttpClientErrorException.Unauthorized");

        } catch (HttpClientErrorException.Unauthorized e) {
            assertNotNull(e);
            assertNotNull(e.getStatusCode());
            assertEquals(HttpStatus.UNAUTHORIZED, e.getStatusCode());

            ErrorResponse errorResponse = e.getResponseBodyAs(ErrorResponse.class);
            assertNotNull(errorResponse);

        } catch (Exception e) {
            fail("Should throw HttpClientErrorException.Unauthorized, but got " + e.getMessage());
        }
    }

    @Test
    @Order(2)
    @DisplayName("""
            Регистрируем пользователя.
            Проверяем что его проекты пусты.
            Создаём новый проект.
            Проверяем что задач нет.
            Создаём новую задачу.
            Выходим из системы.
            """)
    public void test2() {

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

        assertNotNull(headers);
        URI uri = headers.getLocation();
        assertNotNull(uri);

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

        TaskWD task = new TaskWD("test_task", "test_description", "begin");

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

        HttpHeaders headers2 = new HttpHeaders();
        headers2.setBearerAuth(jwtResponse.refreshToken());

        ResponseEntity<Void> response6 = rest.exchange(
                authUrl + "/auth/logout",
                HttpMethod.GET,
                new HttpEntity<>(headers2),
                Void.class);
        assertNotNull(response6);
        assertNotNull(response6.getStatusCode());
        assertEquals(HttpStatus.OK, response6.getStatusCode());
    }
}
