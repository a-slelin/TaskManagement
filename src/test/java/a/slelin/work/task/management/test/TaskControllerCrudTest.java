package a.slelin.work.task.management.test;

import a.slelin.work.task.management.dto.SheetDto;
import a.slelin.work.task.management.dto.TaskRD;
import a.slelin.work.task.management.dto.TaskWD;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.List;

import static a.slelin.work.task.management.test.TestConfig.TASK_URL;
import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test")
@DisplayName("Тест контроллера задач")
@TestClassOrder(ClassOrderer.OrderAnnotation.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class TaskControllerCrudTest {

    @Autowired
    private RestTemplate rest;

    @Test
    @Order(1)
    @DisplayName("Тестируем получение всех задач")
    public void getAllTasks() {
        ResponseEntity<SheetDto<TaskRD>> response = rest.exchange(
                TASK_URL,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {
                });
        assertNotNull(response);
        assertNotNull(response.getStatusCode());
        assertEquals(HttpStatus.OK, response.getStatusCode());

        SheetDto<TaskRD> sheet = response.getBody();
        assertNotNull(sheet);
        assertNotNull(sheet.page());
        assertNotNull(sheet.content());

        List<TaskRD> tasks = sheet.content();
        assertNotNull(tasks);

        tasks.forEach(task -> {
            assertNotNull(task);
            assertNotNull(task.id());
            assertNotNull(task.project());
            assertNotNull(task.user());
        });
    }

    @Test
    @Order(2)
    @DisplayName("Тестируем получение задачи по id = 1")
    public void getTaskById() {
        long id = 1;

        ResponseEntity<TaskRD> response = rest.exchange(
                TASK_URL + "/" + id,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {
                });
        assertNotNull(response);
        assertNotNull(response.getStatusCode());
        assertEquals(HttpStatus.OK, response.getStatusCode());

        TaskRD task = response.getBody();
        assertNotNull(task);
        assertNotNull(task.id());
        assertEquals(id, task.id());
        assertNotNull(task.project());
        assertNotNull(task.user());
    }

    @Test
    @Order(3)
    @DisplayName("Тестируем обновление задачи (id = 1)")
    public void updateTask() {
        long id = 1;

        TaskRD task = rest.getForObject(TASK_URL + "/" + id, TaskRD.class);
        assertNotNull(task);
        assertNotNull(task.id());
        assertEquals(id, task.id());

        String title = "updatedTitle";
        String description = "updatedDescription";
        String status = "begin";
        TaskWD newTask = new TaskWD(title, description, status);

        ResponseEntity<TaskRD> response = rest.exchange(
                TASK_URL + "/" + id,
                HttpMethod.PUT,
                new HttpEntity<>(newTask),
                TaskRD.class);
        assertNotNull(response);
        assertNotNull(response.getStatusCode());
        assertEquals(HttpStatus.OK, response.getStatusCode());

        TaskRD updatedTask = response.getBody();
        assertNotNull(updatedTask);
        assertNotNull(updatedTask.id());
        assertEquals(id, updatedTask.id());
        assertNotNull(updatedTask.title());
        assertEquals(title, updatedTask.title());
        assertNotNull(updatedTask.description());
        assertEquals(description, updatedTask.description());
        assertNotNull(updatedTask.status());
        assertEquals(status, updatedTask.status());
    }

    @Test
    @Order(4)
    @DisplayName("Тестируем патчинг задачи (id = 1)")
    public void patchTask() {
        long id = 1;

        TaskRD task = rest.getForObject(TASK_URL + "/" + id, TaskRD.class);
        assertNotNull(task);
        assertNotNull(task.id());
        assertEquals(id, task.id());

        String title = "updatedTitle";
        TaskWD newTask = TaskWD.builder()
                .title(title)
                .build();

        ResponseEntity<TaskRD> response = rest.exchange(
                TASK_URL + "/" + id,
                HttpMethod.PATCH,
                new HttpEntity<>(newTask),
                TaskRD.class);
        assertNotNull(response);
        assertNotNull(response.getStatusCode());
        assertEquals(HttpStatus.OK, response.getStatusCode());

        TaskRD updatedTask = response.getBody();
        assertNotNull(updatedTask);
        assertNotNull(updatedTask.id());
        assertEquals(id, updatedTask.id());
        assertNotNull(updatedTask.title());
        assertEquals(title, updatedTask.title());
        assertNotNull(updatedTask.description());
        assertEquals(task.description(), updatedTask.description());
        assertNotNull(updatedTask.status());
        assertEquals(task.status(), updatedTask.status());
    }

    @Test
    @Order(5)
    @DisplayName("Тестируем удаление задачи (id = 1)")
    public void deleteTask() {
        long id = 1;

        TaskRD task = rest.getForObject(TASK_URL + "/" + id, TaskRD.class);
        assertNotNull(task);
        assertNotNull(task.id());
        assertEquals(id, task.id());

        ResponseEntity<Void> response = rest.exchange(
                TASK_URL + "/" + id,
                HttpMethod.DELETE,
                null,
                Void.class);
        assertNotNull(response);
        assertNotNull(response.getStatusCode());
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());

        assertThrows(HttpClientErrorException.NotFound.class, () ->
                rest.getForObject(TASK_URL + "/" + id, TaskRD.class));
    }
}
