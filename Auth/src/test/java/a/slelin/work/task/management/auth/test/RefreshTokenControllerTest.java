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

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Fail.fail;
import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DisplayName("Тест контроллера, отвечающего за токены")
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class RefreshTokenControllerTest {

    @Autowired
    private RestTemplate rest;

    @LocalServerPort
    private int port;

    private String baseUrl;

    private String apiUrl;

    @BeforeEach
    void beforeEach() {
        baseUrl = "http://localhost:%d".formatted(port);
        apiUrl = baseUrl + "/api/admin/tokens";
    }

    @Test
    @Order(1)
    @DirtiesContext
    @DisplayName("Не авторизованный пользователь не может обращаться к токенам")
    public void test1() {

        /*
         * Пытаемся получить доступ к токенам.
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
    @DisplayName("Обычный пользователь не может обращаться к токенам")
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
         * Пытаемся получить доступ к токенам.
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
    @DisplayName("Администратор может обращаться к токенам")
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
         * Получаем все токены.
         * */

        ResponseEntity<SheetDto<RefreshTokenRD>> response2 = rest.exchange(
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
    @DisplayName("Тестируем получение всех токенов")
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
         * Получаем все токены.
         * */

        ResponseEntity<SheetDto<RefreshTokenRD>> response2 = rest.exchange(
                apiUrl,
                HttpMethod.GET,
                new HttpEntity<>(headers),
                new ParameterizedTypeReference<>() {
                }
        );
        assertNotNull(response2);
        assertNotNull(response2.getStatusCode());
        assertEquals(HttpStatus.OK, response2.getStatusCode());

        SheetDto<RefreshTokenRD> sheet = response2.getBody();
        assertNotNull(sheet);
        assertNotNull(sheet.page());

        List<RefreshTokenRD> tokens = sheet.content();
        assertNotNull(tokens);
        assertFalse(tokens.isEmpty());

        for (RefreshTokenRD token : tokens) {
            assertNotNull(token);
            assertNotNull(token.id());
            assertNotNull(token.token());
            assertNotNull(token.createdAt());
            assertNotNull(token.expiryDate());

            assertTrue(token.createdAt().isBefore(token.expiryDate()));
        }
    }

    @Test
    @Order(5)
    @DirtiesContext
    @DisplayName("Тестируем получение токена по идентификатору")
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
         * Получаем все токены.
         * */

        ResponseEntity<SheetDto<RefreshTokenRD>> response2 = rest.exchange(
                apiUrl,
                HttpMethod.GET,
                new HttpEntity<>(headers),
                new ParameterizedTypeReference<>() {
                }
        );
        assertNotNull(response2);
        assertNotNull(response2.getStatusCode());
        assertEquals(HttpStatus.OK, response2.getStatusCode());

        SheetDto<RefreshTokenRD> sheet = response2.getBody();
        assertNotNull(sheet);
        assertNotNull(sheet.page());

        List<RefreshTokenRD> tokens = sheet.content();
        assertNotNull(tokens);
        assertFalse(tokens.isEmpty());

        for (RefreshTokenRD token : tokens) {
            assertNotNull(token);
            assertNotNull(token.id());
            assertNotNull(token.token());
            assertNotNull(token.createdAt());
            assertNotNull(token.expiryDate());

            assertTrue(token.createdAt().isBefore(token.expiryDate()));
        }

        RefreshTokenRD token = tokens.getFirst();
        assertNotNull(token);
        String tokenId = token.id();
        assertNotNull(tokenId);

        /*
         * Получаем токен по идентификатору.
         * */

        ResponseEntity<RefreshTokenRD> response3 = rest.exchange(
                apiUrl + "/{id}",
                HttpMethod.GET,
                new HttpEntity<>(headers),
                RefreshTokenRD.class,
                tokenId
        );
        assertNotNull(response3);
        assertNotNull(response3.getStatusCode());
        assertEquals(HttpStatus.OK, response3.getStatusCode());

        RefreshTokenRD token2 = response3.getBody();
        assertNotNull(token2);
        assertEquals(token, token2);
    }

    @Test
    @Order(6)
    @DirtiesContext
    @DisplayName("Тестируем получение токена по плохому идентификатору")
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
         * Пытаемся получить токен по неправильному идентификатору
         * */

        String tokenId = UUID.randomUUID().toString();

        //noinspection CatchMayIgnoreException
        try {
            rest.exchange(
                    apiUrl + "/{id}",
                    HttpMethod.GET,
                    new HttpEntity<>(headers),
                    RefreshTokenRD.class,
                    tokenId
            );
            fail("Should throw HttpClientErrorException.NotFound");

        } catch (HttpClientErrorException.NotFound e) {
            assertNotNull(e);
            assertNotNull(e.getStatusCode());
            assertEquals(HttpStatus.NOT_FOUND, e.getStatusCode());

            ErrorResponse errorResponse = e.getResponseBodyAs(ErrorResponse.class);
            assertNotNull(errorResponse);
            assertNotNull(errorResponse.path());
            assertEquals(apiUrl + "/" + tokenId, errorResponse.path());
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
    @DisplayName("Тестируем получение токенов по пользователю")
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
         * Получаем идентификатор администратора.
         * */

        ResponseEntity<UserRD> response2 = rest.exchange(
                baseUrl + "/api/user",
                HttpMethod.GET,
                new HttpEntity<>(headers),
                UserRD.class
        );
        assertNotNull(response2);
        assertNotNull(response2.getStatusCode());
        assertEquals(HttpStatus.OK, response2.getStatusCode());

        UserRD user = response2.getBody();
        assertNotNull(user);
        String userId = user.id();
        assertNotNull(userId);

        /*
         * Получаем все токены администратора.
         * */

        ResponseEntity<SheetDto<RefreshTokenRD>> response3 = rest.exchange(
                apiUrl + "/user/{id}",
                HttpMethod.GET,
                new HttpEntity<>(headers),
                new ParameterizedTypeReference<>() {
                },
                userId
        );
        assertNotNull(response3);
        assertNotNull(response3.getStatusCode());
        assertEquals(HttpStatus.OK, response3.getStatusCode());

        SheetDto<RefreshTokenRD> sheet = response3.getBody();
        assertNotNull(sheet);
        assertNotNull(sheet.page());

        List<RefreshTokenRD> tokens = sheet.content();
        assertNotNull(tokens);
        assertFalse(tokens.isEmpty());

        boolean flag = false;

        for (RefreshTokenRD token : tokens) {
            assertNotNull(token);
            assertNotNull(token.id());
            assertNotNull(token.token());
            assertNotNull(token.createdAt());
            assertNotNull(token.expiryDate());

            assertTrue(token.createdAt().isBefore(token.expiryDate()));

            if (token.token().equals(jwtResponse.refreshToken())) {
                flag = true;
            }
        }

        assertTrue(flag);
    }

    @Test
    @Order(8)
    @DirtiesContext
    @DisplayName("Тестируем получение токенов по плохому пользователю")
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
         * Пытаемся получить все токены некорректного пользователя.
         * */

        String userId = UUID.randomUUID().toString();

        //noinspection CatchMayIgnoreException
        try {
            rest.exchange(
                    apiUrl + "/user/{id}",
                    HttpMethod.GET,
                    new HttpEntity<>(headers),
                    RefreshTokenRD.class,
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
            assertEquals(apiUrl + "/user/" + userId, errorResponse.path());
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
    @Order(9)
    @DirtiesContext
    @DisplayName("Тестируем получение токенов по фильтру")
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
         * Получаем сессию администратора через фильтр.
         * */

        FilterChain filters = FilterChain
                .empty()
                .add(Filter.of("token", Operation.EQ, jwtResponse.refreshToken()));

        ResponseEntity<SheetDto<RefreshTokenRD>> response2 = rest.exchange(
                apiUrl + "/search",
                HttpMethod.POST,
                new HttpEntity<>(filters, headers),
                new ParameterizedTypeReference<>() {
                }
        );
        assertNotNull(response2);
        assertNotNull(response2.getStatusCode());
        assertEquals(HttpStatus.OK, response2.getStatusCode());

        SheetDto<RefreshTokenRD> sheet = response2.getBody();
        assertNotNull(sheet);
        assertNotNull(sheet.page());

        List<RefreshTokenRD> tokens = sheet.content();
        assertNotNull(tokens);
        assertFalse(tokens.isEmpty());

        boolean flag = true;

        for (RefreshTokenRD token : tokens) {
            assertNotNull(token);
            assertNotNull(token.id());
            assertNotNull(token.token());
            assertNotNull(token.createdAt());
            assertNotNull(token.expiryDate());

            assertTrue(token.createdAt().isBefore(token.expiryDate()));

            if (!token.token().equals(jwtResponse.refreshToken())) {
                flag = false;
            }
        }

        assertTrue(flag);
    }

    @Test
    @Order(10)
    @DirtiesContext
    @DisplayName("Тестируем получение токенов по плохому фильтру")
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
         * Пытаемся получить токены администратора с плохим фильтром.
         * */

        FilterChain filters = FilterChain
                .empty()
                .add(Filter.of("badName", Operation.EQ, jwtResponse.refreshToken()));

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
    @Order(11)
    @DirtiesContext
    @DisplayName("Тестируем удаление токена у обычного пользователя")
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
         * Логинимся под обычным пользователем.
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
        headers2.setBearerAuth(jwtResponse2.refreshToken());

        /*
         * Проверяем, что пользователь может обновить свой токен доступа.
         * */

        ResponseEntity<JwtResponse> response3 = rest.exchange(
                baseUrl + "/auth/refresh",
                HttpMethod.GET,
                new HttpEntity<>(headers2),
                JwtResponse.class
        );
        assertNotNull(response3);
        assertNotNull(response3.getStatusCode());
        assertEquals(HttpStatus.OK, response3.getStatusCode());

        JwtResponse jwtResponse3 = response3.getBody();
        assertNotNull(jwtResponse3);
        assertNotNull(jwtResponse3.accessToken());
        assertNotNull(jwtResponse3.refreshToken());

        assertNotEquals(jwtResponse2.accessToken(), jwtResponse3.accessToken());
        assertEquals(jwtResponse2.refreshToken(), jwtResponse3.refreshToken());

        /*
         * Получаем идентификатор токена.
         * */

        FilterChain filters = FilterChain
                .empty()
                .add(Filter.of("token", Operation.EQ, jwtResponse2.refreshToken()));

        ResponseEntity<SheetDto<RefreshTokenRD>> response4 = rest.exchange(
                apiUrl + "/search",
                HttpMethod.POST,
                new HttpEntity<>(filters, headers),
                new ParameterizedTypeReference<>() {
                }
        );
        assertNotNull(response4);
        assertNotNull(response4.getStatusCode());
        assertEquals(HttpStatus.OK, response4.getStatusCode());

        SheetDto<RefreshTokenRD> sheet = response4.getBody();
        assertNotNull(sheet);
        assertNotNull(sheet.page());

        List<RefreshTokenRD> tokens = sheet.content();
        assertNotNull(tokens);
        assertFalse(tokens.isEmpty());
        assertEquals(1, tokens.size());

        RefreshTokenRD token = tokens.getFirst();
        assertNotNull(token);
        String tokenId = token.id();
        assertNotNull(tokenId);

        /*
         * Завершаем сессию пользователя по идентификатору токена.
         * */

        ResponseEntity<Void> response5 = rest.exchange(
                apiUrl + "/{id}",
                HttpMethod.DELETE,
                new HttpEntity<>(headers),
                Void.class,
                tokenId
        );
        assertNotNull(response5);
        assertNotNull(response5.getStatusCode());
        assertEquals(HttpStatus.NO_CONTENT, response5.getStatusCode());

        /*
         * Проверяем, что пользователь больше не может обновить свой токен,
         * так как его сессия была завершена.
         * */

        //noinspection CatchMayIgnoreException
        try {
            rest.exchange(
                    baseUrl + "/auth/refresh",
                    HttpMethod.GET,
                    new HttpEntity<>(headers2),
                    JwtResponse.class
            );
            fail("Should throw HttpClientErrorException.Unauthorized");

        } catch (HttpClientErrorException.Unauthorized e) {
            assertNotNull(e);
            assertNotNull(e.getStatusCode());
            assertEquals(HttpStatus.UNAUTHORIZED, e.getStatusCode());

            ErrorResponse errorResponse = e.getResponseBodyAs(ErrorResponse.class);
            assertNotNull(errorResponse);
            assertNotNull(errorResponse.path());
            assertEquals(baseUrl + "/auth/refresh", errorResponse.path());
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
    @Order(12)
    @DirtiesContext
    @DisplayName("Тестируем удаление плохого токена у обычного пользователя")
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
         * Пытаемся завершить сессию пользователя по плохому идентификатору токена.
         * */

        String tokenId = UUID.randomUUID().toString();

        //noinspection CatchMayIgnoreException
        try {
            rest.exchange(
                    apiUrl + "/{id}",
                    HttpMethod.DELETE,
                    new HttpEntity<>(headers),
                    Void.class,
                    tokenId
            );
            fail("Should throw HttpClientErrorException.NotFound");

        } catch (HttpClientErrorException.NotFound e) {
            assertNotNull(e);
            assertNotNull(e.getStatusCode());
            assertEquals(HttpStatus.NOT_FOUND, e.getStatusCode());

            ErrorResponse errorResponse = e.getResponseBodyAs(ErrorResponse.class);
            assertNotNull(errorResponse);
            assertNotNull(errorResponse.path());
            assertEquals(apiUrl + "/" + tokenId, errorResponse.path());
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
    @Order(13)
    @DirtiesContext
    @DisplayName("Тестируем удаление токена у администратора")
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
        headers2.setBearerAuth(jwtResponse2.refreshToken());

        /*
         * Получаем идентификатор токена второго администратора.
         * */

        FilterChain filters = FilterChain
                .empty()
                .add(Filter.of("token", Operation.EQ, jwtResponse2.refreshToken()));

        ResponseEntity<SheetDto<RefreshTokenRD>> response4 = rest.exchange(
                apiUrl + "/search",
                HttpMethod.POST,
                new HttpEntity<>(filters, headers),
                new ParameterizedTypeReference<>() {
                }
        );
        assertNotNull(response4);
        assertNotNull(response4.getStatusCode());
        assertEquals(HttpStatus.OK, response4.getStatusCode());

        SheetDto<RefreshTokenRD> sheet = response4.getBody();
        assertNotNull(sheet);
        assertNotNull(sheet.page());

        List<RefreshTokenRD> tokens = sheet.content();
        assertNotNull(tokens);
        assertFalse(tokens.isEmpty());
        assertEquals(1, tokens.size());

        RefreshTokenRD token = tokens.getFirst();
        assertNotNull(token);
        String tokenId = token.id();
        assertNotNull(tokenId);

        /*
         * Проверяем, что не можем завершить сессию другого администратора.
         * */

        //noinspection CatchMayIgnoreException
        try {
            rest.exchange(
                    apiUrl + "/{id}",
                    HttpMethod.DELETE,
                    new HttpEntity<>(headers),
                    Void.class,
                    tokenId
            );
            fail("Should throw HttpClientErrorException.UnprocessableContent");

        } catch (HttpClientErrorException.UnprocessableContent e) {
            assertNotNull(e);
            assertNotNull(e.getStatusCode());
            assertEquals(HttpStatus.UNPROCESSABLE_CONTENT, e.getStatusCode());

            ErrorResponse errorResponse = e.getResponseBodyAs(ErrorResponse.class);
            assertNotNull(errorResponse);
            assertNotNull(errorResponse.path());
            assertEquals(apiUrl + "/" + tokenId, errorResponse.path());
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
    @Order(14)
    @DirtiesContext
    @DisplayName("Тестируем удаление токена у себя же")
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
         * Получаем идентификатор токена.
         * */

        FilterChain filters = FilterChain
                .empty()
                .add(Filter.of("token", Operation.EQ, jwtResponse.refreshToken()));

        ResponseEntity<SheetDto<RefreshTokenRD>> response2 = rest.exchange(
                apiUrl + "/search",
                HttpMethod.POST,
                new HttpEntity<>(filters, headers),
                new ParameterizedTypeReference<>() {
                }
        );
        assertNotNull(response2);
        assertNotNull(response2.getStatusCode());
        assertEquals(HttpStatus.OK, response2.getStatusCode());

        SheetDto<RefreshTokenRD> sheet = response2.getBody();
        assertNotNull(sheet);
        assertNotNull(sheet.page());

        List<RefreshTokenRD> tokens = sheet.content();
        assertNotNull(tokens);
        assertFalse(tokens.isEmpty());
        assertEquals(1, tokens.size());

        RefreshTokenRD token = tokens.getFirst();
        assertNotNull(token);
        String tokenId = token.id();
        assertNotNull(tokenId);

        /*
         * Проверяем, что не можем завершить свою (администраторскую) сессию.
         * */

        //noinspection CatchMayIgnoreException
        try {
            rest.exchange(
                    apiUrl + "/{id}",
                    HttpMethod.DELETE,
                    new HttpEntity<>(headers),
                    Void.class,
                    tokenId
            );
            fail("Should throw HttpClientErrorException.UnprocessableContent");

        } catch (HttpClientErrorException.UnprocessableContent e) {
            assertNotNull(e);
            assertNotNull(e.getStatusCode());
            assertEquals(HttpStatus.UNPROCESSABLE_CONTENT, e.getStatusCode());

            ErrorResponse errorResponse = e.getResponseBodyAs(ErrorResponse.class);
            assertNotNull(errorResponse);
            assertNotNull(errorResponse.path());
            assertEquals(apiUrl + "/" + tokenId, errorResponse.path());
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
    @Order(15)
    @DirtiesContext
    @DisplayName("Тестируем удаление всех токенов у обычного пользователя")
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
         * Логинимся под обычным пользователем.
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
        headers2.setBearerAuth(jwtResponse2.refreshToken());

        /*
         * Входим в систему под тем же пользователем второй раз.
         * */

        LoginRequest login3 = new LoginRequest("alex_petrov", "password");

        ResponseEntity<JwtResponse> response3 = rest.exchange(
                baseUrl + "/auth/login",
                HttpMethod.POST,
                new HttpEntity<>(login3),
                JwtResponse.class);
        assertNotNull(response3);
        assertNotNull(response3.getStatusCode());
        assertEquals(HttpStatus.OK, response3.getStatusCode());

        JwtResponse jwtResponse3 = response3.getBody();
        assertNotNull(jwtResponse3);
        assertNotNull(jwtResponse3.accessToken());
        assertNotNull(jwtResponse3.refreshToken());

        HttpHeaders headers3 = new HttpHeaders();
        headers3.setBearerAuth(jwtResponse3.refreshToken());

        /*
         * Входим в систему под тем же пользователем третий раз.
         * */

        LoginRequest login4 = new LoginRequest("alex_petrov", "password");

        ResponseEntity<JwtResponse> response4 = rest.exchange(
                baseUrl + "/auth/login",
                HttpMethod.POST,
                new HttpEntity<>(login4),
                JwtResponse.class);
        assertNotNull(response4);
        assertNotNull(response4.getStatusCode());
        assertEquals(HttpStatus.OK, response4.getStatusCode());

        JwtResponse jwtResponse4 = response4.getBody();
        assertNotNull(jwtResponse4);
        assertNotNull(jwtResponse4.accessToken());
        assertNotNull(jwtResponse4.refreshToken());

        HttpHeaders headers4 = new HttpHeaders();
        headers4.setBearerAuth(jwtResponse4.refreshToken());

        /*
         * Проверяем, что у пользователя три разных сессии.
         * */

        assertNotEquals(jwtResponse2.accessToken(), jwtResponse3.accessToken());
        assertNotEquals(jwtResponse2.accessToken(), jwtResponse4.accessToken());
        assertNotEquals(jwtResponse3.accessToken(), jwtResponse4.accessToken());

        assertNotEquals(jwtResponse2.refreshToken(), jwtResponse3.refreshToken());
        assertNotEquals(jwtResponse2.refreshToken(), jwtResponse4.refreshToken());
        assertNotEquals(jwtResponse3.refreshToken(), jwtResponse4.refreshToken());

        /*
         * Проверяем, что пользователь может обновить свой токен доступа по 1ой сессии.
         * */

        ResponseEntity<JwtResponse> response5 = rest.exchange(
                baseUrl + "/auth/refresh",
                HttpMethod.GET,
                new HttpEntity<>(headers2),
                JwtResponse.class
        );
        assertNotNull(response5);
        assertNotNull(response5.getStatusCode());
        assertEquals(HttpStatus.OK, response5.getStatusCode());

        JwtResponse jwtResponse5 = response5.getBody();
        assertNotNull(jwtResponse5);
        assertNotNull(jwtResponse5.accessToken());
        assertNotNull(jwtResponse5.refreshToken());

        assertNotEquals(jwtResponse2.accessToken(), jwtResponse5.accessToken());
        assertEquals(jwtResponse2.refreshToken(), jwtResponse5.refreshToken());

        /*
         * Проверяем, что пользователь может обновить свой токен доступа по 2ой сессии.
         * */

        ResponseEntity<JwtResponse> response6 = rest.exchange(
                baseUrl + "/auth/refresh",
                HttpMethod.GET,
                new HttpEntity<>(headers3),
                JwtResponse.class
        );
        assertNotNull(response6);
        assertNotNull(response6.getStatusCode());
        assertEquals(HttpStatus.OK, response6.getStatusCode());

        JwtResponse jwtResponse6 = response6.getBody();
        assertNotNull(jwtResponse6);
        assertNotNull(jwtResponse6.accessToken());
        assertNotNull(jwtResponse6.refreshToken());

        assertNotEquals(jwtResponse3.accessToken(), jwtResponse6.accessToken());
        assertEquals(jwtResponse3.refreshToken(), jwtResponse6.refreshToken());

        /*
         * Проверяем, что пользователь может обновить свой токен доступа по 3ой сессии.
         * */

        ResponseEntity<JwtResponse> response7 = rest.exchange(
                baseUrl + "/auth/refresh",
                HttpMethod.GET,
                new HttpEntity<>(headers4),
                JwtResponse.class
        );
        assertNotNull(response7);
        assertNotNull(response7.getStatusCode());
        assertEquals(HttpStatus.OK, response7.getStatusCode());

        JwtResponse jwtResponse7 = response7.getBody();
        assertNotNull(jwtResponse7);
        assertNotNull(jwtResponse7.accessToken());
        assertNotNull(jwtResponse7.refreshToken());

        assertNotEquals(jwtResponse4.accessToken(), jwtResponse7.accessToken());
        assertEquals(jwtResponse4.refreshToken(), jwtResponse7.refreshToken());

        /*
         * Получаем идентификатор пользователя.
         * */

        ResponseEntity<UserRD> response8 = rest.exchange(
                baseUrl + "/api/admin/users/factor/{factor}",
                HttpMethod.GET,
                new HttpEntity<>(headers),
                UserRD.class,
                "alex_petrov"
        );
        assertNotNull(response8);
        assertNotNull(response8.getStatusCode());
        assertEquals(HttpStatus.OK, response8.getStatusCode());

        UserRD user = response8.getBody();
        assertNotNull(user);
        String userId = user.id();
        assertNotNull(userId);

        /*
         * Завершаем все сессии пользователя.
         * */

        ResponseEntity<Void> response9 = rest.exchange(
                apiUrl + "/user/{id}",
                HttpMethod.DELETE,
                new HttpEntity<>(headers),
                Void.class,
                userId
        );
        assertNotNull(response9);
        assertNotNull(response9.getStatusCode());
        assertEquals(HttpStatus.NO_CONTENT, response9.getStatusCode());

        /*
         * Проверяем, что пользователь больше не может обновить свой токен,
         * так как его сессия была завершена. 1я сессия.
         * */

        //noinspection CatchMayIgnoreException
        try {
            rest.exchange(
                    baseUrl + "/auth/refresh",
                    HttpMethod.GET,
                    new HttpEntity<>(headers2),
                    JwtResponse.class
            );
            fail("Should throw HttpClientErrorException.Unauthorized");

        } catch (HttpClientErrorException.Unauthorized e) {
            assertNotNull(e);
            assertNotNull(e.getStatusCode());
            assertEquals(HttpStatus.UNAUTHORIZED, e.getStatusCode());

            ErrorResponse errorResponse = e.getResponseBodyAs(ErrorResponse.class);
            assertNotNull(errorResponse);
            assertNotNull(errorResponse.path());
            assertEquals(baseUrl + "/auth/refresh", errorResponse.path());
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

        /*
         * Проверяем, что пользователь больше не может обновить свой токен,
         * так как его сессия была завершена. 2я сессия.
         * */

        //noinspection CatchMayIgnoreException
        try {
            rest.exchange(
                    baseUrl + "/auth/refresh",
                    HttpMethod.GET,
                    new HttpEntity<>(headers3),
                    JwtResponse.class
            );
            fail("Should throw HttpClientErrorException.Unauthorized");

        } catch (HttpClientErrorException.Unauthorized e) {
            assertNotNull(e);
            assertNotNull(e.getStatusCode());
            assertEquals(HttpStatus.UNAUTHORIZED, e.getStatusCode());

            ErrorResponse errorResponse = e.getResponseBodyAs(ErrorResponse.class);
            assertNotNull(errorResponse);
            assertNotNull(errorResponse.path());
            assertEquals(baseUrl + "/auth/refresh", errorResponse.path());
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

        /*
         * Проверяем, что пользователь больше не может обновить свой токен,
         * так как его сессия была завершена. 3я сессия.
         * */

        //noinspection CatchMayIgnoreException
        try {
            rest.exchange(
                    baseUrl + "/auth/refresh",
                    HttpMethod.GET,
                    new HttpEntity<>(headers4),
                    JwtResponse.class
            );
            fail("Should throw HttpClientErrorException.Unauthorized");

        } catch (HttpClientErrorException.Unauthorized e) {
            assertNotNull(e);
            assertNotNull(e.getStatusCode());
            assertEquals(HttpStatus.UNAUTHORIZED, e.getStatusCode());

            ErrorResponse errorResponse = e.getResponseBodyAs(ErrorResponse.class);
            assertNotNull(errorResponse);
            assertNotNull(errorResponse.path());
            assertEquals(baseUrl + "/auth/refresh", errorResponse.path());
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
    @Order(16)
    @DirtiesContext
    @DisplayName("Тестируем удаление всех токенов у обычного плохого пользователя")
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
         * Пытаемся завершить все сессии некорректного пользователя.
         * */

        String userId = UUID.randomUUID().toString();

        //noinspection CatchMayIgnoreException
        try {
            rest.exchange(
                    apiUrl + "/user/{id}",
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
            assertEquals(apiUrl + "/user/" + userId, errorResponse.path());
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
    @Order(17)
    @DirtiesContext
    @DisplayName("Тестируем удаление всех токенов у администратора")
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
        assertNotNull(response3.getStatusCode());
        assertEquals(HttpStatus.OK, response3.getStatusCode());

        UserRD admin2 = response3.getBody();
        assertNotNull(admin2);
        String admin2Id = admin2.id();
        assertNotNull(admin2Id);

        /*
         * Пытаемся завершить все сессии второго администратора.
         * */

        //noinspection CatchMayIgnoreException
        try {
            rest.exchange(
                    apiUrl + "/user/{id}",
                    HttpMethod.DELETE,
                    new HttpEntity<>(headers),
                    Void.class,
                    admin2Id
            );
            fail("Should throw HttpClientErrorException.UnprocessableContent");

        } catch (HttpClientErrorException.UnprocessableContent e) {
            assertNotNull(e);
            assertNotNull(e.getStatusCode());
            assertEquals(HttpStatus.UNPROCESSABLE_CONTENT, e.getStatusCode());

            ErrorResponse errorResponse = e.getResponseBodyAs(ErrorResponse.class);
            assertNotNull(errorResponse);
            assertNotNull(errorResponse.path());
            assertEquals(apiUrl + "/user/" + admin2Id, errorResponse.path());
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
    @Order(18)
    @DirtiesContext
    @DisplayName("Тестируем удаление всех токенов у себя же")
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
         * Получаем идентификатор администратора.
         * */

        ResponseEntity<UserRD> response2 = rest.exchange(
                baseUrl + "/api/user",
                HttpMethod.GET,
                new HttpEntity<>(headers),
                UserRD.class
        );
        assertNotNull(response2);
        assertNotNull(response2.getStatusCode());
        assertEquals(HttpStatus.OK, response2.getStatusCode());

        UserRD admin = response2.getBody();
        assertNotNull(admin);
        String adminId = admin.id();
        assertNotNull(adminId);

        /*
         * Пытаемся завершить все сессии администратора.
         * */

        //noinspection CatchMayIgnoreException
        try {
            rest.exchange(
                    apiUrl + "/user/{id}",
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
            assertEquals(apiUrl + "/user/" + adminId, errorResponse.path());
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
}
