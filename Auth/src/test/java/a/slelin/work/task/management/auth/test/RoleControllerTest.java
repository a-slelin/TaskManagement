package a.slelin.work.task.management.auth.test;

import a.slelin.work.task.management.core.dto.SheetDto;
import a.slelin.work.task.management.core.dto.auth.JwtResponse;
import a.slelin.work.task.management.core.dto.auth.LoginRequest;
import a.slelin.work.task.management.core.dto.auth.RoleRD;
import a.slelin.work.task.management.core.dto.auth.RoleWD;
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

import static org.assertj.core.api.Fail.fail;
import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test")
@DisplayName("Тестируем RoleController")
@SuppressWarnings("CatchMayIgnoreException")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class RoleControllerTest {

    @Autowired
    private RestTemplate rest;

    @LocalServerPort
    private int port;

    private String baseUrl;

    private String apiUrl;

    @BeforeEach
    void beforeEach() {
        baseUrl = "http://localhost:%d".formatted(port);
        apiUrl = baseUrl + "/api/admin/roles";
    }

    @Test
    @Order(1)
    @DirtiesContext
    @DisplayName("Тестируем GET /api/admin/roles с неавторизованным пользователем : ошибка 401 неавторизован")
    public void test1() {

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
    @DisplayName("Тестируем GET /api/admin/roles с авторизованным пользователем : ошибка 403 запрещено")
    public void test2() {

        /*
         * Логинимся под пользователем алекс.
         * */

        LoginRequest login = new LoginRequest("alex_petrov", "password");

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

        /*
         * Пытаемся получить все роли.
         * */

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
    @DisplayName("Тестируем GET /api/admin/roles с администратором : успех")
    public void test3() {

        /*
         * Логинимся под администратором.
         * */

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

        /*
         * Получаем все роли.
         * */

        ResponseEntity<SheetDto<RoleRD>> response2 = rest.exchange(
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
    @DisplayName("Тестируем GET /api/admin/roles : успех")
    public void test4() {

        /*
         * Логинимся под администратором.
         * */

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

        /*
         * Получаем все роли.
         * */

        ResponseEntity<SheetDto<RoleRD>> response2 = rest.exchange(
                apiUrl,
                HttpMethod.GET,
                new HttpEntity<>(headers),
                new ParameterizedTypeReference<>() {
                }
        );
        assertNotNull(response2);
        assertNotNull(response2.getStatusCode());
        assertEquals(HttpStatus.OK, response2.getStatusCode());

        SheetDto<RoleRD> sheet = response2.getBody();
        assertNotNull(sheet);
        assertNotNull(sheet.page());

        List<RoleRD> roles = sheet.content();
        assertNotNull(roles);
        assertFalse(roles.isEmpty());
        assertTrue(roles.size() >= 2);

        boolean flagUser = false, flagAdmin = false;

        for (RoleRD role : roles) {
            assertNotNull(role.id());
            assertNotNull(role.name());

            if ("ROLE_USER".equals(role.name())) {
                flagUser = true;
            }

            if ("ROLE_ADMIN".equals(role.name())) {
                flagAdmin = true;
            }
        }

        assertTrue(flagUser);
        assertTrue(flagAdmin);
    }

    @Test
    @Order(5)
    @DirtiesContext
    @DisplayName("Тестируем GET /api/admin/roles/{id} : успех")
    public void test5() {

        /*
         * Логинимся под администратором.
         * */

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

        /*
         * Получаем все роли.
         * */

        ResponseEntity<SheetDto<RoleRD>> response2 = rest.exchange(
                apiUrl,
                HttpMethod.GET,
                new HttpEntity<>(headers),
                new ParameterizedTypeReference<>() {
                }
        );
        assertNotNull(response2);
        assertNotNull(response2.getStatusCode());
        assertEquals(HttpStatus.OK, response2.getStatusCode());

        SheetDto<RoleRD> sheet = response2.getBody();
        assertNotNull(sheet);
        assertNotNull(sheet.page());

        List<RoleRD> roles = sheet.content();
        assertNotNull(roles);
        RoleRD role = roles.getFirst();
        assertNotNull(role);
        assertNotNull(role.id());
        assertNotNull(role.name());

        /*
         * Получаем роль по идентификатору.
         * */

        ResponseEntity<RoleRD> response3 = rest.exchange(
                apiUrl + "/{id}",
                HttpMethod.GET,
                new HttpEntity<>(headers),
                RoleRD.class,
                role.id()

        );
        assertNotNull(response3);
        assertNotNull(response3.getStatusCode());
        assertEquals(HttpStatus.OK, response3.getStatusCode());

        RoleRD roleById = response3.getBody();
        assertNotNull(roleById);
        assertNotNull(roleById.id());
        assertEquals(role.id(), roleById.id());
        assertNotNull(roleById.name());
        assertEquals(role.name(), roleById.name());
    }

    @Test
    @Order(6)
    @DirtiesContext
    @DisplayName("Тестируем GET /api/admin/roles/name/{name} : успех")
    public void test6() {

        /*
         * Логинимся под администратором.
         * */

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

        /*
         * Получаем роль по имени.
         * */

        ResponseEntity<RoleRD> response2 = rest.exchange(
                apiUrl + "/name/ROLE_USER",
                HttpMethod.GET,
                new HttpEntity<>(headers),
                RoleRD.class
        );
        assertNotNull(response2);
        assertNotNull(response2.getStatusCode());
        assertEquals(HttpStatus.OK, response2.getStatusCode());

        RoleRD role = response2.getBody();
        assertNotNull(role);
        assertNotNull(role.id());
        assertNotNull(role.name());
        assertEquals("ROLE_USER", role.name());
    }

    @Test
    @Order(7)
    @DirtiesContext
    @DisplayName("Тестируем POST /api/admin/roles/search : успех")
    public void test7() {

        /*
         * Логинимся под администратором.
         * */

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

        /*
         * Получаем роли по фильтру.
         * */

        FilterChain filters = FilterChain.empty();
        filters.add(Filter.of("name", Operation.STARTS_WITH, "ROLE_"));
        filters.add(Filter.of("name", Operation.LIKE, "US"));

        ResponseEntity<SheetDto<RoleRD>> response2 = rest.exchange(
                apiUrl + "/search",
                HttpMethod.POST,
                new HttpEntity<>(filters, headers),
                new ParameterizedTypeReference<>() {
                }
        );
        assertNotNull(response2);
        assertNotNull(response2.getStatusCode());
        assertEquals(HttpStatus.OK, response2.getStatusCode());

        SheetDto<RoleRD> sheet = response2.getBody();
        assertNotNull(sheet);
        assertNotNull(sheet.page());

        List<RoleRD> roles = sheet.content();
        assertNotNull(roles);
        assertFalse(roles.isEmpty());
        roles.forEach(role -> {
            assertNotNull(role.id());
            assertNotNull(role.name());
            assertTrue(role.name().startsWith("ROLE_"));
            assertTrue(role.name().contains("US"));
        });
    }

    @Test
    @Order(8)
    @DirtiesContext
    @DisplayName("Тестируем POST /api/admin/roles : успех")
    public void test8() {

        /*
         * Логинимся под администратором.
         * */

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

        /*
         * Создаем новую роль.
         * */

        RoleWD role = new RoleWD("ROLE_OPERATOR", "Operate something...");

        ResponseEntity<RoleRD> response2 = rest.exchange(
                apiUrl,
                HttpMethod.POST,
                new HttpEntity<>(role, headers),
                RoleRD.class
        );
        assertNotNull(response2);
        assertNotNull(response2.getStatusCode());
        assertEquals(HttpStatus.CREATED, response2.getStatusCode());

        RoleRD savedRole = response2.getBody();
        assertNotNull(savedRole);
        assertNotNull(savedRole.id());
        assertNotNull(savedRole.name());
        assertEquals(role.name(), savedRole.name());
        assertNotNull(savedRole.description());
        assertEquals(role.description(), savedRole.description());

        /*
         * Проверяем корректность url.
         * */

        HttpHeaders headers2 = response2.getHeaders();
        assertNotNull(headers2);
        List<String> locations = headers2.get("Location");
        assertNotNull(locations);
        String locationStr = locations.getFirst();
        assertNotNull(locationStr);
        URI location = URI.create(locationStr);
        assertNotNull(location);

        ResponseEntity<RoleRD> response3 = rest.exchange(
                location,
                HttpMethod.GET,
                new HttpEntity<>(headers),
                RoleRD.class
        );
        assertNotNull(response3);
        assertNotNull(response3.getStatusCode());
        assertEquals(HttpStatus.OK, response3.getStatusCode());

        RoleRD roleById = response3.getBody();
        assertNotNull(roleById);
        assertNotNull(roleById.id());
        assertEquals(savedRole.id(), roleById.id());
        assertNotNull(roleById.name());
        assertEquals(savedRole.name(), roleById.name());
        assertNotNull(roleById.description());
        assertEquals(savedRole.description(), roleById.description());
    }

    @Test
    @Order(9)
    @DirtiesContext
    @DisplayName("Тестируем PUT /api/admin/roles/{id} : успех")
    public void test9() {

        /*
         * Логинимся под администратором.
         * */

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

        /*
         * Создаем новую роль.
         * */

        RoleWD role = new RoleWD("ROLE_OPERATOR", "Operate something...");

        ResponseEntity<RoleRD> response2 = rest.exchange(
                apiUrl,
                HttpMethod.POST,
                new HttpEntity<>(role, headers),
                RoleRD.class
        );
        assertNotNull(response2);
        assertNotNull(response2.getStatusCode());
        assertEquals(HttpStatus.CREATED, response2.getStatusCode());

        RoleRD savedRole = response2.getBody();
        assertNotNull(savedRole);
        assertNotNull(savedRole.id());
        assertNotNull(savedRole.name());
        assertEquals(role.name(), savedRole.name());
        assertNotNull(savedRole.description());
        assertEquals(role.description(), savedRole.description());

        /*
         * Проверяем корректность url.
         * */

        HttpHeaders headers2 = response2.getHeaders();
        assertNotNull(headers2);
        List<String> locations = headers2.get("Location");
        assertNotNull(locations);
        String locationStr = locations.getFirst();
        assertNotNull(locationStr);
        URI location = URI.create(locationStr);
        assertNotNull(location);

        ResponseEntity<RoleRD> response3 = rest.exchange(
                location,
                HttpMethod.GET,
                new HttpEntity<>(headers),
                RoleRD.class
        );
        assertNotNull(response3);
        assertNotNull(response3.getStatusCode());
        assertEquals(HttpStatus.OK, response3.getStatusCode());

        RoleRD roleById = response3.getBody();
        assertNotNull(roleById);
        assertNotNull(roleById.id());
        assertEquals(savedRole.id(), roleById.id());
        assertNotNull(roleById.name());
        assertEquals(savedRole.name(), roleById.name());
        assertNotNull(roleById.description());
        assertEquals(savedRole.description(), roleById.description());

        /*
         * Обновляем роль.
         * */

        RoleWD updRole = new RoleWD("ROLE_OPERATOR_UPD", "Operate something...UPD");

        ResponseEntity<RoleRD> response4 = rest.exchange(
                location,
                HttpMethod.PUT,
                new HttpEntity<>(updRole, headers),
                RoleRD.class
        );
        assertNotNull(response4);
        assertNotNull(response4.getStatusCode());
        assertEquals(HttpStatus.OK, response4.getStatusCode());

        RoleRD roleUpdated = response4.getBody();
        assertNotNull(roleUpdated);
        assertNotNull(roleUpdated.id());
        assertEquals(savedRole.id(), roleUpdated.id());
        assertNotNull(roleUpdated.name());
        assertEquals(updRole.name(), roleUpdated.name());
        assertNotNull(roleUpdated.description());
        assertEquals(updRole.description(), roleUpdated.description());
    }

    @Test
    @Order(10)
    @DirtiesContext
    @DisplayName("Тестируем PATCH /api/admin/roles/{id} : успех")
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
         * Создаем новую роль.
         * */

        RoleWD role = new RoleWD("ROLE_OPERATOR", "Operate something...");

        ResponseEntity<RoleRD> response2 = rest.exchange(
                apiUrl,
                HttpMethod.POST,
                new HttpEntity<>(role, headers),
                RoleRD.class
        );
        assertNotNull(response2);
        assertNotNull(response2.getStatusCode());
        assertEquals(HttpStatus.CREATED, response2.getStatusCode());

        RoleRD savedRole = response2.getBody();
        assertNotNull(savedRole);
        assertNotNull(savedRole.id());
        assertNotNull(savedRole.name());
        assertEquals(role.name(), savedRole.name());
        assertNotNull(savedRole.description());
        assertEquals(role.description(), savedRole.description());

        /*
         * Проверяем корректность url.
         * */

        HttpHeaders headers2 = response2.getHeaders();
        assertNotNull(headers2);
        List<String> locations = headers2.get("Location");
        assertNotNull(locations);
        String locationStr = locations.getFirst();
        assertNotNull(locationStr);
        URI location = URI.create(locationStr);
        assertNotNull(location);

        ResponseEntity<RoleRD> response3 = rest.exchange(
                location,
                HttpMethod.GET,
                new HttpEntity<>(headers),
                RoleRD.class
        );
        assertNotNull(response3);
        assertNotNull(response3.getStatusCode());
        assertEquals(HttpStatus.OK, response3.getStatusCode());

        RoleRD roleById = response3.getBody();
        assertNotNull(roleById);
        assertNotNull(roleById.id());
        assertEquals(savedRole.id(), roleById.id());
        assertNotNull(roleById.name());
        assertEquals(savedRole.name(), roleById.name());
        assertNotNull(roleById.description());
        assertEquals(savedRole.description(), roleById.description());

        /*
         * Обновляем роль.
         * */

        RoleWD updRole = new RoleWD("ROLE_OPERATOR_UPD", null);

        ResponseEntity<RoleRD> response4 = rest.exchange(
                location,
                HttpMethod.PATCH,
                new HttpEntity<>(updRole, headers),
                RoleRD.class
        );
        assertNotNull(response4);
        assertNotNull(response4.getStatusCode());
        assertEquals(HttpStatus.OK, response4.getStatusCode());

        RoleRD roleUpdated = response4.getBody();
        assertNotNull(roleUpdated);
        assertNotNull(roleUpdated.id());
        assertEquals(savedRole.id(), roleUpdated.id());
        assertNotNull(roleUpdated.name());
        assertEquals(updRole.name(), roleUpdated.name());
        assertNotNull(roleUpdated.description());
        assertEquals(role.description(), roleUpdated.description());
    }

    @Test
    @Order(11)
    @DirtiesContext
    @DisplayName("Тестируем DELETE /api/admin/roles/{id} : успех")
    public void test11() {

        /*
         * Логинимся под администратором.
         * */

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

        /*
         * Создаем новую роль.
         * */

        RoleWD role = new RoleWD("ROLE_OPERATOR", "Operate something...");

        ResponseEntity<RoleRD> response2 = rest.exchange(
                apiUrl,
                HttpMethod.POST,
                new HttpEntity<>(role, headers),
                RoleRD.class
        );
        assertNotNull(response2);
        assertNotNull(response2.getStatusCode());
        assertEquals(HttpStatus.CREATED, response2.getStatusCode());

        RoleRD savedRole = response2.getBody();
        assertNotNull(savedRole);
        assertNotNull(savedRole.id());
        assertNotNull(savedRole.name());
        assertEquals(role.name(), savedRole.name());
        assertNotNull(savedRole.description());
        assertEquals(role.description(), savedRole.description());

        /*
         * Проверяем корректность url.
         * */

        HttpHeaders headers2 = response2.getHeaders();
        assertNotNull(headers2);
        List<String> locations = headers2.get("Location");
        assertNotNull(locations);
        String locationStr = locations.getFirst();
        assertNotNull(locationStr);
        URI location = URI.create(locationStr);
        assertNotNull(location);

        ResponseEntity<RoleRD> response3 = rest.exchange(
                location,
                HttpMethod.GET,
                new HttpEntity<>(headers),
                RoleRD.class
        );
        assertNotNull(response3);
        assertNotNull(response3.getStatusCode());
        assertEquals(HttpStatus.OK, response3.getStatusCode());

        RoleRD roleById = response3.getBody();
        assertNotNull(roleById);
        assertNotNull(roleById.id());
        assertEquals(savedRole.id(), roleById.id());
        assertNotNull(roleById.name());
        assertEquals(savedRole.name(), roleById.name());
        assertNotNull(roleById.description());
        assertEquals(savedRole.description(), roleById.description());

        /*
         * Удаляем роль.
         * */

        ResponseEntity<Void> response4 = rest.exchange(
                location,
                HttpMethod.DELETE,
                new HttpEntity<>(headers),
                Void.class
        );
        assertNotNull(response4);
        assertNotNull(response4.getStatusCode());
        assertEquals(HttpStatus.NO_CONTENT, response4.getStatusCode());

        assertThrows(HttpClientErrorException.NotFound.class, () ->
                rest.exchange(
                        location,
                        HttpMethod.GET,
                        new HttpEntity<>(headers),
                        RoleRD.class
                )
        );
    }

    @Test
    @Order(12)
    @DirtiesContext
    @DisplayName("Тестируем PUT /api/admin/roles/{id} с id системной роли : ошибка 422 необрабатываемый контент")
    public void test12() {

        /*
         * Логинимся под администратором.
         * */

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

        /*
         * Получаем пользовательскую роль.
         * */

        ResponseEntity<RoleRD> response2 = rest.exchange(
                apiUrl + "/name/ROLE_USER",
                HttpMethod.GET,
                new HttpEntity<>(headers),
                RoleRD.class
        );
        assertNotNull(response2);
        assertNotNull(response2.getStatusCode());
        assertEquals(HttpStatus.OK, response2.getStatusCode());

        RoleRD role = response2.getBody();
        assertNotNull(role);
        assertNotNull(role.id());

        /*
         * Пытаемся обновить пользовательскую роль.
         * */

        RoleWD updRole = new RoleWD("ROLE_USER_UPD", "Some new description");

        //noinspection CatchMayIgnoreException
        try {
            rest.exchange(
                    apiUrl + "/{id}",
                    HttpMethod.PUT,
                    new HttpEntity<>(updRole, headers),
                    RoleRD.class,
                    role.id()
            );
            fail("Should throw HttpClientErrorException.UnprocessableContent");

        } catch (HttpClientErrorException.UnprocessableContent e) {
            assertNotNull(e);
            assertNotNull(e.getStatusCode());
            assertEquals(HttpStatus.UNPROCESSABLE_CONTENT, e.getStatusCode());

            ErrorResponse errorResponse = e.getResponseBodyAs(ErrorResponse.class);
            assertNotNull(errorResponse);
            assertNotNull(errorResponse.path());
            assertEquals(apiUrl + "/" + role.id(), errorResponse.path());
            assertNotNull(errorResponse.httpMethod());
            assertEquals(HttpMethod.PUT, errorResponse.httpMethod());
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
    @Order(13)
    @DirtiesContext
    @DisplayName("Тестируем PATCH /api/admin/roles/{id} с id системной роли : ошибка 422 необрабатываемый контент")
    public void test13() {

        /*
         * Логинимся под администратором.
         * */

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

        /*
         * Получаем роль пользователя.
         * */

        ResponseEntity<RoleRD> response2 = rest.exchange(
                apiUrl + "/name/ROLE_USER",
                HttpMethod.GET,
                new HttpEntity<>(headers),
                RoleRD.class
        );
        assertNotNull(response2);
        assertNotNull(response2.getStatusCode());
        assertEquals(HttpStatus.OK, response2.getStatusCode());

        RoleRD role = response2.getBody();
        assertNotNull(role);
        assertNotNull(role.id());

        /*
         * Пытаемся обновить роль пользователя.
         * */

        RoleWD updRole = new RoleWD("ROLE_USER_UPD", "Some new description");

        try {
            rest.exchange(
                    apiUrl + "/{id}",
                    HttpMethod.PATCH,
                    new HttpEntity<>(updRole, headers),
                    RoleRD.class,
                    role.id());
            fail("Should throw HttpClientErrorException.UnprocessableContent");

        } catch (HttpClientErrorException.UnprocessableContent e) {
            assertNotNull(e);
            assertNotNull(e.getStatusCode());
            assertEquals(HttpStatus.UNPROCESSABLE_CONTENT, e.getStatusCode());

            ErrorResponse errorResponse = e.getResponseBodyAs(ErrorResponse.class);
            assertNotNull(errorResponse);
            assertNotNull(errorResponse.path());
            assertEquals(apiUrl + "/" + role.id(), errorResponse.path());
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
    @Order(14)
    @DirtiesContext
    @DisplayName("Тестируем PATCH /api/admin/roles/{id} : успех")
    public void test14() {

        /*
         * Логинимся под администратором.
         * */

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

        /*
         * Получаем роль пользователя.
         * */

        ResponseEntity<RoleRD> response2 = rest.exchange(
                apiUrl + "/name/ROLE_USER",
                HttpMethod.GET,
                new HttpEntity<>(headers),
                RoleRD.class
        );
        assertNotNull(response2);
        assertNotNull(response2.getStatusCode());
        assertEquals(HttpStatus.OK, response2.getStatusCode());

        RoleRD role = response2.getBody();
        assertNotNull(role);
        assertNotNull(role.id());

        /*
         * Обновляем роль пользователя (только описание)
         * */

        RoleWD updRole = new RoleWD(null, "Some new description");

        ResponseEntity<RoleRD> response3 = rest.exchange(
                apiUrl + "/{id}",
                HttpMethod.PATCH,
                new HttpEntity<>(updRole, headers),
                RoleRD.class,
                role.id()
        );
        assertNotNull(response3);
        assertNotNull(response3.getStatusCode());
        assertEquals(HttpStatus.OK, response3.getStatusCode());

        RoleRD role2 = response3.getBody();
        assertNotNull(role2);
        assertNotNull(role2.id());
        assertNotNull(role2.name());
        assertEquals("ROLE_USER", role2.name());
        assertNotNull(role2.description());
        assertEquals(updRole.description(), role2.description());
    }

    @Test
    @Order(15)
    @DirtiesContext
    @DisplayName("Тестируем DELETE /api/admin/roles/{id} с id системной роли : ошибка 422 необрабатываемый контент")
    public void test15() {

        /*
         * Логинимся под администратором.
         * */

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

        /*
         * Получаем роль пользователь.
         * */

        ResponseEntity<RoleRD> response2 = rest.exchange(
                apiUrl + "/name/ROLE_USER",
                HttpMethod.GET,
                new HttpEntity<>(headers),
                RoleRD.class
        );
        assertNotNull(response2);
        assertNotNull(response2.getStatusCode());
        assertEquals(HttpStatus.OK, response2.getStatusCode());

        RoleRD role = response2.getBody();
        assertNotNull(role);
        assertNotNull(role.id());

        /*
         * Пытаемся удалить роль пользователя.
         * */

        try {
            rest.exchange(
                    apiUrl + "/{id}",
                    HttpMethod.DELETE,
                    new HttpEntity<>(headers),
                    Void.class,
                    role.id()
            );
            fail("Should throw HttpClientErrorException.UnprocessableContent");

        } catch (HttpClientErrorException.UnprocessableContent e) {
            assertNotNull(e);
            assertNotNull(e.getStatusCode());
            assertEquals(HttpStatus.UNPROCESSABLE_CONTENT, e.getStatusCode());

            ErrorResponse errorResponse = e.getResponseBodyAs(ErrorResponse.class);
            assertNotNull(errorResponse);
            assertNotNull(errorResponse.path());
            assertEquals(apiUrl + "/" + role.id(), errorResponse.path());
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
