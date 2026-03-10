package a.slelin.work.task.management.test;

import a.slelin.work.task.management.dto.ProjectRD;
import a.slelin.work.task.management.dto.ProjectWD;
import a.slelin.work.task.management.dto.UserRD;
import a.slelin.work.task.management.dto.UserWD;
import a.slelin.work.task.management.entity.User;
import a.slelin.work.task.management.repository.UserRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.util.List;
import java.util.UUID;

import static a.slelin.work.task.management.test.TestConfig.USER_URL;
import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test")
@DisplayName("Тест контроллера пользователей")
@TestClassOrder(ClassOrderer.OrderAnnotation.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class UserControllerTest {

    @Autowired
    private RestTemplate rest;

    @Autowired
    private UserRepository repository;

    @Autowired
    private PasswordEncoder encoder;

    @Test
    @Order(1)
    @DisplayName("Тестируем получение всех пользователей без проектов")
    public void getAllProjects() {
        ResponseEntity<List<UserRD>> response = rest.exchange(
                USER_URL,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {
                });
        assertNotNull(response);
        assertNotNull(response.getStatusCode());
        assertEquals(HttpStatus.OK, response.getStatusCode());

        List<UserRD> users = response.getBody();
        assertNotNull(users);

        users.forEach(user -> {
            assertNotNull(user);
            assertNotNull(user.id());
            assertNull(user.projects());
        });
    }

    @Test
    @Order(2)
    @DisplayName("Тестируем получение всех пользователей c проектами, но без задач")
    public void getAllProjectsWithProjects() {
        ResponseEntity<List<UserRD>> response = rest.exchange(
                USER_URL + "?projects",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {
                });
        assertNotNull(response);
        assertNotNull(response.getStatusCode());
        assertEquals(HttpStatus.OK, response.getStatusCode());

        List<UserRD> users = response.getBody();
        assertNotNull(users);

        users.forEach(user -> {
            assertNotNull(user);
            assertNotNull(user.id());
            assertNotNull(user.projects());
            user.projects().forEach(project -> {
                assertNotNull(project);
                assertNotNull(project.id());
                assertNotNull(project.user());
                assertEquals(project.user(), user.id());
                assertNull(project.tasks());
            });
        });
    }

    @Test
    @Order(3)
    @DisplayName("Тестируем получение всех пользователей c проектами и с задачами")
    public void getAllProjectsWithProjectsAndTasks() {
        ResponseEntity<List<UserRD>> response = rest.exchange(
                USER_URL + "?projects&tasks",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {
                });
        assertNotNull(response);
        assertNotNull(response.getStatusCode());
        assertEquals(HttpStatus.OK, response.getStatusCode());

        List<UserRD> users = response.getBody();
        assertNotNull(users);

        users.forEach(user -> {
            assertNotNull(user);
            assertNotNull(user.id());
            assertNotNull(user.projects());
            user.projects().forEach(project -> {
                assertNotNull(project);
                assertNotNull(project.id());
                assertNotNull(project.user());
                assertEquals(project.user(), user.id());
                assertNotNull(project.tasks());
                project.tasks().forEach(task -> {
                    assertNotNull(task);
                    assertNotNull(task.id());
                    assertNotNull(task.project());
                    assertEquals(project.id(), task.project());
                    assertNotNull(task.user());
                    assertEquals(project.user(), task.user());
                });
            });
        });
    }

    @Test
    @Order(4)
    @DisplayName("Тестируем получение пользователя по id = ? без проектов")
    public void getById() {
        ResponseEntity<List<UserRD>> responseGetAll = rest.exchange(
                USER_URL,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {
                });
        assertNotNull(responseGetAll);
        assertNotNull(responseGetAll.getStatusCode());
        assertEquals(HttpStatus.OK, responseGetAll.getStatusCode());

        List<UserRD> users = responseGetAll.getBody();
        assertNotNull(users);
        assertFalse(users.isEmpty());

        UserRD user = users.getFirst();
        assertNotNull(user);

        String userId = user.id();
        assertNotNull(userId);

        ResponseEntity<UserRD> responseGetById = rest.exchange(
                USER_URL + "/" + userId,
                HttpMethod.GET,
                null,
                UserRD.class);
        assertNotNull(responseGetById);
        assertNotNull(responseGetById.getStatusCode());
        assertEquals(HttpStatus.OK, responseGetById.getStatusCode());

        UserRD getUser = responseGetById.getBody();
        assertNotNull(getUser);
        assertNotNull(getUser.id());
        assertEquals(userId, getUser.id());
        assertNull(getUser.projects());
    }

    @Test
    @Order(5)
    @DisplayName("Тестируем получение пользователя по id = ? с проектами, но без задач")
    public void getByIdWithProjects() {
        ResponseEntity<List<UserRD>> responseGetAll = rest.exchange(
                USER_URL,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {
                });
        assertNotNull(responseGetAll);
        assertNotNull(responseGetAll.getStatusCode());
        assertEquals(HttpStatus.OK, responseGetAll.getStatusCode());

        List<UserRD> users = responseGetAll.getBody();
        assertNotNull(users);
        assertFalse(users.isEmpty());

        UserRD user = users.getFirst();
        assertNotNull(user);

        String userId = user.id();
        assertNotNull(userId);

        ResponseEntity<UserRD> responseGetById = rest.exchange(
                USER_URL + "/" + userId + "?projects",
                HttpMethod.GET,
                null,
                UserRD.class);
        assertNotNull(responseGetById);
        assertNotNull(responseGetById.getStatusCode());
        assertEquals(HttpStatus.OK, responseGetById.getStatusCode());

        UserRD getUser = responseGetById.getBody();
        assertNotNull(getUser);
        assertNotNull(getUser.id());
        assertEquals(userId, getUser.id());
        assertNotNull(getUser.projects());

        getUser.projects().forEach(project -> {
            assertNotNull(project);
            assertNotNull(project.id());
            assertNotNull(project.user());
            assertEquals(project.user(), getUser.id());
            assertNull(project.tasks());
        });
    }

    @Test
    @Order(6)
    @DisplayName("Тестируем получение пользователя по id = ? с проектов и с задачами")
    public void getByIdWithProjectsAndTasks() {
        ResponseEntity<List<UserRD>> responseGetAll = rest.exchange(
                USER_URL,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {
                });
        assertNotNull(responseGetAll);
        assertNotNull(responseGetAll.getStatusCode());
        assertEquals(HttpStatus.OK, responseGetAll.getStatusCode());

        List<UserRD> users = responseGetAll.getBody();
        assertNotNull(users);
        assertFalse(users.isEmpty());

        UserRD user = users.getFirst();
        assertNotNull(user);

        String userId = user.id();
        assertNotNull(userId);

        ResponseEntity<UserRD> responseGetById = rest.exchange(
                USER_URL + "/" + userId + "?projects&tasks",
                HttpMethod.GET,
                null,
                UserRD.class);
        assertNotNull(responseGetById);
        assertNotNull(responseGetById.getStatusCode());
        assertEquals(HttpStatus.OK, responseGetById.getStatusCode());

        UserRD getUser = responseGetById.getBody();
        assertNotNull(getUser);
        assertNotNull(getUser.id());
        assertEquals(userId, getUser.id());
        assertNotNull(getUser.projects());

        getUser.projects().forEach(project -> {
            assertNotNull(project);
            assertNotNull(project.id());
            assertNotNull(project.user());
            assertEquals(project.user(), getUser.id());
            assertNotNull(project.tasks());
            project.tasks().forEach(task -> {
                assertNotNull(task);
                assertNotNull(task.id());
                assertNotNull(task.project());
                assertEquals(project.id(), task.project());
                assertNotNull(task.user());
                assertEquals(project.user(), task.user());
            });
        });
    }

    @Test
    @Order(7)
    @DisplayName("Тестируем получение всех проектов пользователя без задач")
    public void getUserProjects() {
        ResponseEntity<List<UserRD>> responseGetAll = rest.exchange(
                USER_URL,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {
                });
        assertNotNull(responseGetAll);
        assertNotNull(responseGetAll.getStatusCode());
        assertEquals(HttpStatus.OK, responseGetAll.getStatusCode());

        List<UserRD> users = responseGetAll.getBody();
        assertNotNull(users);
        assertFalse(users.isEmpty());

        UserRD user = users.getFirst();
        assertNotNull(user);

        String userId = user.id();
        assertNotNull(userId);

        ResponseEntity<List<ProjectRD>> response = rest.exchange(
                USER_URL + "/" + userId + "/projects",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {
                });
        assertNotNull(response);
        assertNotNull(response.getStatusCode());
        assertEquals(HttpStatus.OK, response.getStatusCode());

        List<ProjectRD> projects = response.getBody();
        assertNotNull(projects);

        projects.forEach(project -> {
            assertNotNull(project);
            assertNotNull(project.id());
            assertNotNull(project.user());
            assertEquals(project.user(), user.id());
            assertNull(project.tasks());
        });
    }

    @Test
    @Order(8)
    @DisplayName("Тестируем получение всех проектов пользователя с задачами")
    public void getUserProjectsWithTasks() {
        ResponseEntity<List<UserRD>> responseGetAll = rest.exchange(
                USER_URL,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {
                });
        assertNotNull(responseGetAll);
        assertNotNull(responseGetAll.getStatusCode());
        assertEquals(HttpStatus.OK, responseGetAll.getStatusCode());

        List<UserRD> users = responseGetAll.getBody();
        assertNotNull(users);
        assertFalse(users.isEmpty());

        UserRD user = users.getFirst();
        assertNotNull(user);

        String userId = user.id();
        assertNotNull(userId);

        ResponseEntity<List<ProjectRD>> response = rest.exchange(
                USER_URL + "/" + userId + "/projects?tasks",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {
                });
        assertNotNull(response);
        assertNotNull(response.getStatusCode());
        assertEquals(HttpStatus.OK, response.getStatusCode());

        List<ProjectRD> projects = response.getBody();
        assertNotNull(projects);

        projects.forEach(project -> {
            assertNotNull(project);
            assertNotNull(project.id());
            assertNotNull(project.user());
            assertEquals(project.user(), user.id());
            assertNotNull(project.tasks());
            project.tasks().forEach(task -> {
                assertNotNull(task);
                assertNotNull(task.id());
                assertNotNull(task.project());
                assertEquals(project.id(), task.project());
                assertNotNull(task.user());
                assertEquals(project.user(), task.user());
            });
        });
    }

    @Test
    @Order(9)
    @DisplayName("Тестируем создание пользователя")
    public void createUser() {
        String username = "username";
        String password = "password";
        String gender = "male";
        String phone = "123456789";
        String email = "mail@example.com";
        UserWD user = new UserWD(username, password, gender, phone, email);

        ResponseEntity<UserRD> response = rest.exchange(
                USER_URL,
                HttpMethod.POST,
                new HttpEntity<>(user),
                UserRD.class);
        assertNotNull(response);
        assertNotNull(response.getStatusCode());
        assertEquals(HttpStatus.CREATED, response.getStatusCode());

        HttpHeaders headers = response.getHeaders();
        assertNotNull(headers);
        List<String> locations = headers.get("Location");
        assertNotNull(locations);
        String locationStr = locations.getFirst();
        assertNotNull(locationStr);
        URI location = URI.create(locationStr);

        UserRD savedUser = response.getBody();
        assertNotNull(savedUser);
        assertNotNull(savedUser.id());
        UUID userId = UUID.fromString(savedUser.id());
        assertNotNull(userId);
        assertNotNull(savedUser.username());
        assertEquals(username, savedUser.username());
        assertNotNull(savedUser.gender());
        assertEquals(gender, savedUser.gender());
        assertNotNull(savedUser.phone());
        assertEquals(phone, savedUser.phone());
        assertNotNull(savedUser.email());
        assertEquals(email, savedUser.email());
        assertNull(savedUser.projects());

        assertTrue(locationStr.contains(savedUser.id()));

        ResponseEntity<UserRD> userByIdResponse = rest.getForEntity(location, UserRD.class);
        assertNotNull(userByIdResponse);
        assertNotNull(userByIdResponse.getStatusCode());
        assertEquals(HttpStatus.OK, userByIdResponse.getStatusCode());

        UserRD userById = userByIdResponse.getBody();
        assertNotNull(userById);
        assertEquals(savedUser, userById);

        User userEntity = repository.findById(userId).orElse(null);
        assertNotNull(userEntity);
        assertNotNull(userEntity.getPassword());
        assertNotEquals(password, userEntity.getPassword());
        assertTrue(encoder.matches(password, userEntity.getPassword()));
    }

    @Test
    @Order(10)
    @DisplayName("Тестируем создание проекта")
    public void createProject() {
        ResponseEntity<List<UserRD>> responseGetAll = rest.exchange(
                USER_URL,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {
                });
        assertNotNull(responseGetAll);
        assertNotNull(responseGetAll.getStatusCode());
        assertEquals(HttpStatus.OK, responseGetAll.getStatusCode());

        List<UserRD> users = responseGetAll.getBody();
        assertNotNull(users);
        assertFalse(users.isEmpty());

        UserRD user = users.getFirst();
        assertNotNull(user);

        String userId = user.id();
        assertNotNull(userId);

        String name = "name";
        String description = "description";
        ProjectWD project = new ProjectWD(name, description);

        ResponseEntity<ProjectRD> response = rest.exchange(
                USER_URL + "/" + userId + "/projects",
                HttpMethod.POST,
                new HttpEntity<>(project),
                ProjectRD.class);
        assertNotNull(response);
        assertNotNull(response.getStatusCode());
        assertEquals(HttpStatus.CREATED, response.getStatusCode());

        HttpHeaders headers = response.getHeaders();
        assertNotNull(headers);
        List<String> locations = headers.get("Location");
        assertNotNull(locations);
        String locationStr = locations.getFirst();
        assertNotNull(locationStr);
        URI location = URI.create(locationStr);

        ProjectRD savedProject = response.getBody();
        assertNotNull(savedProject);
        assertNotNull(savedProject.id());
        assertNotNull(savedProject.name());
        assertEquals(name, savedProject.name());
        assertNotNull(savedProject.description());
        assertEquals(description, savedProject.description());
        assertNotNull(savedProject.user());
        assertEquals(savedProject.user(), userId);
        assertNull(savedProject.tasks());

        assertTrue(locationStr.contains(savedProject.id().toString()));

        ResponseEntity<ProjectRD> projectByIdResponse = rest.getForEntity(location, ProjectRD.class);
        assertNotNull(projectByIdResponse);
        assertNotNull(projectByIdResponse.getStatusCode());
        assertEquals(HttpStatus.OK, projectByIdResponse.getStatusCode());

        ProjectRD projectById = projectByIdResponse.getBody();
        assertNotNull(projectById);
        assertEquals(savedProject, projectById);
    }

    @Test
    @Order(11)
    @DisplayName("Тестируем обновление пользователя")
    public void updateUser() {
        ResponseEntity<List<UserRD>> responseGetAll = rest.exchange(
                USER_URL,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {
                });
        assertNotNull(responseGetAll);
        assertNotNull(responseGetAll.getStatusCode());
        assertEquals(HttpStatus.OK, responseGetAll.getStatusCode());

        List<UserRD> users = responseGetAll.getBody();
        assertNotNull(users);
        assertFalse(users.isEmpty());

        UserRD user = users.getFirst();
        assertNotNull(user);

        String userId = user.id();
        assertNotNull(userId);

        String username = "username";
        String password = "password";
        String gender = "male";
        String phone = "123456789";
        String email = "mail@example.com";
        UserWD newUser = new UserWD(username, password, gender, phone, email);

        ResponseEntity<UserRD> response = rest.exchange(
                USER_URL + "/" + userId,
                HttpMethod.PUT,
                new HttpEntity<>(newUser),
                UserRD.class);
        assertNotNull(response);
        assertNotNull(response.getStatusCode());
        assertEquals(HttpStatus.OK, response.getStatusCode());

        UserRD updatedUser = response.getBody();
        assertNotNull(updatedUser);
        assertNotNull(updatedUser.id());
        assertNotNull(updatedUser.username());
        assertEquals(username, updatedUser.username());
        assertNotNull(updatedUser.gender());
        assertEquals(gender, updatedUser.gender());
        assertNotNull(updatedUser.phone());
        assertEquals(phone, updatedUser.phone());
        assertNotNull(updatedUser.email());
        assertEquals(email, updatedUser.email());
        assertNull(updatedUser.projects());
    }

    @Test
    @Order(12)
    @DisplayName("Тестируем патчинг пользователя")
    public void patchUser() {
        ResponseEntity<List<UserRD>> responseGetAll = rest.exchange(
                USER_URL,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {
                });
        assertNotNull(responseGetAll);
        assertNotNull(responseGetAll.getStatusCode());
        assertEquals(HttpStatus.OK, responseGetAll.getStatusCode());

        List<UserRD> users = responseGetAll.getBody();
        assertNotNull(users);
        assertFalse(users.isEmpty());

        UserRD user = users.getFirst();
        assertNotNull(user);

        String userId = user.id();
        assertNotNull(userId);

        String username = "username";
        String email = "mail@example.com";
        UserWD newUser = UserWD.builder()
                .username(username)
                .email(email)
                .build();

        ResponseEntity<UserRD> response = rest.exchange(
                USER_URL + "/" + userId,
                HttpMethod.PATCH,
                new HttpEntity<>(newUser),
                UserRD.class);
        assertNotNull(response);
        assertNotNull(response.getStatusCode());
        assertEquals(HttpStatus.OK, response.getStatusCode());

        UserRD updatedUser = response.getBody();
        assertNotNull(updatedUser);
        assertNotNull(updatedUser.id());
        assertNotNull(updatedUser.username());
        assertEquals(username, updatedUser.username());
        assertEquals(user.gender(), updatedUser.gender());
        assertEquals(user.phone(), updatedUser.phone());
        assertNotNull(updatedUser.email());
        assertEquals(email, updatedUser.email());
        assertNull(updatedUser.projects());
    }

    @Test
    @Order(13)
    @DisplayName("Тестируем удаление проектов пользователя")
    public void deleteUserProjects() {
        ResponseEntity<List<UserRD>> responseGetAll = rest.exchange(
                USER_URL,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {
                });
        assertNotNull(responseGetAll);
        assertNotNull(responseGetAll.getStatusCode());
        assertEquals(HttpStatus.OK, responseGetAll.getStatusCode());

        List<UserRD> users = responseGetAll.getBody();
        assertNotNull(users);
        assertFalse(users.isEmpty());

        UserRD user = users.getFirst();
        assertNotNull(user);

        String userId = user.id();
        assertNotNull(userId);

        ResponseEntity<Void> response = rest.exchange(
                USER_URL + "/" + userId + "/projects",
                HttpMethod.DELETE,
                null,
                Void.class);
        assertNotNull(response);
        assertNotNull(response.getStatusCode());
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());

        ResponseEntity<List<ProjectRD>> responseGetUserProjects = rest.exchange(
                USER_URL + "/" + userId + "/projects",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {
                });
        assertNotNull(responseGetUserProjects);
        assertNotNull(responseGetUserProjects.getStatusCode());
        assertEquals(HttpStatus.OK, responseGetUserProjects.getStatusCode());

        List<ProjectRD> projects = responseGetUserProjects.getBody();
        assertNotNull(projects);
        assertTrue(projects.isEmpty());
    }

    @Test
    @Order(14)
    @DisplayName("Тестируем удаление пользователя")
    public void deleteUser() {
        ResponseEntity<List<UserRD>> responseGetAll = rest.exchange(
                USER_URL,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {
                });
        assertNotNull(responseGetAll);
        assertNotNull(responseGetAll.getStatusCode());
        assertEquals(HttpStatus.OK, responseGetAll.getStatusCode());

        List<UserRD> users = responseGetAll.getBody();
        assertNotNull(users);
        assertFalse(users.isEmpty());

        UserRD user = users.getFirst();
        assertNotNull(user);

        String userId = user.id();
        assertNotNull(userId);

        ResponseEntity<Void> response = rest.exchange(
                USER_URL + "/" + userId,
                HttpMethod.DELETE,
                null,
                Void.class);
        assertNotNull(response);
        assertNotNull(response.getStatusCode());
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());

        assertThrows(HttpClientErrorException.NotFound.class, () ->
                rest.getForObject(USER_URL + "/" + userId, UserRD.class));
    }
}
