package a.slelin.work.task.management.auth.test;

import a.slelin.work.task.management.core.dto.SheetDto;
import a.slelin.work.task.management.core.dto.auth.*;
import a.slelin.work.task.management.core.exception.ErrorResponse;
import a.slelin.work.task.management.core.util.filter.Filter;
import a.slelin.work.task.management.core.util.filter.FilterChain;
import a.slelin.work.task.management.core.util.filter.Operation;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
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
import java.util.UUID;

import static org.assertj.core.api.Fail.fail;
import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DisplayName("Тест контроллера, отвечающего за управление пользователями администратором")
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class UserAdminControllerTest {

    @Autowired
    private RestTemplate rest;

    @LocalServerPort
    private int port;

    private String baseUrl;

    private String apiUrl;

    @BeforeEach
    void beforeEach() {
        baseUrl = "http://localhost:%d".formatted(port);
        apiUrl = baseUrl + "/api/admin/users";
    }

    @Test
    @Order(1)
    @DirtiesContext
    @DisplayName("Не авторизованный пользователь не может обращаться к пользователям")
    public void test1() {

        /*
         * Пытаемся получить доступ к пользователям.
         * */

        //noinspection CatchMayIgnoreException
        try {
            rest.exchange(
                    apiUrl,
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
            assertEquals(apiUrl, errorResponse.path());
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
    @DisplayName("Обычный пользователь не может обращаться к пользователям")
    public void test2() {

        /*
         * Логинимся под обычным пользователем.
         * */

        LoginRequest login = new LoginRequest("alex_petrov", "password");

        ResponseEntity<JwtResponse> response = rest.exchange(
                baseUrl + "/auth/login",
                HttpMethod.POST,
                new HttpEntity<>(login),
                JwtResponse.class);
        assertNotNull(response);
        assertNotNull(response.getStatusCode());
        assertEquals(HttpStatus.OK, response.getStatusCode());

        JwtResponse jwtResponse = response.getBody();
        assertNotNull(jwtResponse);
        assertNotNull(jwtResponse.accessToken());
        assertNotNull(jwtResponse.refreshToken());

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(jwtResponse.accessToken());

        /*
         * Пытаемся получить доступ к пользователям.
         * */

        //noinspection CatchMayIgnoreException
        try {
            rest.exchange(
                    apiUrl,
                    HttpMethod.GET,
                    new HttpEntity<>(headers),
                    new ParameterizedTypeReference<>() {
                    }
            );
            fail("Should throw HttpClientErrorException.Forbidden");

        } catch (HttpClientErrorException.Forbidden e) {
            assertNotNull(e);
            assertNotNull(e.getStatusCode());
            assertEquals(HttpStatus.FORBIDDEN, e.getStatusCode());

            ErrorResponse errorResponse = e.getResponseBodyAs(ErrorResponse.class);
            assertNotNull(errorResponse);
            assertNotNull(errorResponse.path());
            assertEquals(apiUrl, errorResponse.path());
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
    @Order(3)
    @DirtiesContext
    @DisplayName("Администратор может обращаться к пользователям")
    public void test3() {

        /*
         * Логинимся под администратором.
         * */

        LoginRequest login = new LoginRequest("admin", "password");

        ResponseEntity<JwtResponse> response = rest.exchange(
                baseUrl + "/auth/login",
                HttpMethod.POST,
                new HttpEntity<>(login),
                JwtResponse.class);
        assertNotNull(response);
        assertNotNull(response.getStatusCode());
        assertEquals(HttpStatus.OK, response.getStatusCode());

        JwtResponse jwtResponse = response.getBody();
        assertNotNull(jwtResponse);
        assertNotNull(jwtResponse.accessToken());
        assertNotNull(jwtResponse.refreshToken());

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(jwtResponse.accessToken());

        /*
         * Получаем всех пользователей.
         * */

        ResponseEntity<SheetDto<UserRD>> response2 = rest.exchange(
                apiUrl,
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
    @DisplayName("Тестируем получение всех пользователей")
    public void test4() {

        /*
         * Логинимся под администратором.
         * */

        LoginRequest login = new LoginRequest("admin", "password");

        ResponseEntity<JwtResponse> response = rest.exchange(
                baseUrl + "/auth/login",
                HttpMethod.POST,
                new HttpEntity<>(login),
                JwtResponse.class);
        assertNotNull(response);
        assertNotNull(response.getStatusCode());
        assertEquals(HttpStatus.OK, response.getStatusCode());

        JwtResponse jwtResponse = response.getBody();
        assertNotNull(jwtResponse);
        assertNotNull(jwtResponse.accessToken());
        assertNotNull(jwtResponse.refreshToken());

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(jwtResponse.accessToken());

        /*
         * Получаем всех пользователей.
         * */

        ResponseEntity<SheetDto<UserRD>> response2 = rest.exchange(
                apiUrl,
                HttpMethod.GET,
                new HttpEntity<>(headers),
                new ParameterizedTypeReference<>() {
                }
        );
        assertNotNull(response2);
        assertNotNull(response2.getStatusCode());
        assertEquals(HttpStatus.OK, response2.getStatusCode());

        SheetDto<UserRD> sheet = response2.getBody();
        assertNotNull(sheet);
        assertNotNull(sheet.page());

        List<UserRD> users = sheet.content();
        assertNotNull(users);
        assertFalse(users.isEmpty());

        for (UserRD user : users) {
            assertNotNull(user);
            assertNotNull(user.id());
            assertNotNull(user.username());
        }
    }

    @Test
    @Order(5)
    @DirtiesContext
    @DisplayName("Тестируем получение пользователя по идентификатору")
    public void test5() {

        /*
         * Логинимся под администратором.
         * */

        LoginRequest login = new LoginRequest("admin", "password");

        ResponseEntity<JwtResponse> response = rest.exchange(
                baseUrl + "/auth/login",
                HttpMethod.POST,
                new HttpEntity<>(login),
                JwtResponse.class);
        assertNotNull(response);
        assertNotNull(response.getStatusCode());
        assertEquals(HttpStatus.OK, response.getStatusCode());

        JwtResponse jwtResponse = response.getBody();
        assertNotNull(jwtResponse);
        assertNotNull(jwtResponse.accessToken());
        assertNotNull(jwtResponse.refreshToken());

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(jwtResponse.accessToken());

        /*
         * Получаем идентификатор пользователя.
         * */

        ResponseEntity<SheetDto<UserRD>> response2 = rest.exchange(
                apiUrl,
                HttpMethod.GET,
                new HttpEntity<>(headers),
                new ParameterizedTypeReference<>() {
                }
        );
        assertNotNull(response2);
        assertNotNull(response2.getStatusCode());
        assertEquals(HttpStatus.OK, response2.getStatusCode());

        SheetDto<UserRD> sheet = response2.getBody();
        assertNotNull(sheet);
        assertNotNull(sheet.page());

        List<UserRD> users = sheet.content();
        assertNotNull(users);
        assertFalse(users.isEmpty());

        UserRD user = users.getFirst();
        assertNotNull(user);
        String userId = user.id();
        assertNotNull(userId);

        /*
         * Получаем пользователя по идентификатору.
         * */

        ResponseEntity<UserRD> response3 = rest.exchange(
                apiUrl + "/{id}",
                HttpMethod.GET,
                new HttpEntity<>(headers),
                UserRD.class,
                userId
        );
        assertNotNull(response3);
        assertNotNull(response3.getStatusCode());
        assertEquals(HttpStatus.OK, response3.getStatusCode());

        UserRD user2 = response3.getBody();
        assertNotNull(user2);
        assertEquals(user, user2);
    }

    @Test
    @Order(6)
    @DirtiesContext
    @DisplayName("Тестируем получение пользователя по плохому идентификатору")
    public void test6() {

        /*
         * Логинимся под администратором.
         * */

        LoginRequest login = new LoginRequest("admin", "password");

        ResponseEntity<JwtResponse> response = rest.exchange(
                baseUrl + "/auth/login",
                HttpMethod.POST,
                new HttpEntity<>(login),
                JwtResponse.class);
        assertNotNull(response);
        assertNotNull(response.getStatusCode());
        assertEquals(HttpStatus.OK, response.getStatusCode());

        JwtResponse jwtResponse = response.getBody();
        assertNotNull(jwtResponse);
        assertNotNull(jwtResponse.accessToken());
        assertNotNull(jwtResponse.refreshToken());

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(jwtResponse.accessToken());

        /*
         * Пытаемся получить доступ к пользователю по плохому идентификатору.
         * */

        String userId = UUID.randomUUID().toString();

        //noinspection CatchMayIgnoreException
        try {
            rest.exchange(
                    apiUrl + "/{id}",
                    HttpMethod.GET,
                    new HttpEntity<>(headers),
                    UserRD.class,
                    userId

            );
            fail("Should throw HttpClientErrorException.NotFound");

        } catch (HttpClientErrorException.NotFound e) {
            assertNotNull(e);
            assertNotNull(e.getStatusCode());
            assertEquals(HttpStatus.NOT_FOUND, e.getStatusCode());

            ErrorResponse errorResponse = e.getResponseBodyAs(ErrorResponse.class);
            assertNotNull(errorResponse);
            assertNotNull(errorResponse.path());
            assertEquals(apiUrl + "/" + userId, errorResponse.path());
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
    @DisplayName("Тестируем получение пользователя по имени в системе")
    public void test7() {

        /*
         * Логинимся под администратором.
         * */

        LoginRequest login = new LoginRequest("admin", "password");

        ResponseEntity<JwtResponse> response = rest.exchange(
                baseUrl + "/auth/login",
                HttpMethod.POST,
                new HttpEntity<>(login),
                JwtResponse.class);
        assertNotNull(response);
        assertNotNull(response.getStatusCode());
        assertEquals(HttpStatus.OK, response.getStatusCode());

        JwtResponse jwtResponse = response.getBody();
        assertNotNull(jwtResponse);
        assertNotNull(jwtResponse.accessToken());
        assertNotNull(jwtResponse.refreshToken());

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(jwtResponse.accessToken());

        /*
         * Получаем пользователя по имени.
         * */

        ResponseEntity<UserRD> response2 = rest.exchange(
                apiUrl + "/factor/{name}",
                HttpMethod.GET,
                new HttpEntity<>(headers),
                UserRD.class,
                "admin"
        );
        assertNotNull(response2);
        assertNotNull(response2.getStatusCode());
        assertEquals(HttpStatus.OK, response2.getStatusCode());

        UserRD admin = response2.getBody();
        assertNotNull(admin);
        assertNotNull(admin.id());
        assertNotNull(admin.username());
        assertEquals("admin", admin.username());
    }

    @Test
    @Order(8)
    @DirtiesContext
    @DisplayName("Тестируем получение пользователя по телефону")
    public void test8() {

        /*
         * Логинимся под администратором.
         * */

        LoginRequest login = new LoginRequest("admin", "password");

        ResponseEntity<JwtResponse> response = rest.exchange(
                baseUrl + "/auth/login",
                HttpMethod.POST,
                new HttpEntity<>(login),
                JwtResponse.class);
        assertNotNull(response);
        assertNotNull(response.getStatusCode());
        assertEquals(HttpStatus.OK, response.getStatusCode());

        JwtResponse jwtResponse = response.getBody();
        assertNotNull(jwtResponse);
        assertNotNull(jwtResponse.accessToken());
        assertNotNull(jwtResponse.refreshToken());

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(jwtResponse.accessToken());

        /*
         * Получаем пользователя.
         * */

        ResponseEntity<SheetDto<UserRD>> response2 = rest.exchange(
                apiUrl,
                HttpMethod.GET,
                new HttpEntity<>(headers),
                new ParameterizedTypeReference<>() {
                }
        );
        assertNotNull(response2);
        assertNotNull(response2.getStatusCode());
        assertEquals(HttpStatus.OK, response2.getStatusCode());

        SheetDto<UserRD> sheet = response2.getBody();
        assertNotNull(sheet);
        assertNotNull(sheet.page());

        List<UserRD> users = sheet.content();
        assertNotNull(users);
        assertFalse(users.isEmpty());

        UserRD user = null;
        for (UserRD userRD : users) {
            assertNotNull(userRD);
            if (userRD.phone() != null) {
                user = userRD;
                break;
            }
        }
        assertNotNull(user);
        String phone = user.phone();
        assertNotNull(phone);

        /*
         * Получаем пользователя по телефону.
         * */

        ResponseEntity<UserRD> response3 = rest.exchange(
                apiUrl + "/factor/{phone}",
                HttpMethod.GET,
                new HttpEntity<>(headers),
                UserRD.class,
                phone
        );
        assertNotNull(response3);
        assertNotNull(response3.getStatusCode());
        assertEquals(HttpStatus.OK, response3.getStatusCode());

        UserRD user2 = response3.getBody();
        assertNotNull(user2);
        assertEquals(user, user2);
    }

    @Test
    @Order(9)
    @DirtiesContext
    @DisplayName("Тестируем получение пользователя по электронной почте")
    public void test9() {

        /*
         * Логинимся под администратором.
         * */

        LoginRequest login = new LoginRequest("admin", "password");

        ResponseEntity<JwtResponse> response = rest.exchange(
                baseUrl + "/auth/login",
                HttpMethod.POST,
                new HttpEntity<>(login),
                JwtResponse.class);
        assertNotNull(response);
        assertNotNull(response.getStatusCode());
        assertEquals(HttpStatus.OK, response.getStatusCode());

        JwtResponse jwtResponse = response.getBody();
        assertNotNull(jwtResponse);
        assertNotNull(jwtResponse.accessToken());
        assertNotNull(jwtResponse.refreshToken());

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(jwtResponse.accessToken());

        /*
         * Получаем пользователя.
         * */

        ResponseEntity<SheetDto<UserRD>> response2 = rest.exchange(
                apiUrl,
                HttpMethod.GET,
                new HttpEntity<>(headers),
                new ParameterizedTypeReference<>() {
                }
        );
        assertNotNull(response2);
        assertNotNull(response2.getStatusCode());
        assertEquals(HttpStatus.OK, response2.getStatusCode());

        SheetDto<UserRD> sheet = response2.getBody();
        assertNotNull(sheet);
        assertNotNull(sheet.page());

        List<UserRD> users = sheet.content();
        assertNotNull(users);
        assertFalse(users.isEmpty());

        UserRD user = null;
        for (UserRD userRD : users) {
            assertNotNull(userRD);
            if (userRD.email() != null) {
                user = userRD;
                break;
            }
        }
        assertNotNull(user);
        String email = user.phone();
        assertNotNull(email);

        /*
         * Получаем пользователя по почте.
         * */

        ResponseEntity<UserRD> response3 = rest.exchange(
                apiUrl + "/factor/{email}",
                HttpMethod.GET,
                new HttpEntity<>(headers),
                UserRD.class,
                email
        );
        assertNotNull(response3);
        assertNotNull(response3.getStatusCode());
        assertEquals(HttpStatus.OK, response3.getStatusCode());

        UserRD user2 = response3.getBody();
        assertNotNull(user2);
        assertEquals(user, user2);
    }

    @Test
    @Order(10)
    @DirtiesContext
    @DisplayName("Тестируем получение всех пользователей по фильтру")
    public void test10() {

        /*
         * Логинимся под администратором.
         * */

        LoginRequest login = new LoginRequest("admin", "password");

        ResponseEntity<JwtResponse> response = rest.exchange(
                baseUrl + "/auth/login",
                HttpMethod.POST,
                new HttpEntity<>(login),
                JwtResponse.class);
        assertNotNull(response);
        assertNotNull(response.getStatusCode());
        assertEquals(HttpStatus.OK, response.getStatusCode());

        JwtResponse jwtResponse = response.getBody();
        assertNotNull(jwtResponse);
        assertNotNull(jwtResponse.accessToken());
        assertNotNull(jwtResponse.refreshToken());

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(jwtResponse.accessToken());

        /*
         * Получаем всех пользователей по фильтру.
         * */

        FilterChain filters = FilterChain
                .empty()
                .add(Filter.of("phone", Operation.IS_NOT_NULL));

        ResponseEntity<SheetDto<UserRD>> response2 = rest.exchange(
                apiUrl + "/search",
                HttpMethod.POST,
                new HttpEntity<>(filters, headers),
                new ParameterizedTypeReference<>() {
                }
        );
        assertNotNull(response2);
        assertNotNull(response2.getStatusCode());
        assertEquals(HttpStatus.OK, response2.getStatusCode());

        SheetDto<UserRD> sheet = response2.getBody();
        assertNotNull(sheet);
        assertNotNull(sheet.page());

        List<UserRD> users = sheet.content();
        assertNotNull(users);
        assertFalse(users.isEmpty());

        for (UserRD user : users) {
            assertNotNull(user);
            assertNotNull(user.id());
            assertNotNull(user.username());
            assertNotNull(user.phone());
        }
    }

    @Test
    @Order(11)
    @DirtiesContext
    @DisplayName("Тестируем получение всех пользователей по плохому фильтру")
    public void test11() {

        /*
         * Логинимся под администратором.
         * */

        LoginRequest login = new LoginRequest("admin", "password");

        ResponseEntity<JwtResponse> response = rest.exchange(
                baseUrl + "/auth/login",
                HttpMethod.POST,
                new HttpEntity<>(login),
                JwtResponse.class);
        assertNotNull(response);
        assertNotNull(response.getStatusCode());
        assertEquals(HttpStatus.OK, response.getStatusCode());

        JwtResponse jwtResponse = response.getBody();
        assertNotNull(jwtResponse);
        assertNotNull(jwtResponse.accessToken());
        assertNotNull(jwtResponse.refreshToken());

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(jwtResponse.accessToken());

        /*
         * Пытаемся получить пользователей по плохому фильтру.
         * */

        FilterChain filters = FilterChain
                .empty()
                .add(Filter.of("invalid", Operation.IS_NOT_NULL));

        //noinspection CatchMayIgnoreException
        try {
            rest.exchange(
                    apiUrl + "/search",
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
            assertEquals(apiUrl + "/search", errorResponse.path());
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
    @Order(12)
    @DirtiesContext
    @DisplayName("Тестируем создание нового пользователя")
    public void test12() {

        /*
         * Логинимся под администратором.
         * */

        LoginRequest login = new LoginRequest("admin", "password");

        ResponseEntity<JwtResponse> response = rest.exchange(
                baseUrl + "/auth/login",
                HttpMethod.POST,
                new HttpEntity<>(login),
                JwtResponse.class);
        assertNotNull(response);
        assertNotNull(response.getStatusCode());
        assertEquals(HttpStatus.OK, response.getStatusCode());

        JwtResponse jwtResponse = response.getBody();
        assertNotNull(jwtResponse);
        assertNotNull(jwtResponse.accessToken());
        assertNotNull(jwtResponse.refreshToken());

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(jwtResponse.accessToken());

        /*
         * Создаем нового пользователя.
         * */

        UserWD newUser = UserWD.builder()
                .username("username")
                .password("password")
                .gender("male")
                .phone("+76848478484")
                .email("email@google.com")
                .build();

        ResponseEntity<UserRD> response2 = rest.exchange(
                apiUrl,
                HttpMethod.POST,
                new HttpEntity<>(newUser, headers),
                UserRD.class,
                "admin"
        );
        assertNotNull(response2);
        assertNotNull(response2.getStatusCode());
        assertEquals(HttpStatus.CREATED, response2.getStatusCode());

        UserRD user = response2.getBody();
        assertNotNull(user);
        assertNotNull(user.id());
        assertNotNull(user.username());
        assertEquals(newUser.username(), user.username());
        assertNotNull(user.gender());
        assertEquals(newUser.gender(), user.gender());
        assertNotNull(user.phone());
        assertEquals(newUser.phone(), user.phone());
        assertNotNull(user.email());
        assertEquals(newUser.email(), user.email());

        HttpHeaders headers2 = response2.getHeaders();
        assertNotNull(headers2);
        List<String> locations = headers2.get("Location");
        assertNotNull(locations);
        String locationStr = locations.getFirst();
        assertNotNull(locationStr);
        URI location = URI.create(locationStr);
        assertNotNull(location);

        /*
         * Проверяем url для нового пользователя.
         * */

        ResponseEntity<UserRD> response3 = rest.exchange(
                location,
                HttpMethod.GET,
                new HttpEntity<>(headers),
                UserRD.class
        );
        assertNotNull(response3);
        assertNotNull(response3.getStatusCode());
        assertEquals(HttpStatus.OK, response3.getStatusCode());

        UserRD savedUser = response3.getBody();
        assertNotNull(savedUser);
        assertEquals(user, savedUser);
    }

    @Test
    @Order(13)
    @DirtiesContext
    @DisplayName("Тестируем уникальность имени в системе")
    public void test13() {

        /*
         * Логинимся под администратором.
         * */

        LoginRequest login = new LoginRequest("admin", "password");

        ResponseEntity<JwtResponse> response = rest.exchange(
                baseUrl + "/auth/login",
                HttpMethod.POST,
                new HttpEntity<>(login),
                JwtResponse.class);
        assertNotNull(response);
        assertNotNull(response.getStatusCode());
        assertEquals(HttpStatus.OK, response.getStatusCode());

        JwtResponse jwtResponse = response.getBody();
        assertNotNull(jwtResponse);
        assertNotNull(jwtResponse.accessToken());
        assertNotNull(jwtResponse.refreshToken());

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(jwtResponse.accessToken());

        /*
         * Пытаемся создать нового пользователя в существующим именем.
         * */

        UserWD newUser = UserWD.builder()
                .username("admin")
                .password("password")
                .gender("male")
                .phone("+76848478484")
                .email("email@google.com")
                .build();

        //noinspection CatchMayIgnoreException
        try {
            rest.exchange(
                    apiUrl,
                    HttpMethod.POST,
                    new HttpEntity<>(newUser, headers),
                    UserRD.class,
                    "admin"
            );
            fail("Should throw HttpClientErrorException.Conflict");

        } catch (HttpClientErrorException.Conflict e) {
            assertNotNull(e);
            assertNotNull(e.getStatusCode());
            assertEquals(HttpStatus.CONFLICT, e.getStatusCode());

            ErrorResponse errorResponse = e.getResponseBodyAs(ErrorResponse.class);
            assertNotNull(errorResponse);
            assertNotNull(errorResponse.path());
            assertEquals(apiUrl, errorResponse.path());
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
    @Order(14)
    @DirtiesContext
    @DisplayName("Тестируем уникальность телефона в системе")
    public void test14() {

        /*
         * Логинимся под администратором.
         * */

        LoginRequest login = new LoginRequest("admin", "password");

        ResponseEntity<JwtResponse> response = rest.exchange(
                baseUrl + "/auth/login",
                HttpMethod.POST,
                new HttpEntity<>(login),
                JwtResponse.class);
        assertNotNull(response);
        assertNotNull(response.getStatusCode());
        assertEquals(HttpStatus.OK, response.getStatusCode());

        JwtResponse jwtResponse = response.getBody();
        assertNotNull(jwtResponse);
        assertNotNull(jwtResponse.accessToken());
        assertNotNull(jwtResponse.refreshToken());

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(jwtResponse.accessToken());

        /*
         * Пытаемся создать нового пользователя в существующим телефоном.
         * */

        UserWD newUser = UserWD.builder()
                .username("username")
                .password("password")
                .gender("male")
                .phone("+79051234567")
                .email("email@google.com")
                .build();

        //noinspection CatchMayIgnoreException
        try {
            rest.exchange(
                    apiUrl,
                    HttpMethod.POST,
                    new HttpEntity<>(newUser, headers),
                    UserRD.class,
                    "admin"
            );
            fail("Should throw HttpClientErrorException.Conflict");

        } catch (HttpClientErrorException.Conflict e) {
            assertNotNull(e);
            assertNotNull(e.getStatusCode());
            assertEquals(HttpStatus.CONFLICT, e.getStatusCode());

            ErrorResponse errorResponse = e.getResponseBodyAs(ErrorResponse.class);
            assertNotNull(errorResponse);
            assertNotNull(errorResponse.path());
            assertEquals(apiUrl, errorResponse.path());
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
    @DisplayName("Тестируем уникальность электронной почты в системе")
    public void test15() {

        /*
         * Логинимся под администратором.
         * */

        LoginRequest login = new LoginRequest("admin", "password");

        ResponseEntity<JwtResponse> response = rest.exchange(
                baseUrl + "/auth/login",
                HttpMethod.POST,
                new HttpEntity<>(login),
                JwtResponse.class);
        assertNotNull(response);
        assertNotNull(response.getStatusCode());
        assertEquals(HttpStatus.OK, response.getStatusCode());

        JwtResponse jwtResponse = response.getBody();
        assertNotNull(jwtResponse);
        assertNotNull(jwtResponse.accessToken());
        assertNotNull(jwtResponse.refreshToken());

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(jwtResponse.accessToken());

        /*
         * Пытаемся создать нового пользователя в существующей почтой.
         * */

        UserWD newUser = UserWD.builder()
                .username("username")
                .password("password")
                .gender("male")
                .phone("+79051454567")
                .email("alex.petrov@google.com")
                .build();

        //noinspection CatchMayIgnoreException
        try {
            rest.exchange(
                    apiUrl,
                    HttpMethod.POST,
                    new HttpEntity<>(newUser, headers),
                    UserRD.class,
                    "admin"
            );
            fail("Should throw HttpClientErrorException.Conflict");

        } catch (HttpClientErrorException.Conflict e) {
            assertNotNull(e);
            assertNotNull(e.getStatusCode());
            assertEquals(HttpStatus.CONFLICT, e.getStatusCode());

            ErrorResponse errorResponse = e.getResponseBodyAs(ErrorResponse.class);
            assertNotNull(errorResponse);
            assertNotNull(errorResponse.path());
            assertEquals(apiUrl, errorResponse.path());
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
    @Order(16)
    @DirtiesContext
    @DisplayName("Тестируем патчинг обычного пользователя")
    public void test16() {

        /*
         * Логинимся под администратором.
         * */

        LoginRequest login = new LoginRequest("admin", "password");

        ResponseEntity<JwtResponse> response = rest.exchange(
                baseUrl + "/auth/login",
                HttpMethod.POST,
                new HttpEntity<>(login),
                JwtResponse.class);
        assertNotNull(response);
        assertNotNull(response.getStatusCode());
        assertEquals(HttpStatus.OK, response.getStatusCode());

        JwtResponse jwtResponse = response.getBody();
        assertNotNull(jwtResponse);
        assertNotNull(jwtResponse.accessToken());
        assertNotNull(jwtResponse.refreshToken());

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(jwtResponse.accessToken());

        /*
         * Получаем пользователя.
         * */

        ResponseEntity<SheetDto<UserRD>> response2 = rest.exchange(
                apiUrl,
                HttpMethod.GET,
                new HttpEntity<>(headers),
                new ParameterizedTypeReference<>() {
                }
        );
        assertNotNull(response2);
        assertNotNull(response2.getBody());
        assertEquals(HttpStatus.OK, response2.getStatusCode());

        SheetDto<UserRD> sheet = response2.getBody();
        assertNotNull(sheet);
        assertNotNull(sheet.page());

        List<UserRD> users = sheet.content();
        assertNotNull(users);
        assertFalse(users.isEmpty());

        UserRD user = null;
        for (UserRD userRD : users) {
            assertNotNull(userRD);
            assertNotNull(userRD.id());
            assertNotNull(userRD.username());

            if (!userRD.username().contains("admin")) {
                user = userRD;
            }
        }
        assertNotNull(user);
        String userId = user.id();
        assertNotNull(userId);

        /*
         * Обновляем пользователя.
         * */

        UserWD ptcUser = UserWD.builder()
                .username("username")
                .email("email@google.com")
                .build();

        ResponseEntity<UserRD> response3 = rest.exchange(
                apiUrl + "/{id}",
                HttpMethod.PATCH,
                new HttpEntity<>(ptcUser, headers),
                UserRD.class,
                userId
        );
        assertNotNull(response3);
        assertNotNull(response3.getStatusCode());
        assertEquals(HttpStatus.OK, response3.getStatusCode());

        UserRD savedUser = response3.getBody();
        assertNotNull(savedUser);
        assertNotNull(savedUser.id());
        assertNotNull(savedUser.username());
        assertEquals(ptcUser.username(), savedUser.username());
        assertEquals(user.gender(), savedUser.gender());
        assertEquals(user.phone(), savedUser.phone());
        assertNotNull(savedUser.email());
        assertEquals(ptcUser.email(), savedUser.email());
    }

    @Test
    @Order(17)
    @DirtiesContext
    @DisplayName("Тестируем патчинг обычного пользователя по плохому идентификатору")
    public void test17() {

        /*
         * Логинимся под администратором.
         * */

        LoginRequest login = new LoginRequest("admin", "password");

        ResponseEntity<JwtResponse> response = rest.exchange(
                baseUrl + "/auth/login",
                HttpMethod.POST,
                new HttpEntity<>(login),
                JwtResponse.class);
        assertNotNull(response);
        assertNotNull(response.getStatusCode());
        assertEquals(HttpStatus.OK, response.getStatusCode());

        JwtResponse jwtResponse = response.getBody();
        assertNotNull(jwtResponse);
        assertNotNull(jwtResponse.accessToken());
        assertNotNull(jwtResponse.refreshToken());

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(jwtResponse.accessToken());

        /*
         * Пытаемся обновить пользователя по плохому идентификатору.
         * */

        String userId = UUID.randomUUID().toString();

        UserWD ptcUser = UserWD.builder()
                .username("username")
                .email("email@google.com")
                .build();

        //noinspection CatchMayIgnoreException
        try {
            rest.exchange(
                    apiUrl + "/{id}",
                    HttpMethod.PATCH,
                    new HttpEntity<>(ptcUser, headers),
                    UserRD.class,
                    userId
            );
            fail("Should throw HttpClientErrorException.NotFound");

        } catch (HttpClientErrorException.NotFound e) {
            assertNotNull(e);
            assertNotNull(e.getStatusCode());
            assertEquals(HttpStatus.NOT_FOUND, e.getStatusCode());

            ErrorResponse errorResponse = e.getResponseBodyAs(ErrorResponse.class);
            assertNotNull(errorResponse);
            assertNotNull(errorResponse.path());
            assertEquals(apiUrl + "/" + userId, errorResponse.path());
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
    @Order(18)
    @DirtiesContext
    @DisplayName("Тестируем патчинг другого администратора")
    public void test18() {

        /*
         * Логинимся под администратором.
         * */

        LoginRequest login = new LoginRequest("admin", "password");

        ResponseEntity<JwtResponse> response = rest.exchange(
                baseUrl + "/auth/login",
                HttpMethod.POST,
                new HttpEntity<>(login),
                JwtResponse.class);
        assertNotNull(response);
        assertNotNull(response.getStatusCode());
        assertEquals(HttpStatus.OK, response.getStatusCode());

        JwtResponse jwtResponse = response.getBody();
        assertNotNull(jwtResponse);
        assertNotNull(jwtResponse.accessToken());
        assertNotNull(jwtResponse.refreshToken());

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(jwtResponse.accessToken());

        /*
         * Логинимся под вторым администратором.
         * */

        LoginRequest login2 = new LoginRequest("admin2", "password");

        ResponseEntity<JwtResponse> response2 = rest.exchange(
                baseUrl + "/auth/login",
                HttpMethod.POST,
                new HttpEntity<>(login2),
                JwtResponse.class);
        assertNotNull(response2);
        assertNotNull(response2.getStatusCode());
        assertEquals(HttpStatus.OK, response2.getStatusCode());

        JwtResponse jwtResponse2 = response2.getBody();
        assertNotNull(jwtResponse2);
        assertNotNull(jwtResponse2.accessToken());
        assertNotNull(jwtResponse2.refreshToken());

        HttpHeaders headers2 = new HttpHeaders();
        headers2.setBearerAuth(jwtResponse2.accessToken());

        /*
         * Получаем идентификатор второго администратора.
         * */

        ResponseEntity<UserRD> response3 = rest.exchange(
                baseUrl + "/api/user",
                HttpMethod.GET,
                new HttpEntity<>(headers2),
                UserRD.class
        );
        assertNotNull(response3);
        assertNotNull(response3.getBody());
        assertEquals(HttpStatus.OK, response3.getStatusCode());

        UserRD admin = response3.getBody();
        assertNotNull(admin);
        String adminId = admin.id();
        assertNotNull(adminId);

        /*
         * Пытаемся обновить второго администратора.
         * */

        UserWD ptcUser = UserWD.builder()
                .username("username")
                .email("email@google.com")
                .build();

        //noinspection CatchMayIgnoreException
        try {
            rest.exchange(
                    apiUrl + "/{id}",
                    HttpMethod.PATCH,
                    new HttpEntity<>(ptcUser, headers),
                    UserRD.class,
                    adminId
            );
            fail("Should throw HttpClientErrorException.UnprocessableContent");

        } catch (HttpClientErrorException.UnprocessableContent e) {
            assertNotNull(e);
            assertNotNull(e.getStatusCode());
            assertEquals(HttpStatus.UNPROCESSABLE_CONTENT, e.getStatusCode());

            ErrorResponse errorResponse = e.getResponseBodyAs(ErrorResponse.class);
            assertNotNull(errorResponse);
            assertNotNull(errorResponse.path());
            assertEquals(apiUrl + "/" + adminId, errorResponse.path());
            assertNotNull(errorResponse.httpMethod());
            assertEquals(HttpMethod.PATCH, errorResponse.httpMethod());
            assertNotNull(errorResponse.httpStatus());
            assertEquals(HttpStatus.UNPROCESSABLE_CONTENT, errorResponse.httpStatus());
            assertNotNull(errorResponse.message());
            assertNotNull(errorResponse.exception());
            assertNotNull(errorResponse.timestamp());

        } catch (Exception e) {
            fail("Should throw HttpClientErrorException.UnprocessableContent, but got " + e.getMessage());
        }
    }

    @Test
    @Order(19)
    @DirtiesContext
    @DisplayName("Тестируем патчинг самого себя")
    public void test19() {

        /*
         * Логинимся под администратором.
         * */

        LoginRequest login = new LoginRequest("admin", "password");

        ResponseEntity<JwtResponse> response = rest.exchange(
                baseUrl + "/auth/login",
                HttpMethod.POST,
                new HttpEntity<>(login),
                JwtResponse.class);
        assertNotNull(response);
        assertNotNull(response.getStatusCode());
        assertEquals(HttpStatus.OK, response.getStatusCode());

        JwtResponse jwtResponse = response.getBody();
        assertNotNull(jwtResponse);
        assertNotNull(jwtResponse.accessToken());
        assertNotNull(jwtResponse.refreshToken());

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(jwtResponse.accessToken());

        /*
         * Получаем идентификатор администратора.
         * */

        ResponseEntity<UserRD> response2 = rest.exchange(
                baseUrl + "/api/user",
                HttpMethod.GET,
                new HttpEntity<>(headers),
                UserRD.class
        );
        assertNotNull(response2);
        assertNotNull(response2.getBody());
        assertEquals(HttpStatus.OK, response2.getStatusCode());

        UserRD admin = response2.getBody();
        assertNotNull(admin);
        String adminId = admin.id();
        assertNotNull(adminId);

        /*
         * Обновляем себя же (администратора)
         * */

        UserWD ptcUser = UserWD.builder()
                .username("username")
                .email("email@google.com")
                .build();

        ResponseEntity<UserRD> response3 = rest.exchange(
                apiUrl + "/{id}",
                HttpMethod.PATCH,
                new HttpEntity<>(ptcUser, headers),
                UserRD.class,
                adminId
        );
        assertNotNull(response3);
        assertNotNull(response3.getStatusCode());
        assertEquals(HttpStatus.OK, response3.getStatusCode());

        UserRD savedUser = response3.getBody();
        assertNotNull(savedUser);
        assertNotNull(savedUser.id());
        assertNotNull(savedUser.username());
        assertEquals(ptcUser.username(), savedUser.username());
        assertEquals(admin.gender(), savedUser.gender());
        assertEquals(admin.phone(), savedUser.phone());
        assertNotNull(savedUser.email());
        assertEquals(ptcUser.email(), savedUser.email());
    }

    @Test
    @Order(20)
    @DirtiesContext
    @DisplayName("Тестируем уникальность имени в системе при патчинге")
    public void test20() {

        /*
         * Логинимся под администратором.
         * */

        LoginRequest login = new LoginRequest("admin", "password");

        ResponseEntity<JwtResponse> response = rest.exchange(
                baseUrl + "/auth/login",
                HttpMethod.POST,
                new HttpEntity<>(login),
                JwtResponse.class);
        assertNotNull(response);
        assertNotNull(response.getStatusCode());
        assertEquals(HttpStatus.OK, response.getStatusCode());

        JwtResponse jwtResponse = response.getBody();
        assertNotNull(jwtResponse);
        assertNotNull(jwtResponse.accessToken());
        assertNotNull(jwtResponse.refreshToken());

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(jwtResponse.accessToken());

        /*
         * Получаем пользователя.
         * */

        ResponseEntity<SheetDto<UserRD>> response2 = rest.exchange(
                apiUrl,
                HttpMethod.GET,
                new HttpEntity<>(headers),
                new ParameterizedTypeReference<>() {
                }
        );
        assertNotNull(response2);
        assertNotNull(response2.getBody());
        assertEquals(HttpStatus.OK, response2.getStatusCode());

        SheetDto<UserRD> sheet = response2.getBody();
        assertNotNull(sheet);
        assertNotNull(sheet.page());

        List<UserRD> users = sheet.content();
        assertNotNull(users);
        assertFalse(users.isEmpty());

        UserRD user = null;
        for (UserRD userRD : users) {
            assertNotNull(userRD);
            assertNotNull(userRD.id());
            assertNotNull(userRD.username());

            if (!userRD.username().contains("admin")) {
                user = userRD;
            }
        }
        assertNotNull(user);
        String userId = user.id();
        assertNotNull(userId);

        /*
         * Пытаемся обновить пользователя с уже существующим именем.
         * */

        UserWD ptcUser = UserWD.builder()
                .username("alex_petrov")
                .build();

        //noinspection CatchMayIgnoreException
        try {
            rest.exchange(
                    apiUrl + "/{id}",
                    HttpMethod.PATCH,
                    new HttpEntity<>(ptcUser, headers),
                    UserRD.class,
                    userId
            );
            fail("Should throw HttpClientErrorException.Conflict");

        } catch (HttpClientErrorException.Conflict e) {
            assertNotNull(e);
            assertNotNull(e.getStatusCode());
            assertEquals(HttpStatus.CONFLICT, e.getStatusCode());

            ErrorResponse errorResponse = e.getResponseBodyAs(ErrorResponse.class);
            assertNotNull(errorResponse);
            assertNotNull(errorResponse.path());
            assertEquals(apiUrl + "/" + userId, errorResponse.path());
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
    @Order(21)
    @DirtiesContext
    @DisplayName("Тестируем уникальность телефона в системе при патчинге")
    public void test21() {

        /*
         * Логинимся под администратором.
         * */

        LoginRequest login = new LoginRequest("admin", "password");

        ResponseEntity<JwtResponse> response = rest.exchange(
                baseUrl + "/auth/login",
                HttpMethod.POST,
                new HttpEntity<>(login),
                JwtResponse.class);
        assertNotNull(response);
        assertNotNull(response.getStatusCode());
        assertEquals(HttpStatus.OK, response.getStatusCode());

        JwtResponse jwtResponse = response.getBody();
        assertNotNull(jwtResponse);
        assertNotNull(jwtResponse.accessToken());
        assertNotNull(jwtResponse.refreshToken());

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(jwtResponse.accessToken());

        /*
         * Получаем пользователя.
         * */

        ResponseEntity<SheetDto<UserRD>> response2 = rest.exchange(
                apiUrl,
                HttpMethod.GET,
                new HttpEntity<>(headers),
                new ParameterizedTypeReference<>() {
                }
        );
        assertNotNull(response2);
        assertNotNull(response2.getBody());
        assertEquals(HttpStatus.OK, response2.getStatusCode());

        SheetDto<UserRD> sheet = response2.getBody();
        assertNotNull(sheet);
        assertNotNull(sheet.page());

        List<UserRD> users = sheet.content();
        assertNotNull(users);
        assertFalse(users.isEmpty());

        UserRD user = null;
        for (UserRD userRD : users) {
            assertNotNull(userRD);
            assertNotNull(userRD.id());
            assertNotNull(userRD.username());

            if (!userRD.username().contains("admin")) {
                user = userRD;
            }
        }
        assertNotNull(user);
        String userId = user.id();
        assertNotNull(userId);

        /*
         * Пытаемся обновить пользователя с уже существующим телефоном.
         * */

        UserWD ptcUser = UserWD.builder()
                .phone("+79051234567")
                .build();

        //noinspection CatchMayIgnoreException
        try {
            rest.exchange(
                    apiUrl + "/{id}",
                    HttpMethod.PATCH,
                    new HttpEntity<>(ptcUser, headers),
                    UserRD.class,
                    userId
            );
            fail("Should throw HttpClientErrorException.Conflict");

        } catch (HttpClientErrorException.Conflict e) {
            assertNotNull(e);
            assertNotNull(e.getStatusCode());
            assertEquals(HttpStatus.CONFLICT, e.getStatusCode());

            ErrorResponse errorResponse = e.getResponseBodyAs(ErrorResponse.class);
            assertNotNull(errorResponse);
            assertNotNull(errorResponse.path());
            assertEquals(apiUrl + "/" + userId, errorResponse.path());
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
    @Order(22)
    @DirtiesContext
    @DisplayName("Тестируем уникальность электронной почты в системе при патчинге")
    public void test22() {

        /*
         * Логинимся под администратором.
         * */

        LoginRequest login = new LoginRequest("admin", "password");

        ResponseEntity<JwtResponse> response = rest.exchange(
                baseUrl + "/auth/login",
                HttpMethod.POST,
                new HttpEntity<>(login),
                JwtResponse.class);
        assertNotNull(response);
        assertNotNull(response.getStatusCode());
        assertEquals(HttpStatus.OK, response.getStatusCode());

        JwtResponse jwtResponse = response.getBody();
        assertNotNull(jwtResponse);
        assertNotNull(jwtResponse.accessToken());
        assertNotNull(jwtResponse.refreshToken());

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(jwtResponse.accessToken());

        /*
         * Получаем пользователя.
         * */

        ResponseEntity<SheetDto<UserRD>> response2 = rest.exchange(
                apiUrl,
                HttpMethod.GET,
                new HttpEntity<>(headers),
                new ParameterizedTypeReference<>() {
                }
        );
        assertNotNull(response2);
        assertNotNull(response2.getBody());
        assertEquals(HttpStatus.OK, response2.getStatusCode());

        SheetDto<UserRD> sheet = response2.getBody();
        assertNotNull(sheet);
        assertNotNull(sheet.page());

        List<UserRD> users = sheet.content();
        assertNotNull(users);
        assertFalse(users.isEmpty());

        UserRD user = null;
        for (UserRD userRD : users) {
            assertNotNull(userRD);
            assertNotNull(userRD.id());
            assertNotNull(userRD.username());

            if (!userRD.username().contains("admin")) {
                user = userRD;
            }
        }
        assertNotNull(user);
        String userId = user.id();
        assertNotNull(userId);

        /*
         * Пытаемся обновить пользователя с уже существующей почтой.
         * */

        UserWD ptcUser = UserWD.builder()
                .email("alex.petrov@google.com")
                .build();

        //noinspection CatchMayIgnoreException
        try {
            rest.exchange(
                    apiUrl + "/{id}",
                    HttpMethod.PATCH,
                    new HttpEntity<>(ptcUser, headers),
                    UserRD.class,
                    userId
            );
            fail("Should throw HttpClientErrorException.Conflict");

        } catch (HttpClientErrorException.Conflict e) {
            assertNotNull(e);
            assertNotNull(e.getStatusCode());
            assertEquals(HttpStatus.CONFLICT, e.getStatusCode());

            ErrorResponse errorResponse = e.getResponseBodyAs(ErrorResponse.class);
            assertNotNull(errorResponse);
            assertNotNull(errorResponse.path());
            assertEquals(apiUrl + "/" + userId, errorResponse.path());
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
    @Order(23)
    @DirtiesContext
    @DisplayName("Тестируем удаление обычного пользователя")
    public void test23() {

        /*
         * Логинимся под администратором.
         * */

        LoginRequest login = new LoginRequest("admin", "password");

        ResponseEntity<JwtResponse> response = rest.exchange(
                baseUrl + "/auth/login",
                HttpMethod.POST,
                new HttpEntity<>(login),
                JwtResponse.class);
        assertNotNull(response);
        assertNotNull(response.getStatusCode());
        assertEquals(HttpStatus.OK, response.getStatusCode());

        JwtResponse jwtResponse = response.getBody();
        assertNotNull(jwtResponse);
        assertNotNull(jwtResponse.accessToken());
        assertNotNull(jwtResponse.refreshToken());

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(jwtResponse.accessToken());

        /*
         * Логинимся под пользователем.
         * */

        LoginRequest login2 = new LoginRequest("alex_petrov", "password");

        ResponseEntity<JwtResponse> response2 = rest.exchange(
                baseUrl + "/auth/login",
                HttpMethod.POST,
                new HttpEntity<>(login2),
                JwtResponse.class);
        assertNotNull(response2);
        assertNotNull(response2.getStatusCode());
        assertEquals(HttpStatus.OK, response2.getStatusCode());

        JwtResponse jwtResponse2 = response2.getBody();
        assertNotNull(jwtResponse2);
        assertNotNull(jwtResponse2.accessToken());
        assertNotNull(jwtResponse2.refreshToken());

        HttpHeaders headers2 = new HttpHeaders();
        headers2.setBearerAuth(jwtResponse2.accessToken());

        /*
         * Получаем идентификатор пользователя.
         * */

        ResponseEntity<UserRD> response3 = rest.exchange(
                baseUrl + "/api/user",
                HttpMethod.GET,
                new HttpEntity<>(headers2),
                UserRD.class
        );
        assertNotNull(response3);
        assertNotNull(response3.getBody());
        assertEquals(HttpStatus.OK, response3.getStatusCode());

        UserRD user = response3.getBody();
        assertNotNull(user);
        String userId = user.id();
        assertNotNull(userId);

        /*
         * Удаляем пользователя.
         * */

        ResponseEntity<Void> response4 = rest.exchange(
                apiUrl + "/{id}",
                HttpMethod.DELETE,
                new HttpEntity<>(headers),
                Void.class,
                userId
        );
        assertNotNull(response4);
        assertNotNull(response4.getStatusCode());
        assertEquals(HttpStatus.NO_CONTENT, response4.getStatusCode());

        /*
         * Проверяем, что аккаунт удален.
         * */

        //noinspection CatchMayIgnoreException
        try {
            rest.exchange(
                    baseUrl + "/api/user",
                    HttpMethod.GET,
                    new HttpEntity<>(headers2),
                    UserRD.class
            );
            fail("Should throw HttpClientErrorException.NotFound");

        } catch (HttpClientErrorException.NotFound e) {
            assertNotNull(e);
            assertNotNull(e.getStatusCode());
            assertEquals(HttpStatus.NOT_FOUND, e.getStatusCode());

            ErrorResponse errorResponse = e.getResponseBodyAs(ErrorResponse.class);
            assertNotNull(errorResponse);
            assertNotNull(errorResponse.path());
            assertEquals(baseUrl + "/api/user", errorResponse.path());
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
    @Order(24)
    @DirtiesContext
    @DisplayName("Тестируем удаление обычного пользователя по плохому идентификатору")
    public void test24() {

        /*
         * Логинимся под администратором.
         * */

        LoginRequest login = new LoginRequest("admin", "password");

        ResponseEntity<JwtResponse> response = rest.exchange(
                baseUrl + "/auth/login",
                HttpMethod.POST,
                new HttpEntity<>(login),
                JwtResponse.class);
        assertNotNull(response);
        assertNotNull(response.getStatusCode());
        assertEquals(HttpStatus.OK, response.getStatusCode());

        JwtResponse jwtResponse = response.getBody();
        assertNotNull(jwtResponse);
        assertNotNull(jwtResponse.accessToken());
        assertNotNull(jwtResponse.refreshToken());

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(jwtResponse.accessToken());

        /*
         * Пытаемся удалить пользователя по плохому идентификатору.
         * */

        String userId = UUID.randomUUID().toString();

        //noinspection CatchMayIgnoreException
        try {
            rest.exchange(
                    apiUrl + "/{id}",
                    HttpMethod.DELETE,
                    new HttpEntity<>(headers),
                    Void.class,
                    userId
            );
            fail("Should throw HttpClientErrorException.NotFound");

        } catch (HttpClientErrorException.NotFound e) {
            assertNotNull(e);
            assertNotNull(e.getStatusCode());
            assertEquals(HttpStatus.NOT_FOUND, e.getStatusCode());

            ErrorResponse errorResponse = e.getResponseBodyAs(ErrorResponse.class);
            assertNotNull(errorResponse);
            assertNotNull(errorResponse.path());
            assertEquals(apiUrl + "/" + userId, errorResponse.path());
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
    @Order(25)
    @DirtiesContext
    @DisplayName("Тестируем удаление другого администратора")
    public void test25() {

        /*
         * Логинимся под администратором.
         * */

        LoginRequest login = new LoginRequest("admin", "password");

        ResponseEntity<JwtResponse> response = rest.exchange(
                baseUrl + "/auth/login",
                HttpMethod.POST,
                new HttpEntity<>(login),
                JwtResponse.class);
        assertNotNull(response);
        assertNotNull(response.getStatusCode());
        assertEquals(HttpStatus.OK, response.getStatusCode());

        JwtResponse jwtResponse = response.getBody();
        assertNotNull(jwtResponse);
        assertNotNull(jwtResponse.accessToken());
        assertNotNull(jwtResponse.refreshToken());

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(jwtResponse.accessToken());

        /*
         * Логинимся под вторым администратором.
         * */

        LoginRequest login2 = new LoginRequest("admin2", "password");

        ResponseEntity<JwtResponse> response2 = rest.exchange(
                baseUrl + "/auth/login",
                HttpMethod.POST,
                new HttpEntity<>(login2),
                JwtResponse.class);
        assertNotNull(response2);
        assertNotNull(response2.getStatusCode());
        assertEquals(HttpStatus.OK, response2.getStatusCode());

        JwtResponse jwtResponse2 = response2.getBody();
        assertNotNull(jwtResponse2);
        assertNotNull(jwtResponse2.accessToken());
        assertNotNull(jwtResponse2.refreshToken());

        HttpHeaders headers2 = new HttpHeaders();
        headers2.setBearerAuth(jwtResponse2.accessToken());

        /*
         * Получаем идентификатор второго администратора.
         * */

        ResponseEntity<UserRD> response3 = rest.exchange(
                baseUrl + "/api/user",
                HttpMethod.GET,
                new HttpEntity<>(headers2),
                UserRD.class
        );
        assertNotNull(response3);
        assertNotNull(response3.getBody());
        assertEquals(HttpStatus.OK, response3.getStatusCode());

        UserRD admin = response3.getBody();
        assertNotNull(admin);
        String adminId = admin.id();
        assertNotNull(adminId);

        /*
         * Пытаемся удалить администратора
         * */

        //noinspection CatchMayIgnoreException
        try {
            rest.exchange(
                    apiUrl + "/{id}",
                    HttpMethod.DELETE,
                    new HttpEntity<>(headers),
                    Void.class,
                    adminId
            );
            fail("Should throw HttpClientErrorException.UnprocessableContent");

        } catch (HttpClientErrorException.UnprocessableContent e) {
            assertNotNull(e);
            assertNotNull(e.getStatusCode());
            assertEquals(HttpStatus.UNPROCESSABLE_CONTENT, e.getStatusCode());

            ErrorResponse errorResponse = e.getResponseBodyAs(ErrorResponse.class);
            assertNotNull(errorResponse);
            assertNotNull(errorResponse.path());
            assertEquals(apiUrl + "/" + adminId, errorResponse.path());
            assertNotNull(errorResponse.httpMethod());
            assertEquals(HttpMethod.DELETE, errorResponse.httpMethod());
            assertNotNull(errorResponse.httpStatus());
            assertEquals(HttpStatus.UNPROCESSABLE_CONTENT, errorResponse.httpStatus());
            assertNotNull(errorResponse.message());
            assertNotNull(errorResponse.exception());
            assertNotNull(errorResponse.timestamp());

        } catch (Exception e) {
            fail("Should throw HttpClientErrorException.UnprocessableContent, but got " + e.getMessage());
        }
    }

    @Test
    @Order(26)
    @DirtiesContext
    @DisplayName("Тестируем удаление себя же")
    public void test26() {

        /*
         * Логинимся под администратором.
         * */

        LoginRequest login = new LoginRequest("admin", "password");

        ResponseEntity<JwtResponse> response = rest.exchange(
                baseUrl + "/auth/login",
                HttpMethod.POST,
                new HttpEntity<>(login),
                JwtResponse.class);
        assertNotNull(response);
        assertNotNull(response.getStatusCode());
        assertEquals(HttpStatus.OK, response.getStatusCode());

        JwtResponse jwtResponse = response.getBody();
        assertNotNull(jwtResponse);
        assertNotNull(jwtResponse.accessToken());
        assertNotNull(jwtResponse.refreshToken());

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(jwtResponse.accessToken());

        /*
         * Получаем идентификатор администратора.
         * */

        ResponseEntity<UserRD> response2 = rest.exchange(
                baseUrl + "/api/user",
                HttpMethod.GET,
                new HttpEntity<>(headers),
                UserRD.class
        );
        assertNotNull(response2);
        assertNotNull(response2.getBody());
        assertEquals(HttpStatus.OK, response2.getStatusCode());

        UserRD admin = response2.getBody();
        assertNotNull(admin);
        String adminId = admin.id();
        assertNotNull(adminId);

        /*
         * Удаляем себя же.
         * */

        ResponseEntity<Void> response3 = rest.exchange(
                apiUrl + "/{id}",
                HttpMethod.DELETE,
                new HttpEntity<>(headers),
                Void.class,
                adminId
        );
        assertNotNull(response3);
        assertNotNull(response3.getStatusCode());
        assertEquals(HttpStatus.NO_CONTENT, response3.getStatusCode());

        /*
         * Проверяем, что аккаунт удален.
         * */

        //noinspection CatchMayIgnoreException
        try {
            rest.exchange(
                    baseUrl + "/api/user",
                    HttpMethod.GET,
                    new HttpEntity<>(headers),
                    UserRD.class
            );
            fail("Should throw HttpClientErrorException.NotFound");

        } catch (HttpClientErrorException.NotFound e) {
            assertNotNull(e);
            assertNotNull(e.getStatusCode());
            assertEquals(HttpStatus.NOT_FOUND, e.getStatusCode());

            ErrorResponse errorResponse = e.getResponseBodyAs(ErrorResponse.class);
            assertNotNull(errorResponse);
            assertNotNull(errorResponse.path());
            assertEquals(baseUrl + "/api/user", errorResponse.path());
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
    @DisplayName("Тестируем назначение новой роли")
    public void test27() {

        /*
         * Логинимся под администратором.
         * */

        LoginRequest login = new LoginRequest("admin", "password");

        ResponseEntity<JwtResponse> response = rest.exchange(
                baseUrl + "/auth/login",
                HttpMethod.POST,
                new HttpEntity<>(login),
                JwtResponse.class);
        assertNotNull(response);
        assertNotNull(response.getStatusCode());
        assertEquals(HttpStatus.OK, response.getStatusCode());

        JwtResponse jwtResponse = response.getBody();
        assertNotNull(jwtResponse);
        assertNotNull(jwtResponse.accessToken());
        assertNotNull(jwtResponse.refreshToken());

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(jwtResponse.accessToken());

        /*
         * Логинимся под пользователем.
         * */

        LoginRequest login2 = new LoginRequest("alex_petrov", "password");

        ResponseEntity<JwtResponse> response2 = rest.exchange(
                baseUrl + "/auth/login",
                HttpMethod.POST,
                new HttpEntity<>(login2),
                JwtResponse.class);
        assertNotNull(response2);
        assertNotNull(response2.getStatusCode());
        assertEquals(HttpStatus.OK, response2.getStatusCode());

        JwtResponse jwtResponse2 = response2.getBody();
        assertNotNull(jwtResponse2);
        assertNotNull(jwtResponse2.accessToken());
        assertNotNull(jwtResponse2.refreshToken());

        HttpHeaders headers2 = new HttpHeaders();
        headers2.setBearerAuth(jwtResponse2.accessToken());

        /*
         * Получаем идентификатор пользователя.
         * */

        ResponseEntity<UserRD> response3 = rest.exchange(
                baseUrl + "/api/user",
                HttpMethod.GET,
                new HttpEntity<>(headers2),
                UserRD.class
        );
        assertNotNull(response3);
        assertNotNull(response3.getBody());
        assertEquals(HttpStatus.OK, response3.getStatusCode());

        UserRD user = response3.getBody();
        assertNotNull(user);
        String userId = user.id();
        assertNotNull(userId);

        /*
         * Проверяем, что у пользователя нет роли администратора.
         * */

        //noinspection CatchMayIgnoreException
        try {
            rest.exchange(
                    apiUrl,
                    HttpMethod.GET,
                    new HttpEntity<>(headers2),
                    new ParameterizedTypeReference<SheetDto<UserRD>>() {
                    }
            );
            fail("Should throw HttpClientErrorException.Forbidden");

        } catch (HttpClientErrorException.Forbidden e) {
            assertNotNull(e);
            assertNotNull(e.getStatusCode());
            assertEquals(HttpStatus.FORBIDDEN, e.getStatusCode());

            ErrorResponse errorResponse = e.getResponseBodyAs(ErrorResponse.class);
            assertNotNull(errorResponse);
            assertNotNull(errorResponse.path());
            assertEquals(apiUrl, errorResponse.path());
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

        /*
         * Назначаем пользователю роль администратора.
         * */

        RoleCollection roles = RoleCollection.of("ROLE_ADMIN");

        ResponseEntity<Void> response4 = rest.exchange(
                apiUrl + "/{id}/grant",
                HttpMethod.PATCH,
                new HttpEntity<>(roles, headers),
                Void.class,
                userId
        );
        assertNotNull(response4);
        assertNotNull(response4.getStatusCode());
        assertEquals(HttpStatus.NO_CONTENT, response4.getStatusCode());

        /*
         * Логинимся под новым администратором.
         * */

        LoginRequest login3 = new LoginRequest("alex_petrov", "password");

        ResponseEntity<JwtResponse> response5 = rest.exchange(
                baseUrl + "/auth/login",
                HttpMethod.POST,
                new HttpEntity<>(login3),
                JwtResponse.class);
        assertNotNull(response5);
        assertNotNull(response5.getStatusCode());
        assertEquals(HttpStatus.OK, response5.getStatusCode());

        JwtResponse jwtResponse3 = response5.getBody();
        assertNotNull(jwtResponse3);
        assertNotNull(jwtResponse3.accessToken());
        assertNotNull(jwtResponse3.refreshToken());

        HttpHeaders headers3 = new HttpHeaders();
        headers3.setBearerAuth(jwtResponse3.accessToken());

        /*
         * Проверяем, что у пользователя есть роль администратора.
         * */

        ResponseEntity<SheetDto<UserRD>> response6 = rest.exchange(
                apiUrl,
                HttpMethod.GET,
                new HttpEntity<>(headers3),
                new ParameterizedTypeReference<>() {
                }
        );
        assertNotNull(response6);
        assertNotNull(response6.getStatusCode());
        assertEquals(HttpStatus.OK, response6.getStatusCode());
    }

    @Test
    @Order(28)
    @DirtiesContext
    @DisplayName("Тестируем назначение новой роли по имени")
    public void test28() {

        /*
         * Логинимся под администратором.
         * */

        LoginRequest login = new LoginRequest("admin", "password");

        ResponseEntity<JwtResponse> response = rest.exchange(
                baseUrl + "/auth/login",
                HttpMethod.POST,
                new HttpEntity<>(login),
                JwtResponse.class);
        assertNotNull(response);
        assertNotNull(response.getStatusCode());
        assertEquals(HttpStatus.OK, response.getStatusCode());

        JwtResponse jwtResponse = response.getBody();
        assertNotNull(jwtResponse);
        assertNotNull(jwtResponse.accessToken());
        assertNotNull(jwtResponse.refreshToken());

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(jwtResponse.accessToken());

        /*
         * Логинимся под пользователем.
         * */

        LoginRequest login2 = new LoginRequest("alex_petrov", "password");

        ResponseEntity<JwtResponse> response2 = rest.exchange(
                baseUrl + "/auth/login",
                HttpMethod.POST,
                new HttpEntity<>(login2),
                JwtResponse.class);
        assertNotNull(response2);
        assertNotNull(response2.getStatusCode());
        assertEquals(HttpStatus.OK, response2.getStatusCode());

        JwtResponse jwtResponse2 = response2.getBody();
        assertNotNull(jwtResponse2);
        assertNotNull(jwtResponse2.accessToken());
        assertNotNull(jwtResponse2.refreshToken());

        HttpHeaders headers2 = new HttpHeaders();
        headers2.setBearerAuth(jwtResponse2.accessToken());

        /*
         * Получаем идентификатор пользователя.
         * */

        ResponseEntity<UserRD> response3 = rest.exchange(
                baseUrl + "/api/user",
                HttpMethod.GET,
                new HttpEntity<>(headers2),
                UserRD.class
        );
        assertNotNull(response3);
        assertNotNull(response3.getBody());
        assertEquals(HttpStatus.OK, response3.getStatusCode());

        UserRD user = response3.getBody();
        assertNotNull(user);
        String userId = user.id();
        assertNotNull(userId);

        /*
         * Проверяем, что у пользователя нет роли администратора.
         * */

        //noinspection CatchMayIgnoreException
        try {
            rest.exchange(
                    apiUrl,
                    HttpMethod.GET,
                    new HttpEntity<>(headers2),
                    new ParameterizedTypeReference<SheetDto<UserRD>>() {
                    }
            );
            fail("Should throw HttpClientErrorException.Forbidden");

        } catch (HttpClientErrorException.Forbidden e) {
            assertNotNull(e);
            assertNotNull(e.getStatusCode());
            assertEquals(HttpStatus.FORBIDDEN, e.getStatusCode());

            ErrorResponse errorResponse = e.getResponseBodyAs(ErrorResponse.class);
            assertNotNull(errorResponse);
            assertNotNull(errorResponse.path());
            assertEquals(apiUrl, errorResponse.path());
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

        /*
         * Назначаем пользователю роль администратора.
         * */

        ResponseEntity<Void> response4 = rest.exchange(
                apiUrl + "/{id}/grant/{name}",
                HttpMethod.PATCH,
                new HttpEntity<>(headers),
                Void.class,
                userId,
                "ROLE_ADMIN"
        );
        assertNotNull(response4);
        assertNotNull(response4.getStatusCode());
        assertEquals(HttpStatus.NO_CONTENT, response4.getStatusCode());

        /*
         * Логинимся под новым администратором.
         * */

        LoginRequest login3 = new LoginRequest("alex_petrov", "password");

        ResponseEntity<JwtResponse> response5 = rest.exchange(
                baseUrl + "/auth/login",
                HttpMethod.POST,
                new HttpEntity<>(login3),
                JwtResponse.class);
        assertNotNull(response5);
        assertNotNull(response5.getStatusCode());
        assertEquals(HttpStatus.OK, response5.getStatusCode());

        JwtResponse jwtResponse3 = response5.getBody();
        assertNotNull(jwtResponse3);
        assertNotNull(jwtResponse3.accessToken());
        assertNotNull(jwtResponse3.refreshToken());

        HttpHeaders headers3 = new HttpHeaders();
        headers3.setBearerAuth(jwtResponse3.accessToken());

        /*
         * Проверяем, что у пользователя есть роль администратора.
         * */

        ResponseEntity<SheetDto<UserRD>> response6 = rest.exchange(
                apiUrl,
                HttpMethod.GET,
                new HttpEntity<>(headers3),
                new ParameterizedTypeReference<>() {
                }
        );
        assertNotNull(response6);
        assertNotNull(response6.getStatusCode());
        assertEquals(HttpStatus.OK, response6.getStatusCode());
    }

    @Test
    @Order(29)
    @DirtiesContext
    @DisplayName("Тестируем идемпотентность назначения новой роли")
    public void test29() {

        /*
         * Логинимся под администратором.
         * */

        LoginRequest login = new LoginRequest("admin", "password");

        ResponseEntity<JwtResponse> response = rest.exchange(
                baseUrl + "/auth/login",
                HttpMethod.POST,
                new HttpEntity<>(login),
                JwtResponse.class);
        assertNotNull(response);
        assertNotNull(response.getStatusCode());
        assertEquals(HttpStatus.OK, response.getStatusCode());

        JwtResponse jwtResponse = response.getBody();
        assertNotNull(jwtResponse);
        assertNotNull(jwtResponse.accessToken());
        assertNotNull(jwtResponse.refreshToken());

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(jwtResponse.accessToken());

        /*
         * Логинимся под пользователем.
         * */

        LoginRequest login2 = new LoginRequest("alex_petrov", "password");

        ResponseEntity<JwtResponse> response2 = rest.exchange(
                baseUrl + "/auth/login",
                HttpMethod.POST,
                new HttpEntity<>(login2),
                JwtResponse.class);
        assertNotNull(response2);
        assertNotNull(response2.getStatusCode());
        assertEquals(HttpStatus.OK, response2.getStatusCode());

        JwtResponse jwtResponse2 = response2.getBody();
        assertNotNull(jwtResponse2);
        assertNotNull(jwtResponse2.accessToken());
        assertNotNull(jwtResponse2.refreshToken());

        HttpHeaders headers2 = new HttpHeaders();
        headers2.setBearerAuth(jwtResponse2.accessToken());

        /*
         * Получаем идентификатор пользователя.
         * */

        ResponseEntity<UserRD> response3 = rest.exchange(
                baseUrl + "/api/user",
                HttpMethod.GET,
                new HttpEntity<>(headers2),
                UserRD.class
        );
        assertNotNull(response3);
        assertNotNull(response3.getBody());
        assertEquals(HttpStatus.OK, response3.getStatusCode());

        UserRD user = response3.getBody();
        assertNotNull(user);
        String userId = user.id();
        assertNotNull(userId);

        /*
         * Назначаем пользователю роль пользователя
         * */

        ResponseEntity<Void> response4 = rest.exchange(
                apiUrl + "/{id}/grant/{name}",
                HttpMethod.PATCH,
                new HttpEntity<>(headers),
                Void.class,
                userId,
                "ROLE_USER"
        );
        assertNotNull(response4);
        assertNotNull(response4.getStatusCode());
        assertEquals(HttpStatus.NO_CONTENT, response4.getStatusCode());
    }

    @Test
    @Order(30)
    @DirtiesContext
    @DisplayName("Тестируем множественное назначение ролей")
    public void test30() {

        /*
         * Логинимся под администратором.
         * */

        LoginRequest login = new LoginRequest("admin", "password");

        ResponseEntity<JwtResponse> response = rest.exchange(
                baseUrl + "/auth/login",
                HttpMethod.POST,
                new HttpEntity<>(login),
                JwtResponse.class);
        assertNotNull(response);
        assertNotNull(response.getStatusCode());
        assertEquals(HttpStatus.OK, response.getStatusCode());

        JwtResponse jwtResponse = response.getBody();
        assertNotNull(jwtResponse);
        assertNotNull(jwtResponse.accessToken());
        assertNotNull(jwtResponse.refreshToken());

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(jwtResponse.accessToken());

        /*
         * Логинимся под пользователем.
         * */

        LoginRequest login2 = new LoginRequest("alex_petrov", "password");

        ResponseEntity<JwtResponse> response2 = rest.exchange(
                baseUrl + "/auth/login",
                HttpMethod.POST,
                new HttpEntity<>(login2),
                JwtResponse.class);
        assertNotNull(response2);
        assertNotNull(response2.getStatusCode());
        assertEquals(HttpStatus.OK, response2.getStatusCode());

        JwtResponse jwtResponse2 = response2.getBody();
        assertNotNull(jwtResponse2);
        assertNotNull(jwtResponse2.accessToken());
        assertNotNull(jwtResponse2.refreshToken());

        HttpHeaders headers2 = new HttpHeaders();
        headers2.setBearerAuth(jwtResponse2.accessToken());

        /*
         * Получаем идентификатор пользователя.
         * */

        ResponseEntity<UserRD> response3 = rest.exchange(
                baseUrl + "/api/user",
                HttpMethod.GET,
                new HttpEntity<>(headers2),
                UserRD.class
        );
        assertNotNull(response3);
        assertNotNull(response3.getBody());
        assertEquals(HttpStatus.OK, response3.getStatusCode());

        UserRD user = response3.getBody();
        assertNotNull(user);
        String userId = user.id();
        assertNotNull(userId);

        /*
         * Проверяем, что у пользователя нет роли администратора.
         * */

        //noinspection CatchMayIgnoreException
        try {
            rest.exchange(
                    apiUrl,
                    HttpMethod.GET,
                    new HttpEntity<>(headers2),
                    new ParameterizedTypeReference<SheetDto<UserRD>>() {
                    }
            );
            fail("Should throw HttpClientErrorException.Forbidden");

        } catch (HttpClientErrorException.Forbidden e) {
            assertNotNull(e);
            assertNotNull(e.getStatusCode());
            assertEquals(HttpStatus.FORBIDDEN, e.getStatusCode());

            ErrorResponse errorResponse = e.getResponseBodyAs(ErrorResponse.class);
            assertNotNull(errorResponse);
            assertNotNull(errorResponse.path());
            assertEquals(apiUrl, errorResponse.path());
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

        /*
         * Назначаем пользователю роль администратора и пользователя.
         * */

        RoleCollection roles = RoleCollection.of("ROLE_ADMIN", "ROLE_USER");

        ResponseEntity<Void> response4 = rest.exchange(
                apiUrl + "/{id}/grant",
                HttpMethod.PATCH,
                new HttpEntity<>(roles, headers),
                Void.class,
                userId
        );
        assertNotNull(response4);
        assertNotNull(response4.getStatusCode());
        assertEquals(HttpStatus.NO_CONTENT, response4.getStatusCode());

        /*
         * Логинимся под новым администратором.
         * */

        LoginRequest login3 = new LoginRequest("alex_petrov", "password");

        ResponseEntity<JwtResponse> response5 = rest.exchange(
                baseUrl + "/auth/login",
                HttpMethod.POST,
                new HttpEntity<>(login3),
                JwtResponse.class);
        assertNotNull(response5);
        assertNotNull(response5.getStatusCode());
        assertEquals(HttpStatus.OK, response5.getStatusCode());

        JwtResponse jwtResponse3 = response5.getBody();
        assertNotNull(jwtResponse3);
        assertNotNull(jwtResponse3.accessToken());
        assertNotNull(jwtResponse3.refreshToken());

        HttpHeaders headers3 = new HttpHeaders();
        headers3.setBearerAuth(jwtResponse3.accessToken());

        /*
         * Проверяем, что у пользователя есть роль администратора.
         * */

        ResponseEntity<SheetDto<UserRD>> response6 = rest.exchange(
                apiUrl,
                HttpMethod.GET,
                new HttpEntity<>(headers3),
                new ParameterizedTypeReference<>() {
                }
        );
        assertNotNull(response6);
        assertNotNull(response6.getStatusCode());
        assertEquals(HttpStatus.OK, response6.getStatusCode());
    }

    @Test
    @Order(31)
    @DirtiesContext
    @DisplayName("Тестируем назначение новой роли с плохим пользователем")
    public void test31() {

        /*
         * Логинимся под администратором.
         * */

        LoginRequest login = new LoginRequest("admin", "password");

        ResponseEntity<JwtResponse> response = rest.exchange(
                baseUrl + "/auth/login",
                HttpMethod.POST,
                new HttpEntity<>(login),
                JwtResponse.class);
        assertNotNull(response);
        assertNotNull(response.getStatusCode());
        assertEquals(HttpStatus.OK, response.getStatusCode());

        JwtResponse jwtResponse = response.getBody();
        assertNotNull(jwtResponse);
        assertNotNull(jwtResponse.accessToken());
        assertNotNull(jwtResponse.refreshToken());

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(jwtResponse.accessToken());

        /*
         * Пытаемся выдать роль плохому пользователю.
         * */

        String userId = UUID.randomUUID().toString();
        RoleCollection roles = RoleCollection.of("ROLE_ADMIN");

        //noinspection CatchMayIgnoreException
        try {
            rest.exchange(
                    apiUrl + "/{id}/grant",
                    HttpMethod.PATCH,
                    new HttpEntity<>(roles, headers),
                    Void.class,
                    userId
            );
            fail("Should throw HttpClientErrorException.NotFound");

        } catch (HttpClientErrorException.NotFound e) {
            assertNotNull(e);
            assertNotNull(e.getStatusCode());
            assertEquals(HttpStatus.NOT_FOUND, e.getStatusCode());

            ErrorResponse errorResponse = e.getResponseBodyAs(ErrorResponse.class);
            assertNotNull(errorResponse);
            assertNotNull(errorResponse.path());
            assertEquals(apiUrl + "/" + userId + "/grant", errorResponse.path());
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
    @Order(32)
    @DirtiesContext
    @DisplayName("Тестируем назначение плохой роли")
    public void test32() {

        /*
         * Логинимся под администратором.
         * */

        LoginRequest login = new LoginRequest("admin", "password");

        ResponseEntity<JwtResponse> response = rest.exchange(
                baseUrl + "/auth/login",
                HttpMethod.POST,
                new HttpEntity<>(login),
                JwtResponse.class);
        assertNotNull(response);
        assertNotNull(response.getStatusCode());
        assertEquals(HttpStatus.OK, response.getStatusCode());

        JwtResponse jwtResponse = response.getBody();
        assertNotNull(jwtResponse);
        assertNotNull(jwtResponse.accessToken());
        assertNotNull(jwtResponse.refreshToken());

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(jwtResponse.accessToken());

        /*
         * Логинимся под пользователем.
         * */

        LoginRequest login2 = new LoginRequest("alex_petrov", "password");

        ResponseEntity<JwtResponse> response2 = rest.exchange(
                baseUrl + "/auth/login",
                HttpMethod.POST,
                new HttpEntity<>(login2),
                JwtResponse.class);
        assertNotNull(response2);
        assertNotNull(response2.getStatusCode());
        assertEquals(HttpStatus.OK, response2.getStatusCode());

        JwtResponse jwtResponse2 = response2.getBody();
        assertNotNull(jwtResponse2);
        assertNotNull(jwtResponse2.accessToken());
        assertNotNull(jwtResponse2.refreshToken());

        HttpHeaders headers2 = new HttpHeaders();
        headers2.setBearerAuth(jwtResponse2.accessToken());

        /*
         * Получаем идентификатор пользователя.
         * */

        ResponseEntity<UserRD> response3 = rest.exchange(
                baseUrl + "/api/user",
                HttpMethod.GET,
                new HttpEntity<>(headers2),
                UserRD.class
        );
        assertNotNull(response3);
        assertNotNull(response3.getBody());
        assertEquals(HttpStatus.OK, response3.getStatusCode());

        UserRD user = response3.getBody();
        assertNotNull(user);
        String userId = user.id();
        assertNotNull(userId);

        /*
         * Пытаемся назначить плохую роль.
         * */

        //noinspection CatchMayIgnoreException
        try {
            rest.exchange(
                    apiUrl + "/{id}/grant/{name}",
                    HttpMethod.PATCH,
                    new HttpEntity<>(headers),
                    Void.class,
                    userId,
                    "BAD"
            );
            fail("Should throw HttpClientErrorException.NotFound");

        } catch (HttpClientErrorException.NotFound e) {
            assertNotNull(e);
            assertNotNull(e.getStatusCode());
            assertEquals(HttpStatus.NOT_FOUND, e.getStatusCode());

            ErrorResponse errorResponse = e.getResponseBodyAs(ErrorResponse.class);
            assertNotNull(errorResponse);
            assertNotNull(errorResponse.path());
            assertEquals(apiUrl + "/" + userId + "/grant/BAD", errorResponse.path());
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
    @Order(33)
    @DirtiesContext
    @DisplayName("Тестируем удаление роли у пользователя")
    public void test33() {

        /*
         * Логинимся под администратором.
         * */

        LoginRequest login = new LoginRequest("admin", "password");

        ResponseEntity<JwtResponse> response = rest.exchange(
                baseUrl + "/auth/login",
                HttpMethod.POST,
                new HttpEntity<>(login),
                JwtResponse.class);
        assertNotNull(response);
        assertNotNull(response.getStatusCode());
        assertEquals(HttpStatus.OK, response.getStatusCode());

        JwtResponse jwtResponse = response.getBody();
        assertNotNull(jwtResponse);
        assertNotNull(jwtResponse.accessToken());
        assertNotNull(jwtResponse.refreshToken());

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(jwtResponse.accessToken());

        /*
         * Создаем новую роль.
         * */

        RoleWD role = new RoleWD("ROLE_OPERATOR");

        ResponseEntity<RoleRD> response2 = rest.exchange(
                baseUrl + "/api/admin/roles",
                HttpMethod.POST,
                new HttpEntity<>(role, headers),
                RoleRD.class
        );
        assertNotNull(response2);
        assertNotNull(response2.getStatusCode());
        assertEquals(HttpStatus.CREATED, response2.getStatusCode());

        /*
         * Логинимся под пользователем.
         * */

        LoginRequest login2 = new LoginRequest("alex_petrov", "password");

        ResponseEntity<JwtResponse> response3 = rest.exchange(
                baseUrl + "/auth/login",
                HttpMethod.POST,
                new HttpEntity<>(login2),
                JwtResponse.class);
        assertNotNull(response3);
        assertNotNull(response3.getStatusCode());
        assertEquals(HttpStatus.OK, response3.getStatusCode());

        JwtResponse jwtResponse2 = response3.getBody();
        assertNotNull(jwtResponse2);
        assertNotNull(jwtResponse2.accessToken());
        assertNotNull(jwtResponse2.refreshToken());

        HttpHeaders headers2 = new HttpHeaders();
        headers2.setBearerAuth(jwtResponse2.accessToken());

        /*
         * Получаем идентификатор пользователя.
         * */

        ResponseEntity<UserRD> response4 = rest.exchange(
                baseUrl + "/api/user",
                HttpMethod.GET,
                new HttpEntity<>(headers2),
                UserRD.class
        );
        assertNotNull(response4);
        assertNotNull(response4.getBody());
        assertEquals(HttpStatus.OK, response4.getStatusCode());

        UserRD user = response4.getBody();
        assertNotNull(user);
        String userId = user.id();
        assertNotNull(userId);

        /*
         * Назначаем пользователю роль оператора.
         * */

        ResponseEntity<Void> response5 = rest.exchange(
                apiUrl + "/{id}/grant/{name}",
                HttpMethod.PATCH,
                new HttpEntity<>(headers),
                Void.class,
                userId,
                "ROLE_OPERATOR"
        );
        assertNotNull(response5);
        assertNotNull(response5.getStatusCode());
        assertEquals(HttpStatus.NO_CONTENT, response5.getStatusCode());

        /*
         * Отбираем у пользователя роль оператора.
         * */

        RoleCollection roles = RoleCollection.of("ROLE_OPERATOR");

        ResponseEntity<Void> response6 = rest.exchange(
                apiUrl + "/{id}/revoke",
                HttpMethod.PATCH,
                new HttpEntity<>(roles, headers),
                Void.class,
                userId
        );
        assertNotNull(response6);
        assertNotNull(response6.getStatusCode());
        assertEquals(HttpStatus.NO_CONTENT, response6.getStatusCode());
    }

    @Test
    @Order(34)
    @DirtiesContext
    @DisplayName("Тестируем удаление роли у пользователя по имени")
    public void test34() {

        /*
         * Логинимся под администратором.
         * */

        LoginRequest login = new LoginRequest("admin", "password");

        ResponseEntity<JwtResponse> response = rest.exchange(
                baseUrl + "/auth/login",
                HttpMethod.POST,
                new HttpEntity<>(login),
                JwtResponse.class);
        assertNotNull(response);
        assertNotNull(response.getStatusCode());
        assertEquals(HttpStatus.OK, response.getStatusCode());

        JwtResponse jwtResponse = response.getBody();
        assertNotNull(jwtResponse);
        assertNotNull(jwtResponse.accessToken());
        assertNotNull(jwtResponse.refreshToken());

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(jwtResponse.accessToken());

        /*
         * Создаем новую роль.
         * */

        RoleWD role = new RoleWD("ROLE_OPERATOR");

        ResponseEntity<RoleRD> response2 = rest.exchange(
                baseUrl + "/api/admin/roles",
                HttpMethod.POST,
                new HttpEntity<>(role, headers),
                RoleRD.class
        );
        assertNotNull(response2);
        assertNotNull(response2.getStatusCode());
        assertEquals(HttpStatus.CREATED, response2.getStatusCode());

        /*
         * Логинимся под пользователем.
         * */

        LoginRequest login2 = new LoginRequest("alex_petrov", "password");

        ResponseEntity<JwtResponse> response3 = rest.exchange(
                baseUrl + "/auth/login",
                HttpMethod.POST,
                new HttpEntity<>(login2),
                JwtResponse.class);
        assertNotNull(response3);
        assertNotNull(response3.getStatusCode());
        assertEquals(HttpStatus.OK, response3.getStatusCode());

        JwtResponse jwtResponse2 = response3.getBody();
        assertNotNull(jwtResponse2);
        assertNotNull(jwtResponse2.accessToken());
        assertNotNull(jwtResponse2.refreshToken());

        HttpHeaders headers2 = new HttpHeaders();
        headers2.setBearerAuth(jwtResponse2.accessToken());

        /*
         * Получаем идентификатор пользователя.
         * */

        ResponseEntity<UserRD> response4 = rest.exchange(
                baseUrl + "/api/user",
                HttpMethod.GET,
                new HttpEntity<>(headers2),
                UserRD.class
        );
        assertNotNull(response4);
        assertNotNull(response4.getBody());
        assertEquals(HttpStatus.OK, response4.getStatusCode());

        UserRD user = response4.getBody();
        assertNotNull(user);
        String userId = user.id();
        assertNotNull(userId);

        /*
         * Назначаем пользователю роль оператора.
         * */

        ResponseEntity<Void> response5 = rest.exchange(
                apiUrl + "/{id}/grant/{name}",
                HttpMethod.PATCH,
                new HttpEntity<>(headers),
                Void.class,
                userId,
                "ROLE_OPERATOR"
        );
        assertNotNull(response5);
        assertNotNull(response5.getStatusCode());
        assertEquals(HttpStatus.NO_CONTENT, response5.getStatusCode());

        /*
         * Отбираем у пользователя роль оператора.
         * */

        ResponseEntity<Void> response6 = rest.exchange(
                apiUrl + "/{id}/revoke/{name}",
                HttpMethod.PATCH,
                new HttpEntity<>(headers),
                Void.class,
                userId,
                "ROLE_OPERATOR"
        );
        assertNotNull(response6);
        assertNotNull(response6.getStatusCode());
        assertEquals(HttpStatus.NO_CONTENT, response6.getStatusCode());
    }

    @Test
    @Order(35)
    @DirtiesContext
    @DisplayName("Тестируем удаление роли у плохого пользователя")
    public void test35() {

        /*
         * Логинимся под администратором.
         * */

        LoginRequest login = new LoginRequest("admin", "password");

        ResponseEntity<JwtResponse> response = rest.exchange(
                baseUrl + "/auth/login",
                HttpMethod.POST,
                new HttpEntity<>(login),
                JwtResponse.class);
        assertNotNull(response);
        assertNotNull(response.getStatusCode());
        assertEquals(HttpStatus.OK, response.getStatusCode());

        JwtResponse jwtResponse = response.getBody();
        assertNotNull(jwtResponse);
        assertNotNull(jwtResponse.accessToken());
        assertNotNull(jwtResponse.refreshToken());

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(jwtResponse.accessToken());

        /*
         * Создаем новую роль.
         * */

        RoleWD role = new RoleWD("ROLE_OPERATOR");

        ResponseEntity<RoleRD> response2 = rest.exchange(
                baseUrl + "/api/admin/roles",
                HttpMethod.POST,
                new HttpEntity<>(role, headers),
                RoleRD.class
        );
        assertNotNull(response2);
        assertNotNull(response2.getStatusCode());
        assertEquals(HttpStatus.CREATED, response2.getStatusCode());

        /*
         * Логинимся под пользователем.
         * */

        LoginRequest login2 = new LoginRequest("alex_petrov", "password");

        ResponseEntity<JwtResponse> response3 = rest.exchange(
                baseUrl + "/auth/login",
                HttpMethod.POST,
                new HttpEntity<>(login2),
                JwtResponse.class);
        assertNotNull(response3);
        assertNotNull(response3.getStatusCode());
        assertEquals(HttpStatus.OK, response3.getStatusCode());

        JwtResponse jwtResponse2 = response3.getBody();
        assertNotNull(jwtResponse2);
        assertNotNull(jwtResponse2.accessToken());
        assertNotNull(jwtResponse2.refreshToken());

        HttpHeaders headers2 = new HttpHeaders();
        headers2.setBearerAuth(jwtResponse2.accessToken());

        /*
         * Получаем идентификатор пользователя.
         * */

        ResponseEntity<UserRD> response4 = rest.exchange(
                baseUrl + "/api/user",
                HttpMethod.GET,
                new HttpEntity<>(headers2),
                UserRD.class
        );
        assertNotNull(response4);
        assertNotNull(response4.getBody());
        assertEquals(HttpStatus.OK, response4.getStatusCode());

        UserRD user = response4.getBody();
        assertNotNull(user);
        String userId = user.id();
        assertNotNull(userId);

        /*
         * Назначаем пользователю роль оператора.
         * */

        ResponseEntity<Void> response5 = rest.exchange(
                apiUrl + "/{id}/grant/{name}",
                HttpMethod.PATCH,
                new HttpEntity<>(headers),
                Void.class,
                userId,
                "ROLE_OPERATOR"
        );
        assertNotNull(response5);
        assertNotNull(response5.getStatusCode());
        assertEquals(HttpStatus.NO_CONTENT, response5.getStatusCode());

        /*
         * Отбираем у плохого пользователя роль оператора.
         * */

        String badUserId = UUID.randomUUID().toString();

        //noinspection CatchMayIgnoreException
        try {
            rest.exchange(
                    apiUrl + "/{id}/revoke/{name}",
                    HttpMethod.PATCH,
                    new HttpEntity<>(headers),
                    Void.class,
                    badUserId,
                    "ROLE_OPERATOR"
            );
            fail("Should throw HttpClientErrorException.NotFound");

        } catch (HttpClientErrorException.NotFound e) {
            assertNotNull(e);
            assertNotNull(e.getStatusCode());
            assertEquals(HttpStatus.NOT_FOUND, e.getStatusCode());

            ErrorResponse errorResponse = e.getResponseBodyAs(ErrorResponse.class);
            assertNotNull(errorResponse);
            assertNotNull(errorResponse.path());
            assertEquals(apiUrl + "/" + badUserId + "/revoke/ROLE_OPERATOR", errorResponse.path());
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
    @Order(36)
    @DirtiesContext
    @DisplayName("Тестируем удаление плохой роли пользователя")
    public void test36() {

        /*
         * Логинимся под администратором.
         * */

        LoginRequest login = new LoginRequest("admin", "password");

        ResponseEntity<JwtResponse> response = rest.exchange(
                baseUrl + "/auth/login",
                HttpMethod.POST,
                new HttpEntity<>(login),
                JwtResponse.class);
        assertNotNull(response);
        assertNotNull(response.getStatusCode());
        assertEquals(HttpStatus.OK, response.getStatusCode());

        JwtResponse jwtResponse = response.getBody();
        assertNotNull(jwtResponse);
        assertNotNull(jwtResponse.accessToken());
        assertNotNull(jwtResponse.refreshToken());

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(jwtResponse.accessToken());

        /*
         * Логинимся под пользователем.
         * */

        LoginRequest login2 = new LoginRequest("alex_petrov", "password");

        ResponseEntity<JwtResponse> response2 = rest.exchange(
                baseUrl + "/auth/login",
                HttpMethod.POST,
                new HttpEntity<>(login2),
                JwtResponse.class);
        assertNotNull(response2);
        assertNotNull(response2.getStatusCode());
        assertEquals(HttpStatus.OK, response2.getStatusCode());

        JwtResponse jwtResponse2 = response2.getBody();
        assertNotNull(jwtResponse2);
        assertNotNull(jwtResponse2.accessToken());
        assertNotNull(jwtResponse2.refreshToken());

        HttpHeaders headers2 = new HttpHeaders();
        headers2.setBearerAuth(jwtResponse2.accessToken());

        /*
         * Получаем идентификатор пользователя.
         * */

        ResponseEntity<UserRD> response3 = rest.exchange(
                baseUrl + "/api/user",
                HttpMethod.GET,
                new HttpEntity<>(headers2),
                UserRD.class
        );
        assertNotNull(response3);
        assertNotNull(response3.getBody());
        assertEquals(HttpStatus.OK, response3.getStatusCode());

        UserRD user = response3.getBody();
        assertNotNull(user);
        String userId = user.id();
        assertNotNull(userId);

        /*
         * Отбираем у пользователя несуществующую роль.
         * */

        //noinspection CatchMayIgnoreException
        try {
            rest.exchange(
                    apiUrl + "/{id}/revoke/{name}",
                    HttpMethod.PATCH,
                    new HttpEntity<>(headers),
                    Void.class,
                    userId,
                    "ROLE_OPERATOR"
            );
            fail("Should throw HttpClientErrorException.NotFound");

        } catch (HttpClientErrorException.NotFound e) {
            assertNotNull(e);
            assertNotNull(e.getStatusCode());
            assertEquals(HttpStatus.NOT_FOUND, e.getStatusCode());

            ErrorResponse errorResponse = e.getResponseBodyAs(ErrorResponse.class);
            assertNotNull(errorResponse);
            assertNotNull(errorResponse.path());
            assertEquals(apiUrl + "/" + userId + "/revoke/ROLE_OPERATOR", errorResponse.path());
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
    @Order(37)
    @DirtiesContext
    @DisplayName("Тестируем удаление роли у администратора")
    public void test37() {

        /*
         * Логинимся под администратором.
         * */

        LoginRequest login = new LoginRequest("admin", "password");

        ResponseEntity<JwtResponse> response = rest.exchange(
                baseUrl + "/auth/login",
                HttpMethod.POST,
                new HttpEntity<>(login),
                JwtResponse.class);
        assertNotNull(response);
        assertNotNull(response.getStatusCode());
        assertEquals(HttpStatus.OK, response.getStatusCode());

        JwtResponse jwtResponse = response.getBody();
        assertNotNull(jwtResponse);
        assertNotNull(jwtResponse.accessToken());
        assertNotNull(jwtResponse.refreshToken());

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(jwtResponse.accessToken());

        /*
         * Логинимся под другим администратором.
         * */

        LoginRequest login2 = new LoginRequest("admin2", "password");

        ResponseEntity<JwtResponse> response3 = rest.exchange(
                baseUrl + "/auth/login",
                HttpMethod.POST,
                new HttpEntity<>(login2),
                JwtResponse.class);
        assertNotNull(response3);
        assertNotNull(response3.getStatusCode());
        assertEquals(HttpStatus.OK, response3.getStatusCode());

        JwtResponse jwtResponse2 = response3.getBody();
        assertNotNull(jwtResponse2);
        assertNotNull(jwtResponse2.accessToken());
        assertNotNull(jwtResponse2.refreshToken());

        HttpHeaders headers2 = new HttpHeaders();
        headers2.setBearerAuth(jwtResponse2.accessToken());

        /*
         * Получаем идентификатор администратора.
         * */

        ResponseEntity<UserRD> response4 = rest.exchange(
                baseUrl + "/api/user",
                HttpMethod.GET,
                new HttpEntity<>(headers2),
                UserRD.class
        );
        assertNotNull(response4);
        assertNotNull(response4.getBody());
        assertEquals(HttpStatus.OK, response4.getStatusCode());

        UserRD admin = response4.getBody();
        assertNotNull(admin);
        String adminId = admin.id();
        assertNotNull(adminId);

        /*
         * Пытаемся удалить роль у администратора.
         * */

        //noinspection CatchMayIgnoreException
        try {
            rest.exchange(
                    apiUrl + "/{id}/revoke/{name}",
                    HttpMethod.PATCH,
                    new HttpEntity<>(headers),
                    Void.class,
                    adminId,
                    "ROLE_ADMIN"
            );
            fail("Should throw HttpClientErrorException.UnprocessableContent");

        } catch (HttpClientErrorException.UnprocessableContent e) {
            assertNotNull(e);
            assertNotNull(e.getStatusCode());
            assertEquals(HttpStatus.UNPROCESSABLE_CONTENT, e.getStatusCode());

            ErrorResponse errorResponse = e.getResponseBodyAs(ErrorResponse.class);
            assertNotNull(errorResponse);
            assertNotNull(errorResponse.path());
            assertEquals(apiUrl + "/" + adminId + "/revoke/ROLE_ADMIN", errorResponse.path());
            assertNotNull(errorResponse.httpMethod());
            assertEquals(HttpMethod.PATCH, errorResponse.httpMethod());
            assertNotNull(errorResponse.httpStatus());
            assertEquals(HttpStatus.UNPROCESSABLE_CONTENT, errorResponse.httpStatus());
            assertNotNull(errorResponse.message());
            assertNotNull(errorResponse.exception());
            assertNotNull(errorResponse.timestamp());

        } catch (Exception e) {
            fail("Should throw HttpClientErrorException.UnprocessableContent, but got " + e.getMessage());
        }
    }

    @Test
    @Order(38)
    @DirtiesContext
    @DisplayName("Тестируем удаление роли у себя же")
    public void test38() {

        /*
         * Логинимся под администратором.
         * */

        LoginRequest login = new LoginRequest("admin", "password");

        ResponseEntity<JwtResponse> response = rest.exchange(
                baseUrl + "/auth/login",
                HttpMethod.POST,
                new HttpEntity<>(login),
                JwtResponse.class);
        assertNotNull(response);
        assertNotNull(response.getStatusCode());
        assertEquals(HttpStatus.OK, response.getStatusCode());

        JwtResponse jwtResponse = response.getBody();
        assertNotNull(jwtResponse);
        assertNotNull(jwtResponse.accessToken());
        assertNotNull(jwtResponse.refreshToken());

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(jwtResponse.accessToken());

        /*
         * Получаем идентификатор администратора.
         * */

        ResponseEntity<UserRD> response2 = rest.exchange(
                baseUrl + "/api/user",
                HttpMethod.GET,
                new HttpEntity<>(headers),
                UserRD.class
        );
        assertNotNull(response2);
        assertNotNull(response2.getBody());
        assertEquals(HttpStatus.OK, response2.getStatusCode());

        UserRD admin = response2.getBody();
        assertNotNull(admin);
        String adminId = admin.id();
        assertNotNull(adminId);

        /*
         * Проверяем, что администратор имеет роль администратора.
         * */

        ResponseEntity<SheetDto<UserRD>> response3 = rest.exchange(
                apiUrl,
                HttpMethod.GET,
                new HttpEntity<>(headers),
                new ParameterizedTypeReference<>() {
                }
        );
        assertNotNull(response3);
        assertNotNull(response3.getBody());
        assertEquals(HttpStatus.OK, response3.getStatusCode());

        /*
         * Удаляем роль у администратора.
         * */

        ResponseEntity<Void> response4 = rest.exchange(
                apiUrl + "/{id}/revoke/{name}",
                HttpMethod.PATCH,
                new HttpEntity<>(headers),
                Void.class,
                adminId,
                "ROLE_ADMIN"
        );
        assertNotNull(response4);
        assertNotNull(response4.getStatusCode());
        assertEquals(HttpStatus.NO_CONTENT, response4.getStatusCode());

        /*
         * Логинимся под администратором второй раз.
         * */

        LoginRequest login2 = new LoginRequest("admin", "password");

        ResponseEntity<JwtResponse> response5 = rest.exchange(
                baseUrl + "/auth/login",
                HttpMethod.POST,
                new HttpEntity<>(login2),
                JwtResponse.class);
        assertNotNull(response5);
        assertNotNull(response5.getStatusCode());
        assertEquals(HttpStatus.OK, response5.getStatusCode());

        JwtResponse jwtResponse2 = response5.getBody();
        assertNotNull(jwtResponse2);
        assertNotNull(jwtResponse2.accessToken());
        assertNotNull(jwtResponse2.refreshToken());

        HttpHeaders headers2 = new HttpHeaders();
        headers2.setBearerAuth(jwtResponse2.accessToken());

        /*
         * Проверяем, что администратор теперь не имеет роль администратора.
         * */

        //noinspection CatchMayIgnoreException
        try {
            rest.exchange(
                    apiUrl,
                    HttpMethod.GET,
                    new HttpEntity<>(headers2),
                    new ParameterizedTypeReference<>() {
                    }
            );
            fail("Should throw HttpClientErrorException.Forbidden");

        } catch (HttpClientErrorException.Forbidden e) {
            assertNotNull(e);
            assertNotNull(e.getStatusCode());
            assertEquals(HttpStatus.FORBIDDEN, e.getStatusCode());

            ErrorResponse errorResponse = e.getResponseBodyAs(ErrorResponse.class);
            assertNotNull(errorResponse);
            assertNotNull(errorResponse.path());
            assertEquals(apiUrl, errorResponse.path());
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
    @Order(39)
    @DirtiesContext
    @DisplayName("Тестируем удаление роли пользователя у пользователя")
    public void test39() {

        /*
         * Логинимся под администратором.
         * */

        LoginRequest login = new LoginRequest("admin", "password");

        ResponseEntity<JwtResponse> response = rest.exchange(
                baseUrl + "/auth/login",
                HttpMethod.POST,
                new HttpEntity<>(login),
                JwtResponse.class);
        assertNotNull(response);
        assertNotNull(response.getStatusCode());
        assertEquals(HttpStatus.OK, response.getStatusCode());

        JwtResponse jwtResponse = response.getBody();
        assertNotNull(jwtResponse);
        assertNotNull(jwtResponse.accessToken());
        assertNotNull(jwtResponse.refreshToken());

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(jwtResponse.accessToken());

        /*
         * Логинимся под пользователем.
         * */

        LoginRequest login2 = new LoginRequest("alex_petrov", "password");

        ResponseEntity<JwtResponse> response2 = rest.exchange(
                baseUrl + "/auth/login",
                HttpMethod.POST,
                new HttpEntity<>(login2),
                JwtResponse.class);
        assertNotNull(response2);
        assertNotNull(response2.getStatusCode());
        assertEquals(HttpStatus.OK, response2.getStatusCode());

        JwtResponse jwtResponse2 = response2.getBody();
        assertNotNull(jwtResponse2);
        assertNotNull(jwtResponse2.accessToken());
        assertNotNull(jwtResponse2.refreshToken());

        HttpHeaders headers2 = new HttpHeaders();
        headers2.setBearerAuth(jwtResponse2.accessToken());

        /*
         * Получаем идентификатор пользователя.
         * */

        ResponseEntity<UserRD> response4 = rest.exchange(
                baseUrl + "/api/user",
                HttpMethod.GET,
                new HttpEntity<>(headers2),
                UserRD.class
        );
        assertNotNull(response4);
        assertNotNull(response4.getBody());
        assertEquals(HttpStatus.OK, response4.getStatusCode());

        UserRD user = response4.getBody();
        assertNotNull(user);
        String userId = user.id();
        assertNotNull(userId);

        /*
         * Пытаемся отобрать у пользователя роль пользователя.
         * */

        //noinspection CatchMayIgnoreException
        try {
            rest.exchange(
                    apiUrl + "/{id}/revoke/{name}",
                    HttpMethod.PATCH,
                    new HttpEntity<>(headers),
                    Void.class,
                    userId,
                    "ROLE_USER"
            );
            fail("Should throw HttpClientErrorException.UnprocessableContent");

        } catch (HttpClientErrorException.UnprocessableContent e) {
            assertNotNull(e);
            assertNotNull(e.getStatusCode());
            assertEquals(HttpStatus.UNPROCESSABLE_CONTENT, e.getStatusCode());

            ErrorResponse errorResponse = e.getResponseBodyAs(ErrorResponse.class);
            assertNotNull(errorResponse);
            assertNotNull(errorResponse.path());
            assertEquals(apiUrl + "/" + userId + "/revoke/ROLE_USER", errorResponse.path());
            assertNotNull(errorResponse.httpMethod());
            assertEquals(HttpMethod.PATCH, errorResponse.httpMethod());
            assertNotNull(errorResponse.httpStatus());
            assertEquals(HttpStatus.UNPROCESSABLE_CONTENT, errorResponse.httpStatus());
            assertNotNull(errorResponse.message());
            assertNotNull(errorResponse.exception());
            assertNotNull(errorResponse.timestamp());

        } catch (Exception e) {
            fail("Should throw HttpClientErrorException.UnprocessableContent, but got " + e.getMessage());
        }
    }

    @Test
    @Order(40)
    @DirtiesContext
    @DisplayName("Тестируем идемпотентность удаления роли у пользователя")
    public void test40() {

        /*
         * Логинимся под администратором.
         * */

        LoginRequest login = new LoginRequest("admin", "password");

        ResponseEntity<JwtResponse> response = rest.exchange(
                baseUrl + "/auth/login",
                HttpMethod.POST,
                new HttpEntity<>(login),
                JwtResponse.class);
        assertNotNull(response);
        assertNotNull(response.getStatusCode());
        assertEquals(HttpStatus.OK, response.getStatusCode());

        JwtResponse jwtResponse = response.getBody();
        assertNotNull(jwtResponse);
        assertNotNull(jwtResponse.accessToken());
        assertNotNull(jwtResponse.refreshToken());

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(jwtResponse.accessToken());

        /*
         * Логинимся под пользователем.
         * */

        LoginRequest login2 = new LoginRequest("alex_petrov", "password");

        ResponseEntity<JwtResponse> response2 = rest.exchange(
                baseUrl + "/auth/login",
                HttpMethod.POST,
                new HttpEntity<>(login2),
                JwtResponse.class);
        assertNotNull(response2);
        assertNotNull(response2.getStatusCode());
        assertEquals(HttpStatus.OK, response2.getStatusCode());

        JwtResponse jwtResponse2 = response2.getBody();
        assertNotNull(jwtResponse2);
        assertNotNull(jwtResponse2.accessToken());
        assertNotNull(jwtResponse2.refreshToken());

        HttpHeaders headers2 = new HttpHeaders();
        headers2.setBearerAuth(jwtResponse2.accessToken());

        /*
         * Получаем идентификатор пользователя.
         * */

        ResponseEntity<UserRD> response4 = rest.exchange(
                baseUrl + "/api/user",
                HttpMethod.GET,
                new HttpEntity<>(headers2),
                UserRD.class
        );
        assertNotNull(response4);
        assertNotNull(response4.getBody());
        assertEquals(HttpStatus.OK, response4.getStatusCode());

        UserRD user = response4.getBody();
        assertNotNull(user);
        String userId = user.id();
        assertNotNull(userId);

        /*
         * Удаляем у пользователя роль администратора.
         * */

        ResponseEntity<Void> response5 = rest.exchange(
                apiUrl + "/{id}/revoke/{name}",
                HttpMethod.PATCH,
                new HttpEntity<>(headers),
                Void.class,
                userId,
                "ROLE_ADMIN"
        );
        assertNotNull(response5);
        assertNotNull(response5.getStatusCode());
        assertEquals(HttpStatus.NO_CONTENT, response5.getStatusCode());
    }
}
