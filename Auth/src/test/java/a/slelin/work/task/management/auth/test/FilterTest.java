package a.slelin.work.task.management.auth.test;

import a.slelin.work.task.management.core.dto.SheetDto;
import a.slelin.work.task.management.core.dto.auth.JwtResponse;
import a.slelin.work.task.management.core.dto.auth.LoginRequest;
import a.slelin.work.task.management.core.dto.auth.UserRD;
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

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Fail.fail;
import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DisplayName("Тестируем фильтрацию")
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class FilterTest {

    private static final String alexUUID = "ff80a205-67e1-4d22-b886-1be26e51ee9f";

    private static final String ekaterinaUUID = "5a53277c-487f-4ef8-bd7e-c1256de14785";

    @Autowired
    private RestTemplate rest;

    @LocalServerPort
    private int port;

    private String baseUrl;

    private String usersUrl;

    @BeforeEach
    void beforeEach() {
        baseUrl = "http://localhost:%d".formatted(port);
        usersUrl = baseUrl + "/api/admin/users";
    }

    @Test
    @Order(1)
    @DirtiesContext
    @DisplayName("Тестируем equals с UUID")
    public void test1() {

        LoginRequest login = new LoginRequest("admin", "password");

        ResponseEntity<JwtResponse> response = rest.exchange(
                baseUrl + "/auth/login",
                HttpMethod.POST,
                new HttpEntity<>(login),
                JwtResponse.class
        );
        assertNotNull(response);
        assertNotNull(response.getStatusCode());
        assertEquals(HttpStatus.OK, response.getStatusCode());

        JwtResponse jwtResponse = response.getBody();
        assertNotNull(jwtResponse);
        assertNotNull(jwtResponse.accessToken());
        assertNotNull(jwtResponse.refreshToken());

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(jwtResponse.accessToken());

        FilterChain filters = FilterChain
                .empty()
                .add(Filter.of("id", Operation.EQ, UUID.fromString(alexUUID)));

        ResponseEntity<SheetDto<UserRD>> response2 = rest.exchange(
                usersUrl + "/search",
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
        assertEquals(1, users.size());

        UserRD user = users.getFirst();
        assertNotNull(user);
        assertNotNull(user.id());
        assertEquals(alexUUID, user.id());
    }

    @Test
    @Order(2)
    @DirtiesContext
    @DisplayName("Тестируем equals с UUID (String)")
    public void test2() {

        LoginRequest login = new LoginRequest("admin", "password");

        ResponseEntity<JwtResponse> response = rest.exchange(
                baseUrl + "/auth/login",
                HttpMethod.POST,
                new HttpEntity<>(login),
                JwtResponse.class
        );
        assertNotNull(response);
        assertNotNull(response.getStatusCode());
        assertEquals(HttpStatus.OK, response.getStatusCode());

        JwtResponse jwtResponse = response.getBody();
        assertNotNull(jwtResponse);
        assertNotNull(jwtResponse.accessToken());
        assertNotNull(jwtResponse.refreshToken());

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(jwtResponse.accessToken());

        FilterChain filters = FilterChain
                .empty()
                .add(Filter.of("id", Operation.EQ, alexUUID));

        ResponseEntity<SheetDto<UserRD>> response2 = rest.exchange(
                usersUrl + "/search",
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
        assertEquals(1, users.size());

        UserRD user = users.getFirst();
        assertNotNull(user);
        assertNotNull(user.id());
        assertEquals(alexUUID, user.id());
    }

    @Test
    @Order(3)
    @DirtiesContext
    @DisplayName("Тестируем not equals с UUID")
    public void test3() {

        LoginRequest login = new LoginRequest("admin", "password");

        ResponseEntity<JwtResponse> response = rest.exchange(
                baseUrl + "/auth/login",
                HttpMethod.POST,
                new HttpEntity<>(login),
                JwtResponse.class
        );
        assertNotNull(response);
        assertNotNull(response.getStatusCode());
        assertEquals(HttpStatus.OK, response.getStatusCode());

        JwtResponse jwtResponse = response.getBody();
        assertNotNull(jwtResponse);
        assertNotNull(jwtResponse.accessToken());
        assertNotNull(jwtResponse.refreshToken());

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(jwtResponse.accessToken());

        FilterChain filters = FilterChain
                .empty()
                .add(Filter.of("id", Operation.NEQ, UUID.fromString(alexUUID)));

        ResponseEntity<SheetDto<UserRD>> response2 = rest.exchange(
                usersUrl + "/search",
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
            assertNotEquals(alexUUID, user.id());
        }
    }

    @Test
    @Order(4)
    @DirtiesContext
    @DisplayName("Тестируем not equals с UUID (String)")
    public void test4() {

        LoginRequest login = new LoginRequest("admin", "password");

        ResponseEntity<JwtResponse> response = rest.exchange(
                baseUrl + "/auth/login",
                HttpMethod.POST,
                new HttpEntity<>(login),
                JwtResponse.class
        );
        assertNotNull(response);
        assertNotNull(response.getStatusCode());
        assertEquals(HttpStatus.OK, response.getStatusCode());

        JwtResponse jwtResponse = response.getBody();
        assertNotNull(jwtResponse);
        assertNotNull(jwtResponse.accessToken());
        assertNotNull(jwtResponse.refreshToken());

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(jwtResponse.accessToken());

        FilterChain filters = FilterChain
                .empty()
                .add(Filter.of("id", Operation.NEQ, alexUUID));

        ResponseEntity<SheetDto<UserRD>> response2 = rest.exchange(
                usersUrl + "/search",
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
            assertNotEquals(alexUUID, user.id());
        }
    }

    @Test
    @Order(5)
    @DirtiesContext
    @DisplayName("Тестируем is null с UUID")
    public void test5() {

        LoginRequest login = new LoginRequest("admin", "password");

        ResponseEntity<JwtResponse> response = rest.exchange(
                baseUrl + "/auth/login",
                HttpMethod.POST,
                new HttpEntity<>(login),
                JwtResponse.class
        );
        assertNotNull(response);
        assertNotNull(response.getStatusCode());
        assertEquals(HttpStatus.OK, response.getStatusCode());

        JwtResponse jwtResponse = response.getBody();
        assertNotNull(jwtResponse);
        assertNotNull(jwtResponse.accessToken());
        assertNotNull(jwtResponse.refreshToken());

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(jwtResponse.accessToken());

        FilterChain filters = FilterChain
                .empty()
                .add(Filter.of("id", Operation.IS_NULL));

        ResponseEntity<SheetDto<UserRD>> response2 = rest.exchange(
                usersUrl + "/search",
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
        assertTrue(users.isEmpty());
    }

    @Test
    @Order(6)
    @DirtiesContext
    @DisplayName("Тестируем is not null с UUID")
    public void test6() {

        LoginRequest login = new LoginRequest("admin", "password");

        ResponseEntity<JwtResponse> response = rest.exchange(
                baseUrl + "/auth/login",
                HttpMethod.POST,
                new HttpEntity<>(login),
                JwtResponse.class
        );
        assertNotNull(response);
        assertNotNull(response.getStatusCode());
        assertEquals(HttpStatus.OK, response.getStatusCode());

        JwtResponse jwtResponse = response.getBody();
        assertNotNull(jwtResponse);
        assertNotNull(jwtResponse.accessToken());
        assertNotNull(jwtResponse.refreshToken());

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(jwtResponse.accessToken());

        FilterChain filters = FilterChain
                .empty()
                .add(Filter.of("id", Operation.IS_NOT_NULL));

        ResponseEntity<SheetDto<UserRD>> response2 = rest.exchange(
                usersUrl + "/search",
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
        }
    }

    @Test
    @Order(7)
    @DirtiesContext
    @DisplayName("Тестируем greater с UUID")
    public void test7() {

        LoginRequest login = new LoginRequest("admin", "password");

        ResponseEntity<JwtResponse> response = rest.exchange(
                baseUrl + "/auth/login",
                HttpMethod.POST,
                new HttpEntity<>(login),
                JwtResponse.class
        );
        assertNotNull(response);
        assertNotNull(response.getStatusCode());
        assertEquals(HttpStatus.OK, response.getStatusCode());

        JwtResponse jwtResponse = response.getBody();
        assertNotNull(jwtResponse);
        assertNotNull(jwtResponse.accessToken());
        assertNotNull(jwtResponse.refreshToken());

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(jwtResponse.accessToken());

        FilterChain filters = FilterChain
                .empty()
                .add(Filter.of("id", Operation.GT, UUID.fromString(alexUUID)));

        //noinspection CatchMayIgnoreException
        try {
            rest.exchange(
                    usersUrl + "/search",
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
            assertEquals(usersUrl + "/search", errorResponse.path());
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
    @Order(8)
    @DirtiesContext
    @DisplayName("Тестируем greater or equals с UUID")
    public void test8() {

        LoginRequest login = new LoginRequest("admin", "password");

        ResponseEntity<JwtResponse> response = rest.exchange(
                baseUrl + "/auth/login",
                HttpMethod.POST,
                new HttpEntity<>(login),
                JwtResponse.class
        );
        assertNotNull(response);
        assertNotNull(response.getStatusCode());
        assertEquals(HttpStatus.OK, response.getStatusCode());

        JwtResponse jwtResponse = response.getBody();
        assertNotNull(jwtResponse);
        assertNotNull(jwtResponse.accessToken());
        assertNotNull(jwtResponse.refreshToken());

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(jwtResponse.accessToken());

        FilterChain filters = FilterChain
                .empty()
                .add(Filter.of("id", Operation.GE, UUID.fromString(alexUUID)));

        //noinspection CatchMayIgnoreException
        try {
            rest.exchange(
                    usersUrl + "/search",
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
            assertEquals(usersUrl + "/search", errorResponse.path());
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
    @DisplayName("Тестируем less с UUID")
    public void test9() {

        LoginRequest login = new LoginRequest("admin", "password");

        ResponseEntity<JwtResponse> response = rest.exchange(
                baseUrl + "/auth/login",
                HttpMethod.POST,
                new HttpEntity<>(login),
                JwtResponse.class
        );
        assertNotNull(response);
        assertNotNull(response.getStatusCode());
        assertEquals(HttpStatus.OK, response.getStatusCode());

        JwtResponse jwtResponse = response.getBody();
        assertNotNull(jwtResponse);
        assertNotNull(jwtResponse.accessToken());
        assertNotNull(jwtResponse.refreshToken());

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(jwtResponse.accessToken());

        FilterChain filters = FilterChain
                .empty()
                .add(Filter.of("id", Operation.LT, UUID.fromString(alexUUID)));

        //noinspection CatchMayIgnoreException
        try {
            rest.exchange(
                    usersUrl + "/search",
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
            assertEquals(usersUrl + "/search", errorResponse.path());
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
    @Order(10)
    @DirtiesContext
    @DisplayName("Тестируем less or equals с UUID")
    public void test10() {

        LoginRequest login = new LoginRequest("admin", "password");

        ResponseEntity<JwtResponse> response = rest.exchange(
                baseUrl + "/auth/login",
                HttpMethod.POST,
                new HttpEntity<>(login),
                JwtResponse.class
        );
        assertNotNull(response);
        assertNotNull(response.getStatusCode());
        assertEquals(HttpStatus.OK, response.getStatusCode());

        JwtResponse jwtResponse = response.getBody();
        assertNotNull(jwtResponse);
        assertNotNull(jwtResponse.accessToken());
        assertNotNull(jwtResponse.refreshToken());

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(jwtResponse.accessToken());

        FilterChain filters = FilterChain
                .empty()
                .add(Filter.of("id", Operation.LE, UUID.fromString(alexUUID)));

        //noinspection CatchMayIgnoreException
        try {
            rest.exchange(
                    usersUrl + "/search",
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
            assertEquals(usersUrl + "/search", errorResponse.path());
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
    @Order(11)
    @DirtiesContext
    @DisplayName("Тестируем is empty с UUID")
    public void test11() {

        LoginRequest login = new LoginRequest("admin", "password");

        ResponseEntity<JwtResponse> response = rest.exchange(
                baseUrl + "/auth/login",
                HttpMethod.POST,
                new HttpEntity<>(login),
                JwtResponse.class
        );
        assertNotNull(response);
        assertNotNull(response.getStatusCode());
        assertEquals(HttpStatus.OK, response.getStatusCode());

        JwtResponse jwtResponse = response.getBody();
        assertNotNull(jwtResponse);
        assertNotNull(jwtResponse.accessToken());
        assertNotNull(jwtResponse.refreshToken());

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(jwtResponse.accessToken());

        FilterChain filters = FilterChain
                .empty()
                .add(Filter.of("id", Operation.IS_EMPTY));

        //noinspection CatchMayIgnoreException
        try {
            rest.exchange(
                    usersUrl + "/search",
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
            assertEquals(usersUrl + "/search", errorResponse.path());
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
    @DisplayName("Тестируем is not empty с UUID")
    public void test12() {

        LoginRequest login = new LoginRequest("admin", "password");

        ResponseEntity<JwtResponse> response = rest.exchange(
                baseUrl + "/auth/login",
                HttpMethod.POST,
                new HttpEntity<>(login),
                JwtResponse.class
        );
        assertNotNull(response);
        assertNotNull(response.getStatusCode());
        assertEquals(HttpStatus.OK, response.getStatusCode());

        JwtResponse jwtResponse = response.getBody();
        assertNotNull(jwtResponse);
        assertNotNull(jwtResponse.accessToken());
        assertNotNull(jwtResponse.refreshToken());

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(jwtResponse.accessToken());

        FilterChain filters = FilterChain
                .empty()
                .add(Filter.of("id", Operation.IS_NOT_EMPTY));

        //noinspection CatchMayIgnoreException
        try {
            rest.exchange(
                    usersUrl + "/search",
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
            assertEquals(usersUrl + "/search", errorResponse.path());
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
    @DirtiesContext
    @DisplayName("Тестируем is true с UUID")
    public void test13() {

        LoginRequest login = new LoginRequest("admin", "password");

        ResponseEntity<JwtResponse> response = rest.exchange(
                baseUrl + "/auth/login",
                HttpMethod.POST,
                new HttpEntity<>(login),
                JwtResponse.class
        );
        assertNotNull(response);
        assertNotNull(response.getStatusCode());
        assertEquals(HttpStatus.OK, response.getStatusCode());

        JwtResponse jwtResponse = response.getBody();
        assertNotNull(jwtResponse);
        assertNotNull(jwtResponse.accessToken());
        assertNotNull(jwtResponse.refreshToken());

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(jwtResponse.accessToken());

        FilterChain filters = FilterChain
                .empty()
                .add(Filter.of("id", Operation.IS_TRUE));

        //noinspection CatchMayIgnoreException
        try {
            rest.exchange(
                    usersUrl + "/search",
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
            assertEquals(usersUrl + "/search", errorResponse.path());
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
    @Order(14)
    @DirtiesContext
    @DisplayName("Тестируем is false с UUID")
    public void test14() {

        LoginRequest login = new LoginRequest("admin", "password");

        ResponseEntity<JwtResponse> response = rest.exchange(
                baseUrl + "/auth/login",
                HttpMethod.POST,
                new HttpEntity<>(login),
                JwtResponse.class
        );
        assertNotNull(response);
        assertNotNull(response.getStatusCode());
        assertEquals(HttpStatus.OK, response.getStatusCode());

        JwtResponse jwtResponse = response.getBody();
        assertNotNull(jwtResponse);
        assertNotNull(jwtResponse.accessToken());
        assertNotNull(jwtResponse.refreshToken());

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(jwtResponse.accessToken());

        FilterChain filters = FilterChain
                .empty()
                .add(Filter.of("id", Operation.IS_FALSE));

        //noinspection CatchMayIgnoreException
        try {
            rest.exchange(
                    usersUrl + "/search",
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
            assertEquals(usersUrl + "/search", errorResponse.path());
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
    @Order(15)
    @DirtiesContext
    @DisplayName("Тестируем between с UUID")
    public void test15() {

        LoginRequest login = new LoginRequest("admin", "password");

        ResponseEntity<JwtResponse> response = rest.exchange(
                baseUrl + "/auth/login",
                HttpMethod.POST,
                new HttpEntity<>(login),
                JwtResponse.class
        );
        assertNotNull(response);
        assertNotNull(response.getStatusCode());
        assertEquals(HttpStatus.OK, response.getStatusCode());

        JwtResponse jwtResponse = response.getBody();
        assertNotNull(jwtResponse);
        assertNotNull(jwtResponse.accessToken());
        assertNotNull(jwtResponse.refreshToken());

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(jwtResponse.accessToken());

        FilterChain filters = FilterChain
                .empty()
                .add(Filter.of("id", Operation.BETWEEN,
                        UUID.fromString(alexUUID), UUID.fromString(ekaterinaUUID)));

        //noinspection CatchMayIgnoreException
        try {
            rest.exchange(
                    usersUrl + "/search",
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
            assertEquals(usersUrl + "/search", errorResponse.path());
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
    @Order(16)
    @DirtiesContext
    @DisplayName("Тестируем not between с UUID")
    public void test16() {

        LoginRequest login = new LoginRequest("admin", "password");

        ResponseEntity<JwtResponse> response = rest.exchange(
                baseUrl + "/auth/login",
                HttpMethod.POST,
                new HttpEntity<>(login),
                JwtResponse.class
        );
        assertNotNull(response);
        assertNotNull(response.getStatusCode());
        assertEquals(HttpStatus.OK, response.getStatusCode());

        JwtResponse jwtResponse = response.getBody();
        assertNotNull(jwtResponse);
        assertNotNull(jwtResponse.accessToken());
        assertNotNull(jwtResponse.refreshToken());

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(jwtResponse.accessToken());

        FilterChain filters = FilterChain
                .empty()
                .add(Filter.of("id", Operation.NOT_BETWEEN,
                        UUID.fromString(alexUUID), UUID.fromString(ekaterinaUUID)));

        //noinspection CatchMayIgnoreException
        try {
            rest.exchange(
                    usersUrl + "/search",
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
            assertEquals(usersUrl + "/search", errorResponse.path());
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
    @Order(17)
    @DirtiesContext
    @DisplayName("Тестируем before с UUID")
    public void test17() {

        LoginRequest login = new LoginRequest("admin", "password");

        ResponseEntity<JwtResponse> response = rest.exchange(
                baseUrl + "/auth/login",
                HttpMethod.POST,
                new HttpEntity<>(login),
                JwtResponse.class
        );
        assertNotNull(response);
        assertNotNull(response.getStatusCode());
        assertEquals(HttpStatus.OK, response.getStatusCode());

        JwtResponse jwtResponse = response.getBody();
        assertNotNull(jwtResponse);
        assertNotNull(jwtResponse.accessToken());
        assertNotNull(jwtResponse.refreshToken());

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(jwtResponse.accessToken());

        FilterChain filters = FilterChain
                .empty()
                .add(Filter.of("id", Operation.BEFORE, UUID.fromString(alexUUID)));

        //noinspection CatchMayIgnoreException
        try {
            rest.exchange(
                    usersUrl + "/search",
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
            assertEquals(usersUrl + "/search", errorResponse.path());
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
    @Order(18)
    @DirtiesContext
    @DisplayName("Тестируем after с UUID")
    public void test18() {

        LoginRequest login = new LoginRequest("admin", "password");

        ResponseEntity<JwtResponse> response = rest.exchange(
                baseUrl + "/auth/login",
                HttpMethod.POST,
                new HttpEntity<>(login),
                JwtResponse.class
        );
        assertNotNull(response);
        assertNotNull(response.getStatusCode());
        assertEquals(HttpStatus.OK, response.getStatusCode());

        JwtResponse jwtResponse = response.getBody();
        assertNotNull(jwtResponse);
        assertNotNull(jwtResponse.accessToken());
        assertNotNull(jwtResponse.refreshToken());

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(jwtResponse.accessToken());

        FilterChain filters = FilterChain
                .empty()
                .add(Filter.of("id", Operation.AFTER, UUID.fromString(alexUUID)));

        //noinspection CatchMayIgnoreException
        try {
            rest.exchange(
                    usersUrl + "/search",
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
            assertEquals(usersUrl + "/search", errorResponse.path());
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
    @Order(19)
    @DirtiesContext
    @DisplayName("Тестируем like с UUID")
    public void test19() {

        LoginRequest login = new LoginRequest("admin", "password");

        ResponseEntity<JwtResponse> response = rest.exchange(
                baseUrl + "/auth/login",
                HttpMethod.POST,
                new HttpEntity<>(login),
                JwtResponse.class
        );
        assertNotNull(response);
        assertNotNull(response.getStatusCode());
        assertEquals(HttpStatus.OK, response.getStatusCode());

        JwtResponse jwtResponse = response.getBody();
        assertNotNull(jwtResponse);
        assertNotNull(jwtResponse.accessToken());
        assertNotNull(jwtResponse.refreshToken());

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(jwtResponse.accessToken());

        FilterChain filters = FilterChain
                .empty()
                .add(Filter.of("id", Operation.LIKE, UUID.fromString(alexUUID)));

        ResponseEntity<SheetDto<UserRD>> response2 = rest.exchange(
                usersUrl + "/search",
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
            assertEquals(alexUUID, user.id());
        }
    }

    @Test
    @Order(20)
    @DirtiesContext
    @DisplayName("Тестируем like с UUID (String)")
    public void test20() {

        LoginRequest login = new LoginRequest("admin", "password");

        ResponseEntity<JwtResponse> response = rest.exchange(
                baseUrl + "/auth/login",
                HttpMethod.POST,
                new HttpEntity<>(login),
                JwtResponse.class
        );
        assertNotNull(response);
        assertNotNull(response.getStatusCode());
        assertEquals(HttpStatus.OK, response.getStatusCode());

        JwtResponse jwtResponse = response.getBody();
        assertNotNull(jwtResponse);
        assertNotNull(jwtResponse.accessToken());
        assertNotNull(jwtResponse.refreshToken());

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(jwtResponse.accessToken());

        String patch = alexUUID.substring(2, 7);

        FilterChain filters = FilterChain
                .empty()
                .add(Filter.of("id", Operation.LIKE, patch));

        ResponseEntity<SheetDto<UserRD>> response2 = rest.exchange(
                usersUrl + "/search",
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
            assertTrue(user.id().contains(patch));
        }
    }

    @Test
    @Order(21)
    @DirtiesContext
    @DisplayName("Тестируем not like с UUID")
    public void test21() {

        LoginRequest login = new LoginRequest("admin", "password");

        ResponseEntity<JwtResponse> response = rest.exchange(
                baseUrl + "/auth/login",
                HttpMethod.POST,
                new HttpEntity<>(login),
                JwtResponse.class
        );
        assertNotNull(response);
        assertNotNull(response.getStatusCode());
        assertEquals(HttpStatus.OK, response.getStatusCode());

        JwtResponse jwtResponse = response.getBody();
        assertNotNull(jwtResponse);
        assertNotNull(jwtResponse.accessToken());
        assertNotNull(jwtResponse.refreshToken());

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(jwtResponse.accessToken());

        FilterChain filters = FilterChain
                .empty()
                .add(Filter.of("id", Operation.NOT_LIKE, UUID.fromString(alexUUID)));

        ResponseEntity<SheetDto<UserRD>> response2 = rest.exchange(
                usersUrl + "/search",
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
            assertNotEquals(alexUUID, user.id());
        }
    }


    @Test
    @Order(22)
    @DirtiesContext
    @DisplayName("Тестируем not like с UUID")
    public void test22() {

        LoginRequest login = new LoginRequest("admin", "password");

        ResponseEntity<JwtResponse> response = rest.exchange(
                baseUrl + "/auth/login",
                HttpMethod.POST,
                new HttpEntity<>(login),
                JwtResponse.class
        );
        assertNotNull(response);
        assertNotNull(response.getStatusCode());
        assertEquals(HttpStatus.OK, response.getStatusCode());

        JwtResponse jwtResponse = response.getBody();
        assertNotNull(jwtResponse);
        assertNotNull(jwtResponse.accessToken());
        assertNotNull(jwtResponse.refreshToken());

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(jwtResponse.accessToken());

        String patch = alexUUID.substring(2, 7);

        FilterChain filters = FilterChain
                .empty()
                .add(Filter.of("id", Operation.NOT_LIKE, patch));

        ResponseEntity<SheetDto<UserRD>> response2 = rest.exchange(
                usersUrl + "/search",
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
            assertFalse(user.id().contains(patch));
        }
    }

    @Test
    @Order(23)
    @DirtiesContext
    @DisplayName("Тестируем starts with с UUID")
    public void test23() {

        LoginRequest login = new LoginRequest("admin", "password");

        ResponseEntity<JwtResponse> response = rest.exchange(
                baseUrl + "/auth/login",
                HttpMethod.POST,
                new HttpEntity<>(login),
                JwtResponse.class
        );
        assertNotNull(response);
        assertNotNull(response.getStatusCode());
        assertEquals(HttpStatus.OK, response.getStatusCode());

        JwtResponse jwtResponse = response.getBody();
        assertNotNull(jwtResponse);
        assertNotNull(jwtResponse.accessToken());
        assertNotNull(jwtResponse.refreshToken());

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(jwtResponse.accessToken());

        FilterChain filters = FilterChain
                .empty()
                .add(Filter.of("id", Operation.STARTS_WITH, UUID.fromString(alexUUID)));

        ResponseEntity<SheetDto<UserRD>> response2 = rest.exchange(
                usersUrl + "/search",
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
            assertTrue(user.id().startsWith(alexUUID));
        }
    }

    @Test
    @Order(24)
    @DirtiesContext
    @DisplayName("Тестируем starts with с UUID (String)")
    public void test24() {

        LoginRequest login = new LoginRequest("admin", "password");

        ResponseEntity<JwtResponse> response = rest.exchange(
                baseUrl + "/auth/login",
                HttpMethod.POST,
                new HttpEntity<>(login),
                JwtResponse.class
        );
        assertNotNull(response);
        assertNotNull(response.getStatusCode());
        assertEquals(HttpStatus.OK, response.getStatusCode());

        JwtResponse jwtResponse = response.getBody();
        assertNotNull(jwtResponse);
        assertNotNull(jwtResponse.accessToken());
        assertNotNull(jwtResponse.refreshToken());

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(jwtResponse.accessToken());

        String start = alexUUID.substring(0, 4);

        FilterChain filters = FilterChain
                .empty()
                .add(Filter.of("id", Operation.STARTS_WITH, start));

        ResponseEntity<SheetDto<UserRD>> response2 = rest.exchange(
                usersUrl + "/search",
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
            assertTrue(user.id().startsWith(start));
        }
    }

    @Test
    @Order(25)
    @DirtiesContext
    @DisplayName("Тестируем not starts with с UUID")
    public void test25() {

        LoginRequest login = new LoginRequest("admin", "password");

        ResponseEntity<JwtResponse> response = rest.exchange(
                baseUrl + "/auth/login",
                HttpMethod.POST,
                new HttpEntity<>(login),
                JwtResponse.class
        );
        assertNotNull(response);
        assertNotNull(response.getStatusCode());
        assertEquals(HttpStatus.OK, response.getStatusCode());

        JwtResponse jwtResponse = response.getBody();
        assertNotNull(jwtResponse);
        assertNotNull(jwtResponse.accessToken());
        assertNotNull(jwtResponse.refreshToken());

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(jwtResponse.accessToken());

        FilterChain filters = FilterChain
                .empty()
                .add(Filter.of("id", Operation.NOT_STARTS_WITH, UUID.fromString(alexUUID)));

        ResponseEntity<SheetDto<UserRD>> response2 = rest.exchange(
                usersUrl + "/search",
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
            assertFalse(user.id().startsWith(alexUUID));
        }
    }

    @Test
    @Order(26)
    @DirtiesContext
    @DisplayName("Тестируем not starts with с UUID (String)")
    public void test26() {

        LoginRequest login = new LoginRequest("admin", "password");

        ResponseEntity<JwtResponse> response = rest.exchange(
                baseUrl + "/auth/login",
                HttpMethod.POST,
                new HttpEntity<>(login),
                JwtResponse.class
        );
        assertNotNull(response);
        assertNotNull(response.getStatusCode());
        assertEquals(HttpStatus.OK, response.getStatusCode());

        JwtResponse jwtResponse = response.getBody();
        assertNotNull(jwtResponse);
        assertNotNull(jwtResponse.accessToken());
        assertNotNull(jwtResponse.refreshToken());

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(jwtResponse.accessToken());

        String start = alexUUID.substring(0, 4);

        FilterChain filters = FilterChain
                .empty()
                .add(Filter.of("id", Operation.NOT_STARTS_WITH, start));

        ResponseEntity<SheetDto<UserRD>> response2 = rest.exchange(
                usersUrl + "/search",
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
            assertFalse(user.id().startsWith(start));
        }
    }

    @Test
    @Order(27)
    @DirtiesContext
    @DisplayName("Тестируем ends with с UUID")
    public void test27() {

        LoginRequest login = new LoginRequest("admin", "password");

        ResponseEntity<JwtResponse> response = rest.exchange(
                baseUrl + "/auth/login",
                HttpMethod.POST,
                new HttpEntity<>(login),
                JwtResponse.class
        );
        assertNotNull(response);
        assertNotNull(response.getStatusCode());
        assertEquals(HttpStatus.OK, response.getStatusCode());

        JwtResponse jwtResponse = response.getBody();
        assertNotNull(jwtResponse);
        assertNotNull(jwtResponse.accessToken());
        assertNotNull(jwtResponse.refreshToken());

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(jwtResponse.accessToken());

        FilterChain filters = FilterChain
                .empty()
                .add(Filter.of("id", Operation.ENDS_WITH, UUID.fromString(alexUUID)));

        ResponseEntity<SheetDto<UserRD>> response2 = rest.exchange(
                usersUrl + "/search",
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
            assertTrue(user.id().endsWith(alexUUID));
        }
    }

    @Test
    @Order(28)
    @DirtiesContext
    @DisplayName("Тестируем ends with с UUID (String)")
    public void test28() {

        LoginRequest login = new LoginRequest("admin", "password");

        ResponseEntity<JwtResponse> response = rest.exchange(
                baseUrl + "/auth/login",
                HttpMethod.POST,
                new HttpEntity<>(login),
                JwtResponse.class
        );
        assertNotNull(response);
        assertNotNull(response.getStatusCode());
        assertEquals(HttpStatus.OK, response.getStatusCode());

        JwtResponse jwtResponse = response.getBody();
        assertNotNull(jwtResponse);
        assertNotNull(jwtResponse.accessToken());
        assertNotNull(jwtResponse.refreshToken());

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(jwtResponse.accessToken());

        String end = alexUUID.substring(alexUUID.length() - 4);

        FilterChain filters = FilterChain
                .empty()
                .add(Filter.of("id", Operation.ENDS_WITH, end));

        ResponseEntity<SheetDto<UserRD>> response2 = rest.exchange(
                usersUrl + "/search",
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
            assertTrue(user.id().endsWith(end));
        }
    }

    @Test
    @Order(29)
    @DirtiesContext
    @DisplayName("Тестируем not ends with с UUID")
    public void test29() {

        LoginRequest login = new LoginRequest("admin", "password");

        ResponseEntity<JwtResponse> response = rest.exchange(
                baseUrl + "/auth/login",
                HttpMethod.POST,
                new HttpEntity<>(login),
                JwtResponse.class
        );
        assertNotNull(response);
        assertNotNull(response.getStatusCode());
        assertEquals(HttpStatus.OK, response.getStatusCode());

        JwtResponse jwtResponse = response.getBody();
        assertNotNull(jwtResponse);
        assertNotNull(jwtResponse.accessToken());
        assertNotNull(jwtResponse.refreshToken());

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(jwtResponse.accessToken());

        FilterChain filters = FilterChain
                .empty()
                .add(Filter.of("id", Operation.NOT_ENDS_WITH, UUID.fromString(alexUUID)));

        ResponseEntity<SheetDto<UserRD>> response2 = rest.exchange(
                usersUrl + "/search",
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
            assertFalse(user.id().endsWith(alexUUID));
        }
    }

    @Test
    @Order(30)
    @DirtiesContext
    @DisplayName("Тестируем not ends with с UUID (String)")
    public void test30() {

        LoginRequest login = new LoginRequest("admin", "password");

        ResponseEntity<JwtResponse> response = rest.exchange(
                baseUrl + "/auth/login",
                HttpMethod.POST,
                new HttpEntity<>(login),
                JwtResponse.class
        );
        assertNotNull(response);
        assertNotNull(response.getStatusCode());
        assertEquals(HttpStatus.OK, response.getStatusCode());

        JwtResponse jwtResponse = response.getBody();
        assertNotNull(jwtResponse);
        assertNotNull(jwtResponse.accessToken());
        assertNotNull(jwtResponse.refreshToken());

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(jwtResponse.accessToken());

        String end = alexUUID.substring(alexUUID.length() - 4);

        FilterChain filters = FilterChain
                .empty()
                .add(Filter.of("id", Operation.NOT_ENDS_WITH, end));

        ResponseEntity<SheetDto<UserRD>> response2 = rest.exchange(
                usersUrl + "/search",
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
            assertFalse(user.id().endsWith(end));
        }
    }

    @Test
    @Order(31)
    @DirtiesContext
    @DisplayName("Тестируем in с UUID")
    public void test31() {

        LoginRequest login = new LoginRequest("admin", "password");

        ResponseEntity<JwtResponse> response = rest.exchange(
                baseUrl + "/auth/login",
                HttpMethod.POST,
                new HttpEntity<>(login),
                JwtResponse.class
        );
        assertNotNull(response);
        assertNotNull(response.getStatusCode());
        assertEquals(HttpStatus.OK, response.getStatusCode());

        JwtResponse jwtResponse = response.getBody();
        assertNotNull(jwtResponse);
        assertNotNull(jwtResponse.accessToken());
        assertNotNull(jwtResponse.refreshToken());

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(jwtResponse.accessToken());

        List<UUID> list = new ArrayList<>();
        list.add(UUID.fromString(alexUUID));
        list.add(UUID.fromString(ekaterinaUUID));

        FilterChain filters = FilterChain
                .empty()
                .add(Filter.of("id", Operation.IN, list));

        ResponseEntity<SheetDto<UserRD>> response2 = rest.exchange(
                usersUrl + "/search",
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
            assertTrue(user.id().equals(alexUUID) || user.id().equals(ekaterinaUUID));
        }
    }

    @Test
    @Order(32)
    @DirtiesContext
    @DisplayName("Тестируем in с UUID (String)")
    public void test32() {

        LoginRequest login = new LoginRequest("admin", "password");

        ResponseEntity<JwtResponse> response = rest.exchange(
                baseUrl + "/auth/login",
                HttpMethod.POST,
                new HttpEntity<>(login),
                JwtResponse.class
        );
        assertNotNull(response);
        assertNotNull(response.getStatusCode());
        assertEquals(HttpStatus.OK, response.getStatusCode());

        JwtResponse jwtResponse = response.getBody();
        assertNotNull(jwtResponse);
        assertNotNull(jwtResponse.accessToken());
        assertNotNull(jwtResponse.refreshToken());

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(jwtResponse.accessToken());

        List<String> list = new ArrayList<>();
        list.add(alexUUID);
        list.add(ekaterinaUUID);

        FilterChain filters = FilterChain
                .empty()
                .add(Filter.of("id", Operation.IN, list));

        ResponseEntity<SheetDto<UserRD>> response2 = rest.exchange(
                usersUrl + "/search",
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
            assertTrue(user.id().equals(alexUUID) || user.id().equals(ekaterinaUUID));
        }
    }

    @Test
    @Order(33)
    @DirtiesContext
    @DisplayName("Тестируем not in с UUID")
    public void test33() {

        LoginRequest login = new LoginRequest("admin", "password");

        ResponseEntity<JwtResponse> response = rest.exchange(
                baseUrl + "/auth/login",
                HttpMethod.POST,
                new HttpEntity<>(login),
                JwtResponse.class
        );
        assertNotNull(response);
        assertNotNull(response.getStatusCode());
        assertEquals(HttpStatus.OK, response.getStatusCode());

        JwtResponse jwtResponse = response.getBody();
        assertNotNull(jwtResponse);
        assertNotNull(jwtResponse.accessToken());
        assertNotNull(jwtResponse.refreshToken());

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(jwtResponse.accessToken());

        List<UUID> list = new ArrayList<>();
        list.add(UUID.fromString(alexUUID));
        list.add(UUID.fromString(ekaterinaUUID));

        FilterChain filters = FilterChain
                .empty()
                .add(Filter.of("id", Operation.NOT_IN, list));

        ResponseEntity<SheetDto<UserRD>> response2 = rest.exchange(
                usersUrl + "/search",
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
            assertNotEquals(alexUUID, user.id());
            assertNotEquals(ekaterinaUUID, user.id());
        }
    }

    @Test
    @Order(34)
    @DirtiesContext
    @DisplayName("Тестируем not in с UUID (String)")
    public void test34() {

        LoginRequest login = new LoginRequest("admin", "password");

        ResponseEntity<JwtResponse> response = rest.exchange(
                baseUrl + "/auth/login",
                HttpMethod.POST,
                new HttpEntity<>(login),
                JwtResponse.class
        );
        assertNotNull(response);
        assertNotNull(response.getStatusCode());
        assertEquals(HttpStatus.OK, response.getStatusCode());

        JwtResponse jwtResponse = response.getBody();
        assertNotNull(jwtResponse);
        assertNotNull(jwtResponse.accessToken());
        assertNotNull(jwtResponse.refreshToken());

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(jwtResponse.accessToken());

        List<String> list = new ArrayList<>();
        list.add(alexUUID);
        list.add(ekaterinaUUID);

        FilterChain filters = FilterChain
                .empty()
                .add(Filter.of("id", Operation.NOT_IN, list));

        ResponseEntity<SheetDto<UserRD>> response2 = rest.exchange(
                usersUrl + "/search",
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
            assertNotEquals(alexUUID, user.id());
            assertNotEquals(ekaterinaUUID, user.id());
        }
    }
}
