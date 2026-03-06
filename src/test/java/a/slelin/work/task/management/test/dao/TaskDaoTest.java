package a.slelin.work.task.management.test.dao;

import a.slelin.work.task.management.dao.ProjectDao;
import a.slelin.work.task.management.dao.TaskDao;
import a.slelin.work.task.management.entity.Status;
import a.slelin.work.task.management.entity.Task;
import a.slelin.work.task.management.util.DatabaseUtil;
import org.junit.jupiter.api.*;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class TaskDaoTest {

    private final static ProjectDao projectDao = ProjectDao.getInstance();

    private final static TaskDao taskDao = TaskDao.getInstance();

    private static Task testTask;

    @BeforeAll
    public static void setUp() {
        DatabaseUtil.setTestMode();
    }

    @Test
    @Order(1)
    public void getAllTasks() {
        List<Task> tasks = taskDao.getAll();
        assertNotNull(tasks);
        assertEquals(12, tasks.size());
    }

    @Test
    @Order(2)
    public void getTaskById() {
        Task task = taskDao.getById(1L);
        assertNotNull(task);
    }

    @Test
    @Order(3)
    public void createTask() {
        testTask = Task.builder()
                .title("test")
                .description("test")
                .status(Status.IN_PROGRESS)
                .project(projectDao.getById(1L))
                .build();

        testTask = taskDao.create(testTask);
        assertNotNull(testTask);
        assertNotNull(testTask.getId());
    }

    @Test
    @Order(4)
    public void updateTask() {
        testTask.setTitle("test2");
        testTask = taskDao.update(testTask);
        assertNotNull(testTask);
        assertNotNull(testTask.getTitle());
        assertEquals("test2", testTask.getTitle());
    }

    @Test
    @Order(5)
    public void deleteTask() {
        taskDao.delete(testTask);
        List<Task> tasks = taskDao.getAll();
        assertNotNull(tasks);
        assertEquals(12, tasks.size());
    }

    @AfterAll
    public static void tearDown() {
        DatabaseUtil.resetTestMode();
    }
}
