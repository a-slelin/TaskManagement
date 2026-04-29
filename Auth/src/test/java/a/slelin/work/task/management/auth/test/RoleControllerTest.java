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
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DisplayName("Тест контроллера, отвечающего за роли")
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
    @DisplayName("Не авторизованный пользователь не может обращаться к ролям")
    public void test1() {
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

        } catch (Exception e) {
            fail("Should throw HttpClientErrorException.Unauthorized, but got " + e.getMessage());
        }
    }

    @Test
    @Order(2)
    @DirtiesContext
    @DisplayName("Обычный пользователь не может обращаться к ролям")
    public void test2() {
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

        //noinspection CatchMayIgnoreException
        try {
            rest.exchange(
                    apiUrl,
                    HttpMethod.GET,
                    new HttpEntity<>(headers),
                    new ParameterizedTypeReference<>() {
                    }
            );
            fail("Should throw HttpClientErrorException.Unauthorized");

        } catch (HttpClientErrorException.Forbidden e) {
            assertNotNull(e);
            assertNotNull(e.getStatusCode());
            assertEquals(HttpStatus.FORBIDDEN, e.getStatusCode());

        } catch (Exception e) {
            fail("Should throw HttpClientErrorException.Unauthorized, but got " + e.getMessage());
        }
    }

    @Test
    @Order(3)
    @DirtiesContext
    @DisplayName("Администратор может обращаться к ролям")
    public void test3() {
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
    @DisplayName("Тестируем получение всех ролей")
    public void test4() {
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
    @DisplayName("Тестируем получение роли по идентификатору")
    public void test5() {
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

        ResponseEntity<RoleRD> response3 = rest.exchange(
                apiUrl + "/{id}",
                HttpMethod.GET,
                new HttpEntity<>(headers),
                RoleRD.class,
                role.id());
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
    @DisplayName("Тестируем получение роли по имени (ROLE_USER)")
    public void test6() {
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

        ResponseEntity<RoleRD> response2 = rest.exchange(
                apiUrl + "/name/ROLE_USER",
                HttpMethod.GET,
                new HttpEntity<>(headers),
                RoleRD.class);
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
    @DisplayName("Тестируем получение ролей по фильтру")
    public void test7() {
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

        FilterChain filters = FilterChain.empty();
        filters.add(Filter.of("name", Operation.STARTS_WITH, "ROLE_"));
        filters.add(Filter.of("name", Operation.LIKE, "US"));

        ResponseEntity<SheetDto<RoleRD>> response2 = rest.exchange(
                apiUrl + "/search",
                HttpMethod.POST,
                new HttpEntity<>(filters, headers),
                new ParameterizedTypeReference<>() {
                });
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
    @DisplayName("Тестируем создание роли")
    public void test8() {
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

        RoleWD role = new RoleWD("ROLE_OPERATOR", "Operate something...");

        ResponseEntity<RoleRD> response2 = rest.exchange(
                apiUrl,
                HttpMethod.POST,
                new HttpEntity<>(role, headers),
                RoleRD.class);
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
                RoleRD.class);
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
    @DisplayName("Тестируем полное обновление роли")
    public void test9() {
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

        RoleWD role = new RoleWD("ROLE_OPERATOR", "Operate something...");

        ResponseEntity<RoleRD> response2 = rest.exchange(
                apiUrl,
                HttpMethod.POST,
                new HttpEntity<>(role, headers),
                RoleRD.class);
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
                RoleRD.class);
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

        RoleWD updRole = new RoleWD("ROLE_OPERATOR_UPD", "Operate something...UPD");

        ResponseEntity<RoleRD> response4 = rest.exchange(
                location,
                HttpMethod.PUT,
                new HttpEntity<>(updRole, headers),
                RoleRD.class);
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
    @DisplayName("Тестируем патчинг роли")
    public void test10() {
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

        RoleWD role = new RoleWD("ROLE_OPERATOR", "Operate something...");

        ResponseEntity<RoleRD> response2 = rest.exchange(
                apiUrl,
                HttpMethod.POST,
                new HttpEntity<>(role, headers),
                RoleRD.class);
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
                RoleRD.class);
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

        RoleWD updRole = new RoleWD("ROLE_OPERATOR_UPD", null);

        ResponseEntity<RoleRD> response4 = rest.exchange(
                location,
                HttpMethod.PATCH,
                new HttpEntity<>(updRole, headers),
                RoleRD.class);
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
    @DisplayName("Тестируем удаление роли")
    public void test11() {
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

        RoleWD role = new RoleWD("ROLE_OPERATOR", "Operate something...");

        ResponseEntity<RoleRD> response2 = rest.exchange(
                apiUrl,
                HttpMethod.POST,
                new HttpEntity<>(role, headers),
                RoleRD.class);
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
                RoleRD.class);
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

        ResponseEntity<Void> response4 = rest.exchange(
                location,
                HttpMethod.DELETE,
                new HttpEntity<>(headers),
                Void.class);
        assertNotNull(response4);
        assertNotNull(response4.getStatusCode());
        assertEquals(HttpStatus.NO_CONTENT, response4.getStatusCode());

        assertThrows(HttpClientErrorException.NotFound.class, () ->
                rest.exchange(
                        location,
                        HttpMethod.GET,
                        new HttpEntity<>(headers),
                        RoleRD.class
                ));
    }

    @Test
    @Order(12)
    @DirtiesContext
    @DisplayName("Тестируем полное обновление системной роли : ошибка")
    public void test12() {
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

        ResponseEntity<RoleRD> response2 = rest.exchange(
                apiUrl + "/name/ROLE_USER",
                HttpMethod.GET,
                new HttpEntity<>(headers),
                RoleRD.class);
        assertNotNull(response2);
        assertNotNull(response2.getStatusCode());
        assertEquals(HttpStatus.OK, response2.getStatusCode());

        RoleRD role = response2.getBody();
        assertNotNull(role);
        assertNotNull(role.id());

        RoleWD updRole = new RoleWD("ROLE_USER_UPD", "Some new description");

        //noinspection CatchMayIgnoreException
        try {
            rest.exchange(
                    apiUrl + "/{id}",
                    HttpMethod.PUT,
                    new HttpEntity<>(updRole, headers),
                    RoleRD.class,
                    role.id());
            fail("Should throw HttpClientErrorException.BadRequest");

        } catch (HttpClientErrorException.BadRequest e) {
            assertNotNull(e);
            assertNotNull(e.getStatusCode());
            assertEquals(HttpStatus.BAD_REQUEST, e.getStatusCode());

            ErrorResponse errorResponse = e.getResponseBodyAs(ErrorResponse.class);
            assertNotNull(errorResponse);

        } catch (Exception e) {
            fail("Should throw HttpClientErrorException.BadRequest, but got " + e.getMessage());
        }
    }

    @Test
    @Order(13)
    @DirtiesContext
    @DisplayName("Тестируем патчинг роли : ошибка")
    public void test13() {
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

        ResponseEntity<RoleRD> response2 = rest.exchange(
                apiUrl + "/name/ROLE_USER",
                HttpMethod.GET,
                new HttpEntity<>(headers),
                RoleRD.class);
        assertNotNull(response2);
        assertNotNull(response2.getStatusCode());
        assertEquals(HttpStatus.OK, response2.getStatusCode());

        RoleRD role = response2.getBody();
        assertNotNull(role);
        assertNotNull(role.id());

        RoleWD updRole = new RoleWD("ROLE_USER_UPD", "Some new description");

        //noinspection CatchMayIgnoreException
        try {
            rest.exchange(
                    apiUrl + "/{id}",
                    HttpMethod.PATCH,
                    new HttpEntity<>(updRole, headers),
                    RoleRD.class,
                    role.id());
            fail("Should throw HttpClientErrorException.BadRequest");

        } catch (HttpClientErrorException.BadRequest e) {
            assertNotNull(e);
            assertNotNull(e.getStatusCode());
            assertEquals(HttpStatus.BAD_REQUEST, e.getStatusCode());

            ErrorResponse errorResponse = e.getResponseBodyAs(ErrorResponse.class);
            assertNotNull(errorResponse);

        } catch (Exception e) {
            fail("Should throw HttpClientErrorException.BadRequest, but got " + e.getMessage());
        }
    }

    @Test
    @Order(14)
    @DirtiesContext
    @DisplayName("Тестируем патчинг роли : успех - обновляем только описание")
    public void test14() {
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

        ResponseEntity<RoleRD> response2 = rest.exchange(
                apiUrl + "/name/ROLE_USER",
                HttpMethod.GET,
                new HttpEntity<>(headers),
                RoleRD.class);
        assertNotNull(response2);
        assertNotNull(response2.getStatusCode());
        assertEquals(HttpStatus.OK, response2.getStatusCode());

        RoleRD role = response2.getBody();
        assertNotNull(role);
        assertNotNull(role.id());

        RoleWD updRole = new RoleWD(null, "Some new description");

        ResponseEntity<RoleRD> response3 = rest.exchange(
                apiUrl + "/{id}",
                HttpMethod.PATCH,
                new HttpEntity<>(updRole, headers),
                RoleRD.class,
                role.id());
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
    @DisplayName("Тестируем удаление системной роли : ошибка")
    public void test15() {
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

        ResponseEntity<RoleRD> response2 = rest.exchange(
                apiUrl + "/name/ROLE_USER",
                HttpMethod.GET,
                new HttpEntity<>(headers),
                RoleRD.class);
        assertNotNull(response2);
        assertNotNull(response2.getStatusCode());
        assertEquals(HttpStatus.OK, response2.getStatusCode());

        RoleRD role = response2.getBody();
        assertNotNull(role);
        assertNotNull(role.id());

        //noinspection CatchMayIgnoreException
        try {
            rest.exchange(
                    apiUrl + "/{id}",
                    HttpMethod.DELETE,
                    new HttpEntity<>(headers),
                    Void.class,
                    role.id());
            fail("Should throw HttpClientErrorException.BadRequest");

        } catch (HttpClientErrorException.BadRequest e) {
            assertNotNull(e);
            assertNotNull(e.getStatusCode());
            assertEquals(HttpStatus.BAD_REQUEST, e.getStatusCode());

            ErrorResponse errorResponse = e.getResponseBodyAs(ErrorResponse.class);
            assertNotNull(errorResponse);

        } catch (Exception e) {
            fail("Should throw HttpClientErrorException.BadRequest, but got " + e.getMessage());
        }
    }
}
