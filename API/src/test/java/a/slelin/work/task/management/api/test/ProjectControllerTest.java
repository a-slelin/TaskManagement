package a.slelin.work.task.management.api.test;

import a.slelin.work.task.management.core.dto.*;
import a.slelin.work.task.management.core.dto.api.ProjectRD;
import a.slelin.work.task.management.core.dto.api.ProjectWD;
import a.slelin.work.task.management.core.dto.api.TaskRD;
import a.slelin.work.task.management.core.dto.api.TaskWD;
import a.slelin.work.task.management.core.dto.auth.UserRD;
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

import java.net.URI;
import java.util.List;

import static org.assertj.core.api.Fail.fail;
import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test")
@DisplayName("Тестируем ProjectController")
@SuppressWarnings("CatchMayIgnoreException")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class ProjectControllerTest {

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

    @BeforeEach
    void beforeEach() {
        projectUrl = "http://localhost:%d/api/projects".formatted(port);
    }

    @Test
    @Order(1)
    @DirtiesContext
    @DisplayName("Тестируем GET /api/projects с неавторизованным пользователем : ошибка 401 неавторизован")
    public void test1() {

        try {
            rest.exchange(
                    projectUrl,
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
            assertNotNull(errorResponse.path());
            assertEquals(projectUrl, errorResponse.path());
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
    @DisplayName("Тестируем GET /api/projects с авторизованным пользователем : успех")
    public void test2() {

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(alexToken);

        ResponseEntity<SheetDto<UserRD>> response2 = rest.exchange(
                projectUrl,
                HttpMethod.GET,
                new HttpEntity<>(headers),
                new ParameterizedTypeReference<>() {
                }
        );
        assertNotNull(response2);
        assertNotNull(response2.getStatusCode());
        assertEquals(HttpStatus.OK, response2.getStatusCode());
    }

    @Test
    @Order(3)
    @DirtiesContext
    @DisplayName("Тестируем GET /api/projects с администратором : успех")
    public void test3() {

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(adminToken);

        ResponseEntity<SheetDto<UserRD>> response2 = rest.exchange(
                projectUrl,
                HttpMethod.GET,
                new HttpEntity<>(headers),
                new ParameterizedTypeReference<>() {
                }
        );
        assertNotNull(response2);
        assertNotNull(response2.getStatusCode());
        assertEquals(HttpStatus.OK, response2.getStatusCode());
    }

    @Test
    @Order(4)
    @DirtiesContext
    @DisplayName("Тестируем GET /api/projects : успех")
    public void test4() {

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
        assertNotNull(sheet.content());

        List<ProjectRD> projects = sheet.content();
        assertNotNull(projects);
        assertFalse(projects.isEmpty());

        projects.forEach(project -> {
            assertNotNull(project);
            assertNotNull(project.id());
            assertNotNull(project.name());
        });
    }

    @Test
    @Order(5)
    @DirtiesContext
    @DisplayName("Тестируем GET /api/projects/{id} : успех")
    public void test5() {

        /*
         * Получаем все проекты пользователя алекс.
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
        assertNotNull(sheet.content());

        List<ProjectRD> projects = sheet.content();
        assertNotNull(projects);
        assertFalse(projects.isEmpty());

        ProjectRD project = projects.getFirst();
        assertNotNull(project);
        Long projectId = project.id();
        assertNotNull(projectId);

        /*
         * Получаем проект по идентификатору.
         * */

        ResponseEntity<ProjectRD> response2 = rest.exchange(
                projectUrl + "/{id}",
                HttpMethod.GET,
                new HttpEntity<>(headers),
                ProjectRD.class,
                projectId
        );
        assertNotNull(response2);
        assertNotNull(response2.getStatusCode());
        assertEquals(HttpStatus.OK, response2.getStatusCode());

        ProjectRD project2 = response2.getBody();
        assertNotNull(project2);
        assertEquals(project, project2);
    }

    @Test
    @Order(6)
    @DirtiesContext
    @DisplayName("Тестируем GET /api/projects/{id} с некорректным id : ошибка 404 не найдено")
    public void test6() {

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(alexToken);

        Long id = Long.MAX_VALUE;

        try {
            rest.exchange(
                    projectUrl + "/{id}",
                    HttpMethod.GET,
                    new HttpEntity<>(headers),
                    ProjectRD.class,
                    id
            );
            fail("Should throw HttpClientErrorException.NotFound");

        } catch (HttpClientErrorException.NotFound e) {
            assertNotNull(e);
            assertNotNull(e.getStatusCode());
            assertEquals(HttpStatus.NOT_FOUND, e.getStatusCode());

            ErrorResponse errorResponse = e.getResponseBodyAs(ErrorResponse.class);
            assertNotNull(errorResponse);
            assertNotNull(errorResponse.path());
            assertEquals(projectUrl + "/" + id, errorResponse.path());
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
    @Order(7)
    @DirtiesContext
    @DisplayName("Тестируем GET /api/projects/{id} с чужим id : ошибка 403 запрещено")
    public void test7() {

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
        assertNotNull(sheet.content());

        List<ProjectRD> projects = sheet.content();
        assertNotNull(projects);
        assertFalse(projects.isEmpty());

        ProjectRD project = projects.getFirst();
        assertNotNull(project);
        Long projectId = project.id();
        assertNotNull(projectId);

        /*
         * Пытаемся получить чужой проект.
         * */

        HttpHeaders headers2 = new HttpHeaders();
        headers2.setBearerAuth(ekaterinaToken);

        try {
            rest.exchange(
                    projectUrl + "/{id}",
                    HttpMethod.GET,
                    new HttpEntity<>(headers2),
                    ProjectRD.class,
                    projectId
            );
            fail("Should throw HttpClientErrorException.Forbidden");

        } catch (HttpClientErrorException.Forbidden e) {
            assertNotNull(e);
            assertNotNull(e.getStatusCode());
            assertEquals(HttpStatus.FORBIDDEN, e.getStatusCode());

            ErrorResponse errorResponse = e.getResponseBodyAs(ErrorResponse.class);
            assertNotNull(errorResponse);
            assertNotNull(errorResponse.path());
            assertEquals(projectUrl + "/" + projectId, errorResponse.path());
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
    @Order(8)
    @DirtiesContext
    @DisplayName("Тестируем GET /api/projects/{id}/tasks : успех")
    public void test8() {

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
        assertNotNull(sheet.content());

        List<ProjectRD> projects = sheet.content();
        assertNotNull(projects);
        assertFalse(projects.isEmpty());

        ProjectRD project = projects.getFirst();
        assertNotNull(project);
        Long projectId = project.id();
        assertNotNull(projectId);

        /*
         * Получаем все задачи проекта.
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
        assertNotNull(sheet2.content());

        List<TaskRD> tasks = sheet2.content();
        assertNotNull(tasks);
        assertFalse(tasks.isEmpty());

        tasks.forEach(task -> {
            assertNotNull(task);
            assertNotNull(task.id());
            assertNotNull(task.title());
        });
    }

    @Test
    @Order(9)
    @DirtiesContext
    @DisplayName("Тестируем GET /api/projects/{id}/tasks с некорректным id : ошибка 404 не найдено")
    public void test9() {

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(alexToken);

        Long id = Long.MAX_VALUE;

        try {
            rest.exchange(
                    projectUrl + "/{id}/tasks",
                    HttpMethod.GET,
                    new HttpEntity<>(headers),
                    new ParameterizedTypeReference<>() {
                    },
                    id
            );
            fail("Should throw HttpClientErrorException.NotFound");

        } catch (HttpClientErrorException.NotFound e) {
            assertNotNull(e);
            assertNotNull(e.getStatusCode());
            assertEquals(HttpStatus.NOT_FOUND, e.getStatusCode());

            ErrorResponse errorResponse = e.getResponseBodyAs(ErrorResponse.class);
            assertNotNull(errorResponse);
            assertNotNull(errorResponse.path());
            assertEquals(projectUrl + "/" + id + "/tasks", errorResponse.path());
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
    @Order(10)
    @DirtiesContext
    @DisplayName("Тестируем GET /api/projects/{id}/tasks с чужим id : ошибка 403 запрещено")
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
        assertNotNull(sheet.content());

        List<ProjectRD> projects = sheet.content();
        assertNotNull(projects);
        assertFalse(projects.isEmpty());

        ProjectRD project = projects.getFirst();
        assertNotNull(project);
        Long projectId = project.id();
        assertNotNull(projectId);

        /*
         * Пытаемся получить задачи чужого проекта.
         * */

        HttpHeaders headers2 = new HttpHeaders();
        headers2.setBearerAuth(ekaterinaToken);

        try {
            rest.exchange(
                    projectUrl + "/{id}/tasks",
                    HttpMethod.GET,
                    new HttpEntity<>(headers2),
                    ProjectRD.class,
                    projectId
            );
            fail("Should throw HttpClientErrorException.Forbidden");

        } catch (HttpClientErrorException.Forbidden e) {
            assertNotNull(e);
            assertNotNull(e.getStatusCode());
            assertEquals(HttpStatus.FORBIDDEN, e.getStatusCode());

            ErrorResponse errorResponse = e.getResponseBodyAs(ErrorResponse.class);
            assertNotNull(errorResponse);
            assertNotNull(errorResponse.path());
            assertEquals(projectUrl + "/" + projectId + "/tasks", errorResponse.path());
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
    @Order(11)
    @DirtiesContext
    @DisplayName("Тестируем POST /api/projects/search : успех")
    public void test11() {

        FilterChain filters = FilterChain
                .empty()
                .add(Filter.of("description", Operation.IS_NOT_NULL));

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(alexToken);

        ResponseEntity<SheetDto<ProjectRD>> response = rest.exchange(
                projectUrl + "/search",
                HttpMethod.POST,
                new HttpEntity<>(filters, headers),
                new ParameterizedTypeReference<>() {
                }
        );
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

        projects.forEach(project -> {
            assertNotNull(project);
            assertNotNull(project.id());
            assertNotNull(project.name());
            assertNotNull(project.description());
        });
    }

    @Test
    @Order(12)
    @DirtiesContext
    @DisplayName("Тестируем POST /api/projects/search с некорректным фильтром : ошибка 400 плохой запрос")
    public void test12() {

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(alexToken);

        FilterChain filters = FilterChain
                .empty()
                .add(Filter.of("bad", Operation.IS_NOT_EMPTY));

        try {
            rest.exchange(
                    projectUrl + "/search",
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
            assertEquals(projectUrl + "/search", errorResponse.path());
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
    @Order(13)
    @DisplayName("Тестируем POST /api/projects : успех")
    public void test13() {

        /*
         * Создаем новый проект у пользователя алекс.
         * */

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(alexToken);

        ProjectWD newProject = new ProjectWD("tmp_name", "tmp_description");

        ResponseEntity<ProjectRD> response = rest.exchange(
                projectUrl,
                HttpMethod.POST,
                new HttpEntity<>(newProject, headers),
                new ParameterizedTypeReference<>() {
                }
        );
        assertNotNull(response);
        assertNotNull(response.getStatusCode());
        assertEquals(HttpStatus.CREATED, response.getStatusCode());

        ProjectRD project = response.getBody();
        assertNotNull(project);
        assertNotNull(project.id());
        assertNotNull(project.name());
        assertEquals(newProject.name(), project.name());
        assertNotNull(project.description());
        assertEquals(newProject.description(), project.description());

        /*
         * Проверяем, что url с местоположением корректный.
         * */

        HttpHeaders headers2 = response.getHeaders();
        assertNotNull(headers2);
        List<String> locations = headers2.get("Location");
        assertNotNull(locations);
        String locationStr = locations.getFirst();
        assertNotNull(locationStr);
        URI location = URI.create(locationStr);

        assertTrue(locationStr.contains(project.id().toString()));

        ResponseEntity<ProjectRD> response2 = rest.exchange(
                location,
                HttpMethod.GET,
                new HttpEntity<>(headers),
                ProjectRD.class
        );
        assertNotNull(response2);
        assertNotNull(response2.getStatusCode());
        assertEquals(HttpStatus.OK, response2.getStatusCode());

        ProjectRD project2 = response2.getBody();
        assertNotNull(project2);
        assertEquals(project, project2);
    }

    @Test
    @Order(14)
    @DirtiesContext
    @DisplayName("Тестируем POST /api/projects с нарушением уникальных полей : ошибка 409 конфликт")
    public void test14() {

        /*
         * Получаем название первого проекта
         * пользователя алекс.
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
        assertNotNull(sheet.content());

        List<ProjectRD> projects = sheet.content();
        assertNotNull(projects);
        assertFalse(projects.isEmpty());

        ProjectRD project = projects.getFirst();
        assertNotNull(project);
        String projectName = project.name();
        assertNotNull(projectName);

        /*
         * Пытаемся создать проект с тем же именем.
         * */

        ProjectWD newProject = new ProjectWD(projectName, "tmp_description");

        try {
            rest.exchange(
                    projectUrl,
                    HttpMethod.POST,
                    new HttpEntity<>(newProject, headers),
                    ProjectRD.class
            );
            fail("Should throw HttpClientErrorException.Conflict");

        } catch (HttpClientErrorException.Conflict e) {
            assertNotNull(e);
            assertNotNull(e.getStatusCode());
            assertEquals(HttpStatus.CONFLICT, e.getStatusCode());

            ErrorResponse errorResponse = e.getResponseBodyAs(ErrorResponse.class);
            assertNotNull(errorResponse);
            assertNotNull(errorResponse.path());
            assertEquals(projectUrl, errorResponse.path());
            assertNotNull(errorResponse.httpMethod());
            assertEquals(HttpMethod.POST, errorResponse.httpMethod());
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
    @Order(15)
    @DirtiesContext
    @DisplayName("Тестируем POST /api/projects/{id}/tasks : успех")
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
        assertNotNull(sheet.content());

        List<ProjectRD> projects = sheet.content();
        assertNotNull(projects);
        assertFalse(projects.isEmpty());

        ProjectRD project = projects.getFirst();
        assertNotNull(project);
        Long projectId = project.id();
        assertNotNull(projectId);

        /*
         * Создаём новую задачу в этом проекте.
         * */

        TaskWD newTask = new TaskWD("tmp_title", "in_progress", "tmp_description");

        ResponseEntity<TaskRD> response2 = rest.exchange(
                projectUrl + "/{id}/tasks",
                HttpMethod.POST,
                new HttpEntity<>(newTask, headers),
                TaskRD.class,
                projectId
        );
        assertNotNull(response2);
        assertNotNull(response2.getStatusCode());
        assertEquals(HttpStatus.CREATED, response2.getStatusCode());

        TaskRD task = response2.getBody();
        assertNotNull(task);
        assertNotNull(task.id());
        assertNotNull(task.title());
        assertEquals(newTask.title(), task.title());
        assertNotNull(task.status());
        assertEquals(newTask.status(), task.status());
        assertNotNull(task.description());
        assertEquals(newTask.description(), task.description());

        /*
         * Проверяем, что url с местоположением корректен.
         * */

        HttpHeaders headers2 = response2.getHeaders();
        assertNotNull(headers2);
        List<String> locations = headers2.get("Location");
        assertNotNull(locations);
        String locationStr = locations.getFirst();
        assertNotNull(locationStr);
        URI location = URI.create(locationStr);

        assertTrue(locationStr.contains(task.id().toString()));

        ResponseEntity<TaskRD> response3 = rest.exchange(
                location,
                HttpMethod.GET,
                new HttpEntity<>(headers),
                TaskRD.class
        );
        assertNotNull(response3);
        assertNotNull(response3.getStatusCode());
        assertEquals(HttpStatus.OK, response3.getStatusCode());

        TaskRD task2 = response3.getBody();
        assertNotNull(task2);
        assertEquals(task, task2);
    }

    @Test
    @Order(16)
    @DirtiesContext
    @DisplayName("Тестируем POST /api/projects/{id}/tasks с чужим id : ошибка 403 запрещено")
    public void test16() {

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
        assertNotNull(sheet.content());

        List<ProjectRD> projects = sheet.content();
        assertNotNull(projects);
        assertFalse(projects.isEmpty());

        ProjectRD project = projects.getFirst();
        assertNotNull(project);
        Long projectId = project.id();
        assertNotNull(projectId);

        /*
         * Пытаемся создать чужую задачу в этом проекте.
         * */

        HttpHeaders headers2 = new HttpHeaders();
        headers2.setBearerAuth(ekaterinaToken);

        TaskWD newTask = new TaskWD("tmp_title", "in_progress", "tmp_description");

        try {
            rest.exchange(
                    projectUrl + "/{id}/tasks",
                    HttpMethod.POST,
                    new HttpEntity<>(newTask, headers2),
                    TaskRD.class,
                    projectId
            );
            fail("Should throw HttpClientErrorException.Forbidden");

        } catch (HttpClientErrorException.Forbidden e) {
            assertNotNull(e);
            assertNotNull(e.getStatusCode());
            assertEquals(HttpStatus.FORBIDDEN, e.getStatusCode());

            ErrorResponse errorResponse = e.getResponseBodyAs(ErrorResponse.class);
            assertNotNull(errorResponse);
            assertNotNull(errorResponse.path());
            assertEquals(projectUrl + "/" + projectId + "/tasks", errorResponse.path());
            assertNotNull(errorResponse.httpMethod());
            assertEquals(HttpMethod.POST, errorResponse.httpMethod());
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
    @Order(17)
    @DirtiesContext
    @DisplayName("Тестируем POST /api/projects/{id}/tasks с некорректным id : ошибка 404 не найдено")
    public void test17() {

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(alexToken);

        Long projectId = Long.MAX_VALUE;

        TaskWD newTask = new TaskWD("tmp_title", "in_progress", "tmp_description");

        try {
            rest.exchange(
                    projectUrl + "/{id}/tasks",
                    HttpMethod.POST,
                    new HttpEntity<>(newTask, headers),
                    TaskRD.class,
                    projectId
            );
            fail("Should throw HttpClientErrorException.NotFound");

        } catch (HttpClientErrorException.NotFound e) {
            assertNotNull(e);
            assertNotNull(e.getStatusCode());
            assertEquals(HttpStatus.NOT_FOUND, e.getStatusCode());

            ErrorResponse errorResponse = e.getResponseBodyAs(ErrorResponse.class);
            assertNotNull(errorResponse);
            assertNotNull(errorResponse.path());
            assertEquals(projectUrl + "/" + projectId + "/tasks", errorResponse.path());
            assertNotNull(errorResponse.httpMethod());
            assertEquals(HttpMethod.POST, errorResponse.httpMethod());
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
    @Order(18)
    @DirtiesContext
    @DisplayName("Тестируем POST /api/projects/{id}/tasks с некорректной задачей (уникальный поля) : ошибка 409 конфликт")
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
        assertNotNull(sheet.content());

        List<ProjectRD> projects = sheet.content();
        assertNotNull(projects);
        assertFalse(projects.isEmpty());

        ProjectRD project = projects.getFirst();
        assertNotNull(project);
        Long projectId = project.id();
        assertNotNull(projectId);

        /*
         * Получаем заголовок первой задачи из этого проекта.
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
        String taskTitle = task.title();
        assertNotNull(taskTitle);

        /*
         * Пытаемся создать задачу с уже существующим
         * заголовком в этом проекте.
         * */

        TaskWD newTask = new TaskWD(taskTitle, "in_progress", "tmp_description");

        try {
            rest.exchange(
                    projectUrl + "/{id}/tasks",
                    HttpMethod.POST,
                    new HttpEntity<>(newTask, headers),
                    TaskRD.class,
                    projectId
            );
            fail("Should throw HttpClientErrorException.Conflict");

        } catch (HttpClientErrorException.Conflict e) {
            assertNotNull(e);
            assertNotNull(e.getStatusCode());
            assertEquals(HttpStatus.CONFLICT, e.getStatusCode());

            ErrorResponse errorResponse = e.getResponseBodyAs(ErrorResponse.class);
            assertNotNull(errorResponse);
            assertNotNull(errorResponse.path());
            assertEquals(projectUrl + "/" + projectId + "/tasks", errorResponse.path());
            assertNotNull(errorResponse.httpMethod());
            assertEquals(HttpMethod.POST, errorResponse.httpMethod());
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
    @DisplayName("Тестируем PUT /api/projects/{id} : успех")
    public void test19() {

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
        assertNotNull(sheet.content());

        List<ProjectRD> projects = sheet.content();
        assertNotNull(projects);
        assertFalse(projects.isEmpty());

        ProjectRD project = projects.getFirst();
        assertNotNull(project);
        Long projectId = project.id();
        assertNotNull(projectId);

        /*
         * Обновляем этот проект.
         * */

        ProjectWD updProject = new ProjectWD("updatedName", "updatedDescription");

        ResponseEntity<ProjectRD> response2 = rest.exchange(
                projectUrl + "/{id}",
                HttpMethod.PUT,
                new HttpEntity<>(updProject, headers),
                ProjectRD.class,
                projectId
        );
        assertNotNull(response2);
        assertNotNull(response2.getStatusCode());
        assertEquals(HttpStatus.OK, response2.getStatusCode());

        ProjectRD project2 = response2.getBody();
        assertNotNull(project2);
        assertNotNull(project2.id());
        assertEquals(projectId, project2.id());
        assertNotNull(project2.name());
        assertEquals(updProject.name(), project2.name());
        assertNotNull(project2.description());
        assertEquals(updProject.description(), project2.description());
    }

    @Test
    @Order(20)
    @DirtiesContext
    @DisplayName("Тестируем PUT /api/projects/{id} с чужим id : ошибка 403 запрещено")
    public void test20() {

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
        assertNotNull(sheet.content());

        List<ProjectRD> projects = sheet.content();
        assertNotNull(projects);
        assertFalse(projects.isEmpty());

        ProjectRD project = projects.getFirst();
        assertNotNull(project);
        Long projectId = project.id();
        assertNotNull(projectId);

        /*
         * Пытаемся обновить чужой проект.
         * */

        HttpHeaders headers2 = new HttpHeaders();
        headers2.setBearerAuth(ekaterinaToken);

        ProjectWD updProject = new ProjectWD("updatedName", "updatedDescription");

        try {
            rest.exchange(
                    projectUrl + "/{id}",
                    HttpMethod.PUT,
                    new HttpEntity<>(updProject, headers2),
                    ProjectRD.class,
                    projectId
            );
            fail("Should throw HttpClientErrorException.Forbidden");

        } catch (HttpClientErrorException.Forbidden e) {
            assertNotNull(e);
            assertNotNull(e.getStatusCode());
            assertEquals(HttpStatus.FORBIDDEN, e.getStatusCode());

            ErrorResponse errorResponse = e.getResponseBodyAs(ErrorResponse.class);
            assertNotNull(errorResponse);
            assertNotNull(errorResponse.path());
            assertEquals(projectUrl + "/" + projectId, errorResponse.path());
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
    @Order(21)
    @DirtiesContext
    @DisplayName("Тестируем PUT /api/projects/{id} с некорректным id : ошибка 404 не найдено")
    public void test21() {

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(alexToken);

        Long projectId = Long.MAX_VALUE;

        ProjectWD updProject = new ProjectWD("updatedName", "updatedDescription");

        try {
            rest.exchange(
                    projectUrl + "/{id}",
                    HttpMethod.PUT,
                    new HttpEntity<>(updProject, headers),
                    ProjectRD.class,
                    projectId
            );
            fail("Should throw HttpClientErrorException.NotFound");

        } catch (HttpClientErrorException.NotFound e) {
            assertNotNull(e);
            assertNotNull(e.getStatusCode());
            assertEquals(HttpStatus.NOT_FOUND, e.getStatusCode());

            ErrorResponse errorResponse = e.getResponseBodyAs(ErrorResponse.class);
            assertNotNull(errorResponse);
            assertNotNull(errorResponse.path());
            assertEquals(projectUrl + "/" + projectId, errorResponse.path());
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
    @Order(22)
    @DirtiesContext
    @DisplayName("Тестируем PUT /api/projects/{id} с некорректным проектом (уникальные поля) : ошибка 409 конфликт")
    public void test22() {

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
        assertNotNull(sheet.content());

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
        String project2Name = project2.name();
        assertNotNull(project2Name);

        /*
         * Пытаемся обновить проект уже существующим
         * названием проекта у алекса.
         * */

        ProjectWD updProject = new ProjectWD(project2Name, "updatedDescription");

        try {
            rest.exchange(
                    projectUrl + "/{id}",
                    HttpMethod.PUT,
                    new HttpEntity<>(updProject, headers),
                    ProjectRD.class,
                    projectId
            );
            fail("Should throw HttpClientErrorException.Conflict");

        } catch (HttpClientErrorException.Conflict e) {
            assertNotNull(e);
            assertNotNull(e.getStatusCode());
            assertEquals(HttpStatus.CONFLICT, e.getStatusCode());

            ErrorResponse errorResponse = e.getResponseBodyAs(ErrorResponse.class);
            assertNotNull(errorResponse);
            assertNotNull(errorResponse.path());
            assertEquals(projectUrl + "/" + projectId, errorResponse.path());
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
    @Order(23)
    @DirtiesContext
    @DisplayName("Тестируем PATCH /api/projects/{id} : успех")
    public void test23() {

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
        assertNotNull(sheet.content());

        List<ProjectRD> projects = sheet.content();
        assertNotNull(projects);
        assertFalse(projects.isEmpty());

        ProjectRD project = projects.getFirst();
        assertNotNull(project);
        Long projectId = project.id();
        assertNotNull(projectId);

        /*
         * Обновляем проект алекса.
         * */

        ProjectWD updProject = new ProjectWD("updatedName");

        ResponseEntity<ProjectRD> response2 = rest.exchange(
                projectUrl + "/{id}",
                HttpMethod.PATCH,
                new HttpEntity<>(updProject, headers),
                ProjectRD.class,
                projectId
        );
        assertNotNull(response2);
        assertNotNull(response2.getStatusCode());
        assertEquals(HttpStatus.OK, response2.getStatusCode());

        ProjectRD project2 = response2.getBody();
        assertNotNull(project2);
        assertNotNull(project2.id());
        assertEquals(projectId, project2.id());
        assertNotNull(project2.name());
        assertEquals(updProject.name(), project2.name());
        assertNotNull(project2.description());
        assertEquals(project.description(), project2.description());
    }

    @Test
    @Order(24)
    @DirtiesContext
    @DisplayName("Тестируем PATCH /api/projects/{id} с некорректным id : ошибка 404 не найдено")
    public void test24() {

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(alexToken);

        Long projectId = Long.MAX_VALUE;

        ProjectWD updProject = new ProjectWD("updatedName");

        try {
            rest.exchange(
                    projectUrl + "/{id}",
                    HttpMethod.PATCH,
                    new HttpEntity<>(updProject, headers),
                    ProjectRD.class,
                    projectId
            );
            fail("Should throw HttpClientErrorException.NotFound");

        } catch (HttpClientErrorException.NotFound e) {
            assertNotNull(e);
            assertNotNull(e.getStatusCode());
            assertEquals(HttpStatus.NOT_FOUND, e.getStatusCode());

            ErrorResponse errorResponse = e.getResponseBodyAs(ErrorResponse.class);
            assertNotNull(errorResponse);
            assertNotNull(errorResponse.path());
            assertEquals(projectUrl + "/" + projectId, errorResponse.path());
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
    @Order(25)
    @DirtiesContext
    @DisplayName("Тестируем PATCH /api/projects/{id} с чужим id : ошибка 403 запрещено")
    public void test25() {

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
        assertNotNull(sheet.content());

        List<ProjectRD> projects = sheet.content();
        assertNotNull(projects);
        assertFalse(projects.isEmpty());

        ProjectRD project = projects.getFirst();
        assertNotNull(project);
        Long projectId = project.id();
        assertNotNull(projectId);

        /*
         * Пытаемся обновить чужой проект.
         * */

        HttpHeaders headers2 = new HttpHeaders();
        headers2.setBearerAuth(ekaterinaToken);

        ProjectWD updProject = new ProjectWD("updatedName");

        try {
            rest.exchange(
                    projectUrl + "/{id}",
                    HttpMethod.PATCH,
                    new HttpEntity<>(updProject, headers2),
                    ProjectRD.class,
                    projectId
            );
            fail("Should throw HttpClientErrorException.Forbidden");

        } catch (HttpClientErrorException.Forbidden e) {
            assertNotNull(e);
            assertNotNull(e.getStatusCode());
            assertEquals(HttpStatus.FORBIDDEN, e.getStatusCode());

            ErrorResponse errorResponse = e.getResponseBodyAs(ErrorResponse.class);
            assertNotNull(errorResponse);
            assertNotNull(errorResponse.path());
            assertEquals(projectUrl + "/" + projectId, errorResponse.path());
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
    @Order(26)
    @DirtiesContext
    @DisplayName("Тестируем PATCH /api/projects/{id} с некорректным проектом (уникальные поля) : ошибка 409 конфликт")
    public void test26() {

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
        assertNotNull(sheet.content());

        List<ProjectRD> projects = sheet.content();
        assertNotNull(projects);
        assertFalse(projects.isEmpty());
        assertTrue(projects.size() >= 2);

        ProjectRD project = projects.getFirst();
        assertNotNull(project);
        Long projectId = project.id();
        assertNotNull(projectId);

        /*
         * Пытаемся обновить проект уже существующим
         * названием у алекса.
         * */

        ProjectRD project2 = projects.getLast();
        assertNotNull(project2);
        String project2Name = project2.name();
        assertNotNull(project2Name);

        ProjectWD updProject = new ProjectWD(project2Name);

        try {
            rest.exchange(
                    projectUrl + "/{id}",
                    HttpMethod.PATCH,
                    new HttpEntity<>(updProject, headers),
                    ProjectRD.class,
                    projectId
            );
            fail("Should throw HttpClientErrorException.Conflict");

        } catch (HttpClientErrorException.Conflict e) {
            assertNotNull(e);
            assertNotNull(e.getStatusCode());
            assertEquals(HttpStatus.CONFLICT, e.getStatusCode());

            ErrorResponse errorResponse = e.getResponseBodyAs(ErrorResponse.class);
            assertNotNull(errorResponse);
            assertNotNull(errorResponse.path());
            assertEquals(projectUrl + "/" + projectId, errorResponse.path());
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
    @Order(27)
    @DirtiesContext
    @DisplayName("Тестируем DELETE /api/projects : успех")
    public void test27() {

        /*
         * Удаляем проекты пользователя алекс.
         * */

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(alexToken);

        ResponseEntity<Void> response = rest.exchange(
                projectUrl,
                HttpMethod.DELETE,
                new HttpEntity<>(headers),
                Void.class
        );
        assertNotNull(response);
        assertNotNull(response.getStatusCode());
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());

        /*
         * Проверяем, что у алекса больше нет проектов.
         * */

        ResponseEntity<SheetDto<ProjectRD>> response2 = rest.exchange(
                projectUrl,
                HttpMethod.GET,
                new HttpEntity<>(headers),
                new ParameterizedTypeReference<>() {
                }
        );
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
    @Order(28)
    @DirtiesContext
    @DisplayName("Тестируем DELETE /api/projects/{id} : успех")
    public void test28() {

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
        assertNotNull(sheet.content());

        List<ProjectRD> projects = sheet.content();
        assertNotNull(projects);
        assertFalse(projects.isEmpty());

        ProjectRD project = projects.getFirst();
        assertNotNull(project);
        Long projectId = project.id();
        assertNotNull(projectId);

        /*
         * Удаляем проект.
         * */

        ResponseEntity<Void> response2 = rest.exchange(
                projectUrl + "/{id}",
                HttpMethod.DELETE,
                new HttpEntity<>(headers),
                Void.class,
                projectId
        );
        assertNotNull(response2);
        assertNotNull(response2.getStatusCode());
        assertEquals(HttpStatus.NO_CONTENT, response2.getStatusCode());

        /*
         * Проверяем, что проект был удален.
         * */

        try {
            rest.exchange(
                    projectUrl + "/{id}",
                    HttpMethod.GET,
                    new HttpEntity<>(headers),
                    ProjectRD.class,
                    projectId
            );
            fail("Should throw HttpClientErrorException.NotFound");

        } catch (HttpClientErrorException.NotFound e) {
            assertNotNull(e);
            assertNotNull(e.getStatusCode());
            assertEquals(HttpStatus.NOT_FOUND, e.getStatusCode());

            ErrorResponse errorResponse = e.getResponseBodyAs(ErrorResponse.class);
            assertNotNull(errorResponse);
            assertNotNull(errorResponse.path());
            assertEquals(projectUrl + "/" + projectId, errorResponse.path());
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
    @Order(29)
    @DirtiesContext
    @DisplayName("Тестируем DELETE /api/projects/{id} с чужим id : ошибка 403 запрещено")
    public void test29() {

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
        assertNotNull(sheet.content());

        List<ProjectRD> projects = sheet.content();
        assertNotNull(projects);
        assertFalse(projects.isEmpty());

        ProjectRD project = projects.getFirst();
        assertNotNull(project);
        Long projectId = project.id();
        assertNotNull(projectId);

        /*
         * Пытаемся удалить чужой проект.
         * */

        HttpHeaders headers2 = new HttpHeaders();
        headers2.setBearerAuth(ekaterinaToken);

        try {
            rest.exchange(
                    projectUrl + "/{id}",
                    HttpMethod.DELETE,
                    new HttpEntity<>(headers2),
                    Void.class,
                    projectId
            );
            fail("Should throw HttpClientErrorException.Forbidden");

        } catch (HttpClientErrorException.Forbidden e) {
            assertNotNull(e);
            assertNotNull(e.getStatusCode());
            assertEquals(HttpStatus.FORBIDDEN, e.getStatusCode());

            ErrorResponse errorResponse = e.getResponseBodyAs(ErrorResponse.class);
            assertNotNull(errorResponse);
            assertNotNull(errorResponse.path());
            assertEquals(projectUrl + "/" + projectId, errorResponse.path());
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

    @Test
    @Order(30)
    @DirtiesContext
    @DisplayName("Тестируем DELETE /api/projects/{id} с некорректным id : ошибка 404 не найдено")
    public void test30() {

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(alexToken);

        Long projectId = Long.MAX_VALUE;

        try {
            rest.exchange(
                    projectUrl + "/{id}",
                    HttpMethod.DELETE,
                    new HttpEntity<>(headers),
                    Void.class,
                    projectId
            );
            fail("Should throw HttpClientErrorException.NotFound");

        } catch (HttpClientErrorException.NotFound e) {
            assertNotNull(e);
            assertNotNull(e.getStatusCode());
            assertEquals(HttpStatus.NOT_FOUND, e.getStatusCode());

            ErrorResponse errorResponse = e.getResponseBodyAs(ErrorResponse.class);
            assertNotNull(errorResponse);
            assertNotNull(errorResponse.path());
            assertEquals(projectUrl + "/" + projectId, errorResponse.path());
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
    @Order(31)
    @DirtiesContext
    @DisplayName("Тестируем DELETE /api/projects/{id}/tasks : успех")
    public void test31() {

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
        assertNotNull(sheet.content());

        List<ProjectRD> projects = sheet.content();
        assertNotNull(projects);
        assertFalse(projects.isEmpty());

        ProjectRD project = projects.getFirst();
        assertNotNull(project);
        Long projectId = project.id();
        assertNotNull(projectId);

        /*
         * Удаляем задачи этого проекта.
         * */

        ResponseEntity<Void> response2 = rest.exchange(
                projectUrl + "/{id}/tasks",
                HttpMethod.DELETE,
                new HttpEntity<>(headers),
                Void.class,
                projectId
        );
        assertNotNull(response2);
        assertNotNull(response2.getStatusCode());
        assertEquals(HttpStatus.NO_CONTENT, response2.getStatusCode());

        /*
         * Проверяем, что задач у проекта больше нет.
         * */

        ResponseEntity<SheetDto<TaskRD>> response3 = rest.exchange(
                projectUrl + "/{id}/tasks",
                HttpMethod.GET,
                new HttpEntity<>(headers),
                new ParameterizedTypeReference<>() {
                },
                projectId
        );
        assertNotNull(response3);
        assertNotNull(response3.getStatusCode());
        assertEquals(HttpStatus.OK, response3.getStatusCode());

        SheetDto<TaskRD> sheet2 = response3.getBody();
        assertNotNull(sheet2);
        assertNotNull(sheet2.page());

        List<TaskRD> tasks = sheet2.content();
        assertNotNull(tasks);
        assertTrue(tasks.isEmpty());
    }

    @Test
    @Order(32)
    @DirtiesContext
    @DisplayName("Тестируем DELETE /api/projects/{id}/tasks с чужим id : ошибка 403 запрещено")
    public void test32() {

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
        assertNotNull(sheet.content());

        List<ProjectRD> projects = sheet.content();
        assertNotNull(projects);
        assertFalse(projects.isEmpty());

        ProjectRD project = projects.getFirst();
        assertNotNull(project);
        Long projectId = project.id();
        assertNotNull(projectId);

        /*
         * Пытаемся удалить задачи у чужого проекта.
         * */

        HttpHeaders headers2 = new HttpHeaders();
        headers2.setBearerAuth(ekaterinaToken);

        try {
            rest.exchange(
                    projectUrl + "/{id}/tasks",
                    HttpMethod.DELETE,
                    new HttpEntity<>(headers2),
                    Void.class,
                    projectId
            );
            fail("Should throw HttpClientErrorException.Forbidden");

        } catch (HttpClientErrorException.Forbidden e) {
            assertNotNull(e);
            assertNotNull(e.getStatusCode());
            assertEquals(HttpStatus.FORBIDDEN, e.getStatusCode());

            ErrorResponse errorResponse = e.getResponseBodyAs(ErrorResponse.class);
            assertNotNull(errorResponse);
            assertNotNull(errorResponse.path());
            assertEquals(projectUrl + "/" + projectId + "/tasks", errorResponse.path());
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

    @Test
    @Order(33)
    @DirtiesContext
    @DisplayName("Тестируем DELETE /api/projects/{id}/tasks с некорректным id : ошибка 404 не найдено")
    public void test33() {

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(alexToken);

        Long projectId = Long.MAX_VALUE;

        try {
            rest.exchange(
                    projectUrl + "/{id}/tasks",
                    HttpMethod.DELETE,
                    new HttpEntity<>(headers),
                    Void.class,
                    projectId
            );
            fail("Should throw HttpClientErrorException.NotFound");

        } catch (HttpClientErrorException.NotFound e) {
            assertNotNull(e);
            assertNotNull(e.getStatusCode());
            assertEquals(HttpStatus.NOT_FOUND, e.getStatusCode());

            ErrorResponse errorResponse = e.getResponseBodyAs(ErrorResponse.class);
            assertNotNull(errorResponse);
            assertNotNull(errorResponse.path());
            assertEquals(projectUrl + "/" + projectId + "/tasks", errorResponse.path());
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
}