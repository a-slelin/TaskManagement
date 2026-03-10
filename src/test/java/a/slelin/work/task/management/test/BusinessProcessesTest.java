package a.slelin.work.task.management.test;

import a.slelin.work.task.management.dto.ProjectRD;
import a.slelin.work.task.management.dto.TaskRD;
import a.slelin.work.task.management.dto.UserRD;
import a.slelin.work.task.management.exception.ErrorResponse;
import a.slelin.work.task.management.exception.TaskSetProjectException;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.List;

import static a.slelin.work.task.management.test.TestConfig.*;
import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test")
@DisplayName("Тест бизнес процессов")
@TestClassOrder(ClassOrderer.OrderAnnotation.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class BusinessProcessesTest {

    @Autowired
    private RestTemplate rest;

    @Test
    @Order(1)
    @DisplayName("Перетаскиваем задачу с одного проекта на другой")
    public void testDrawProject() {

        // Берём случайного пользователя из всех пользователей
        ResponseEntity<List<UserRD>> responseAllUsers = rest.exchange(
                USER_URL,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {
                });
        assertNotNull(responseAllUsers);
        assertNotNull(responseAllUsers.getStatusCode());
        assertEquals(HttpStatus.OK, responseAllUsers.getStatusCode());

        List<UserRD> users = responseAllUsers.getBody();
        assertNotNull(users);
        assertFalse(users.isEmpty());

        UserRD user = users.getFirst();
        assertNotNull(user);

        String userId = user.id();
        assertNotNull(userId);

        ResponseEntity<List<ProjectRD>> responseUserProjects = rest.exchange(
                USER_URL + "/" + userId + "/projects?tasks",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {
                }
        );
        assertNotNull(responseUserProjects);
        assertNotNull(responseUserProjects.getStatusCode());
        assertEquals(HttpStatus.OK, responseUserProjects.getStatusCode());

        /*
         * Теперь у этого пользователя берём его проекты.
         * У него должно быть хотя бы 2 проекта.
         * В первом проекте должна быть хотя бы одна задача.
         * */
        List<ProjectRD> projects = responseUserProjects.getBody();
        assertNotNull(projects);
        assertTrue(projects.size() >= 2);

        ProjectRD project1 = projects.getFirst();
        ProjectRD project2 = projects.getLast();
        assertNotNull(project1);
        assertNotNull(project2);
        assertNotNull(project1.tasks());
        assertNotNull(project2.tasks());
        assertFalse(project1.tasks().isEmpty());

        TaskRD task = project1.tasks().getFirst();
        assertNotNull(task);
        assertTrue(project1.tasks().contains(task));
        assertFalse(project2.tasks().contains(task));

        // Перемещаем задачу из первого проекта во второй
        ResponseEntity<TaskRD> responseMovingTask = rest.exchange(
                TASK_URL + "/" + task.id() + "/project/" + project2.id(),
                HttpMethod.POST,
                null,
                TaskRD.class);
        assertNotNull(responseMovingTask);
        assertNotNull(responseMovingTask.getStatusCode());
        assertEquals(HttpStatus.OK, responseMovingTask.getStatusCode());

        // У задачи должен поменяться только проект
        TaskRD movingTask = responseMovingTask.getBody();
        assertNotNull(movingTask);
        assertNotNull(movingTask.id());
        assertEquals(task.id(), movingTask.id());
        assertNotNull(movingTask.user());
        assertEquals(task.user(), movingTask.user());
        assertEquals(task.status(), movingTask.status());
        assertEquals(task.title(), movingTask.title());
        assertEquals(task.description(), movingTask.description());
        assertNotNull(movingTask.project());
        assertNotEquals(task.project(), movingTask.project());
        assertEquals(movingTask.project(), project2.id());

        // Проверяем, что в базе данных тоже все сохранилось
        TaskRD taskCheck = rest.getForObject(TASK_URL + "/" + movingTask.id(), TaskRD.class);
        assertNotNull(taskCheck);
        assertNotNull(taskCheck.id());
        assertEquals(taskCheck.id(), movingTask.id());
        assertNotNull(taskCheck.project());
        assertEquals(taskCheck.project(), movingTask.project());

        // В первом проекте этой задачи теперь нет
        ProjectRD project1Updated = rest.getForObject(PROJECT_URL + "/" + project1.id() + "?tasks",
                ProjectRD.class);
        assertNotNull(project1Updated);
        assertNotNull(project1Updated.id());
        assertEquals(project1.id(), project1Updated.id());
        assertNotNull(project1Updated.tasks());
        assertFalse(project1Updated.tasks().contains(movingTask));

        // Во втором проекте эта задача есть
        ProjectRD project2Updated = rest.getForObject(PROJECT_URL + "/" + project2.id() + "?tasks",
                ProjectRD.class);
        assertNotNull(project2Updated);
        assertNotNull(project2Updated.id());
        assertEquals(project2.id(), project2Updated.id());
        assertNotNull(project2Updated.tasks());
        assertTrue(project2Updated.tasks().contains(movingTask));
    }

    @Test
    @Order(2)
    @DisplayName("Пробуем перетаскивать задачу на тот же самый проект")
    public void testDrawProject2() {

        // Берём случайного пользователя из всех пользователей
        ResponseEntity<List<UserRD>> responseAllUsers = rest.exchange(
                USER_URL,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {
                });
        assertNotNull(responseAllUsers);
        assertNotNull(responseAllUsers.getStatusCode());
        assertEquals(HttpStatus.OK, responseAllUsers.getStatusCode());

        List<UserRD> users = responseAllUsers.getBody();
        assertNotNull(users);
        assertFalse(users.isEmpty());

        UserRD user = users.getFirst();
        assertNotNull(user);

        String userId = user.id();
        assertNotNull(userId);

        ResponseEntity<List<ProjectRD>> responseUserProjects = rest.exchange(
                USER_URL + "/" + userId + "/projects?tasks",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {
                }
        );
        assertNotNull(responseUserProjects);
        assertNotNull(responseUserProjects.getStatusCode());
        assertEquals(HttpStatus.OK, responseUserProjects.getStatusCode());

        /*
         * Теперь у этого пользователя берём его проект.
         * В проекте должна быть хотя бы одна задача.
         * */
        List<ProjectRD> projects = responseUserProjects.getBody();
        assertNotNull(projects);
        assertFalse(projects.isEmpty());

        ProjectRD project = projects.getFirst();
        assertNotNull(project);
        assertNotNull(project.tasks());
        assertFalse(project.tasks().isEmpty());

        TaskRD task = project.tasks().getFirst();
        assertNotNull(task);
        assertTrue(project.tasks().contains(task));

        String url = TASK_URL + "/" + task.id() + "/project/" + project.id();
        try {
            rest.exchange(
                    url,
                    HttpMethod.POST,
                    null,
                    TaskRD.class
            );
            fail("Задача не должна была переместиться.");
        } catch (HttpClientErrorException.BadRequest e) {
            assertNotNull(e);
            ErrorResponse errorResponse = e.getResponseBodyAs(ErrorResponse.class);
            assertNotNull(errorResponse);

            assertNotNull(errorResponse.path());
            assertEquals(url, errorResponse.path());
            assertNotNull(errorResponse.httpStatus());
            assertEquals(HttpStatus.BAD_REQUEST, errorResponse.httpStatus());
            assertNotNull(errorResponse.httpMethod());
            assertEquals(HttpMethod.POST, errorResponse.httpMethod());
            assertNotNull(errorResponse.exception());
            assertEquals(TaskSetProjectException.class.getSimpleName(), errorResponse.exception());
            assertNotNull(errorResponse.message());
            assertNotNull(errorResponse.timeStamp());
            assertNotNull(errorResponse.details());
        } catch (Exception e) {
            fail("Получили ошибку другого рода.");

        }

        TaskRD taskCheck = rest.getForObject(TASK_URL + "/" + task.id(), TaskRD.class);
        assertNotNull(taskCheck);
        assertNotNull(taskCheck.id());
        assertEquals(taskCheck.id(), task.id());
        assertNotNull(taskCheck.project());
        assertEquals(taskCheck.project(), task.project());

        ProjectRD projectUpdated = rest.getForObject(PROJECT_URL + "/" + project.id() + "?tasks",
                ProjectRD.class);
        assertNotNull(projectUpdated);
        assertNotNull(projectUpdated.id());
        assertEquals(project.id(), projectUpdated.id());
        assertNotNull(projectUpdated.tasks());
        assertTrue(projectUpdated.tasks().contains(task));
    }

    @Test
    @Order(3)
    @DisplayName("Перетаскиваем задачу с одного проекта одного пользователя на другой проект другого пользователя")
    public void testDrawProject3() {

        // Берём случайных 2 пользователей из всех пользователей
        ResponseEntity<List<UserRD>> responseAllUsers = rest.exchange(
                USER_URL,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {
                });
        assertNotNull(responseAllUsers);
        assertNotNull(responseAllUsers.getStatusCode());
        assertEquals(HttpStatus.OK, responseAllUsers.getStatusCode());

        List<UserRD> users = responseAllUsers.getBody();
        assertNotNull(users);
        assertTrue(users.size() >= 2);

        UserRD user1 = users.getFirst();
        UserRD user2 = users.getLast();
        assertNotNull(user1);
        assertNotNull(user2);

        String user1Id = user1.id();
        String user2Id = user2.id();
        assertNotNull(user1Id);
        assertNotNull(user2Id);

        ResponseEntity<List<ProjectRD>> responseUser1Projects = rest.exchange(
                USER_URL + "/" + user1Id + "/projects?tasks",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {
                }
        );
        assertNotNull(responseUser1Projects);
        assertNotNull(responseUser1Projects.getStatusCode());
        assertEquals(HttpStatus.OK, responseUser1Projects.getStatusCode());

        ResponseEntity<List<ProjectRD>> responseUser2Projects = rest.exchange(
                USER_URL + "/" + user2Id + "/projects?tasks",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {
                }
        );
        assertNotNull(responseUser2Projects);
        assertNotNull(responseUser2Projects.getStatusCode());
        assertEquals(HttpStatus.OK, responseUser2Projects.getStatusCode());

        /*
         * У каждого из пользователей должно быть по проекту.
         * Причём у первого должна быть хотя бы одна задача.
         * */
        List<ProjectRD> projects1 = responseUser1Projects.getBody();
        assertNotNull(projects1);
        assertFalse(projects1.isEmpty());

        List<ProjectRD> projects2 = responseUser2Projects.getBody();
        assertNotNull(projects2);
        assertFalse(projects2.isEmpty());

        ProjectRD project1 = projects1.getFirst();
        ProjectRD project2 = projects2.getFirst();
        assertNotNull(project1);
        assertNotNull(project2);
        assertNotNull(project1.tasks());
        assertNotNull(project2.tasks());
        assertFalse(project1.tasks().isEmpty());

        TaskRD task = project1.tasks().getFirst();
        assertNotNull(task);
        assertTrue(project1.tasks().contains(task));
        assertFalse(project2.tasks().contains(task));

        String url = TASK_URL + "/" + task.id() + "/project/" + project2.id();
        try {
            rest.exchange(
                    url,
                    HttpMethod.POST,
                    null,
                    TaskRD.class
            );
            fail("Должны получить ошибку при перемещении задачи.");
        } catch (HttpClientErrorException.BadRequest e) {
            assertNotNull(e);
            ErrorResponse errorResponse = e.getResponseBodyAs(ErrorResponse.class);
            assertNotNull(errorResponse);

            assertNotNull(errorResponse.path());
            assertEquals(url, errorResponse.path());
            assertNotNull(errorResponse.httpStatus());
            assertEquals(HttpStatus.BAD_REQUEST, errorResponse.httpStatus());
            assertNotNull(errorResponse.httpMethod());
            assertEquals(HttpMethod.POST, errorResponse.httpMethod());
            assertNotNull(errorResponse.exception());
            assertEquals(TaskSetProjectException.class.getSimpleName(), errorResponse.exception());
            assertNotNull(errorResponse.message());
            assertNotNull(errorResponse.timeStamp());
            assertNotNull(errorResponse.details());
        } catch (Exception e) {
            fail("Получили исключение другого рода.");
        }

        TaskRD taskCheck = rest.getForObject(TASK_URL + "/" + task.id(), TaskRD.class);
        assertNotNull(taskCheck);
        assertNotNull(taskCheck.id());
        assertEquals(taskCheck.id(), task.id());
        assertNotNull(taskCheck.project());
        assertEquals(taskCheck.project(), task.project());

        ProjectRD project1Updated = rest.getForObject(PROJECT_URL + "/" + project1.id() + "?tasks",
                ProjectRD.class);
        assertNotNull(project1Updated);
        assertNotNull(project1Updated.id());
        assertEquals(project1.id(), project1Updated.id());
        assertNotNull(project1Updated.tasks());
        assertTrue(project1Updated.tasks().contains(task));

        // Во втором проекте эта задача есть
        ProjectRD project2Updated = rest.getForObject(PROJECT_URL + "/" + project2.id() + "?tasks",
                ProjectRD.class);
        assertNotNull(project2Updated);
        assertNotNull(project2Updated.id());
        assertEquals(project2.id(), project2Updated.id());
        assertNotNull(project2Updated.tasks());
        assertFalse(project2Updated.tasks().contains(task));
    }
}
