package a.slelin.work.task.management.test.dao;

import a.slelin.work.task.management.dao.UserDao;
import a.slelin.work.task.management.entity.Gender;
import a.slelin.work.task.management.entity.User;
import a.slelin.work.task.management.util.DatabaseUtil;
import org.junit.jupiter.api.*;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class UserDaoTest {

    public final static UserDao userDao = UserDao.getInstance();

    private static User testUser;

    @BeforeAll
    public static void setUp() {
        DatabaseUtil.setTestMode();
    }

    @Test
    @Order(1)
    public void getAllUsers() {
        List<User> users = userDao.getAll();
        assertNotNull(users);
        assertEquals(3, users.size());
    }

    @Test
    @Order(2)
    public void getUserById() {
        User user = userDao.getById(UUID.fromString("35e9edb1-4cc7-4278-9677-c581857c1998"));
        assertNotNull(user);
    }

    @Test
    @Order(3)
    public void createUser() {
        testUser = User.builder()
                .username("test")
                .password("test")
                .gender(Gender.UNDEFINED)
                .phone("test")
                .email("test")
                .build();

        userDao.create(testUser);
        assertNotNull(testUser);
        assertNotNull(testUser.getId());
    }

    @Test
    @Order(4)
    public void updateUser() {
        testUser.setUsername("test2");
        userDao.update(testUser);
        assertNotNull(testUser);
        assertNotNull(testUser.getUsername());
        assertEquals("test2", testUser.getUsername());
    }

    @Test
    @Order(5)
    public void deleteUser() {
        userDao.delete(testUser);
        List<User> users = userDao.getAll();
        assertNotNull(users);
        assertEquals(3, users.size());
    }

    @AfterAll
    public static void tearDown() {
        DatabaseUtil.resetTestMode();
    }

}
