package a.slelin.work.task.management.api.test;

import a.slelin.work.task.management.core.dto.SheetDto;
import a.slelin.work.task.management.core.dto.api.ProjectRD;
import a.slelin.work.task.management.core.dto.api.TaskRD;
import a.slelin.work.task.management.core.dto.api.TaskWD;
import a.slelin.work.task.management.core.exception.ErrorResponse;
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

import java.util.List;

import static org.assertj.core.api.Fail.fail;
import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test")
@DisplayName("Тестируем TaskController")
@SuppressWarnings("CatchMayIgnoreException")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class TaskControllerTest {

    @Autowired
    private RestTemplate rest;

    @Autowired
    @Qualifier("alexToken")
    private String alexToken;

    @Autowired
    @Qualifier("ekaterinaToken")
    private String ekaterinaToken;

    @Autowired
    @Qualifier("adminToken")
    private String adminToken;

    @LocalServerPort
    private int port;

    private String projectUrl;

    private String taskUrl;

    @BeforeEach
    void beforeEach() {
        String baseUrl = "http://localhost:%d".formatted(port);
        projectUrl = baseUrl + "/api/projects";
        taskUrl = baseUrl + "/api/tasks";
    }

    @Test
    @Order(1)
    @DirtiesContext
    @DisplayName("Тестируем GET /api/tasks/{id} с неавторизированным пользователем : ошибка 401 неавторизован")
    public void test1() {

        /*
         * Получаем первый проект пользователя алекс.
         * */

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(alexToken);

        ResponseEntity<SheetDto<ProjectRD>> response = rest.exchange(
                projectUrl,
                HttpMethod.GET,
                new HttpEntity<>(headers),
                new ParameterizedTypeReference<>() {
                }
        );
        assertNotNull(response);
        assertNotNull(response.getStatusCode());
        assertEquals(HttpStatus.OK, response.getStatusCode());

        SheetDto<ProjectRD> sheet = response.getBody();
        assertNotNull(sheet);
        assertNotNull(sheet.page());

        List<ProjectRD> projects = sheet.content();
        assertNotNull(projects);
        assertFalse(projects.isEmpty());

        ProjectRD project = projects.getFirst();
        assertNotNull(project);
        Long projectId = project.id();
        assertNotNull(projectId);

        /*
         * Получаем первую задачу из этого проекта.
         * */

        ResponseEntity<SheetDto<TaskRD>> response2 = rest.exchange(
                projectUrl + "/{id}/tasks",
                HttpMethod.GET,
                new HttpEntity<>(headers),
                new ParameterizedTypeReference<>() {
                },
                projectId
        );
        assertNotNull(response2);
        assertNotNull(response2.getStatusCode());
        assertEquals(HttpStatus.OK, response2.getStatusCode());

        SheetDto<TaskRD> sheet2 = response2.getBody();
        assertNotNull(sheet2);
        assertNotNull(sheet2.page());

        List<TaskRD> tasks = sheet2.content();
        assertNotNull(tasks);
        assertFalse(tasks.isEmpty());

        TaskRD task = tasks.getFirst();
        assertNotNull(task);
        Long taskId = task.id();
        assertNotNull(taskId);

        /*
         * Пытаемся получить задачу по идентификатору
         * не передавая токен доступа.
         * */

        try {
            rest.exchange(
                    taskUrl + "/{id}",
                    HttpMethod.GET,
                    null,
                    TaskRD.class,
                    taskId
            );
            fail("Should throw HttpClientErrorException.Unauthorized");

        } catch (HttpClientErrorException.Unauthorized e) {
            assertNotNull(e);
            assertNotNull(e.getStatusCode());
            assertEquals(HttpStatus.UNAUTHORIZED, e.getStatusCode());

            ErrorResponse errorResponse = e.getResponseBodyAs(ErrorResponse.class);
            assertNotNull(errorResponse);
            assertNotNull(errorResponse.path());
            assertEquals(taskUrl + "/" + taskId, errorResponse.path());
            assertNotNull(errorResponse.httpMethod());
            assertEquals(HttpMethod.GET, errorResponse.httpMethod());
            assertNotNull(errorResponse.httpStatus());
            assertEquals(HttpStatus.UNAUTHORIZED, errorResponse.httpStatus());
            assertNotNull(errorResponse.message());
            assertNotNull(errorResponse.exception());
            assertNotNull(errorResponse.timestamp());

        } catch (Exception e) {
            fail("Should throw HttpClientErrorException.Unauthorized, but got " + e.getMessage());
        }
    }

    @Test
    @Order(2)
    @DirtiesContext
    @DisplayName("Тестируем GET /api/tasks/{id} с авторизованным пользователем : успех")
    public void test2() {

        /*
         * Получаем первый проект пользователя алекс.
         * */

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(alexToken);

        ResponseEntity<SheetDto<ProjectRD>> response = rest.exchange(
                projectUrl,
                HttpMethod.GET,
                new HttpEntity<>(headers),
                new ParameterizedTypeReference<>() {
                }
        );
        assertNotNull(response);
        assertNotNull(response.getStatusCode());
        assertEquals(HttpStatus.OK, response.getStatusCode());

        SheetDto<ProjectRD> sheet = response.getBody();
        assertNotNull(sheet);
        assertNotNull(sheet.page());

        List<ProjectRD> projects = sheet.content();
        assertNotNull(projects);
        assertFalse(projects.isEmpty());

        ProjectRD project = projects.getFirst();
        assertNotNull(project);
        Long projectId = project.id();
        assertNotNull(projectId);

        /*
         * Получаем первую задачу из этого проекта.
         * */

        ResponseEntity<SheetDto<TaskRD>> response2 = rest.exchange(
                projectUrl + "/{id}/tasks",
                HttpMethod.GET,
                new HttpEntity<>(headers),
                new ParameterizedTypeReference<>() {
                },
                projectId
        );
        assertNotNull(response2);
        assertNotNull(response2.getStatusCode());
        assertEquals(HttpStatus.OK, response2.getStatusCode());

        SheetDto<TaskRD> sheet2 = response2.getBody();
        assertNotNull(sheet2);
        assertNotNull(sheet2.page());

        List<TaskRD> tasks = sheet2.content();
        assertNotNull(tasks);
        assertFalse(tasks.isEmpty());

        TaskRD task = tasks.getFirst();
        assertNotNull(task);
        Long taskId = task.id();
        assertNotNull(taskId);

        /*
         * Получаем задачу по идентификатору.
         * */

        ResponseEntity<TaskRD> response3 = rest.exchange(
                taskUrl + "/{id}",
                HttpMethod.GET,
                new HttpEntity<>(headers),
                TaskRD.class,
                taskId
        );
        assertNotNull(response3);
        assertNotNull(response3.getStatusCode());
        assertEquals(HttpStatus.OK, response3.getStatusCode());
    }

    @Test
    @Order(3)
    @DirtiesContext
    @DisplayName("Тестируем GET /api/tasks/{id} с администратором : успех")
    public void test3() {

        /*
         * Получаем первый проект администратора.
         * */

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(adminToken);

        ResponseEntity<SheetDto<ProjectRD>> response = rest.exchange(
                projectUrl,
                HttpMethod.GET,
                new HttpEntity<>(headers),
                new ParameterizedTypeReference<>() {
                }
        );
        assertNotNull(response);
        assertNotNull(response.getStatusCode());
        assertEquals(HttpStatus.OK, response.getStatusCode());

        SheetDto<ProjectRD> sheet = response.getBody();
        assertNotNull(sheet);
        assertNotNull(sheet.page());

        List<ProjectRD> projects = sheet.content();
        assertNotNull(projects);
        assertFalse(projects.isEmpty());

        ProjectRD project = projects.getFirst();
        assertNotNull(project);
        Long projectId = project.id();
        assertNotNull(projectId);

        /*
         * Получаем первую задачу из этого проекта.
         * */

        ResponseEntity<SheetDto<TaskRD>> response2 = rest.exchange(
                projectUrl + "/{id}/tasks",
                HttpMethod.GET,
                new HttpEntity<>(headers),
                new ParameterizedTypeReference<>() {
                },
                projectId
        );
        assertNotNull(response2);
        assertNotNull(response2.getStatusCode());
        assertEquals(HttpStatus.OK, response2.getStatusCode());

        SheetDto<TaskRD> sheet2 = response2.getBody();
        assertNotNull(sheet2);
        assertNotNull(sheet2.page());

        List<TaskRD> tasks = sheet2.content();
        assertNotNull(tasks);
        assertFalse(tasks.isEmpty());

        TaskRD task = tasks.getFirst();
        assertNotNull(task);
        Long taskId = task.id();
        assertNotNull(taskId);

        /*
         * Получаем задачу по идентификатору.
         * */

        ResponseEntity<TaskRD> response3 = rest.exchange(
                taskUrl + "/{id}",
                HttpMethod.GET,
                new HttpEntity<>(headers),
                TaskRD.class,
                taskId
        );
        assertNotNull(response3);
        assertNotNull(response3.getStatusCode());
        assertEquals(HttpStatus.OK, response3.getStatusCode());
    }

    @Test
    @Order(4)
    @DirtiesContext
    @DisplayName("Тестируем GET /api/tasks/{id} : успех")
    public void test4() {

        /*
         * Получаем первый проект пользователя алекс.
         * */

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(alexToken);

        ResponseEntity<SheetDto<ProjectRD>> response = rest.exchange(
                projectUrl,
                HttpMethod.GET,
                new HttpEntity<>(headers),
                new ParameterizedTypeReference<>() {
                }
        );
        assertNotNull(response);
        assertNotNull(response.getStatusCode());
        assertEquals(HttpStatus.OK, response.getStatusCode());

        SheetDto<ProjectRD> sheet = response.getBody();
        assertNotNull(sheet);
        assertNotNull(sheet.page());

        List<ProjectRD> projects = sheet.content();
        assertNotNull(projects);
        assertFalse(projects.isEmpty());

        ProjectRD project = projects.getFirst();
        assertNotNull(project);
        Long projectId = project.id();
        assertNotNull(projectId);

        /*
         * Получаем первую задачу из этого проекта.
         * */

        ResponseEntity<SheetDto<TaskRD>> response2 = rest.exchange(
                projectUrl + "/{id}/tasks",
                HttpMethod.GET,
                new HttpEntity<>(headers),
                new ParameterizedTypeReference<>() {
                },
                projectId
        );
        assertNotNull(response2);
        assertNotNull(response2.getStatusCode());
        assertEquals(HttpStatus.OK, response2.getStatusCode());

        SheetDto<TaskRD> sheet2 = response2.getBody();
        assertNotNull(sheet2);
        assertNotNull(sheet2.page());

        List<TaskRD> tasks = sheet2.content();
        assertNotNull(tasks);
        assertFalse(tasks.isEmpty());

        TaskRD task = tasks.getFirst();
        assertNotNull(task);
        Long taskId = task.id();
        assertNotNull(taskId);

        /*
         * Получаем задачу по идентификатору.
         * */

        ResponseEntity<TaskRD> response3 = rest.exchange(
                taskUrl + "/{id}",
                HttpMethod.GET,
                new HttpEntity<>(headers),
                TaskRD.class,
                taskId
        );
        assertNotNull(response3);
        assertNotNull(response3.getStatusCode());
        assertEquals(HttpStatus.OK, response3.getStatusCode());

        TaskRD task2 = response3.getBody();
        assertNotNull(task2);
        assertEquals(task, task2);
    }

    @Test
    @Order(5)
    @DirtiesContext
    @DisplayName("Тестируем GET /api/tasks/{id} с некорректным id : ошибка 404 не найдено")
    public void test5() {

        /*
         * Пытаемся получить задачу по некорректному идентификатору
         * у пользователя алекс.
         * */

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(alexToken);

        Long taskId = Long.MAX_VALUE;

        try {
            rest.exchange(
                    taskUrl + "/{id}",
                    HttpMethod.GET,
                    new HttpEntity<>(headers),
                    TaskRD.class,
                    taskId
            );
            fail("Should throw HttpClientErrorException.NotFound");

        } catch (HttpClientErrorException.NotFound e) {
            assertNotNull(e);
            assertNotNull(e.getStatusCode());
            assertEquals(HttpStatus.NOT_FOUND, e.getStatusCode());

            ErrorResponse errorResponse = e.getResponseBodyAs(ErrorResponse.class);
            assertNotNull(errorResponse);
            assertNotNull(errorResponse.path());
            assertEquals(taskUrl + "/" + taskId, errorResponse.path());
            assertNotNull(errorResponse.httpMethod());
            assertEquals(HttpMethod.GET, errorResponse.httpMethod());
            assertNotNull(errorResponse.httpStatus());
            assertEquals(HttpStatus.NOT_FOUND, errorResponse.httpStatus());
            assertNotNull(errorResponse.message());
            assertNotNull(errorResponse.exception());
            assertNotNull(errorResponse.timestamp());

        } catch (Exception e) {
            fail("Should throw HttpClientErrorException.NotFound, but got " + e.getMessage());
        }
    }

    @Test
    @Order(6)
    @DirtiesContext
    @DisplayName("Тестируем GET /api/tasks/{id} с чужим id : ошибка 403 запрещено")
    public void test6() {

        /*
         * Получаем первый проект пользователя алекс.
         * */

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(alexToken);

        ResponseEntity<SheetDto<ProjectRD>> response = rest.exchange(
                projectUrl,
                HttpMethod.GET,
                new HttpEntity<>(headers),
                new ParameterizedTypeReference<>() {
                }
        );
        assertNotNull(response);
        assertNotNull(response.getStatusCode());
        assertEquals(HttpStatus.OK, response.getStatusCode());

        SheetDto<ProjectRD> sheet = response.getBody();
        assertNotNull(sheet);
        assertNotNull(sheet.page());

        List<ProjectRD> projects = sheet.content();
        assertNotNull(projects);
        assertFalse(projects.isEmpty());

        ProjectRD project = projects.getFirst();
        assertNotNull(project);
        Long projectId = project.id();
        assertNotNull(projectId);

        /*
         * Получаем первую задачу из этого проекта.
         * */

        ResponseEntity<SheetDto<TaskRD>> response2 = rest.exchange(
                projectUrl + "/{id}/tasks",
                HttpMethod.GET,
                new HttpEntity<>(headers),
                new ParameterizedTypeReference<>() {
                },
                projectId
        );
        assertNotNull(response2);
        assertNotNull(response2.getStatusCode());
        assertEquals(HttpStatus.OK, response2.getStatusCode());

        SheetDto<TaskRD> sheet2 = response2.getBody();
        assertNotNull(sheet2);
        assertNotNull(sheet2.page());

        List<TaskRD> tasks = sheet2.content();
        assertNotNull(tasks);
        assertFalse(tasks.isEmpty());

        TaskRD task = tasks.getFirst();
        assertNotNull(task);
        Long taskId = task.id();
        assertNotNull(taskId);

        /*
         * Пытаемся получить чужую задачу
         * по идентификатору.
         * */

        HttpHeaders headers2 = new HttpHeaders();
        headers2.setBearerAuth(ekaterinaToken);

        try {
            rest.exchange(
                    taskUrl + "/{id}",
                    HttpMethod.GET,
                    new HttpEntity<>(headers2),
                    TaskRD.class,
                    taskId
            );
            fail("Should throw HttpClientErrorException.Forbidden");

        } catch (HttpClientErrorException.Forbidden e) {
            assertNotNull(e);
            assertNotNull(e.getStatusCode());
            assertEquals(HttpStatus.FORBIDDEN, e.getStatusCode());

            ErrorResponse errorResponse = e.getResponseBodyAs(ErrorResponse.class);
            assertNotNull(errorResponse);
            assertNotNull(errorResponse.path());
            assertEquals(taskUrl + "/" + taskId, errorResponse.path());
            assertNotNull(errorResponse.httpMethod());
            assertEquals(HttpMethod.GET, errorResponse.httpMethod());
            assertNotNull(errorResponse.httpStatus());
            assertEquals(HttpStatus.FORBIDDEN, errorResponse.httpStatus());
            assertNotNull(errorResponse.message());
            assertNotNull(errorResponse.exception());
            assertNotNull(errorResponse.timestamp());

        } catch (Exception e) {
            fail("Should throw HttpClientErrorException.Forbidden, but got " + e.getMessage());
        }
    }

    @Test
    @Order(7)
    @DirtiesContext
    @DisplayName("Тестируем POST /api/tasks/search : успех")
    public void test7() {

        /*
         * Ищем все задачи пользователя алекс,
         * которые находятся в процессе.
         * */

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(alexToken);

        FilterChain filters = FilterChain
                .empty()
                .add(Filter.of("status", Operation.EQ, "in_progress"));

        ResponseEntity<SheetDto<TaskRD>> response = rest.exchange(
                taskUrl + "/search",
                HttpMethod.POST,
                new HttpEntity<>(filters, headers),
                new ParameterizedTypeReference<>() {
                }
        );
        assertNotNull(response);
        assertNotNull(response.getStatusCode());
        assertEquals(HttpStatus.OK, response.getStatusCode());

        SheetDto<TaskRD> sheet = response.getBody();
        assertNotNull(sheet);
        assertNotNull(sheet.page());

        List<TaskRD> tasks = sheet.content();
        assertNotNull(tasks);
        assertFalse(tasks.isEmpty());

        for (TaskRD task : tasks) {
            assertNotNull(task);
            assertNotNull(task.id());
            assertNotNull(task.title());
            assertNotNull(task.status());
            assertEquals("in_progress", task.status());
        }
    }

    @Test
    @Order(8)
    @DirtiesContext
    @DisplayName("Тестируем POST /api/tasks/search с некорректным фильтром : ошибка 400 плохой запрос")
    public void test8() {

        /*
         * Пытаемся получить задачи по некорректному фильтру
         * у пользователя алекс.
         * */

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(alexToken);

        FilterChain filters = FilterChain
                .empty()
                .add(Filter.of("bad", Operation.EQ, "some"));

        try {
            rest.exchange(
                    taskUrl + "/search",
                    HttpMethod.POST,
                    new HttpEntity<>(filters, headers),
                    new ParameterizedTypeReference<>() {
                    }
            );
            fail("Should throw HttpClientErrorException.BadRequest");

        } catch (HttpClientErrorException.BadRequest e) {
            assertNotNull(e);
            assertNotNull(e.getStatusCode());
            assertEquals(HttpStatus.BAD_REQUEST, e.getStatusCode());

            ErrorResponse errorResponse = e.getResponseBodyAs(ErrorResponse.class);
            assertNotNull(errorResponse);
            assertNotNull(errorResponse.path());
            assertEquals(taskUrl + "/search", errorResponse.path());
            assertNotNull(errorResponse.httpMethod());
            assertEquals(HttpMethod.POST, errorResponse.httpMethod());
            assertNotNull(errorResponse.httpStatus());
            assertEquals(HttpStatus.BAD_REQUEST, errorResponse.httpStatus());
            assertNotNull(errorResponse.message());
            assertNotNull(errorResponse.exception());
            assertNotNull(errorResponse.timestamp());

        } catch (Exception e) {
            fail("Should throw HttpClientErrorException.BadRequest, but got " + e.getMessage());
        }
    }

    @Test
    @Order(9)
    @DirtiesContext
    @DisplayName("Тестируем PUT /api/tasks/{id} : успех")
    public void test9() {

        /*
         * Получаем первый проект пользователя алекс.
         * */

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(alexToken);

        ResponseEntity<SheetDto<ProjectRD>> response = rest.exchange(
                projectUrl,
                HttpMethod.GET,
                new HttpEntity<>(headers),
                new ParameterizedTypeReference<>() {
                }
        );
        assertNotNull(response);
        assertNotNull(response.getStatusCode());
        assertEquals(HttpStatus.OK, response.getStatusCode());

        SheetDto<ProjectRD> sheet = response.getBody();
        assertNotNull(sheet);
        assertNotNull(sheet.page());

        List<ProjectRD> projects = sheet.content();
        assertNotNull(projects);
        assertFalse(projects.isEmpty());

        ProjectRD project = projects.getFirst();
        assertNotNull(project);
        Long projectId = project.id();
        assertNotNull(projectId);

        /*
         * Получаем первую задачу из этого проекта.
         * */

        ResponseEntity<SheetDto<TaskRD>> response2 = rest.exchange(
                projectUrl + "/{id}/tasks",
                HttpMethod.GET,
                new HttpEntity<>(headers),
                new ParameterizedTypeReference<>() {
                },
                projectId
        );
        assertNotNull(response2);
        assertNotNull(response2.getStatusCode());
        assertEquals(HttpStatus.OK, response2.getStatusCode());

        SheetDto<TaskRD> sheet2 = response2.getBody();
        assertNotNull(sheet2);
        assertNotNull(sheet2.page());

        List<TaskRD> tasks = sheet2.content();
        assertNotNull(tasks);
        assertFalse(tasks.isEmpty());

        TaskRD task = tasks.getFirst();
        assertNotNull(task);
        Long taskId = task.id();
        assertNotNull(taskId);

        /*
         * Обновляем задачу по идентификатору.
         * */

        TaskWD updTask = TaskWD.builder()
                .title("some_title")
                .status("begin")
                .description("some_description")
                .build();

        ResponseEntity<TaskRD> response3 = rest.exchange(
                taskUrl + "/{id}",
                HttpMethod.PUT,
                new HttpEntity<>(updTask, headers),
                TaskRD.class,
                taskId
        );
        assertNotNull(response3);
        assertNotNull(response3.getStatusCode());
        assertEquals(HttpStatus.OK, response3.getStatusCode());

        TaskRD task2 = response3.getBody();
        assertNotNull(task2);
        assertNotNull(task2.id());
        assertNotNull(task2.title());
        assertEquals(updTask.title(), task2.title());
        assertNotNull(task2.status());
        assertEquals(updTask.status(), task2.status());
        assertNotNull(task2.description());
        assertEquals(updTask.description(), task2.description());

        /*
         * Проверяем, что задача полностью была обновлена.
         * */

        ResponseEntity<TaskRD> response4 = rest.exchange(
                taskUrl + "/{id}",
                HttpMethod.GET,
                new HttpEntity<>(headers),
                TaskRD.class,
                taskId
        );
        assertNotNull(response4);
        assertNotNull(response4.getStatusCode());
        assertEquals(HttpStatus.OK, response4.getStatusCode());

        TaskRD task3 = response3.getBody();
        assertNotNull(task3);
        assertEquals(task2, task3);
    }

    @Test
    @Order(10)
    @DirtiesContext
    @DisplayName("Тестируем PUT /api/tasks/{id} c некорректной задачей : ошибка 400 плохой запрос")
    public void test10() {

        /*
         * Получаем первый проект пользователя алекс.
         * */

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(alexToken);

        ResponseEntity<SheetDto<ProjectRD>> response = rest.exchange(
                projectUrl,
                HttpMethod.GET,
                new HttpEntity<>(headers),
                new ParameterizedTypeReference<>() {
                }
        );
        assertNotNull(response);
        assertNotNull(response.getStatusCode());
        assertEquals(HttpStatus.OK, response.getStatusCode());

        SheetDto<ProjectRD> sheet = response.getBody();
        assertNotNull(sheet);
        assertNotNull(sheet.page());

        List<ProjectRD> projects = sheet.content();
        assertNotNull(projects);
        assertFalse(projects.isEmpty());

        ProjectRD project = projects.getFirst();
        assertNotNull(project);
        Long projectId = project.id();
        assertNotNull(projectId);

        /*
         * Получаем первую задачу из этого проекта.
         * */

        ResponseEntity<SheetDto<TaskRD>> response2 = rest.exchange(
                projectUrl + "/{id}/tasks",
                HttpMethod.GET,
                new HttpEntity<>(headers),
                new ParameterizedTypeReference<>() {
                },
                projectId
        );
        assertNotNull(response2);
        assertNotNull(response2.getStatusCode());
        assertEquals(HttpStatus.OK, response2.getStatusCode());

        SheetDto<TaskRD> sheet2 = response2.getBody();
        assertNotNull(sheet2);
        assertNotNull(sheet2.page());

        List<TaskRD> tasks = sheet2.content();
        assertNotNull(tasks);
        assertFalse(tasks.isEmpty());

        TaskRD task = tasks.getFirst();
        assertNotNull(task);
        Long taskId = task.id();
        assertNotNull(taskId);

        /*
         * Пытаемся обновить некорректную задачу по идентификатору.
         * */

        TaskWD updTask = TaskWD.builder()
                .title("some_title")
                .status("some_status")
                .description("some_description")
                .build();

        try {
            rest.exchange(
                    taskUrl + "/{id}",
                    HttpMethod.PUT,
                    new HttpEntity<>(updTask, headers),
                    TaskRD.class,
                    taskId
            );
            fail("Should throw HttpClientErrorException.BadRequest");

        } catch (HttpClientErrorException.BadRequest e) {
            assertNotNull(e);
            assertNotNull(e.getStatusCode());
            assertEquals(HttpStatus.BAD_REQUEST, e.getStatusCode());

            ErrorResponse errorResponse = e.getResponseBodyAs(ErrorResponse.class);
            assertNotNull(errorResponse);
            assertNotNull(errorResponse.path());
            assertEquals(taskUrl + "/" + taskId, errorResponse.path());
            assertNotNull(errorResponse.httpMethod());
            assertEquals(HttpMethod.PUT, errorResponse.httpMethod());
            assertNotNull(errorResponse.httpStatus());
            assertEquals(HttpStatus.BAD_REQUEST, errorResponse.httpStatus());
            assertNotNull(errorResponse.message());
            assertNotNull(errorResponse.exception());
            assertNotNull(errorResponse.timestamp());

        } catch (Exception e) {
            fail("Should throw HttpClientErrorException.BadRequest, but got " + e.getMessage());
        }
    }

    @Test
    @Order(11)
    @DirtiesContext
    @DisplayName("Тестируем PUT /api/tasks/{id} c некорректным id : ошибка 404 не найдено")
    public void test11() {

        /*
         * Пытаемся обновить задачу пользователя алекс
         * по некорректному идентификатору.
         * */

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(alexToken);

        Long taskId = Long.MAX_VALUE;

        TaskWD updTask = TaskWD.builder()
                .title("some_title")
                .status("begin")
                .description("some_description")
                .build();

        try {
            rest.exchange(
                    taskUrl + "/{id}",
                    HttpMethod.PUT,
                    new HttpEntity<>(updTask, headers),
                    TaskRD.class,
                    taskId
            );
            fail("Should throw HttpClientErrorException.NotFound");

        } catch (HttpClientErrorException.NotFound e) {
            assertNotNull(e);
            assertNotNull(e.getStatusCode());
            assertEquals(HttpStatus.NOT_FOUND, e.getStatusCode());

            ErrorResponse errorResponse = e.getResponseBodyAs(ErrorResponse.class);
            assertNotNull(errorResponse);
            assertNotNull(errorResponse.path());
            assertEquals(taskUrl + "/" + taskId, errorResponse.path());
            assertNotNull(errorResponse.httpMethod());
            assertEquals(HttpMethod.PUT, errorResponse.httpMethod());
            assertNotNull(errorResponse.httpStatus());
            assertEquals(HttpStatus.NOT_FOUND, errorResponse.httpStatus());
            assertNotNull(errorResponse.message());
            assertNotNull(errorResponse.exception());
            assertNotNull(errorResponse.timestamp());

        } catch (Exception e) {
            fail("Should throw HttpClientErrorException.NotFound, but got " + e.getMessage());
        }
    }

    @Test
    @Order(12)
    @DirtiesContext
    @DisplayName("Тестируем PUT /api/tasks/{id} с чужим id : ошибка 403 запрещено")
    public void test12() {

        /*
         * Получаем первый проект пользователя алекс.
         * */

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(alexToken);

        ResponseEntity<SheetDto<ProjectRD>> response = rest.exchange(
                projectUrl,
                HttpMethod.GET,
                new HttpEntity<>(headers),
                new ParameterizedTypeReference<>() {
                }
        );
        assertNotNull(response);
        assertNotNull(response.getStatusCode());
        assertEquals(HttpStatus.OK, response.getStatusCode());

        SheetDto<ProjectRD> sheet = response.getBody();
        assertNotNull(sheet);
        assertNotNull(sheet.page());

        List<ProjectRD> projects = sheet.content();
        assertNotNull(projects);
        assertFalse(projects.isEmpty());

        ProjectRD project = projects.getFirst();
        assertNotNull(project);
        Long projectId = project.id();
        assertNotNull(projectId);

        /*
         * Получаем первую задачу из этого проекта.
         * */

        ResponseEntity<SheetDto<TaskRD>> response2 = rest.exchange(
                projectUrl + "/{id}/tasks",
                HttpMethod.GET,
                new HttpEntity<>(headers),
                new ParameterizedTypeReference<>() {
                },
                projectId
        );
        assertNotNull(response2);
        assertNotNull(response2.getStatusCode());
        assertEquals(HttpStatus.OK, response2.getStatusCode());

        SheetDto<TaskRD> sheet2 = response2.getBody();
        assertNotNull(sheet2);
        assertNotNull(sheet2.page());

        List<TaskRD> tasks = sheet2.content();
        assertNotNull(tasks);
        assertFalse(tasks.isEmpty());

        TaskRD task = tasks.getFirst();
        assertNotNull(task);
        Long taskId = task.id();
        assertNotNull(taskId);

        /*
         * Пытаемся обновить чужую задачу по идентификатору.
         * */

        HttpHeaders headers2 = new HttpHeaders();
        headers2.setBearerAuth(ekaterinaToken);

        TaskWD updTask = TaskWD.builder()
                .title("some_title")
                .status("begin")
                .description("some_description")
                .build();

        try {
            rest.exchange(
                    taskUrl + "/{id}",
                    HttpMethod.PUT,
                    new HttpEntity<>(updTask, headers2),
                    TaskRD.class,
                    taskId
            );
            fail("Should throw HttpClientErrorException.Forbidden");

        } catch (HttpClientErrorException.Forbidden e) {
            assertNotNull(e);
            assertNotNull(e.getStatusCode());
            assertEquals(HttpStatus.FORBIDDEN, e.getStatusCode());

            ErrorResponse errorResponse = e.getResponseBodyAs(ErrorResponse.class);
            assertNotNull(errorResponse);
            assertNotNull(errorResponse.path());
            assertEquals(taskUrl + "/" + taskId, errorResponse.path());
            assertNotNull(errorResponse.httpMethod());
            assertEquals(HttpMethod.PUT, errorResponse.httpMethod());
            assertNotNull(errorResponse.httpStatus());
            assertEquals(HttpStatus.FORBIDDEN, errorResponse.httpStatus());
            assertNotNull(errorResponse.message());
            assertNotNull(errorResponse.exception());
            assertNotNull(errorResponse.timestamp());

        } catch (Exception e) {
            fail("Should throw HttpClientErrorException.Forbidden, but got " + e.getMessage());
        }
    }

    @Test
    @Order(13)
    @DirtiesContext
    @DisplayName("Тестируем PUT /api/tasks/{id} c некорректной задачей (уникальные поля) : ошибка 409 конфликт")
    public void test13() {

        /*
         * Получаем первый проект пользователя алекс.
         * */

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(alexToken);

        ResponseEntity<SheetDto<ProjectRD>> response = rest.exchange(
                projectUrl,
                HttpMethod.GET,
                new HttpEntity<>(headers),
                new ParameterizedTypeReference<>() {
                }
        );
        assertNotNull(response);
        assertNotNull(response.getStatusCode());
        assertEquals(HttpStatus.OK, response.getStatusCode());

        SheetDto<ProjectRD> sheet = response.getBody();
        assertNotNull(sheet);
        assertNotNull(sheet.page());

        List<ProjectRD> projects = sheet.content();
        assertNotNull(projects);
        assertFalse(projects.isEmpty());

        ProjectRD project = projects.getFirst();
        assertNotNull(project);
        Long projectId = project.id();
        assertNotNull(projectId);

        /*
         * Получаем первую задачу из этого проекта.
         * */

        ResponseEntity<SheetDto<TaskRD>> response2 = rest.exchange(
                projectUrl + "/{id}/tasks",
                HttpMethod.GET,
                new HttpEntity<>(headers),
                new ParameterizedTypeReference<>() {
                },
                projectId
        );
        assertNotNull(response2);
        assertNotNull(response2.getStatusCode());
        assertEquals(HttpStatus.OK, response2.getStatusCode());

        SheetDto<TaskRD> sheet2 = response2.getBody();
        assertNotNull(sheet2);
        assertNotNull(sheet2.page());

        List<TaskRD> tasks = sheet2.content();
        assertNotNull(tasks);
        assertFalse(tasks.isEmpty());
        assertTrue(tasks.size() >= 2);

        TaskRD task = tasks.getFirst();
        assertNotNull(task);
        Long taskId = task.id();
        assertNotNull(taskId);

        TaskRD task2 = tasks.getLast();
        assertNotNull(task2);
        String task2Title = task2.title();
        assertNotNull(task2Title);

        /*
         * Пытаемся обновить задачу по идентификатору
         * с заголовком, который уже есть у этого проекта.
         * */

        TaskWD updTask = TaskWD.builder()
                .title(task2Title)
                .status("begin")
                .description("some_description")
                .build();

        try {
            rest.exchange(
                    taskUrl + "/{id}",
                    HttpMethod.PUT,
                    new HttpEntity<>(updTask, headers),
                    TaskRD.class,
                    taskId
            );
            fail("Should throw HttpClientErrorException.Conflict");

        } catch (HttpClientErrorException.Conflict e) {
            assertNotNull(e);
            assertNotNull(e.getStatusCode());
            assertEquals(HttpStatus.CONFLICT, e.getStatusCode());

            ErrorResponse errorResponse = e.getResponseBodyAs(ErrorResponse.class);
            assertNotNull(errorResponse);
            assertNotNull(errorResponse.path());
            assertEquals(taskUrl + "/" + taskId, errorResponse.path());
            assertNotNull(errorResponse.httpMethod());
            assertEquals(HttpMethod.PUT, errorResponse.httpMethod());
            assertNotNull(errorResponse.httpStatus());
            assertEquals(HttpStatus.CONFLICT, errorResponse.httpStatus());
            assertNotNull(errorResponse.message());
            assertNotNull(errorResponse.exception());
            assertNotNull(errorResponse.timestamp());

        } catch (Exception e) {
            fail("Should throw HttpClientErrorException.Conflict, but got " + e.getMessage());
        }
    }

    @Test
    @Order(14)
    @DirtiesContext
    @DisplayName("Тестируем PATCH /api/tasks/{id} : успех")
    public void test14() {

        /*
         * Получаем первый проект пользователя алекс.
         * */

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(alexToken);

        ResponseEntity<SheetDto<ProjectRD>> response = rest.exchange(
                projectUrl,
                HttpMethod.GET,
                new HttpEntity<>(headers),
                new ParameterizedTypeReference<>() {
                }
        );
        assertNotNull(response);
        assertNotNull(response.getStatusCode());
        assertEquals(HttpStatus.OK, response.getStatusCode());

        SheetDto<ProjectRD> sheet = response.getBody();
        assertNotNull(sheet);
        assertNotNull(sheet.page());

        List<ProjectRD> projects = sheet.content();
        assertNotNull(projects);
        assertFalse(projects.isEmpty());

        ProjectRD project = projects.getFirst();
        assertNotNull(project);
        Long projectId = project.id();
        assertNotNull(projectId);

        /*
         * Получаем первую задачу из этого проекта.
         * */

        ResponseEntity<SheetDto<TaskRD>> response2 = rest.exchange(
                projectUrl + "/{id}/tasks",
                HttpMethod.GET,
                new HttpEntity<>(headers),
                new ParameterizedTypeReference<>() {
                },
                projectId
        );
        assertNotNull(response2);
        assertNotNull(response2.getStatusCode());
        assertEquals(HttpStatus.OK, response2.getStatusCode());

        SheetDto<TaskRD> sheet2 = response2.getBody();
        assertNotNull(sheet2);
        assertNotNull(sheet2.page());

        List<TaskRD> tasks = sheet2.content();
        assertNotNull(tasks);
        assertFalse(tasks.isEmpty());

        TaskRD task = tasks.getFirst();
        assertNotNull(task);
        Long taskId = task.id();
        assertNotNull(taskId);

        /*
         * Обновляем задачу по идентификатору.
         * */

        TaskWD updTask = TaskWD.builder()
                .status("begin")
                .description("some_description")
                .build();

        ResponseEntity<TaskRD> response3 = rest.exchange(
                taskUrl + "/{id}",
                HttpMethod.PATCH,
                new HttpEntity<>(updTask, headers),
                TaskRD.class,
                taskId
        );
        assertNotNull(response3);
        assertNotNull(response3.getStatusCode());
        assertEquals(HttpStatus.OK, response3.getStatusCode());

        TaskRD task2 = response3.getBody();
        assertNotNull(task2);
        assertNotNull(task2.id());
        assertNotNull(task2.title());
        assertEquals(task.title(), task2.title());
        assertNotNull(task2.status());
        assertEquals(updTask.status(), task2.status());
        assertNotNull(task2.description());
        assertEquals(updTask.description(), task2.description());

        /*
         * Проверяем, что задача была обновлена.
         * */

        ResponseEntity<TaskRD> response4 = rest.exchange(
                taskUrl + "/{id}",
                HttpMethod.GET,
                new HttpEntity<>(headers),
                TaskRD.class,
                taskId
        );
        assertNotNull(response4);
        assertNotNull(response4.getStatusCode());
        assertEquals(HttpStatus.OK, response4.getStatusCode());

        TaskRD task3 = response3.getBody();
        assertNotNull(task3);
        assertEquals(task2, task3);
    }

    @Test
    @Order(15)
    @DirtiesContext
    @DisplayName("Тестируем PATCH /api/tasks/{id} с некорректной задачей : ошибка 400 плохой запрос")
    public void test15() {

        /*
         * Получаем первый проект пользователя алекс.
         * */

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(alexToken);

        ResponseEntity<SheetDto<ProjectRD>> response = rest.exchange(
                projectUrl,
                HttpMethod.GET,
                new HttpEntity<>(headers),
                new ParameterizedTypeReference<>() {
                }
        );
        assertNotNull(response);
        assertNotNull(response.getStatusCode());
        assertEquals(HttpStatus.OK, response.getStatusCode());

        SheetDto<ProjectRD> sheet = response.getBody();
        assertNotNull(sheet);
        assertNotNull(sheet.page());

        List<ProjectRD> projects = sheet.content();
        assertNotNull(projects);
        assertFalse(projects.isEmpty());

        ProjectRD project = projects.getFirst();
        assertNotNull(project);
        Long projectId = project.id();
        assertNotNull(projectId);

        /*
         * Получаем первую задачу из этого проекта.
         * */

        ResponseEntity<SheetDto<TaskRD>> response2 = rest.exchange(
                projectUrl + "/{id}/tasks",
                HttpMethod.GET,
                new HttpEntity<>(headers),
                new ParameterizedTypeReference<>() {
                },
                projectId
        );
        assertNotNull(response2);
        assertNotNull(response2.getStatusCode());
        assertEquals(HttpStatus.OK, response2.getStatusCode());

        SheetDto<TaskRD> sheet2 = response2.getBody();
        assertNotNull(sheet2);
        assertNotNull(sheet2.page());

        List<TaskRD> tasks = sheet2.content();
        assertNotNull(tasks);
        assertFalse(tasks.isEmpty());

        TaskRD task = tasks.getFirst();
        assertNotNull(task);
        Long taskId = task.id();
        assertNotNull(taskId);

        /*
         * Пытаемся обновить плохую задачу по идентификатору.
         * */

        TaskWD updTask = TaskWD.builder()
                .status("some_status")
                .build();

        try {
            rest.exchange(
                    taskUrl + "/{id}",
                    HttpMethod.PATCH,
                    new HttpEntity<>(updTask, headers),
                    TaskRD.class,
                    taskId
            );
            fail("Should throw HttpClientErrorException.BadRequest");

        } catch (HttpClientErrorException.BadRequest e) {
            assertNotNull(e);
            assertNotNull(e.getStatusCode());
            assertEquals(HttpStatus.BAD_REQUEST, e.getStatusCode());

            ErrorResponse errorResponse = e.getResponseBodyAs(ErrorResponse.class);
            assertNotNull(errorResponse);
            assertNotNull(errorResponse.path());
            assertEquals(taskUrl + "/" + taskId, errorResponse.path());
            assertNotNull(errorResponse.httpMethod());
            assertEquals(HttpMethod.PATCH, errorResponse.httpMethod());
            assertNotNull(errorResponse.httpStatus());
            assertEquals(HttpStatus.BAD_REQUEST, errorResponse.httpStatus());
            assertNotNull(errorResponse.message());
            assertNotNull(errorResponse.exception());
            assertNotNull(errorResponse.timestamp());

        } catch (Exception e) {
            fail("Should throw HttpClientErrorException.BadRequest, but got " + e.getMessage());
        }
    }

    @Test
    @Order(16)
    @DirtiesContext
    @DisplayName("Тестируем PATCH /api/tasks/{id} с некорректным id : ошибка 404 не найдено")
    public void test16() {

        /*
         * Пытаемся обновить задачу пользователя алекс
         * по некорректному идентификатору.
         * */

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(alexToken);

        Long taskId = Long.MAX_VALUE;

        TaskWD updTask = TaskWD.builder()
                .status("begin")
                .description("some_description")
                .build();

        try {
            rest.exchange(
                    taskUrl + "/{id}",
                    HttpMethod.PATCH,
                    new HttpEntity<>(updTask, headers),
                    TaskRD.class,
                    taskId
            );
            fail("Should throw HttpClientErrorException.NotFound");

        } catch (HttpClientErrorException.NotFound e) {
            assertNotNull(e);
            assertNotNull(e.getStatusCode());
            assertEquals(HttpStatus.NOT_FOUND, e.getStatusCode());

            ErrorResponse errorResponse = e.getResponseBodyAs(ErrorResponse.class);
            assertNotNull(errorResponse);
            assertNotNull(errorResponse.path());
            assertEquals(taskUrl + "/" + taskId, errorResponse.path());
            assertNotNull(errorResponse.httpMethod());
            assertEquals(HttpMethod.PATCH, errorResponse.httpMethod());
            assertNotNull(errorResponse.httpStatus());
            assertEquals(HttpStatus.NOT_FOUND, errorResponse.httpStatus());
            assertNotNull(errorResponse.message());
            assertNotNull(errorResponse.exception());
            assertNotNull(errorResponse.timestamp());

        } catch (Exception e) {
            fail("Should throw HttpClientErrorException.NotFound, but got " + e.getMessage());
        }
    }

    @Test
    @Order(17)
    @DirtiesContext
    @DisplayName("Тестируем PATCH /api/tasks/{id} с чужим id : ошибка 403 запрещено")
    public void test17() {

        /*
         * Получаем первый проект пользователя алекс.
         * */

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(alexToken);

        ResponseEntity<SheetDto<ProjectRD>> response = rest.exchange(
                projectUrl,
                HttpMethod.GET,
                new HttpEntity<>(headers),
                new ParameterizedTypeReference<>() {
                }
        );
        assertNotNull(response);
        assertNotNull(response.getStatusCode());
        assertEquals(HttpStatus.OK, response.getStatusCode());

        SheetDto<ProjectRD> sheet = response.getBody();
        assertNotNull(sheet);
        assertNotNull(sheet.page());

        List<ProjectRD> projects = sheet.content();
        assertNotNull(projects);
        assertFalse(projects.isEmpty());

        ProjectRD project = projects.getFirst();
        assertNotNull(project);
        Long projectId = project.id();
        assertNotNull(projectId);

        /*
         * Получаем первую задачу из этого проекта.
         * */

        ResponseEntity<SheetDto<TaskRD>> response2 = rest.exchange(
                projectUrl + "/{id}/tasks",
                HttpMethod.GET,
                new HttpEntity<>(headers),
                new ParameterizedTypeReference<>() {
                },
                projectId
        );
        assertNotNull(response2);
        assertNotNull(response2.getStatusCode());
        assertEquals(HttpStatus.OK, response2.getStatusCode());

        SheetDto<TaskRD> sheet2 = response2.getBody();
        assertNotNull(sheet2);
        assertNotNull(sheet2.page());

        List<TaskRD> tasks = sheet2.content();
        assertNotNull(tasks);
        assertFalse(tasks.isEmpty());

        TaskRD task = tasks.getFirst();
        assertNotNull(task);
        Long taskId = task.id();
        assertNotNull(taskId);

        /*
         * Пытаемся обновить чужую задачу по идентификатору.
         * */

        HttpHeaders headers2 = new HttpHeaders();
        headers2.setBearerAuth(ekaterinaToken);

        TaskWD updTask = TaskWD.builder()
                .status("begin")
                .description("some_description")
                .build();

        try {
            rest.exchange(
                    taskUrl + "/{id}",
                    HttpMethod.PATCH,
                    new HttpEntity<>(updTask, headers2),
                    TaskRD.class,
                    taskId
            );
            fail("Should throw HttpClientErrorException.Forbidden");

        } catch (HttpClientErrorException.Forbidden e) {
            assertNotNull(e);
            assertNotNull(e.getStatusCode());
            assertEquals(HttpStatus.FORBIDDEN, e.getStatusCode());

            ErrorResponse errorResponse = e.getResponseBodyAs(ErrorResponse.class);
            assertNotNull(errorResponse);
            assertNotNull(errorResponse.path());
            assertEquals(taskUrl + "/" + taskId, errorResponse.path());
            assertNotNull(errorResponse.httpMethod());
            assertEquals(HttpMethod.PATCH, errorResponse.httpMethod());
            assertNotNull(errorResponse.httpStatus());
            assertEquals(HttpStatus.FORBIDDEN, errorResponse.httpStatus());
            assertNotNull(errorResponse.message());
            assertNotNull(errorResponse.exception());
            assertNotNull(errorResponse.timestamp());

        } catch (Exception e) {
            fail("Should throw HttpClientErrorException.Forbidden, but got " + e.getMessage());
        }
    }

    @Test
    @Order(18)
    @DirtiesContext
    @DisplayName("Тестируем PATCH /api/tasks/{id} с некорректной задачей (уникальные поля) : ошибка 409 конфликт")
    public void test18() {

        /*
         * Получаем первый проект пользователя алекс.
         * */

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(alexToken);

        ResponseEntity<SheetDto<ProjectRD>> response = rest.exchange(
                projectUrl,
                HttpMethod.GET,
                new HttpEntity<>(headers),
                new ParameterizedTypeReference<>() {
                }
        );
        assertNotNull(response);
        assertNotNull(response.getStatusCode());
        assertEquals(HttpStatus.OK, response.getStatusCode());

        SheetDto<ProjectRD> sheet = response.getBody();
        assertNotNull(sheet);
        assertNotNull(sheet.page());

        List<ProjectRD> projects = sheet.content();
        assertNotNull(projects);
        assertFalse(projects.isEmpty());

        ProjectRD project = projects.getFirst();
        assertNotNull(project);
        Long projectId = project.id();
        assertNotNull(projectId);

        /*
         * Получаем первую задачу из этого проекта.
         * */

        ResponseEntity<SheetDto<TaskRD>> response2 = rest.exchange(
                projectUrl + "/{id}/tasks",
                HttpMethod.GET,
                new HttpEntity<>(headers),
                new ParameterizedTypeReference<>() {
                },
                projectId
        );
        assertNotNull(response2);
        assertNotNull(response2.getStatusCode());
        assertEquals(HttpStatus.OK, response2.getStatusCode());

        SheetDto<TaskRD> sheet2 = response2.getBody();
        assertNotNull(sheet2);
        assertNotNull(sheet2.page());

        List<TaskRD> tasks = sheet2.content();
        assertNotNull(tasks);
        assertFalse(tasks.isEmpty());
        assertTrue(tasks.size() >= 2);

        TaskRD task = tasks.getFirst();
        assertNotNull(task);
        Long taskId = task.id();
        assertNotNull(taskId);

        TaskRD task2 = tasks.getLast();
        assertNotNull(task2);
        String task2Title = task2.title();
        assertNotNull(task2Title);

        /*
         * Пытаемся обновить задачу по идентификатору
         * с заголовком, который уже есть у другой задачи
         * в этом проекте.
         * */

        TaskWD updTask = TaskWD.builder()
                .title(task2Title)
                .build();

        try {
            rest.exchange(
                    taskUrl + "/{id}",
                    HttpMethod.PATCH,
                    new HttpEntity<>(updTask, headers),
                    TaskRD.class,
                    taskId
            );
            fail("Should throw HttpClientErrorException.Conflict");

        } catch (HttpClientErrorException.Conflict e) {
            assertNotNull(e);
            assertNotNull(e.getStatusCode());
            assertEquals(HttpStatus.CONFLICT, e.getStatusCode());

            ErrorResponse errorResponse = e.getResponseBodyAs(ErrorResponse.class);
            assertNotNull(errorResponse);
            assertNotNull(errorResponse.path());
            assertEquals(taskUrl + "/" + taskId, errorResponse.path());
            assertNotNull(errorResponse.httpMethod());
            assertEquals(HttpMethod.PATCH, errorResponse.httpMethod());
            assertNotNull(errorResponse.httpStatus());
            assertEquals(HttpStatus.CONFLICT, errorResponse.httpStatus());
            assertNotNull(errorResponse.message());
            assertNotNull(errorResponse.exception());
            assertNotNull(errorResponse.timestamp());

        } catch (Exception e) {
            fail("Should throw HttpClientErrorException.Conflict, but got " + e.getMessage());
        }
    }

    @Test
    @Order(19)
    @DirtiesContext
    @DisplayName("Тестируем PATCH /api/tasks/{id}/project/{projectId} : успех")
    public void test19() {

        /*
         * Получаем проекты пользователя алекс.
         * */

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(alexToken);

        ResponseEntity<SheetDto<ProjectRD>> response = rest.exchange(
                projectUrl,
                HttpMethod.GET,
                new HttpEntity<>(headers),
                new ParameterizedTypeReference<>() {
                }
        );
        assertNotNull(response);
        assertNotNull(response.getStatusCode());
        assertEquals(HttpStatus.OK, response.getStatusCode());

        SheetDto<ProjectRD> sheet = response.getBody();
        assertNotNull(sheet);
        assertNotNull(sheet.page());

        List<ProjectRD> projects = sheet.content();
        assertNotNull(projects);
        assertFalse(projects.isEmpty());
        assertTrue(projects.size() >= 2);

        ProjectRD project = projects.getFirst();
        assertNotNull(project);
        Long projectId = project.id();
        assertNotNull(projectId);

        ProjectRD project2 = projects.getLast();
        assertNotNull(project2);
        Long project2Id = project2.id();
        assertNotNull(project2Id);

        /*
         * Получаем первую задачу из первого проекта.
         * */

        ResponseEntity<SheetDto<TaskRD>> response2 = rest.exchange(
                projectUrl + "/{id}/tasks",
                HttpMethod.GET,
                new HttpEntity<>(headers),
                new ParameterizedTypeReference<>() {
                },
                projectId
        );
        assertNotNull(response2);
        assertNotNull(response2.getStatusCode());
        assertEquals(HttpStatus.OK, response2.getStatusCode());

        SheetDto<TaskRD> sheet2 = response2.getBody();
        assertNotNull(sheet2);
        assertNotNull(sheet2.page());

        List<TaskRD> tasks = sheet2.content();
        assertNotNull(tasks);
        assertFalse(tasks.isEmpty());

        TaskRD task = tasks.getFirst();
        assertNotNull(task);
        Long taskId = task.id();
        assertNotNull(taskId);

        /*
         * Переносим задачу на второй проект.
         * */

        ResponseEntity<TaskRD> response3 = rest.exchange(
                taskUrl + "/{id}/project/{projectId}",
                HttpMethod.PATCH,
                new HttpEntity<>(headers),
                TaskRD.class,
                taskId,
                project2Id
        );
        assertNotNull(response3);
        assertNotNull(response3.getStatusCode());
        assertEquals(HttpStatus.OK, response3.getStatusCode());

        TaskRD task2 = response3.getBody();
        assertNotNull(task2);
        assertEquals(task, task2);

        /*
         * Проверяем, что в первом проекте нет этой задачи.
         * */

        ResponseEntity<SheetDto<TaskRD>> response4 = rest.exchange(
                projectUrl + "/{id}/tasks",
                HttpMethod.GET,
                new HttpEntity<>(headers),
                new ParameterizedTypeReference<>() {
                },
                projectId
        );
        assertNotNull(response4);
        assertNotNull(response4.getStatusCode());
        assertEquals(HttpStatus.OK, response4.getStatusCode());

        SheetDto<TaskRD> sheet3 = response4.getBody();
        assertNotNull(sheet3);
        assertNotNull(sheet3.page());

        List<TaskRD> tasks3 = sheet3.content();
        assertNotNull(tasks3);

        for (TaskRD taskRD : tasks3) {
            assertNotNull(taskRD);
            assertNotNull(taskRD.id());
            assertNotEquals(taskId, taskRD.id());
        }

        /*
         * Проверяем, что во втором проекте есть эта задача.
         * */

        ResponseEntity<SheetDto<TaskRD>> response5 = rest.exchange(
                projectUrl + "/{id}/tasks",
                HttpMethod.GET,
                new HttpEntity<>(headers),
                new ParameterizedTypeReference<>() {
                },
                project2Id
        );
        assertNotNull(response5);
        assertNotNull(response5.getStatusCode());
        assertEquals(HttpStatus.OK, response5.getStatusCode());

        SheetDto<TaskRD> sheet4 = response5.getBody();
        assertNotNull(sheet4);
        assertNotNull(sheet4.page());

        List<TaskRD> tasks4 = sheet4.content();
        assertNotNull(tasks4);

        boolean flag = false;
        for (TaskRD taskRD : tasks4) {
            assertNotNull(taskRD);
            assertNotNull(taskRD.id());

            if (taskRD.id().equals(taskId)) {
                flag = true;
            }
        }
        assertTrue(flag);
    }

    @Test
    @Order(20)
    @DirtiesContext
    @DisplayName("Тестируем PATCH /api/tasks/{id}/project/{projectId} с некорректным id : ошибка 404 не найдено")
    public void test20() {

        /*
         * Получаем проекты пользователя алекс.
         * */

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(alexToken);

        ResponseEntity<SheetDto<ProjectRD>> response = rest.exchange(
                projectUrl,
                HttpMethod.GET,
                new HttpEntity<>(headers),
                new ParameterizedTypeReference<>() {
                }
        );
        assertNotNull(response);
        assertNotNull(response.getStatusCode());
        assertEquals(HttpStatus.OK, response.getStatusCode());

        SheetDto<ProjectRD> sheet = response.getBody();
        assertNotNull(sheet);
        assertNotNull(sheet.page());

        List<ProjectRD> projects = sheet.content();
        assertNotNull(projects);
        assertFalse(projects.isEmpty());
        assertTrue(projects.size() >= 2);

        ProjectRD project = projects.getFirst();
        assertNotNull(project);
        Long projectId = project.id();
        assertNotNull(projectId);

        ProjectRD project2 = projects.getLast();
        assertNotNull(project2);
        Long project2Id = project2.id();
        assertNotNull(project2Id);

        /*
         * Пытаемся перенести задачу с некорректным
         * идентификатором.
         * */

        Long taskId = Long.MAX_VALUE;

        try {
            rest.exchange(
                    taskUrl + "/{id}/project/{projectId}",
                    HttpMethod.PATCH,
                    new HttpEntity<>(headers),
                    TaskRD.class,
                    taskId,
                    project2Id
            );
            fail("Should throw HttpClientErrorException.NotFound");

        } catch (HttpClientErrorException.NotFound e) {
            assertNotNull(e);
            assertNotNull(e.getStatusCode());
            assertEquals(HttpStatus.NOT_FOUND, e.getStatusCode());

            ErrorResponse errorResponse = e.getResponseBodyAs(ErrorResponse.class);
            assertNotNull(errorResponse);
            assertNotNull(errorResponse.path());
            assertEquals(taskUrl + "/" + taskId + "/project/" + project2Id, errorResponse.path());
            assertNotNull(errorResponse.httpMethod());
            assertEquals(HttpMethod.PATCH, errorResponse.httpMethod());
            assertNotNull(errorResponse.httpStatus());
            assertEquals(HttpStatus.NOT_FOUND, errorResponse.httpStatus());
            assertNotNull(errorResponse.message());
            assertNotNull(errorResponse.exception());
            assertNotNull(errorResponse.timestamp());

        } catch (Exception e) {
            fail("Should throw HttpClientErrorException.NotFound, but got " + e.getMessage());
        }
    }

    @Test
    @Order(21)
    @DirtiesContext
    @DisplayName("Тестируем PATCH /api/tasks/{id}/project/{projectId} с некорректным projectId : ошибка 404 не найдено")
    public void test21() {

        /*
         * Получаем проекты пользователя алекс.
         * */

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(alexToken);

        ResponseEntity<SheetDto<ProjectRD>> response = rest.exchange(
                projectUrl,
                HttpMethod.GET,
                new HttpEntity<>(headers),
                new ParameterizedTypeReference<>() {
                }
        );
        assertNotNull(response);
        assertNotNull(response.getStatusCode());
        assertEquals(HttpStatus.OK, response.getStatusCode());

        SheetDto<ProjectRD> sheet = response.getBody();
        assertNotNull(sheet);
        assertNotNull(sheet.page());

        List<ProjectRD> projects = sheet.content();
        assertNotNull(projects);
        assertFalse(projects.isEmpty());
        assertTrue(projects.size() >= 2);

        ProjectRD project = projects.getFirst();
        assertNotNull(project);
        Long projectId = project.id();
        assertNotNull(projectId);

        /*
         * Получаем первую задачу из первого проекта.
         * */

        ResponseEntity<SheetDto<TaskRD>> response2 = rest.exchange(
                projectUrl + "/{id}/tasks",
                HttpMethod.GET,
                new HttpEntity<>(headers),
                new ParameterizedTypeReference<>() {
                },
                projectId
        );
        assertNotNull(response2);
        assertNotNull(response2.getStatusCode());
        assertEquals(HttpStatus.OK, response2.getStatusCode());

        SheetDto<TaskRD> sheet2 = response2.getBody();
        assertNotNull(sheet2);
        assertNotNull(sheet2.page());

        List<TaskRD> tasks = sheet2.content();
        assertNotNull(tasks);
        assertFalse(tasks.isEmpty());

        TaskRD task = tasks.getFirst();
        assertNotNull(task);
        Long taskId = task.id();
        assertNotNull(taskId);

        /*
         * Пытаемся перенести задачу на проект
         * с некорректным идентификатором.
         * */

        Long project2Id = Long.MAX_VALUE;

        try {
            rest.exchange(
                    taskUrl + "/{id}/project/{projectId}",
                    HttpMethod.PATCH,
                    new HttpEntity<>(headers),
                    TaskRD.class,
                    taskId,
                    project2Id
            );
            fail("Should throw HttpClientErrorException.NotFound");

        } catch (HttpClientErrorException.NotFound e) {
            assertNotNull(e);
            assertNotNull(e.getStatusCode());
            assertEquals(HttpStatus.NOT_FOUND, e.getStatusCode());

            ErrorResponse errorResponse = e.getResponseBodyAs(ErrorResponse.class);
            assertNotNull(errorResponse);
            assertNotNull(errorResponse.path());
            assertEquals(taskUrl + "/" + taskId + "/project/" + project2Id, errorResponse.path());
            assertNotNull(errorResponse.httpMethod());
            assertEquals(HttpMethod.PATCH, errorResponse.httpMethod());
            assertNotNull(errorResponse.httpStatus());
            assertEquals(HttpStatus.NOT_FOUND, errorResponse.httpStatus());
            assertNotNull(errorResponse.message());
            assertNotNull(errorResponse.exception());
            assertNotNull(errorResponse.timestamp());

        } catch (Exception e) {
            fail("Should throw HttpClientErrorException.NotFound, but got " + e.getMessage());
        }
    }

    @Test
    @Order(22)
    @DirtiesContext
    @DisplayName("Тестируем PATCH /api/tasks/{id}/project/{projectId} с чужим projectId : ошибка 403 запрещено")
    public void test22() {

        /*
         * Получаем проекты пользователя алекс.
         * */

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(alexToken);

        ResponseEntity<SheetDto<ProjectRD>> response = rest.exchange(
                projectUrl,
                HttpMethod.GET,
                new HttpEntity<>(headers),
                new ParameterizedTypeReference<>() {
                }
        );
        assertNotNull(response);
        assertNotNull(response.getStatusCode());
        assertEquals(HttpStatus.OK, response.getStatusCode());

        SheetDto<ProjectRD> sheet = response.getBody();
        assertNotNull(sheet);
        assertNotNull(sheet.page());

        List<ProjectRD> projects = sheet.content();
        assertNotNull(projects);
        assertFalse(projects.isEmpty());
        assertTrue(projects.size() >= 2);

        ProjectRD project = projects.getFirst();
        assertNotNull(project);
        Long projectId = project.id();
        assertNotNull(projectId);

        /*
         * Получаем первую задачу из первого проекта.
         * */

        ResponseEntity<SheetDto<TaskRD>> response2 = rest.exchange(
                projectUrl + "/{id}/tasks",
                HttpMethod.GET,
                new HttpEntity<>(headers),
                new ParameterizedTypeReference<>() {
                },
                projectId
        );
        assertNotNull(response2);
        assertNotNull(response2.getStatusCode());
        assertEquals(HttpStatus.OK, response2.getStatusCode());

        SheetDto<TaskRD> sheet2 = response2.getBody();
        assertNotNull(sheet2);
        assertNotNull(sheet2.page());

        List<TaskRD> tasks = sheet2.content();
        assertNotNull(tasks);
        assertFalse(tasks.isEmpty());

        TaskRD task = tasks.getFirst();
        assertNotNull(task);
        Long taskId = task.id();
        assertNotNull(taskId);

        /*
         * Получаем проект другого пользователя.
         * */

        HttpHeaders headers2 = new HttpHeaders();
        headers2.setBearerAuth(ekaterinaToken);

        ResponseEntity<SheetDto<ProjectRD>> response3 = rest.exchange(
                projectUrl,
                HttpMethod.GET,
                new HttpEntity<>(headers2),
                new ParameterizedTypeReference<>() {
                }
        );
        assertNotNull(response3);
        assertNotNull(response3.getStatusCode());
        assertEquals(HttpStatus.OK, response3.getStatusCode());

        SheetDto<ProjectRD> sheet3 = response3.getBody();
        assertNotNull(sheet3);
        assertNotNull(sheet3.page());

        List<ProjectRD> projects2 = sheet3.content();
        assertNotNull(projects2);
        assertFalse(projects2.isEmpty());
        assertTrue(projects2.size() >= 2);

        ProjectRD project2 = projects2.getFirst();
        assertNotNull(project2);
        Long project2Id = project2.id();
        assertNotNull(project2Id);

        /*
         * Пытаемся перенести задачу с проекта на чужой проект.
         * */

        try {
            rest.exchange(
                    taskUrl + "/{id}/project/{projectId}",
                    HttpMethod.PATCH,
                    new HttpEntity<>(headers),
                    TaskRD.class,
                    taskId,
                    project2Id
            );
            fail("Should throw HttpClientErrorException.Forbidden");

        } catch (HttpClientErrorException.Forbidden e) {
            assertNotNull(e);
            assertNotNull(e.getStatusCode());
            assertEquals(HttpStatus.FORBIDDEN, e.getStatusCode());

            ErrorResponse errorResponse = e.getResponseBodyAs(ErrorResponse.class);
            assertNotNull(errorResponse);
            assertNotNull(errorResponse.path());
            assertEquals(taskUrl + "/" + taskId + "/project/" + project2Id, errorResponse.path());
            assertNotNull(errorResponse.httpMethod());
            assertEquals(HttpMethod.PATCH, errorResponse.httpMethod());
            assertNotNull(errorResponse.httpStatus());
            assertEquals(HttpStatus.FORBIDDEN, errorResponse.httpStatus());
            assertNotNull(errorResponse.message());
            assertNotNull(errorResponse.exception());
            assertNotNull(errorResponse.timestamp());

        } catch (Exception e) {
            fail("Should throw HttpClientErrorException.Forbidden, but got " + e.getMessage());
        }
    }

    @Test
    @Order(23)
    @DirtiesContext
    @DisplayName("Тестируем PATCH /api/tasks/{id}/project/{projectId} с чужим id : ошибка 403 запрещено")
    public void test23() {

        /*
         * Получаем проекты пользователя алекс.
         * */

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(alexToken);

        ResponseEntity<SheetDto<ProjectRD>> response = rest.exchange(
                projectUrl,
                HttpMethod.GET,
                new HttpEntity<>(headers),
                new ParameterizedTypeReference<>() {
                }
        );
        assertNotNull(response);
        assertNotNull(response.getStatusCode());
        assertEquals(HttpStatus.OK, response.getStatusCode());

        SheetDto<ProjectRD> sheet = response.getBody();
        assertNotNull(sheet);
        assertNotNull(sheet.page());

        List<ProjectRD> projects = sheet.content();
        assertNotNull(projects);
        assertFalse(projects.isEmpty());
        assertTrue(projects.size() >= 2);

        ProjectRD project = projects.getFirst();
        assertNotNull(project);
        Long projectId = project.id();
        assertNotNull(projectId);

        /*
         * Получаем первую задачу из первого проекта.
         * */

        ResponseEntity<SheetDto<TaskRD>> response2 = rest.exchange(
                projectUrl + "/{id}/tasks",
                HttpMethod.GET,
                new HttpEntity<>(headers),
                new ParameterizedTypeReference<>() {
                },
                projectId
        );
        assertNotNull(response2);
        assertNotNull(response2.getStatusCode());
        assertEquals(HttpStatus.OK, response2.getStatusCode());

        SheetDto<TaskRD> sheet2 = response2.getBody();
        assertNotNull(sheet2);
        assertNotNull(sheet2.page());

        List<TaskRD> tasks = sheet2.content();
        assertNotNull(tasks);
        assertFalse(tasks.isEmpty());

        TaskRD task = tasks.getFirst();
        assertNotNull(task);
        Long taskId = task.id();
        assertNotNull(taskId);

        /*
         * Получаем проект другого пользователя.
         * */

        HttpHeaders headers2 = new HttpHeaders();
        headers2.setBearerAuth(ekaterinaToken);

        ResponseEntity<SheetDto<ProjectRD>> response3 = rest.exchange(
                projectUrl,
                HttpMethod.GET,
                new HttpEntity<>(headers2),
                new ParameterizedTypeReference<>() {
                }
        );
        assertNotNull(response3);
        assertNotNull(response3.getStatusCode());
        assertEquals(HttpStatus.OK, response3.getStatusCode());

        SheetDto<ProjectRD> sheet3 = response3.getBody();
        assertNotNull(sheet3);
        assertNotNull(sheet3.page());

        List<ProjectRD> projects2 = sheet3.content();
        assertNotNull(projects2);
        assertFalse(projects2.isEmpty());
        assertTrue(projects2.size() >= 2);

        ProjectRD project2 = projects.getFirst();
        assertNotNull(project2);
        Long project2Id = project2.id();
        assertNotNull(project2Id);

        /*
         * Пытаемся перенести чужую задачу
         * с чужого проекта на свой проект.
         * */

        try {
            rest.exchange(
                    taskUrl + "/{id}/project/{projectId}",
                    HttpMethod.PATCH,
                    new HttpEntity<>(headers2),
                    TaskRD.class,
                    taskId,
                    project2Id
            );
            fail("Should throw HttpClientErrorException.Forbidden");

        } catch (HttpClientErrorException.Forbidden e) {
            assertNotNull(e);
            assertNotNull(e.getStatusCode());
            assertEquals(HttpStatus.FORBIDDEN, e.getStatusCode());

            ErrorResponse errorResponse = e.getResponseBodyAs(ErrorResponse.class);
            assertNotNull(errorResponse);
            assertNotNull(errorResponse.path());
            assertEquals(taskUrl + "/" + taskId + "/project/" + project2Id, errorResponse.path());
            assertNotNull(errorResponse.httpMethod());
            assertEquals(HttpMethod.PATCH, errorResponse.httpMethod());
            assertNotNull(errorResponse.httpStatus());
            assertEquals(HttpStatus.FORBIDDEN, errorResponse.httpStatus());
            assertNotNull(errorResponse.message());
            assertNotNull(errorResponse.exception());
            assertNotNull(errorResponse.timestamp());

        } catch (Exception e) {
            fail("Should throw HttpClientErrorException.Forbidden, but got " + e.getMessage());
        }
    }

    @Test
    @Order(24)
    @DirtiesContext
    @DisplayName("Тестируем PATCH /api/tasks/{id}/project/{projectId} с тем же самым projectId : успех")
    public void test24() {

        /*
         * Получаем проекты пользователя алекс.
         * */

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(alexToken);

        ResponseEntity<SheetDto<ProjectRD>> response = rest.exchange(
                projectUrl,
                HttpMethod.GET,
                new HttpEntity<>(headers),
                new ParameterizedTypeReference<>() {
                }
        );
        assertNotNull(response);
        assertNotNull(response.getStatusCode());
        assertEquals(HttpStatus.OK, response.getStatusCode());

        SheetDto<ProjectRD> sheet = response.getBody();
        assertNotNull(sheet);
        assertNotNull(sheet.page());

        List<ProjectRD> projects = sheet.content();
        assertNotNull(projects);
        assertFalse(projects.isEmpty());
        assertTrue(projects.size() >= 2);

        ProjectRD project = projects.getFirst();
        assertNotNull(project);
        Long projectId = project.id();
        assertNotNull(projectId);

        /*
         * Получаем первую задачу из первого проекта.
         * */

        ResponseEntity<SheetDto<TaskRD>> response2 = rest.exchange(
                projectUrl + "/{id}/tasks",
                HttpMethod.GET,
                new HttpEntity<>(headers),
                new ParameterizedTypeReference<>() {
                },
                projectId
        );
        assertNotNull(response2);
        assertNotNull(response2.getStatusCode());
        assertEquals(HttpStatus.OK, response2.getStatusCode());

        SheetDto<TaskRD> sheet2 = response2.getBody();
        assertNotNull(sheet2);
        assertNotNull(sheet2.page());

        List<TaskRD> tasks = sheet2.content();
        assertNotNull(tasks);
        assertFalse(tasks.isEmpty());

        TaskRD task = tasks.getFirst();
        assertNotNull(task);
        Long taskId = task.id();
        assertNotNull(taskId);

        /*
         * Переносим задачу на тот же проект.
         * */

        ResponseEntity<TaskRD> response3 = rest.exchange(
                taskUrl + "/{id}/project/{projectId}",
                HttpMethod.PATCH,
                new HttpEntity<>(headers),
                TaskRD.class,
                taskId,
                projectId
        );
        assertNotNull(response3);
        assertNotNull(response3.getStatusCode());
        assertEquals(HttpStatus.OK, response3.getStatusCode());

        TaskRD task2 = response3.getBody();
        assertNotNull(task2);
        assertEquals(task, task2);

        /*
         * Проверяем, что в первом проекте все так же есть эта задача.
         * */

        ResponseEntity<SheetDto<TaskRD>> response4 = rest.exchange(
                projectUrl + "/{id}/tasks",
                HttpMethod.GET,
                new HttpEntity<>(headers),
                new ParameterizedTypeReference<>() {
                },
                projectId
        );
        assertNotNull(response4);
        assertNotNull(response4.getStatusCode());
        assertEquals(HttpStatus.OK, response4.getStatusCode());

        SheetDto<TaskRD> sheet3 = response4.getBody();
        assertNotNull(sheet3);
        assertNotNull(sheet3.page());

        List<TaskRD> tasks3 = sheet3.content();
        assertNotNull(tasks3);

        boolean flag = false;
        for (TaskRD taskRD : tasks3) {
            assertNotNull(taskRD);
            assertNotNull(taskRD.id());

            if (taskRD.id().equals(taskId)) {
                flag = true;
            }
        }
        assertTrue(flag);
    }

    @Test
    @Order(25)
    @DirtiesContext
    @DisplayName("Тестируем PATCH /api/tasks/{id}/project/{projectId} с нарушением уникальности полей : ошибка 409 конфликт")
    public void test25() {

        /*
         * Получаем проекты пользователя алекс.
         * */

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(alexToken);

        ResponseEntity<SheetDto<ProjectRD>> response = rest.exchange(
                projectUrl,
                HttpMethod.GET,
                new HttpEntity<>(headers),
                new ParameterizedTypeReference<>() {
                }
        );
        assertNotNull(response);
        assertNotNull(response.getStatusCode());
        assertEquals(HttpStatus.OK, response.getStatusCode());

        SheetDto<ProjectRD> sheet = response.getBody();
        assertNotNull(sheet);
        assertNotNull(sheet.page());

        List<ProjectRD> projects = sheet.content();
        assertNotNull(projects);
        assertFalse(projects.isEmpty());
        assertTrue(projects.size() >= 2);

        ProjectRD project = projects.getFirst();
        assertNotNull(project);
        Long projectId = project.id();
        assertNotNull(projectId);

        ProjectRD project2 = projects.getLast();
        assertNotNull(project2);
        Long project2Id = project2.id();
        assertNotNull(project2Id);

        /*
         * Получаем первую задачу из первого проекта.
         * */

        ResponseEntity<SheetDto<TaskRD>> response2 = rest.exchange(
                projectUrl + "/{id}/tasks",
                HttpMethod.GET,
                new HttpEntity<>(headers),
                new ParameterizedTypeReference<>() {
                },
                projectId
        );
        assertNotNull(response2);
        assertNotNull(response2.getStatusCode());
        assertEquals(HttpStatus.OK, response2.getStatusCode());

        SheetDto<TaskRD> sheet2 = response2.getBody();
        assertNotNull(sheet2);
        assertNotNull(sheet2.page());

        List<TaskRD> tasks = sheet2.content();
        assertNotNull(tasks);
        assertFalse(tasks.isEmpty());

        TaskRD task = tasks.getFirst();
        assertNotNull(task);
        Long taskId = task.id();
        assertNotNull(taskId);

        /*
         * Получаем первую задачу из второго проекта.
         * */

        ResponseEntity<SheetDto<TaskRD>> response3 = rest.exchange(
                projectUrl + "/{id}/tasks",
                HttpMethod.GET,
                new HttpEntity<>(headers),
                new ParameterizedTypeReference<>() {
                },
                project2Id
        );
        assertNotNull(response3);
        assertNotNull(response3.getStatusCode());
        assertEquals(HttpStatus.OK, response3.getStatusCode());

        SheetDto<TaskRD> sheet3 = response3.getBody();
        assertNotNull(sheet3);
        assertNotNull(sheet3.page());

        List<TaskRD> tasks2 = sheet3.content();
        assertNotNull(tasks2);
        assertFalse(tasks2.isEmpty());

        TaskRD task2 = tasks2.getFirst();
        assertNotNull(task2);
        String task2Title = task2.title();
        assertNotNull(task2Title);

        /*
         * Меняем название первой задачи из первого
         * проекта на название первой задачи из
         * второго проекта.
         * */

        TaskWD ptcTask = new TaskWD(task2Title);

        ResponseEntity<TaskRD> response4 = rest.exchange(
                taskUrl + "/{id}",
                HttpMethod.PATCH,
                new HttpEntity<>(ptcTask, headers),
                TaskRD.class,
                taskId
        );
        assertNotNull(response4);
        assertNotNull(response4.getStatusCode());
        assertEquals(HttpStatus.OK, response4.getStatusCode());

        /*
         * Пытаемся перенести задачу с проекта на проект.
         * Нарушаем уникальность колонок в бд.
         * */

        try {
            rest.exchange(
                    taskUrl + "/{id}/project/{projectId}",
                    HttpMethod.PATCH,
                    new HttpEntity<>(headers),
                    TaskRD.class,
                    taskId,
                    project2Id
            );
            fail("Should throw HttpClientErrorException.Conflict");

        } catch (HttpClientErrorException.Conflict e) {
            assertNotNull(e);
            assertNotNull(e.getStatusCode());
            assertEquals(HttpStatus.CONFLICT, e.getStatusCode());

            ErrorResponse errorResponse = e.getResponseBodyAs(ErrorResponse.class);
            assertNotNull(errorResponse);
            assertNotNull(errorResponse.path());
            assertEquals(taskUrl + "/" + taskId + "/project/" + project2Id, errorResponse.path());
            assertNotNull(errorResponse.httpMethod());
            assertEquals(HttpMethod.PATCH, errorResponse.httpMethod());
            assertNotNull(errorResponse.httpStatus());
            assertEquals(HttpStatus.CONFLICT, errorResponse.httpStatus());
            assertNotNull(errorResponse.message());
            assertNotNull(errorResponse.exception());
            assertNotNull(errorResponse.timestamp());

        } catch (Exception e) {
            fail("Should throw HttpClientErrorException.Conflict, but got " + e.getMessage());
        }
    }

    @Test
    @Order(26)
    @DirtiesContext
    @DisplayName("Тестируем DELETE /api/tasks/{id} : успех")
    public void test26() {

        /*
         * Получаем проекты пользователя алекс.
         * */

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(alexToken);

        ResponseEntity<SheetDto<ProjectRD>> response = rest.exchange(
                projectUrl,
                HttpMethod.GET,
                new HttpEntity<>(headers),
                new ParameterizedTypeReference<>() {
                }
        );
        assertNotNull(response);
        assertNotNull(response.getStatusCode());
        assertEquals(HttpStatus.OK, response.getStatusCode());

        SheetDto<ProjectRD> sheet = response.getBody();
        assertNotNull(sheet);
        assertNotNull(sheet.page());

        List<ProjectRD> projects = sheet.content();
        assertNotNull(projects);
        assertFalse(projects.isEmpty());
        assertTrue(projects.size() >= 2);

        ProjectRD project = projects.getFirst();
        assertNotNull(project);
        Long projectId = project.id();
        assertNotNull(projectId);

        ProjectRD project2 = projects.getLast();
        assertNotNull(project2);
        Long project2Id = project2.id();
        assertNotNull(project2Id);

        /*
         * Получаем первую задачу из первого проекта.
         * */

        ResponseEntity<SheetDto<TaskRD>> response2 = rest.exchange(
                projectUrl + "/{id}/tasks",
                HttpMethod.GET,
                new HttpEntity<>(headers),
                new ParameterizedTypeReference<>() {
                },
                projectId
        );
        assertNotNull(response2);
        assertNotNull(response2.getStatusCode());
        assertEquals(HttpStatus.OK, response2.getStatusCode());

        SheetDto<TaskRD> sheet2 = response2.getBody();
        assertNotNull(sheet2);
        assertNotNull(sheet2.page());

        List<TaskRD> tasks = sheet2.content();
        assertNotNull(tasks);
        assertFalse(tasks.isEmpty());

        TaskRD task = tasks.getFirst();
        assertNotNull(task);
        Long taskId = task.id();
        assertNotNull(taskId);

        /*
         * Удаляем задачу по идентификатору.
         * */

        ResponseEntity<Void> response3 = rest.exchange(
                taskUrl + "/{id}",
                HttpMethod.DELETE,
                new HttpEntity<>(headers),
                Void.class,
                taskId
        );
        assertNotNull(response3);
        assertNotNull(response3.getStatusCode());
        assertEquals(HttpStatus.NO_CONTENT, response3.getStatusCode());

        /*
         * Проверяем, что задача была удалена.
         * */

        try {
            rest.exchange(
                    taskUrl + "/{id}",
                    HttpMethod.GET,
                    new HttpEntity<>(headers),
                    TaskRD.class,
                    taskId
            );
            fail("Should throw HttpClientErrorException.NotFound");

        } catch (HttpClientErrorException.NotFound e) {
            assertNotNull(e);
            assertNotNull(e.getStatusCode());
            assertEquals(HttpStatus.NOT_FOUND, e.getStatusCode());

            ErrorResponse errorResponse = e.getResponseBodyAs(ErrorResponse.class);
            assertNotNull(errorResponse);
            assertNotNull(errorResponse.path());
            assertEquals(taskUrl + "/" + taskId, errorResponse.path());
            assertNotNull(errorResponse.httpMethod());
            assertEquals(HttpMethod.GET, errorResponse.httpMethod());
            assertNotNull(errorResponse.httpStatus());
            assertEquals(HttpStatus.NOT_FOUND, errorResponse.httpStatus());
            assertNotNull(errorResponse.message());
            assertNotNull(errorResponse.exception());
            assertNotNull(errorResponse.timestamp());

        } catch (Exception e) {
            fail("Should throw HttpClientErrorException.NotFound, but got " + e.getMessage());
        }
    }

    @Test
    @Order(27)
    @DirtiesContext
    @DisplayName("Тестируем DELETE /api/tasks/{id} с некорректным id : ошибка 404 не найдено")
    public void test27() {

        /*
         * Пытаемся удалить задачу по некорректному идентификатору.
         * */

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(alexToken);

        Long taskId = Long.MAX_VALUE;

        try {
            rest.exchange(
                    taskUrl + "/{id}",
                    HttpMethod.DELETE,
                    new HttpEntity<>(headers),
                    Void.class,
                    taskId
            );
            fail("Should throw HttpClientErrorException.NotFound");

        } catch (HttpClientErrorException.NotFound e) {
            assertNotNull(e);
            assertNotNull(e.getStatusCode());
            assertEquals(HttpStatus.NOT_FOUND, e.getStatusCode());

            ErrorResponse errorResponse = e.getResponseBodyAs(ErrorResponse.class);
            assertNotNull(errorResponse);
            assertNotNull(errorResponse.path());
            assertEquals(taskUrl + "/" + taskId, errorResponse.path());
            assertNotNull(errorResponse.httpMethod());
            assertEquals(HttpMethod.DELETE, errorResponse.httpMethod());
            assertNotNull(errorResponse.httpStatus());
            assertEquals(HttpStatus.NOT_FOUND, errorResponse.httpStatus());
            assertNotNull(errorResponse.message());
            assertNotNull(errorResponse.exception());
            assertNotNull(errorResponse.timestamp());

        } catch (Exception e) {
            fail("Should throw HttpClientErrorException.NotFound, but got " + e.getMessage());
        }
    }

    @Test
    @Order(28)
    @DirtiesContext
    @DisplayName("Тестируем DELETE /api/tasks/{id} с чужим id : ошибка 403 запрещено")
    public void test28() {

        /*
         * Получаем проекты пользователя алекс.
         * */

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(alexToken);

        ResponseEntity<SheetDto<ProjectRD>> response = rest.exchange(
                projectUrl,
                HttpMethod.GET,
                new HttpEntity<>(headers),
                new ParameterizedTypeReference<>() {
                }
        );
        assertNotNull(response);
        assertNotNull(response.getStatusCode());
        assertEquals(HttpStatus.OK, response.getStatusCode());

        SheetDto<ProjectRD> sheet = response.getBody();
        assertNotNull(sheet);
        assertNotNull(sheet.page());

        List<ProjectRD> projects = sheet.content();
        assertNotNull(projects);
        assertFalse(projects.isEmpty());
        assertTrue(projects.size() >= 2);

        ProjectRD project = projects.getFirst();
        assertNotNull(project);
        Long projectId = project.id();
        assertNotNull(projectId);

        ProjectRD project2 = projects.getLast();
        assertNotNull(project2);
        Long project2Id = project2.id();
        assertNotNull(project2Id);

        /*
         * Получаем первую задачу из первого проекта.
         * */

        ResponseEntity<SheetDto<TaskRD>> response2 = rest.exchange(
                projectUrl + "/{id}/tasks",
                HttpMethod.GET,
                new HttpEntity<>(headers),
                new ParameterizedTypeReference<>() {
                },
                projectId
        );
        assertNotNull(response2);
        assertNotNull(response2.getStatusCode());
        assertEquals(HttpStatus.OK, response2.getStatusCode());

        SheetDto<TaskRD> sheet2 = response2.getBody();
        assertNotNull(sheet2);
        assertNotNull(sheet2.page());

        List<TaskRD> tasks = sheet2.content();
        assertNotNull(tasks);
        assertFalse(tasks.isEmpty());

        TaskRD task = tasks.getFirst();
        assertNotNull(task);
        Long taskId = task.id();
        assertNotNull(taskId);

        /*
         * Пытаемся удалить задачу с токеном другого пользователя.
         * */

        HttpHeaders headers2 = new HttpHeaders();
        headers2.setBearerAuth(ekaterinaToken);

        try {
            rest.exchange(
                    taskUrl + "/{id}",
                    HttpMethod.DELETE,
                    new HttpEntity<>(headers2),
                    Void.class,
                    taskId
            );
            fail("Should throw HttpClientErrorException.Forbidden");

        } catch (HttpClientErrorException.Forbidden e) {
            assertNotNull(e);
            assertNotNull(e.getStatusCode());
            assertEquals(HttpStatus.FORBIDDEN, e.getStatusCode());

            ErrorResponse errorResponse = e.getResponseBodyAs(ErrorResponse.class);
            assertNotNull(errorResponse);
            assertNotNull(errorResponse.path());
            assertEquals(taskUrl + "/" + taskId, errorResponse.path());
            assertNotNull(errorResponse.httpMethod());
            assertEquals(HttpMethod.DELETE, errorResponse.httpMethod());
            assertNotNull(errorResponse.httpStatus());
            assertEquals(HttpStatus.FORBIDDEN, errorResponse.httpStatus());
            assertNotNull(errorResponse.message());
            assertNotNull(errorResponse.exception());
            assertNotNull(errorResponse.timestamp());

        } catch (Exception e) {
            fail("Should throw HttpClientErrorException.Forbidden, but got " + e.getMessage());
        }
    }
}
