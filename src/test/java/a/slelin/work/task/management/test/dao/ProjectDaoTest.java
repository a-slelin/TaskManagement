package a.slelin.work.task.management.test.dao;

import a.slelin.work.task.management.dao.ProjectDao;
import a.slelin.work.task.management.dao.UserDao;
import a.slelin.work.task.management.entity.Project;
import a.slelin.work.task.management.util.DatabaseUtil;
import org.junit.jupiter.api.*;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ProjectDaoTest {

    public final static UserDao userDao = UserDao.getInstance();

    public final static ProjectDao projectDao = ProjectDao.getInstance();

    private static Project testProject;

    @BeforeAll
    public static void setUp() {
        DatabaseUtil.setTestMode();
    }

    @Test
    @Order(1)
    public void getAllProjects() {
        List<Project> projects = projectDao.getAll();
        assertNotNull(projects);
        assertEquals(3, projects.size());
    }

    @Test
    @Order(2)
    public void getProjectById() {
        Project project = projectDao.getById(1L);
        assertNotNull(project);
    }

    @Test
    @Order(3)
    public void createProject() {
        testProject = Project.builder()
                .name("Test Project")
                .description("Test Description")
                .user(userDao.getById(UUID.fromString("35e9edb1-4cc7-4278-9677-c581857c1998")))
                .build();

        projectDao.create(testProject);

        assertNotNull(testProject);
        assertNotNull(testProject.getId());
    }

    @Test
    @Order(4)
    public void updateProject() {
        testProject.setName("Updated Project");
        projectDao.update(testProject);
        assertNotNull(testProject);
        assertNotNull(testProject.getName());
        assertEquals("Updated Project", testProject.getName());
    }

    @Test
    @Order(5)
    public void deleteProject() {
        projectDao.delete(testProject);
        List<Project> projects = projectDao.getAll();
        assertNotNull(projects);
        assertEquals(3, projects.size());
    }

    @AfterAll
    public static void tearDown() {
        DatabaseUtil.resetTestMode();
    }

}
