package a.slelin.work.task.management.test;

import a.slelin.work.task.management.dto.SheetDto;
import a.slelin.work.task.management.dto.UserRD;
import a.slelin.work.task.management.dto.UserWD;
import a.slelin.work.task.management.exception.EntityNotFoundByIdException;
import a.slelin.work.task.management.exception.EnumParseException;
import a.slelin.work.task.management.exception.ErrorResponse;
import a.slelin.work.task.management.util.filter.Filter;
import a.slelin.work.task.management.util.filter.FilterChain;
import a.slelin.work.task.management.util.filter.FilterParseException;
import a.slelin.work.task.management.util.filter.Operation;
import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.UUID;

import static a.slelin.work.task.management.test.TestConfig.USER_URL;
import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test")
@DisplayName("Тест некоторых ошибок при обращении к api")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class ExpectedErrorApiTest {

    @Autowired
    private RestTemplate rest;

    @Test
    @DisplayName("Тестирование получение несуществующего пользователя")
    public void testGetUser() {
        String id = UUID.randomUUID().toString();

        try {
            rest.getForEntity(USER_URL + "/" + id, UserRD.class);
            fail("Запрос к несуществующему пользователя должен был вызвать ошибку.");
        } catch (HttpClientErrorException.NotFound e) {
            assertNotNull(e);
            ErrorResponse errorResponse = e.getResponseBodyAs(ErrorResponse.class);
            assertNotNull(errorResponse);

            assertNotNull(errorResponse.path());
            assertEquals(USER_URL + "/" + id, errorResponse.path());
            assertNotNull(errorResponse.httpStatus());
            assertEquals(HttpStatus.NOT_FOUND, errorResponse.httpStatus());
            assertNotNull(errorResponse.httpMethod());
            assertEquals(HttpMethod.GET, errorResponse.httpMethod());
            assertNotNull(errorResponse.exception());
            assertEquals(EntityNotFoundByIdException.class.getSimpleName(), errorResponse.exception());
            assertNotNull(errorResponse.message());
            assertNotNull(errorResponse.timeStamp());
            assertNotNull(errorResponse.details());
        } catch (Exception e) {
            fail("Получили исключение другого рода.");
        }
    }

    @Test
    @DisplayName("Тестирование создания пользователя, указав некорректный гендер")
    public void testCreateUser() {
        try {
            rest.exchange(
                    USER_URL,
                    HttpMethod.POST,
                    new HttpEntity<>(UserWD.builder()
                            .username("username")
                            .password("password")
                            .gender("test")
                            .phone("123456789")
                            .email("test@example.com")
                            .build()),
                    UserRD.class
            );
            fail("Запрос на создание пользователя должен был вызвать ошибку.");
        } catch (HttpClientErrorException.BadRequest e) {
            assertNotNull(e);
            ErrorResponse errorResponse = e.getResponseBodyAs(ErrorResponse.class);
            assertNotNull(errorResponse);

            assertNotNull(errorResponse.path());
            assertEquals(USER_URL, errorResponse.path());
            assertNotNull(errorResponse.httpStatus());
            assertEquals(HttpStatus.BAD_REQUEST, errorResponse.httpStatus());
            assertNotNull(errorResponse.httpMethod());
            assertEquals(HttpMethod.POST, errorResponse.httpMethod());
            assertNotNull(errorResponse.exception());
            assertEquals(EnumParseException.class.getSimpleName(), errorResponse.exception());
            assertNotNull(errorResponse.message());
            assertNotNull(errorResponse.timeStamp());
            assertNotNull(errorResponse.details());
        } catch (Exception e) {
            fail("Получили исключение другого рода.");
        }
    }

    @Test
    @DisplayName("Тестирование создание некорретного пользователя")
    public void testCreateUser2() {
        try {
            rest.exchange(
                    USER_URL,
                    HttpMethod.POST,
                    new HttpEntity<>(UserWD.builder()
                            .username("u")
                            .password("password")
                            .gender("male")
                            .phone("bad_phone")
                            .email("test@example.com")
                            .build()),
                    UserRD.class
            );
            fail("Запрос на создание пользователя должен был вызвать ошибку.");
        } catch (HttpClientErrorException.BadRequest e) {
            assertNotNull(e);
            ErrorResponse errorResponse = e.getResponseBodyAs(ErrorResponse.class);
            assertNotNull(errorResponse);

            assertNotNull(errorResponse.path());
            assertEquals(USER_URL, errorResponse.path());
            assertNotNull(errorResponse.httpStatus());
            assertEquals(HttpStatus.BAD_REQUEST, errorResponse.httpStatus());
            assertNotNull(errorResponse.httpMethod());
            assertEquals(HttpMethod.POST, errorResponse.httpMethod());
            assertNotNull(errorResponse.exception());
            assertEquals(ConstraintViolationException.class.getSimpleName(), errorResponse.exception());
            assertNotNull(errorResponse.message());
            assertNotNull(errorResponse.timeStamp());
            assertNotNull(errorResponse.details());
        } catch (Exception e) {
            fail("Получили исключение другого рода.");
        }
    }

    @Test
    @DisplayName("Тестирование некорректной фильтрации")
    public void filterTest() {
        try {
            rest.exchange(
                    USER_URL + "/search",
                    HttpMethod.POST,
                    new HttpEntity<>(
                            FilterChain.empty()
                                    .add(Filter.of("gender", Operation.LE, 5L))),
                    new ParameterizedTypeReference<SheetDto<UserRD>>() {
                    }
            );
            fail("Запрос на создание пользователя должен был вызвать ошибку.");
        } catch (HttpClientErrorException.BadRequest e) {
            assertNotNull(e);
            ErrorResponse errorResponse = e.getResponseBodyAs(ErrorResponse.class);
            assertNotNull(errorResponse);

            assertNotNull(errorResponse.path());
            assertEquals(USER_URL + "/search", errorResponse.path());
            assertNotNull(errorResponse.httpStatus());
            assertEquals(HttpStatus.BAD_REQUEST, errorResponse.httpStatus());
            assertNotNull(errorResponse.httpMethod());
            assertEquals(HttpMethod.POST, errorResponse.httpMethod());
            assertNotNull(errorResponse.exception());
            assertEquals(FilterParseException.class.getSimpleName(), errorResponse.exception());
            assertNotNull(errorResponse.message());
            assertNotNull(errorResponse.timeStamp());
            assertNotNull(errorResponse.details());
        } catch (Exception e) {
            fail("Получили исключение другого рода.");
        }
    }
}
