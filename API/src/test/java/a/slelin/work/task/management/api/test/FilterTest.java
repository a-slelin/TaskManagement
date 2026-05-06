package a.slelin.work.task.management.api.test;

import a.slelin.work.task.management.core.dto.SheetDto;
import a.slelin.work.task.management.core.dto.api.ProjectRD;
import a.slelin.work.task.management.core.dto.api.ProjectWD;
import a.slelin.work.task.management.core.exception.ErrorResponse;
import a.slelin.work.task.management.core.util.filter.Filter;
import a.slelin.work.task.management.core.util.filter.FilterChain;
import a.slelin.work.task.management.core.util.filter.Operation;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
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

import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test")
@DisplayName("Тест фильтрации")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class FilterTest {

    @Autowired
    private RestTemplate rest;

    @Autowired
    @Qualifier("alexToken")
    private String alexToken;

    @Autowired
    @Qualifier("ekaterinaToken")
    private String ekaterinaToken;

    @Autowired
    @Qualifier("adminToken")
    private String adminToken;

    @LocalServerPort
    private int port;

    private String baseUrl;

    private String projectUrl;

    private String taskUrl;

    @BeforeEach
    void beforeEach() {
        baseUrl = "http://localhost:%d".formatted(port);
        projectUrl = baseUrl + "/api/projects";
        taskUrl = baseUrl + "/api/tasks";
    }

    @Test
    @Order(1)
    @DirtiesContext
    @DisplayName("Тестируем equals с Long")
    public void test1() {

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(alexToken);

        ProjectWD newProject = new ProjectWD("tmp_project");

        ResponseEntity<ProjectRD> response = rest.exchange(
                projectUrl,
                HttpMethod.POST,
                new HttpEntity<>(newProject, headers),
                ProjectRD.class
        );
        assertNotNull(response);
        assertNotNull(response.getStatusCode());
        assertEquals(HttpStatus.CREATED, response.getStatusCode());

        ProjectRD project = response.getBody();
        assertNotNull(project);
        Long id = project.id();
        assertNotNull(id);

        FilterChain filters = FilterChain
                .empty()
                .add(Filter.of("id", Operation.EQ, id));

        ResponseEntity<SheetDto<ProjectRD>> response2 = rest.exchange(
                projectUrl + "/search",
                HttpMethod.POST,
                new HttpEntity<>(filters, headers),
                new ParameterizedTypeReference<>() {
                }
        );
        assertNotNull(response2);
        assertNotNull(response2.getStatusCode());
        assertEquals(HttpStatus.OK, response2.getStatusCode());

        SheetDto<ProjectRD> sheet = response2.getBody();
        assertNotNull(sheet);
        assertNotNull(sheet.page());

        List<ProjectRD> projects = sheet.content();
        assertNotNull(projects);
        assertFalse(projects.isEmpty());
        assertEquals(1, projects.size());

        ProjectRD project2 = projects.getFirst();
        assertNotNull(project2);
        assertEquals(project, project2);
    }

    @Test
    @Order(2)
    @DirtiesContext
    @DisplayName("Тестируем equals с Long (String)")
    public void test2() {

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(alexToken);

        ProjectWD newProject = new ProjectWD("tmp_project");

        ResponseEntity<ProjectRD> response = rest.exchange(
                projectUrl,
                HttpMethod.POST,
                new HttpEntity<>(newProject, headers),
                ProjectRD.class
        );
        assertNotNull(response);
        assertNotNull(response.getStatusCode());
        assertEquals(HttpStatus.CREATED, response.getStatusCode());

        ProjectRD project = response.getBody();
        assertNotNull(project);
        Long id = project.id();
        assertNotNull(id);

        FilterChain filters = FilterChain
                .empty()
                .add(Filter.of("id", Operation.EQ, id.toString()));

        ResponseEntity<SheetDto<ProjectRD>> response2 = rest.exchange(
                projectUrl + "/search",
                HttpMethod.POST,
                new HttpEntity<>(filters, headers),
                new ParameterizedTypeReference<>() {
                }
        );
        assertNotNull(response2);
        assertNotNull(response2.getStatusCode());
        assertEquals(HttpStatus.OK, response2.getStatusCode());

        SheetDto<ProjectRD> sheet = response2.getBody();
        assertNotNull(sheet);
        assertNotNull(sheet.page());

        List<ProjectRD> projects = sheet.content();
        assertNotNull(projects);
        assertFalse(projects.isEmpty());
        assertEquals(1, projects.size());

        ProjectRD project2 = projects.getFirst();
        assertNotNull(project2);
        assertEquals(project, project2);
    }

    @Test
    @Order(3)
    @DirtiesContext
    @DisplayName("Тестируем equals с Long (Integer)")
    public void test3() {

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(alexToken);

        ProjectWD newProject = new ProjectWD("tmp_project");

        ResponseEntity<ProjectRD> response = rest.exchange(
                projectUrl,
                HttpMethod.POST,
                new HttpEntity<>(newProject, headers),
                ProjectRD.class
        );
        assertNotNull(response);
        assertNotNull(response.getStatusCode());
        assertEquals(HttpStatus.CREATED, response.getStatusCode());

        ProjectRD project = response.getBody();
        assertNotNull(project);
        Long id = project.id();
        assertNotNull(id);

        FilterChain filters = FilterChain
                .empty()
                .add(Filter.of("id", Operation.EQ, Integer.valueOf(id.toString())));

        ResponseEntity<SheetDto<ProjectRD>> response2 = rest.exchange(
                projectUrl + "/search",
                HttpMethod.POST,
                new HttpEntity<>(filters, headers),
                new ParameterizedTypeReference<>() {
                }
        );
        assertNotNull(response2);
        assertNotNull(response2.getStatusCode());
        assertEquals(HttpStatus.OK, response2.getStatusCode());

        SheetDto<ProjectRD> sheet = response2.getBody();
        assertNotNull(sheet);
        assertNotNull(sheet.page());

        List<ProjectRD> projects = sheet.content();
        assertNotNull(projects);
        assertFalse(projects.isEmpty());
        assertEquals(1, projects.size());

        ProjectRD project2 = projects.getFirst();
        assertNotNull(project2);
        assertEquals(project, project2);
    }

    @Test
    @Order(4)
    @DirtiesContext
    @DisplayName("Тестируем not equals с Long")
    public void test4() {

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(alexToken);

        ProjectWD newProject = new ProjectWD("tmp_project");

        ResponseEntity<ProjectRD> response = rest.exchange(
                projectUrl,
                HttpMethod.POST,
                new HttpEntity<>(newProject, headers),
                ProjectRD.class
        );
        assertNotNull(response);
        assertNotNull(response.getStatusCode());
        assertEquals(HttpStatus.CREATED, response.getStatusCode());

        ProjectRD project = response.getBody();
        assertNotNull(project);
        Long id = project.id();
        assertNotNull(id);

        FilterChain filters = FilterChain
                .empty()
                .add(Filter.of("id", Operation.NEQ, id));

        ResponseEntity<SheetDto<ProjectRD>> response2 = rest.exchange(
                projectUrl + "/search",
                HttpMethod.POST,
                new HttpEntity<>(filters, headers),
                new ParameterizedTypeReference<>() {
                }
        );
        assertNotNull(response2);
        assertNotNull(response2.getStatusCode());
        assertEquals(HttpStatus.OK, response2.getStatusCode());

        SheetDto<ProjectRD> sheet = response2.getBody();
        assertNotNull(sheet);
        assertNotNull(sheet.page());

        List<ProjectRD> projects = sheet.content();
        assertNotNull(projects);
        assertFalse(projects.isEmpty());
        assertFalse(projects.contains(project));
    }

    @Test
    @Order(5)
    @DirtiesContext
    @DisplayName("Тестируем not equals с Long (String)")
    public void test5() {

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(alexToken);

        ProjectWD newProject = new ProjectWD("tmp_project");

        ResponseEntity<ProjectRD> response = rest.exchange(
                projectUrl,
                HttpMethod.POST,
                new HttpEntity<>(newProject, headers),
                ProjectRD.class
        );
        assertNotNull(response);
        assertNotNull(response.getStatusCode());
        assertEquals(HttpStatus.CREATED, response.getStatusCode());

        ProjectRD project = response.getBody();
        assertNotNull(project);
        Long id = project.id();
        assertNotNull(id);

        FilterChain filters = FilterChain
                .empty()
                .add(Filter.of("id", Operation.NEQ, id.toString()));

        ResponseEntity<SheetDto<ProjectRD>> response2 = rest.exchange(
                projectUrl + "/search",
                HttpMethod.POST,
                new HttpEntity<>(filters, headers),
                new ParameterizedTypeReference<>() {
                }
        );
        assertNotNull(response2);
        assertNotNull(response2.getStatusCode());
        assertEquals(HttpStatus.OK, response2.getStatusCode());

        SheetDto<ProjectRD> sheet = response2.getBody();
        assertNotNull(sheet);
        assertNotNull(sheet.page());

        List<ProjectRD> projects = sheet.content();
        assertNotNull(projects);
        assertFalse(projects.isEmpty());
        assertFalse(projects.contains(project));
    }

    @Test
    @Order(6)
    @DirtiesContext
    @DisplayName("Тестируем not equals с Long (Integer)")
    public void test6() {

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(alexToken);

        ProjectWD newProject = new ProjectWD("tmp_project");

        ResponseEntity<ProjectRD> response = rest.exchange(
                projectUrl,
                HttpMethod.POST,
                new HttpEntity<>(newProject, headers),
                ProjectRD.class
        );
        assertNotNull(response);
        assertNotNull(response.getStatusCode());
        assertEquals(HttpStatus.CREATED, response.getStatusCode());

        ProjectRD project = response.getBody();
        assertNotNull(project);
        Long id = project.id();
        assertNotNull(id);

        FilterChain filters = FilterChain
                .empty()
                .add(Filter.of("id", Operation.NEQ, Integer.valueOf(id.toString())));

        ResponseEntity<SheetDto<ProjectRD>> response2 = rest.exchange(
                projectUrl + "/search",
                HttpMethod.POST,
                new HttpEntity<>(filters, headers),
                new ParameterizedTypeReference<>() {
                }
        );
        assertNotNull(response2);
        assertNotNull(response2.getStatusCode());
        assertEquals(HttpStatus.OK, response2.getStatusCode());

        SheetDto<ProjectRD> sheet = response2.getBody();
        assertNotNull(sheet);
        assertNotNull(sheet.page());

        List<ProjectRD> projects = sheet.content();
        assertNotNull(projects);
        assertFalse(projects.isEmpty());
        assertFalse(projects.contains(project));
    }

    @Test
    @Order(7)
    @DirtiesContext
    @DisplayName("Тестируем is null с Long")
    public void test7() {

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(alexToken);

        ResponseEntity<SheetDto<ProjectRD>> response = rest.exchange(
                projectUrl,
                HttpMethod.GET,
                new HttpEntity<>(headers),
                new ParameterizedTypeReference<>() {
                }
        );
        assertNotNull(response);
        assertNotNull(response.getStatusCode());
        assertEquals(HttpStatus.OK, response.getStatusCode());

        SheetDto<ProjectRD> sheet = response.getBody();
        assertNotNull(sheet);
        assertNotNull(sheet.page());

        List<ProjectRD> projects = sheet.content();
        assertNotNull(projects);
        assertFalse(projects.isEmpty());

        FilterChain filters = FilterChain
                .empty()
                .add(Filter.of("id", Operation.IS_NULL));

        ResponseEntity<SheetDto<ProjectRD>> response2 = rest.exchange(
                projectUrl + "/search",
                HttpMethod.POST,
                new HttpEntity<>(filters, headers),
                new ParameterizedTypeReference<>() {
                }
        );
        assertNotNull(response2);
        assertNotNull(response2.getStatusCode());
        assertEquals(HttpStatus.OK, response2.getStatusCode());

        SheetDto<ProjectRD> sheet2 = response2.getBody();
        assertNotNull(sheet2);
        assertNotNull(sheet2.page());

        List<ProjectRD> projects2 = sheet2.content();
        assertNotNull(projects2);
        assertTrue(projects2.isEmpty());
    }

    @Test
    @Order(8)
    @DirtiesContext
    @DisplayName("Тестируем is not null с Long")
    public void test8() {

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(alexToken);

        ResponseEntity<SheetDto<ProjectRD>> response = rest.exchange(
                projectUrl,
                HttpMethod.GET,
                new HttpEntity<>(headers),
                new ParameterizedTypeReference<>() {
                }
        );
        assertNotNull(response);
        assertNotNull(response.getStatusCode());
        assertEquals(HttpStatus.OK, response.getStatusCode());

        SheetDto<ProjectRD> sheet = response.getBody();
        assertNotNull(sheet);
        assertNotNull(sheet.page());

        List<ProjectRD> projects = sheet.content();
        assertNotNull(projects);
        assertFalse(projects.isEmpty());

        FilterChain filters = FilterChain
                .empty()
                .add(Filter.of("id", Operation.IS_NOT_NULL));

        ResponseEntity<SheetDto<ProjectRD>> response2 = rest.exchange(
                projectUrl + "/search",
                HttpMethod.POST,
                new HttpEntity<>(filters, headers),
                new ParameterizedTypeReference<>() {
                }
        );
        assertNotNull(response2);
        assertNotNull(response2.getStatusCode());
        assertEquals(HttpStatus.OK, response2.getStatusCode());

        SheetDto<ProjectRD> sheet2 = response2.getBody();
        assertNotNull(sheet2);
        assertNotNull(sheet2.page());

        List<ProjectRD> projects2 = sheet2.content();
        assertNotNull(projects2);
        assertFalse(projects2.isEmpty());
        assertEquals(projects, projects2);
    }

    @Test
    @Order(9)
    @DirtiesContext
    @DisplayName("Тестируем greater с Long")
    public void test9() {

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(alexToken);

        ResponseEntity<SheetDto<ProjectRD>> response = rest.exchange(
                projectUrl,
                HttpMethod.GET,
                new HttpEntity<>(headers),
                new ParameterizedTypeReference<>() {
                }
        );
        assertNotNull(response);
        assertNotNull(response.getStatusCode());
        assertEquals(HttpStatus.OK, response.getStatusCode());

        SheetDto<ProjectRD> sheet = response.getBody();
        assertNotNull(sheet);
        assertNotNull(sheet.page());

        List<ProjectRD> projects = sheet.content();
        assertNotNull(projects);
        assertFalse(projects.isEmpty());
        assertTrue(projects.size() > 4);

        FilterChain filters = FilterChain
                .empty()
                .add(Filter.of("id", Operation.GT, 4L));

        ResponseEntity<SheetDto<ProjectRD>> response2 = rest.exchange(
                projectUrl + "/search",
                HttpMethod.POST,
                new HttpEntity<>(filters, headers),
                new ParameterizedTypeReference<>() {
                }
        );
        assertNotNull(response2);
        assertNotNull(response2.getStatusCode());
        assertEquals(HttpStatus.OK, response2.getStatusCode());

        SheetDto<ProjectRD> sheet2 = response2.getBody();
        assertNotNull(sheet2);
        assertNotNull(sheet2.page());

        List<ProjectRD> projects2 = sheet2.content();
        assertNotNull(projects2);
        assertFalse(projects2.isEmpty());

        for (ProjectRD projectRD : projects2) {
            assertNotNull(projectRD);
            assertNotNull(projectRD.id());
            assertTrue(projectRD.id() > 4);
        }
    }

    @Test
    @Order(10)
    @DirtiesContext
    @DisplayName("Тестируем greater с Long (String)")
    public void test10() {

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(alexToken);

        ResponseEntity<SheetDto<ProjectRD>> response = rest.exchange(
                projectUrl,
                HttpMethod.GET,
                new HttpEntity<>(headers),
                new ParameterizedTypeReference<>() {
                }
        );
        assertNotNull(response);
        assertNotNull(response.getStatusCode());
        assertEquals(HttpStatus.OK, response.getStatusCode());

        SheetDto<ProjectRD> sheet = response.getBody();
        assertNotNull(sheet);
        assertNotNull(sheet.page());

        List<ProjectRD> projects = sheet.content();
        assertNotNull(projects);
        assertFalse(projects.isEmpty());
        assertTrue(projects.size() > 4);

        FilterChain filters = FilterChain
                .empty()
                .add(Filter.of("id", Operation.GT, "4"));

        ResponseEntity<SheetDto<ProjectRD>> response2 = rest.exchange(
                projectUrl + "/search",
                HttpMethod.POST,
                new HttpEntity<>(filters, headers),
                new ParameterizedTypeReference<>() {
                }
        );
        assertNotNull(response2);
        assertNotNull(response2.getStatusCode());
        assertEquals(HttpStatus.OK, response2.getStatusCode());

        SheetDto<ProjectRD> sheet2 = response2.getBody();
        assertNotNull(sheet2);
        assertNotNull(sheet2.page());

        List<ProjectRD> projects2 = sheet2.content();
        assertNotNull(projects2);
        assertFalse(projects2.isEmpty());

        for (ProjectRD projectRD : projects2) {
            assertNotNull(projectRD);
            assertNotNull(projectRD.id());
            assertTrue(projectRD.id() > 4);
        }
    }

    @Test
    @Order(11)
    @DirtiesContext
    @DisplayName("Тестируем greater с Long (Integer)")
    public void test11() {

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(alexToken);

        ResponseEntity<SheetDto<ProjectRD>> response = rest.exchange(
                projectUrl,
                HttpMethod.GET,
                new HttpEntity<>(headers),
                new ParameterizedTypeReference<>() {
                }
        );
        assertNotNull(response);
        assertNotNull(response.getStatusCode());
        assertEquals(HttpStatus.OK, response.getStatusCode());

        SheetDto<ProjectRD> sheet = response.getBody();
        assertNotNull(sheet);
        assertNotNull(sheet.page());

        List<ProjectRD> projects = sheet.content();
        assertNotNull(projects);
        assertFalse(projects.isEmpty());
        assertTrue(projects.size() > 4);

        FilterChain filters = FilterChain
                .empty()
                .add(Filter.of("id", Operation.GT, 4));

        ResponseEntity<SheetDto<ProjectRD>> response2 = rest.exchange(
                projectUrl + "/search",
                HttpMethod.POST,
                new HttpEntity<>(filters, headers),
                new ParameterizedTypeReference<>() {
                }
        );
        assertNotNull(response2);
        assertNotNull(response2.getStatusCode());
        assertEquals(HttpStatus.OK, response2.getStatusCode());

        SheetDto<ProjectRD> sheet2 = response2.getBody();
        assertNotNull(sheet2);
        assertNotNull(sheet2.page());

        List<ProjectRD> projects2 = sheet2.content();
        assertNotNull(projects2);
        assertFalse(projects2.isEmpty());

        for (ProjectRD projectRD : projects2) {
            assertNotNull(projectRD);
            assertNotNull(projectRD.id());
            assertTrue(projectRD.id() > 4);
        }
    }

    @Test
    @Order(12)
    @DirtiesContext
    @DisplayName("Тестируем greater or equals с Long")
    public void test12() {

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(alexToken);

        ResponseEntity<SheetDto<ProjectRD>> response = rest.exchange(
                projectUrl,
                HttpMethod.GET,
                new HttpEntity<>(headers),
                new ParameterizedTypeReference<>() {
                }
        );
        assertNotNull(response);
        assertNotNull(response.getStatusCode());
        assertEquals(HttpStatus.OK, response.getStatusCode());

        SheetDto<ProjectRD> sheet = response.getBody();
        assertNotNull(sheet);
        assertNotNull(sheet.page());

        List<ProjectRD> projects = sheet.content();
        assertNotNull(projects);
        assertFalse(projects.isEmpty());
        assertTrue(projects.size() >= 4);

        FilterChain filters = FilterChain
                .empty()
                .add(Filter.of("id", Operation.GE, 4L));

        ResponseEntity<SheetDto<ProjectRD>> response2 = rest.exchange(
                projectUrl + "/search",
                HttpMethod.POST,
                new HttpEntity<>(filters, headers),
                new ParameterizedTypeReference<>() {
                }
        );
        assertNotNull(response2);
        assertNotNull(response2.getStatusCode());
        assertEquals(HttpStatus.OK, response2.getStatusCode());

        SheetDto<ProjectRD> sheet2 = response2.getBody();
        assertNotNull(sheet2);
        assertNotNull(sheet2.page());

        List<ProjectRD> projects2 = sheet2.content();
        assertNotNull(projects2);
        assertFalse(projects2.isEmpty());

        for (ProjectRD projectRD : projects2) {
            assertNotNull(projectRD);
            assertNotNull(projectRD.id());
            assertTrue(projectRD.id() >= 4);
        }
    }

    @Test
    @Order(13)
    @DirtiesContext
    @DisplayName("Тестируем greater or equals с Long (String)")
    public void test13() {

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(alexToken);

        ResponseEntity<SheetDto<ProjectRD>> response = rest.exchange(
                projectUrl,
                HttpMethod.GET,
                new HttpEntity<>(headers),
                new ParameterizedTypeReference<>() {
                }
        );
        assertNotNull(response);
        assertNotNull(response.getStatusCode());
        assertEquals(HttpStatus.OK, response.getStatusCode());

        SheetDto<ProjectRD> sheet = response.getBody();
        assertNotNull(sheet);
        assertNotNull(sheet.page());

        List<ProjectRD> projects = sheet.content();
        assertNotNull(projects);
        assertFalse(projects.isEmpty());
        assertTrue(projects.size() >= 4);

        FilterChain filters = FilterChain
                .empty()
                .add(Filter.of("id", Operation.GE, "4"));

        ResponseEntity<SheetDto<ProjectRD>> response2 = rest.exchange(
                projectUrl + "/search",
                HttpMethod.POST,
                new HttpEntity<>(filters, headers),
                new ParameterizedTypeReference<>() {
                }
        );
        assertNotNull(response2);
        assertNotNull(response2.getStatusCode());
        assertEquals(HttpStatus.OK, response2.getStatusCode());

        SheetDto<ProjectRD> sheet2 = response2.getBody();
        assertNotNull(sheet2);
        assertNotNull(sheet2.page());

        List<ProjectRD> projects2 = sheet2.content();
        assertNotNull(projects2);
        assertFalse(projects2.isEmpty());

        for (ProjectRD projectRD : projects2) {
            assertNotNull(projectRD);
            assertNotNull(projectRD.id());
            assertTrue(projectRD.id() >= 4);
        }
    }

    @Test
    @Order(14)
    @DirtiesContext
    @DisplayName("Тестируем greater or equals с Long (Integer)")
    public void test14() {

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(alexToken);

        ResponseEntity<SheetDto<ProjectRD>> response = rest.exchange(
                projectUrl,
                HttpMethod.GET,
                new HttpEntity<>(headers),
                new ParameterizedTypeReference<>() {
                }
        );
        assertNotNull(response);
        assertNotNull(response.getStatusCode());
        assertEquals(HttpStatus.OK, response.getStatusCode());

        SheetDto<ProjectRD> sheet = response.getBody();
        assertNotNull(sheet);
        assertNotNull(sheet.page());

        List<ProjectRD> projects = sheet.content();
        assertNotNull(projects);
        assertFalse(projects.isEmpty());
        assertTrue(projects.size() >= 4);

        FilterChain filters = FilterChain
                .empty()
                .add(Filter.of("id", Operation.GE, 4));

        ResponseEntity<SheetDto<ProjectRD>> response2 = rest.exchange(
                projectUrl + "/search",
                HttpMethod.POST,
                new HttpEntity<>(filters, headers),
                new ParameterizedTypeReference<>() {
                }
        );
        assertNotNull(response2);
        assertNotNull(response2.getStatusCode());
        assertEquals(HttpStatus.OK, response2.getStatusCode());

        SheetDto<ProjectRD> sheet2 = response2.getBody();
        assertNotNull(sheet2);
        assertNotNull(sheet2.page());

        List<ProjectRD> projects2 = sheet2.content();
        assertNotNull(projects2);
        assertFalse(projects2.isEmpty());

        for (ProjectRD projectRD : projects2) {
            assertNotNull(projectRD);
            assertNotNull(projectRD.id());
            assertTrue(projectRD.id() >= 4);
        }
    }

    @Test
    @Order(15)
    @DirtiesContext
    @DisplayName("Тестируем less с Long")
    public void test15() {

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(alexToken);

        ResponseEntity<SheetDto<ProjectRD>> response = rest.exchange(
                projectUrl,
                HttpMethod.GET,
                new HttpEntity<>(headers),
                new ParameterizedTypeReference<>() {
                }
        );
        assertNotNull(response);
        assertNotNull(response.getStatusCode());
        assertEquals(HttpStatus.OK, response.getStatusCode());

        SheetDto<ProjectRD> sheet = response.getBody();
        assertNotNull(sheet);
        assertNotNull(sheet.page());

        List<ProjectRD> projects = sheet.content();
        assertNotNull(projects);
        assertFalse(projects.isEmpty());
        assertTrue(projects.size() > 4);

        FilterChain filters = FilterChain
                .empty()
                .add(Filter.of("id", Operation.LT, 3L));

        ResponseEntity<SheetDto<ProjectRD>> response2 = rest.exchange(
                projectUrl + "/search",
                HttpMethod.POST,
                new HttpEntity<>(filters, headers),
                new ParameterizedTypeReference<>() {
                }
        );
        assertNotNull(response2);
        assertNotNull(response2.getStatusCode());
        assertEquals(HttpStatus.OK, response2.getStatusCode());

        SheetDto<ProjectRD> sheet2 = response2.getBody();
        assertNotNull(sheet2);
        assertNotNull(sheet2.page());

        List<ProjectRD> projects2 = sheet2.content();
        assertNotNull(projects2);
        assertFalse(projects2.isEmpty());

        for (ProjectRD projectRD : projects2) {
            assertNotNull(projectRD);
            assertNotNull(projectRD.id());
            assertTrue(projectRD.id() < 3);
        }
    }

    @Test
    @Order(16)
    @DirtiesContext
    @DisplayName("Тестируем less с Long (String)")
    public void test16() {

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(alexToken);

        ResponseEntity<SheetDto<ProjectRD>> response = rest.exchange(
                projectUrl,
                HttpMethod.GET,
                new HttpEntity<>(headers),
                new ParameterizedTypeReference<>() {
                }
        );
        assertNotNull(response);
        assertNotNull(response.getStatusCode());
        assertEquals(HttpStatus.OK, response.getStatusCode());

        SheetDto<ProjectRD> sheet = response.getBody();
        assertNotNull(sheet);
        assertNotNull(sheet.page());

        List<ProjectRD> projects = sheet.content();
        assertNotNull(projects);
        assertFalse(projects.isEmpty());
        assertTrue(projects.size() > 4);

        FilterChain filters = FilterChain
                .empty()
                .add(Filter.of("id", Operation.LT, "3"));

        ResponseEntity<SheetDto<ProjectRD>> response2 = rest.exchange(
                projectUrl + "/search",
                HttpMethod.POST,
                new HttpEntity<>(filters, headers),
                new ParameterizedTypeReference<>() {
                }
        );
        assertNotNull(response2);
        assertNotNull(response2.getStatusCode());
        assertEquals(HttpStatus.OK, response2.getStatusCode());

        SheetDto<ProjectRD> sheet2 = response2.getBody();
        assertNotNull(sheet2);
        assertNotNull(sheet2.page());

        List<ProjectRD> projects2 = sheet2.content();
        assertNotNull(projects2);
        assertFalse(projects2.isEmpty());

        for (ProjectRD projectRD : projects2) {
            assertNotNull(projectRD);
            assertNotNull(projectRD.id());
            assertTrue(projectRD.id() < 3);
        }
    }

    @Test
    @Order(17)
    @DirtiesContext
    @DisplayName("Тестируем less с Long (Integer)")
    public void test17() {

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(alexToken);

        ResponseEntity<SheetDto<ProjectRD>> response = rest.exchange(
                projectUrl,
                HttpMethod.GET,
                new HttpEntity<>(headers),
                new ParameterizedTypeReference<>() {
                }
        );
        assertNotNull(response);
        assertNotNull(response.getStatusCode());
        assertEquals(HttpStatus.OK, response.getStatusCode());

        SheetDto<ProjectRD> sheet = response.getBody();
        assertNotNull(sheet);
        assertNotNull(sheet.page());

        List<ProjectRD> projects = sheet.content();
        assertNotNull(projects);
        assertFalse(projects.isEmpty());
        assertTrue(projects.size() > 4);

        FilterChain filters = FilterChain
                .empty()
                .add(Filter.of("id", Operation.LT, 3));

        ResponseEntity<SheetDto<ProjectRD>> response2 = rest.exchange(
                projectUrl + "/search",
                HttpMethod.POST,
                new HttpEntity<>(filters, headers),
                new ParameterizedTypeReference<>() {
                }
        );
        assertNotNull(response2);
        assertNotNull(response2.getStatusCode());
        assertEquals(HttpStatus.OK, response2.getStatusCode());

        SheetDto<ProjectRD> sheet2 = response2.getBody();
        assertNotNull(sheet2);
        assertNotNull(sheet2.page());

        List<ProjectRD> projects2 = sheet2.content();
        assertNotNull(projects2);
        assertFalse(projects2.isEmpty());

        for (ProjectRD projectRD : projects2) {
            assertNotNull(projectRD);
            assertNotNull(projectRD.id());
            assertTrue(projectRD.id() < 3);
        }
    }

    @Test
    @Order(18)
    @DirtiesContext
    @DisplayName("Тестируем less or equals с Long")
    public void test18() {

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(alexToken);

        ResponseEntity<SheetDto<ProjectRD>> response = rest.exchange(
                projectUrl,
                HttpMethod.GET,
                new HttpEntity<>(headers),
                new ParameterizedTypeReference<>() {
                }
        );
        assertNotNull(response);
        assertNotNull(response.getStatusCode());
        assertEquals(HttpStatus.OK, response.getStatusCode());

        SheetDto<ProjectRD> sheet = response.getBody();
        assertNotNull(sheet);
        assertNotNull(sheet.page());

        List<ProjectRD> projects = sheet.content();
        assertNotNull(projects);
        assertFalse(projects.isEmpty());
        assertTrue(projects.size() > 4);

        FilterChain filters = FilterChain
                .empty()
                .add(Filter.of("id", Operation.LE, 3L));

        ResponseEntity<SheetDto<ProjectRD>> response2 = rest.exchange(
                projectUrl + "/search",
                HttpMethod.POST,
                new HttpEntity<>(filters, headers),
                new ParameterizedTypeReference<>() {
                }
        );
        assertNotNull(response2);
        assertNotNull(response2.getStatusCode());
        assertEquals(HttpStatus.OK, response2.getStatusCode());

        SheetDto<ProjectRD> sheet2 = response2.getBody();
        assertNotNull(sheet2);
        assertNotNull(sheet2.page());

        List<ProjectRD> projects2 = sheet2.content();
        assertNotNull(projects2);
        assertFalse(projects2.isEmpty());

        for (ProjectRD projectRD : projects2) {
            assertNotNull(projectRD);
            assertNotNull(projectRD.id());
            assertTrue(projectRD.id() <= 3);
        }
    }

    @Test
    @Order(19)
    @DirtiesContext
    @DisplayName("Тестируем less or equals с Long (String)")
    public void test19() {

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(alexToken);

        ResponseEntity<SheetDto<ProjectRD>> response = rest.exchange(
                projectUrl,
                HttpMethod.GET,
                new HttpEntity<>(headers),
                new ParameterizedTypeReference<>() {
                }
        );
        assertNotNull(response);
        assertNotNull(response.getStatusCode());
        assertEquals(HttpStatus.OK, response.getStatusCode());

        SheetDto<ProjectRD> sheet = response.getBody();
        assertNotNull(sheet);
        assertNotNull(sheet.page());

        List<ProjectRD> projects = sheet.content();
        assertNotNull(projects);
        assertFalse(projects.isEmpty());
        assertTrue(projects.size() > 4);

        FilterChain filters = FilterChain
                .empty()
                .add(Filter.of("id", Operation.LE, "3"));

        ResponseEntity<SheetDto<ProjectRD>> response2 = rest.exchange(
                projectUrl + "/search",
                HttpMethod.POST,
                new HttpEntity<>(filters, headers),
                new ParameterizedTypeReference<>() {
                }
        );
        assertNotNull(response2);
        assertNotNull(response2.getStatusCode());
        assertEquals(HttpStatus.OK, response2.getStatusCode());

        SheetDto<ProjectRD> sheet2 = response2.getBody();
        assertNotNull(sheet2);
        assertNotNull(sheet2.page());

        List<ProjectRD> projects2 = sheet2.content();
        assertNotNull(projects2);
        assertFalse(projects2.isEmpty());

        for (ProjectRD projectRD : projects2) {
            assertNotNull(projectRD);
            assertNotNull(projectRD.id());
            assertTrue(projectRD.id() <= 3);
        }
    }

    @Test
    @Order(20)
    @DirtiesContext
    @DisplayName("Тестируем less or equals с Long (Integer)")
    public void test20() {

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(alexToken);

        ResponseEntity<SheetDto<ProjectRD>> response = rest.exchange(
                projectUrl,
                HttpMethod.GET,
                new HttpEntity<>(headers),
                new ParameterizedTypeReference<>() {
                }
        );
        assertNotNull(response);
        assertNotNull(response.getStatusCode());
        assertEquals(HttpStatus.OK, response.getStatusCode());

        SheetDto<ProjectRD> sheet = response.getBody();
        assertNotNull(sheet);
        assertNotNull(sheet.page());

        List<ProjectRD> projects = sheet.content();
        assertNotNull(projects);
        assertFalse(projects.isEmpty());
        assertTrue(projects.size() > 4);

        FilterChain filters = FilterChain
                .empty()
                .add(Filter.of("id", Operation.LE, 3));

        ResponseEntity<SheetDto<ProjectRD>> response2 = rest.exchange(
                projectUrl + "/search",
                HttpMethod.POST,
                new HttpEntity<>(filters, headers),
                new ParameterizedTypeReference<>() {
                }
        );
        assertNotNull(response2);
        assertNotNull(response2.getStatusCode());
        assertEquals(HttpStatus.OK, response2.getStatusCode());

        SheetDto<ProjectRD> sheet2 = response2.getBody();
        assertNotNull(sheet2);
        assertNotNull(sheet2.page());

        List<ProjectRD> projects2 = sheet2.content();
        assertNotNull(projects2);
        assertFalse(projects2.isEmpty());

        for (ProjectRD projectRD : projects2) {
            assertNotNull(projectRD);
            assertNotNull(projectRD.id());
            assertTrue(projectRD.id() <= 3);
        }
    }

    @Test
    @Order(21)
    @DirtiesContext
    @DisplayName("Тестируем like с Long")
    public void test21() {

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(alexToken);

        ResponseEntity<SheetDto<ProjectRD>> response = rest.exchange(
                projectUrl,
                HttpMethod.GET,
                new HttpEntity<>(headers),
                new ParameterizedTypeReference<>() {
                }
        );
        assertNotNull(response);
        assertNotNull(response.getStatusCode());
        assertEquals(HttpStatus.OK, response.getStatusCode());

        SheetDto<ProjectRD> sheet = response.getBody();
        assertNotNull(sheet);
        assertNotNull(sheet.page());

        List<ProjectRD> projects = sheet.content();
        assertNotNull(projects);
        assertFalse(projects.isEmpty());
        assertTrue(projects.size() > 4);

        FilterChain filters = FilterChain
                .empty()
                .add(Filter.of("id", Operation.LIKE, 4L));

        ResponseEntity<SheetDto<ProjectRD>> response2 = rest.exchange(
                projectUrl + "/search",
                HttpMethod.POST,
                new HttpEntity<>(filters, headers),
                new ParameterizedTypeReference<>() {
                }
        );
        assertNotNull(response2);
        assertNotNull(response2.getStatusCode());
        assertEquals(HttpStatus.OK, response2.getStatusCode());

        SheetDto<ProjectRD> sheet2 = response2.getBody();
        assertNotNull(sheet2);
        assertNotNull(sheet2.page());

        List<ProjectRD> projects2 = sheet2.content();
        assertNotNull(projects2);
        assertFalse(projects2.isEmpty());

        for (ProjectRD projectRD : projects2) {
            assertNotNull(projectRD);
            assertNotNull(projectRD.id());
            assertTrue(projectRD.id().toString().contains("4"));
        }
    }

    @Test
    @Order(22)
    @DirtiesContext
    @DisplayName("Тестируем like с Long (String)")
    public void test22() {

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(alexToken);

        ResponseEntity<SheetDto<ProjectRD>> response = rest.exchange(
                projectUrl,
                HttpMethod.GET,
                new HttpEntity<>(headers),
                new ParameterizedTypeReference<>() {
                }
        );
        assertNotNull(response);
        assertNotNull(response.getStatusCode());
        assertEquals(HttpStatus.OK, response.getStatusCode());

        SheetDto<ProjectRD> sheet = response.getBody();
        assertNotNull(sheet);
        assertNotNull(sheet.page());

        List<ProjectRD> projects = sheet.content();
        assertNotNull(projects);
        assertFalse(projects.isEmpty());
        assertTrue(projects.size() > 4);

        FilterChain filters = FilterChain
                .empty()
                .add(Filter.of("id", Operation.LIKE, "4"));

        ResponseEntity<SheetDto<ProjectRD>> response2 = rest.exchange(
                projectUrl + "/search",
                HttpMethod.POST,
                new HttpEntity<>(filters, headers),
                new ParameterizedTypeReference<>() {
                }
        );
        assertNotNull(response2);
        assertNotNull(response2.getStatusCode());
        assertEquals(HttpStatus.OK, response2.getStatusCode());

        SheetDto<ProjectRD> sheet2 = response2.getBody();
        assertNotNull(sheet2);
        assertNotNull(sheet2.page());

        List<ProjectRD> projects2 = sheet2.content();
        assertNotNull(projects2);
        assertFalse(projects2.isEmpty());

        for (ProjectRD projectRD : projects2) {
            assertNotNull(projectRD);
            assertNotNull(projectRD.id());
            assertTrue(projectRD.id().toString().contains("4"));
        }
    }

    @Test
    @Order(23)
    @DirtiesContext
    @DisplayName("Тестируем like с Long (Integer)")
    public void test23() {

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(alexToken);

        ResponseEntity<SheetDto<ProjectRD>> response = rest.exchange(
                projectUrl,
                HttpMethod.GET,
                new HttpEntity<>(headers),
                new ParameterizedTypeReference<>() {
                }
        );
        assertNotNull(response);
        assertNotNull(response.getStatusCode());
        assertEquals(HttpStatus.OK, response.getStatusCode());

        SheetDto<ProjectRD> sheet = response.getBody();
        assertNotNull(sheet);
        assertNotNull(sheet.page());

        List<ProjectRD> projects = sheet.content();
        assertNotNull(projects);
        assertFalse(projects.isEmpty());
        assertTrue(projects.size() > 4);

        FilterChain filters = FilterChain
                .empty()
                .add(Filter.of("id", Operation.LIKE, 4));

        ResponseEntity<SheetDto<ProjectRD>> response2 = rest.exchange(
                projectUrl + "/search",
                HttpMethod.POST,
                new HttpEntity<>(filters, headers),
                new ParameterizedTypeReference<>() {
                }
        );
        assertNotNull(response2);
        assertNotNull(response2.getStatusCode());
        assertEquals(HttpStatus.OK, response2.getStatusCode());

        SheetDto<ProjectRD> sheet2 = response2.getBody();
        assertNotNull(sheet2);
        assertNotNull(sheet2.page());

        List<ProjectRD> projects2 = sheet2.content();
        assertNotNull(projects2);
        assertFalse(projects2.isEmpty());

        for (ProjectRD projectRD : projects2) {
            assertNotNull(projectRD);
            assertNotNull(projectRD.id());
            assertTrue(projectRD.id().toString().contains("4"));
        }
    }

    @Test
    @Order(24)
    @DirtiesContext
    @DisplayName("Тестируем not like с Long")
    public void test24() {

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(alexToken);

        ResponseEntity<SheetDto<ProjectRD>> response = rest.exchange(
                projectUrl,
                HttpMethod.GET,
                new HttpEntity<>(headers),
                new ParameterizedTypeReference<>() {
                }
        );
        assertNotNull(response);
        assertNotNull(response.getStatusCode());
        assertEquals(HttpStatus.OK, response.getStatusCode());

        SheetDto<ProjectRD> sheet = response.getBody();
        assertNotNull(sheet);
        assertNotNull(sheet.page());

        List<ProjectRD> projects = sheet.content();
        assertNotNull(projects);
        assertFalse(projects.isEmpty());
        assertTrue(projects.size() > 4);

        FilterChain filters = FilterChain
                .empty()
                .add(Filter.of("id", Operation.NOT_LIKE, 4L));

        ResponseEntity<SheetDto<ProjectRD>> response2 = rest.exchange(
                projectUrl + "/search",
                HttpMethod.POST,
                new HttpEntity<>(filters, headers),
                new ParameterizedTypeReference<>() {
                }
        );
        assertNotNull(response2);
        assertNotNull(response2.getStatusCode());
        assertEquals(HttpStatus.OK, response2.getStatusCode());

        SheetDto<ProjectRD> sheet2 = response2.getBody();
        assertNotNull(sheet2);
        assertNotNull(sheet2.page());

        List<ProjectRD> projects2 = sheet2.content();
        assertNotNull(projects2);
        assertFalse(projects2.isEmpty());

        for (ProjectRD projectRD : projects2) {
            assertNotNull(projectRD);
            assertNotNull(projectRD.id());
            assertFalse(projectRD.id().toString().contains("4"));
        }
    }

    @Test
    @Order(25)
    @DirtiesContext
    @DisplayName("Тестируем not like с Long (String)")
    public void test25() {

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(alexToken);

        ResponseEntity<SheetDto<ProjectRD>> response = rest.exchange(
                projectUrl,
                HttpMethod.GET,
                new HttpEntity<>(headers),
                new ParameterizedTypeReference<>() {
                }
        );
        assertNotNull(response);
        assertNotNull(response.getStatusCode());
        assertEquals(HttpStatus.OK, response.getStatusCode());

        SheetDto<ProjectRD> sheet = response.getBody();
        assertNotNull(sheet);
        assertNotNull(sheet.page());

        List<ProjectRD> projects = sheet.content();
        assertNotNull(projects);
        assertFalse(projects.isEmpty());
        assertTrue(projects.size() > 4);

        FilterChain filters = FilterChain
                .empty()
                .add(Filter.of("id", Operation.NOT_LIKE, "4"));

        ResponseEntity<SheetDto<ProjectRD>> response2 = rest.exchange(
                projectUrl + "/search",
                HttpMethod.POST,
                new HttpEntity<>(filters, headers),
                new ParameterizedTypeReference<>() {
                }
        );
        assertNotNull(response2);
        assertNotNull(response2.getStatusCode());
        assertEquals(HttpStatus.OK, response2.getStatusCode());

        SheetDto<ProjectRD> sheet2 = response2.getBody();
        assertNotNull(sheet2);
        assertNotNull(sheet2.page());

        List<ProjectRD> projects2 = sheet2.content();
        assertNotNull(projects2);
        assertFalse(projects2.isEmpty());

        for (ProjectRD projectRD : projects2) {
            assertNotNull(projectRD);
            assertNotNull(projectRD.id());
            assertFalse(projectRD.id().toString().contains("4"));
        }
    }

    @Test
    @Order(26)
    @DirtiesContext
    @DisplayName("Тестируем not like с Long (Integer)")
    public void test26() {

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(alexToken);

        ResponseEntity<SheetDto<ProjectRD>> response = rest.exchange(
                projectUrl,
                HttpMethod.GET,
                new HttpEntity<>(headers),
                new ParameterizedTypeReference<>() {
                }
        );
        assertNotNull(response);
        assertNotNull(response.getStatusCode());
        assertEquals(HttpStatus.OK, response.getStatusCode());

        SheetDto<ProjectRD> sheet = response.getBody();
        assertNotNull(sheet);
        assertNotNull(sheet.page());

        List<ProjectRD> projects = sheet.content();
        assertNotNull(projects);
        assertFalse(projects.isEmpty());
        assertTrue(projects.size() > 4);

        FilterChain filters = FilterChain
                .empty()
                .add(Filter.of("id", Operation.NOT_LIKE, 4));

        ResponseEntity<SheetDto<ProjectRD>> response2 = rest.exchange(
                projectUrl + "/search",
                HttpMethod.POST,
                new HttpEntity<>(filters, headers),
                new ParameterizedTypeReference<>() {
                }
        );
        assertNotNull(response2);
        assertNotNull(response2.getStatusCode());
        assertEquals(HttpStatus.OK, response2.getStatusCode());

        SheetDto<ProjectRD> sheet2 = response2.getBody();
        assertNotNull(sheet2);
        assertNotNull(sheet2.page());

        List<ProjectRD> projects2 = sheet2.content();
        assertNotNull(projects2);
        assertFalse(projects2.isEmpty());

        for (ProjectRD projectRD : projects2) {
            assertNotNull(projectRD);
            assertNotNull(projectRD.id());
            assertFalse(projectRD.id().toString().contains("4"));
        }
    }

    @Test
    @Order(27)
    @DirtiesContext
    @DisplayName("Тестируем starts with с Long")
    public void test27() {

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(alexToken);

        ResponseEntity<SheetDto<ProjectRD>> response = rest.exchange(
                projectUrl,
                HttpMethod.GET,
                new HttpEntity<>(headers),
                new ParameterizedTypeReference<>() {
                }
        );
        assertNotNull(response);
        assertNotNull(response.getStatusCode());
        assertEquals(HttpStatus.OK, response.getStatusCode());

        SheetDto<ProjectRD> sheet = response.getBody();
        assertNotNull(sheet);
        assertNotNull(sheet.page());

        List<ProjectRD> projects = sheet.content();
        assertNotNull(projects);
        assertFalse(projects.isEmpty());
        assertTrue(projects.size() > 4);

        FilterChain filters = FilterChain
                .empty()
                .add(Filter.of("id", Operation.STARTS_WITH, 4L));

        ResponseEntity<SheetDto<ProjectRD>> response2 = rest.exchange(
                projectUrl + "/search",
                HttpMethod.POST,
                new HttpEntity<>(filters, headers),
                new ParameterizedTypeReference<>() {
                }
        );
        assertNotNull(response2);
        assertNotNull(response2.getStatusCode());
        assertEquals(HttpStatus.OK, response2.getStatusCode());

        SheetDto<ProjectRD> sheet2 = response2.getBody();
        assertNotNull(sheet2);
        assertNotNull(sheet2.page());

        List<ProjectRD> projects2 = sheet2.content();
        assertNotNull(projects2);
        assertFalse(projects2.isEmpty());

        for (ProjectRD projectRD : projects2) {
            assertNotNull(projectRD);
            assertNotNull(projectRD.id());
            assertTrue(projectRD.id().toString().startsWith("4"));
        }
    }

    @Test
    @Order(28)
    @DirtiesContext
    @DisplayName("Тестируем starts with с Long (String)")
    public void test28() {

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(alexToken);

        ResponseEntity<SheetDto<ProjectRD>> response = rest.exchange(
                projectUrl,
                HttpMethod.GET,
                new HttpEntity<>(headers),
                new ParameterizedTypeReference<>() {
                }
        );
        assertNotNull(response);
        assertNotNull(response.getStatusCode());
        assertEquals(HttpStatus.OK, response.getStatusCode());

        SheetDto<ProjectRD> sheet = response.getBody();
        assertNotNull(sheet);
        assertNotNull(sheet.page());

        List<ProjectRD> projects = sheet.content();
        assertNotNull(projects);
        assertFalse(projects.isEmpty());
        assertTrue(projects.size() > 4);

        FilterChain filters = FilterChain
                .empty()
                .add(Filter.of("id", Operation.STARTS_WITH, "4"));

        ResponseEntity<SheetDto<ProjectRD>> response2 = rest.exchange(
                projectUrl + "/search",
                HttpMethod.POST,
                new HttpEntity<>(filters, headers),
                new ParameterizedTypeReference<>() {
                }
        );
        assertNotNull(response2);
        assertNotNull(response2.getStatusCode());
        assertEquals(HttpStatus.OK, response2.getStatusCode());

        SheetDto<ProjectRD> sheet2 = response2.getBody();
        assertNotNull(sheet2);
        assertNotNull(sheet2.page());

        List<ProjectRD> projects2 = sheet2.content();
        assertNotNull(projects2);
        assertFalse(projects2.isEmpty());

        for (ProjectRD projectRD : projects2) {
            assertNotNull(projectRD);
            assertNotNull(projectRD.id());
            assertTrue(projectRD.id().toString().startsWith("4"));
        }
    }

    @Test
    @Order(29)
    @DirtiesContext
    @DisplayName("Тестируем starts with с Long (Integer)")
    public void test29() {

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(alexToken);

        ResponseEntity<SheetDto<ProjectRD>> response = rest.exchange(
                projectUrl,
                HttpMethod.GET,
                new HttpEntity<>(headers),
                new ParameterizedTypeReference<>() {
                }
        );
        assertNotNull(response);
        assertNotNull(response.getStatusCode());
        assertEquals(HttpStatus.OK, response.getStatusCode());

        SheetDto<ProjectRD> sheet = response.getBody();
        assertNotNull(sheet);
        assertNotNull(sheet.page());

        List<ProjectRD> projects = sheet.content();
        assertNotNull(projects);
        assertFalse(projects.isEmpty());
        assertTrue(projects.size() > 4);

        FilterChain filters = FilterChain
                .empty()
                .add(Filter.of("id", Operation.STARTS_WITH, 4));

        ResponseEntity<SheetDto<ProjectRD>> response2 = rest.exchange(
                projectUrl + "/search",
                HttpMethod.POST,
                new HttpEntity<>(filters, headers),
                new ParameterizedTypeReference<>() {
                }
        );
        assertNotNull(response2);
        assertNotNull(response2.getStatusCode());
        assertEquals(HttpStatus.OK, response2.getStatusCode());

        SheetDto<ProjectRD> sheet2 = response2.getBody();
        assertNotNull(sheet2);
        assertNotNull(sheet2.page());

        List<ProjectRD> projects2 = sheet2.content();
        assertNotNull(projects2);
        assertFalse(projects2.isEmpty());

        for (ProjectRD projectRD : projects2) {
            assertNotNull(projectRD);
            assertNotNull(projectRD.id());
            assertTrue(projectRD.id().toString().startsWith("4"));
        }
    }

    @Test
    @Order(30)
    @DirtiesContext
    @DisplayName("Тестируем not starts with с Long")
    public void test30() {

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(alexToken);

        ResponseEntity<SheetDto<ProjectRD>> response = rest.exchange(
                projectUrl,
                HttpMethod.GET,
                new HttpEntity<>(headers),
                new ParameterizedTypeReference<>() {
                }
        );
        assertNotNull(response);
        assertNotNull(response.getStatusCode());
        assertEquals(HttpStatus.OK, response.getStatusCode());

        SheetDto<ProjectRD> sheet = response.getBody();
        assertNotNull(sheet);
        assertNotNull(sheet.page());

        List<ProjectRD> projects = sheet.content();
        assertNotNull(projects);
        assertFalse(projects.isEmpty());
        assertTrue(projects.size() > 4);

        FilterChain filters = FilterChain
                .empty()
                .add(Filter.of("id", Operation.NOT_STARTS_WITH, 4L));

        ResponseEntity<SheetDto<ProjectRD>> response2 = rest.exchange(
                projectUrl + "/search",
                HttpMethod.POST,
                new HttpEntity<>(filters, headers),
                new ParameterizedTypeReference<>() {
                }
        );
        assertNotNull(response2);
        assertNotNull(response2.getStatusCode());
        assertEquals(HttpStatus.OK, response2.getStatusCode());

        SheetDto<ProjectRD> sheet2 = response2.getBody();
        assertNotNull(sheet2);
        assertNotNull(sheet2.page());

        List<ProjectRD> projects2 = sheet2.content();
        assertNotNull(projects2);
        assertFalse(projects2.isEmpty());

        for (ProjectRD projectRD : projects2) {
            assertNotNull(projectRD);
            assertNotNull(projectRD.id());
            assertFalse(projectRD.id().toString().startsWith("4"));
        }
    }

    @Test
    @Order(31)
    @DirtiesContext
    @DisplayName("Тестируем not starts with с Long (String)")
    public void test31() {

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(alexToken);

        ResponseEntity<SheetDto<ProjectRD>> response = rest.exchange(
                projectUrl,
                HttpMethod.GET,
                new HttpEntity<>(headers),
                new ParameterizedTypeReference<>() {
                }
        );
        assertNotNull(response);
        assertNotNull(response.getStatusCode());
        assertEquals(HttpStatus.OK, response.getStatusCode());

        SheetDto<ProjectRD> sheet = response.getBody();
        assertNotNull(sheet);
        assertNotNull(sheet.page());

        List<ProjectRD> projects = sheet.content();
        assertNotNull(projects);
        assertFalse(projects.isEmpty());
        assertTrue(projects.size() > 4);

        FilterChain filters = FilterChain
                .empty()
                .add(Filter.of("id", Operation.NOT_STARTS_WITH, "4"));

        ResponseEntity<SheetDto<ProjectRD>> response2 = rest.exchange(
                projectUrl + "/search",
                HttpMethod.POST,
                new HttpEntity<>(filters, headers),
                new ParameterizedTypeReference<>() {
                }
        );
        assertNotNull(response2);
        assertNotNull(response2.getStatusCode());
        assertEquals(HttpStatus.OK, response2.getStatusCode());

        SheetDto<ProjectRD> sheet2 = response2.getBody();
        assertNotNull(sheet2);
        assertNotNull(sheet2.page());

        List<ProjectRD> projects2 = sheet2.content();
        assertNotNull(projects2);
        assertFalse(projects2.isEmpty());

        for (ProjectRD projectRD : projects2) {
            assertNotNull(projectRD);
            assertNotNull(projectRD.id());
            assertFalse(projectRD.id().toString().startsWith("4"));
        }
    }

    @Test
    @Order(32)
    @DirtiesContext
    @DisplayName("Тестируем not starts with с Long (Integer)")
    public void test32() {

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(alexToken);

        ResponseEntity<SheetDto<ProjectRD>> response = rest.exchange(
                projectUrl,
                HttpMethod.GET,
                new HttpEntity<>(headers),
                new ParameterizedTypeReference<>() {
                }
        );
        assertNotNull(response);
        assertNotNull(response.getStatusCode());
        assertEquals(HttpStatus.OK, response.getStatusCode());

        SheetDto<ProjectRD> sheet = response.getBody();
        assertNotNull(sheet);
        assertNotNull(sheet.page());

        List<ProjectRD> projects = sheet.content();
        assertNotNull(projects);
        assertFalse(projects.isEmpty());
        assertTrue(projects.size() > 4);

        FilterChain filters = FilterChain
                .empty()
                .add(Filter.of("id", Operation.NOT_STARTS_WITH, 4));

        ResponseEntity<SheetDto<ProjectRD>> response2 = rest.exchange(
                projectUrl + "/search",
                HttpMethod.POST,
                new HttpEntity<>(filters, headers),
                new ParameterizedTypeReference<>() {
                }
        );
        assertNotNull(response2);
        assertNotNull(response2.getStatusCode());
        assertEquals(HttpStatus.OK, response2.getStatusCode());

        SheetDto<ProjectRD> sheet2 = response2.getBody();
        assertNotNull(sheet2);
        assertNotNull(sheet2.page());

        List<ProjectRD> projects2 = sheet2.content();
        assertNotNull(projects2);
        assertFalse(projects2.isEmpty());

        for (ProjectRD projectRD : projects2) {
            assertNotNull(projectRD);
            assertNotNull(projectRD.id());
            assertFalse(projectRD.id().toString().startsWith("4"));
        }
    }

    @Test
    @Order(33)
    @DirtiesContext
    @DisplayName("Тестируем ends with с Long")
    public void test33() {

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(alexToken);

        ResponseEntity<SheetDto<ProjectRD>> response = rest.exchange(
                projectUrl,
                HttpMethod.GET,
                new HttpEntity<>(headers),
                new ParameterizedTypeReference<>() {
                }
        );
        assertNotNull(response);
        assertNotNull(response.getStatusCode());
        assertEquals(HttpStatus.OK, response.getStatusCode());

        SheetDto<ProjectRD> sheet = response.getBody();
        assertNotNull(sheet);
        assertNotNull(sheet.page());

        List<ProjectRD> projects = sheet.content();
        assertNotNull(projects);
        assertFalse(projects.isEmpty());
        assertTrue(projects.size() > 4);

        FilterChain filters = FilterChain
                .empty()
                .add(Filter.of("id", Operation.ENDS_WITH, 4L));

        ResponseEntity<SheetDto<ProjectRD>> response2 = rest.exchange(
                projectUrl + "/search",
                HttpMethod.POST,
                new HttpEntity<>(filters, headers),
                new ParameterizedTypeReference<>() {
                }
        );
        assertNotNull(response2);
        assertNotNull(response2.getStatusCode());
        assertEquals(HttpStatus.OK, response2.getStatusCode());

        SheetDto<ProjectRD> sheet2 = response2.getBody();
        assertNotNull(sheet2);
        assertNotNull(sheet2.page());

        List<ProjectRD> projects2 = sheet2.content();
        assertNotNull(projects2);
        assertFalse(projects2.isEmpty());

        for (ProjectRD projectRD : projects2) {
            assertNotNull(projectRD);
            assertNotNull(projectRD.id());
            assertTrue(projectRD.id().toString().endsWith("4"));
        }
    }

    @Test
    @Order(34)
    @DirtiesContext
    @DisplayName("Тестируем ends with с Long (String)")
    public void test34() {

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(alexToken);

        ResponseEntity<SheetDto<ProjectRD>> response = rest.exchange(
                projectUrl,
                HttpMethod.GET,
                new HttpEntity<>(headers),
                new ParameterizedTypeReference<>() {
                }
        );
        assertNotNull(response);
        assertNotNull(response.getStatusCode());
        assertEquals(HttpStatus.OK, response.getStatusCode());

        SheetDto<ProjectRD> sheet = response.getBody();
        assertNotNull(sheet);
        assertNotNull(sheet.page());

        List<ProjectRD> projects = sheet.content();
        assertNotNull(projects);
        assertFalse(projects.isEmpty());
        assertTrue(projects.size() > 4);

        FilterChain filters = FilterChain
                .empty()
                .add(Filter.of("id", Operation.ENDS_WITH, "4"));

        ResponseEntity<SheetDto<ProjectRD>> response2 = rest.exchange(
                projectUrl + "/search",
                HttpMethod.POST,
                new HttpEntity<>(filters, headers),
                new ParameterizedTypeReference<>() {
                }
        );
        assertNotNull(response2);
        assertNotNull(response2.getStatusCode());
        assertEquals(HttpStatus.OK, response2.getStatusCode());

        SheetDto<ProjectRD> sheet2 = response2.getBody();
        assertNotNull(sheet2);
        assertNotNull(sheet2.page());

        List<ProjectRD> projects2 = sheet2.content();
        assertNotNull(projects2);
        assertFalse(projects2.isEmpty());

        for (ProjectRD projectRD : projects2) {
            assertNotNull(projectRD);
            assertNotNull(projectRD.id());
            assertTrue(projectRD.id().toString().endsWith("4"));
        }
    }

    @Test
    @Order(35)
    @DirtiesContext
    @DisplayName("Тестируем ends with с Long (Integer)")
    public void test35() {

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(alexToken);

        ResponseEntity<SheetDto<ProjectRD>> response = rest.exchange(
                projectUrl,
                HttpMethod.GET,
                new HttpEntity<>(headers),
                new ParameterizedTypeReference<>() {
                }
        );
        assertNotNull(response);
        assertNotNull(response.getStatusCode());
        assertEquals(HttpStatus.OK, response.getStatusCode());

        SheetDto<ProjectRD> sheet = response.getBody();
        assertNotNull(sheet);
        assertNotNull(sheet.page());

        List<ProjectRD> projects = sheet.content();
        assertNotNull(projects);
        assertFalse(projects.isEmpty());
        assertTrue(projects.size() > 4);

        FilterChain filters = FilterChain
                .empty()
                .add(Filter.of("id", Operation.ENDS_WITH, 4));

        ResponseEntity<SheetDto<ProjectRD>> response2 = rest.exchange(
                projectUrl + "/search",
                HttpMethod.POST,
                new HttpEntity<>(filters, headers),
                new ParameterizedTypeReference<>() {
                }
        );
        assertNotNull(response2);
        assertNotNull(response2.getStatusCode());
        assertEquals(HttpStatus.OK, response2.getStatusCode());

        SheetDto<ProjectRD> sheet2 = response2.getBody();
        assertNotNull(sheet2);
        assertNotNull(sheet2.page());

        List<ProjectRD> projects2 = sheet2.content();
        assertNotNull(projects2);
        assertFalse(projects2.isEmpty());

        for (ProjectRD projectRD : projects2) {
            assertNotNull(projectRD);
            assertNotNull(projectRD.id());
            assertTrue(projectRD.id().toString().endsWith("4"));
        }
    }

    @Test
    @Order(36)
    @DirtiesContext
    @DisplayName("Тестируем not ends with с Long")
    public void test36() {

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(alexToken);

        ResponseEntity<SheetDto<ProjectRD>> response = rest.exchange(
                projectUrl,
                HttpMethod.GET,
                new HttpEntity<>(headers),
                new ParameterizedTypeReference<>() {
                }
        );
        assertNotNull(response);
        assertNotNull(response.getStatusCode());
        assertEquals(HttpStatus.OK, response.getStatusCode());

        SheetDto<ProjectRD> sheet = response.getBody();
        assertNotNull(sheet);
        assertNotNull(sheet.page());

        List<ProjectRD> projects = sheet.content();
        assertNotNull(projects);
        assertFalse(projects.isEmpty());
        assertTrue(projects.size() > 4);

        FilterChain filters = FilterChain
                .empty()
                .add(Filter.of("id", Operation.NOT_ENDS_WITH, 4L));

        ResponseEntity<SheetDto<ProjectRD>> response2 = rest.exchange(
                projectUrl + "/search",
                HttpMethod.POST,
                new HttpEntity<>(filters, headers),
                new ParameterizedTypeReference<>() {
                }
        );
        assertNotNull(response2);
        assertNotNull(response2.getStatusCode());
        assertEquals(HttpStatus.OK, response2.getStatusCode());

        SheetDto<ProjectRD> sheet2 = response2.getBody();
        assertNotNull(sheet2);
        assertNotNull(sheet2.page());

        List<ProjectRD> projects2 = sheet2.content();
        assertNotNull(projects2);
        assertFalse(projects2.isEmpty());

        for (ProjectRD projectRD : projects2) {
            assertNotNull(projectRD);
            assertNotNull(projectRD.id());
            assertFalse(projectRD.id().toString().endsWith("4"));
        }
    }

    @Test
    @Order(37)
    @DirtiesContext
    @DisplayName("Тестируем not ends with с Long (String)")
    public void test37() {

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(alexToken);

        ResponseEntity<SheetDto<ProjectRD>> response = rest.exchange(
                projectUrl,
                HttpMethod.GET,
                new HttpEntity<>(headers),
                new ParameterizedTypeReference<>() {
                }
        );
        assertNotNull(response);
        assertNotNull(response.getStatusCode());
        assertEquals(HttpStatus.OK, response.getStatusCode());

        SheetDto<ProjectRD> sheet = response.getBody();
        assertNotNull(sheet);
        assertNotNull(sheet.page());

        List<ProjectRD> projects = sheet.content();
        assertNotNull(projects);
        assertFalse(projects.isEmpty());
        assertTrue(projects.size() > 4);

        FilterChain filters = FilterChain
                .empty()
                .add(Filter.of("id", Operation.NOT_ENDS_WITH, "4"));

        ResponseEntity<SheetDto<ProjectRD>> response2 = rest.exchange(
                projectUrl + "/search",
                HttpMethod.POST,
                new HttpEntity<>(filters, headers),
                new ParameterizedTypeReference<>() {
                }
        );
        assertNotNull(response2);
        assertNotNull(response2.getStatusCode());
        assertEquals(HttpStatus.OK, response2.getStatusCode());

        SheetDto<ProjectRD> sheet2 = response2.getBody();
        assertNotNull(sheet2);
        assertNotNull(sheet2.page());

        List<ProjectRD> projects2 = sheet2.content();
        assertNotNull(projects2);
        assertFalse(projects2.isEmpty());

        for (ProjectRD projectRD : projects2) {
            assertNotNull(projectRD);
            assertNotNull(projectRD.id());
            assertFalse(projectRD.id().toString().endsWith("4"));
        }
    }

    @Test
    @Order(38)
    @DirtiesContext
    @DisplayName("Тестируем not ends with с Long (Integer)")
    public void test38() {

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(alexToken);

        ResponseEntity<SheetDto<ProjectRD>> response = rest.exchange(
                projectUrl,
                HttpMethod.GET,
                new HttpEntity<>(headers),
                new ParameterizedTypeReference<>() {
                }
        );
        assertNotNull(response);
        assertNotNull(response.getStatusCode());
        assertEquals(HttpStatus.OK, response.getStatusCode());

        SheetDto<ProjectRD> sheet = response.getBody();
        assertNotNull(sheet);
        assertNotNull(sheet.page());

        List<ProjectRD> projects = sheet.content();
        assertNotNull(projects);
        assertFalse(projects.isEmpty());
        assertTrue(projects.size() > 4);

        FilterChain filters = FilterChain
                .empty()
                .add(Filter.of("id", Operation.NOT_ENDS_WITH, 4));

        ResponseEntity<SheetDto<ProjectRD>> response2 = rest.exchange(
                projectUrl + "/search",
                HttpMethod.POST,
                new HttpEntity<>(filters, headers),
                new ParameterizedTypeReference<>() {
                }
        );
        assertNotNull(response2);
        assertNotNull(response2.getStatusCode());
        assertEquals(HttpStatus.OK, response2.getStatusCode());

        SheetDto<ProjectRD> sheet2 = response2.getBody();
        assertNotNull(sheet2);
        assertNotNull(sheet2.page());

        List<ProjectRD> projects2 = sheet2.content();
        assertNotNull(projects2);
        assertFalse(projects2.isEmpty());

        for (ProjectRD projectRD : projects2) {
            assertNotNull(projectRD);
            assertNotNull(projectRD.id());
            assertFalse(projectRD.id().toString().endsWith("4"));
        }
    }

    @Test
    @Order(39)
    @DirtiesContext
    @DisplayName("Тестируем in с Long (List<Long>)")
    public void test39() {

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(alexToken);

        ResponseEntity<SheetDto<ProjectRD>> response = rest.exchange(
                projectUrl,
                HttpMethod.GET,
                new HttpEntity<>(headers),
                new ParameterizedTypeReference<>() {
                }
        );
        assertNotNull(response);
        assertNotNull(response.getStatusCode());
        assertEquals(HttpStatus.OK, response.getStatusCode());

        SheetDto<ProjectRD> sheet = response.getBody();
        assertNotNull(sheet);
        assertNotNull(sheet.page());

        List<ProjectRD> projects = sheet.content();
        assertNotNull(projects);
        assertFalse(projects.isEmpty());
        assertTrue(projects.size() > 4);

        List<Long> list = new ArrayList<>();
        list.add(1L);
        list.add(2L);
        list.add(5L);

        FilterChain filters = FilterChain
                .empty()
                .add(Filter.of("id", Operation.IN, list));

        ResponseEntity<SheetDto<ProjectRD>> response2 = rest.exchange(
                projectUrl + "/search",
                HttpMethod.POST,
                new HttpEntity<>(filters, headers),
                new ParameterizedTypeReference<>() {
                }
        );
        assertNotNull(response2);
        assertNotNull(response2.getStatusCode());
        assertEquals(HttpStatus.OK, response2.getStatusCode());

        SheetDto<ProjectRD> sheet2 = response2.getBody();
        assertNotNull(sheet2);
        assertNotNull(sheet2.page());

        List<ProjectRD> projects2 = sheet2.content();
        assertNotNull(projects2);
        assertFalse(projects2.isEmpty());
        assertEquals(3, projects2.size());

        boolean flag1 = false;
        boolean flag2 = false;
        boolean flag5 = false;
        for (ProjectRD projectRD : projects2) {
            assertNotNull(projectRD);
            assertNotNull(projectRD.id());

            if (projectRD.id().equals(1L)) {
                flag1 = true;
            }

            if (projectRD.id().equals(2L)) {
                flag2 = true;
            }

            if (projectRD.id().equals(5L)) {
                flag5 = true;
            }
        }
        assertTrue(flag1);
        assertTrue(flag2);
        assertTrue(flag5);
    }

    @Test
    @Order(40)
    @DirtiesContext
    @DisplayName("Тестируем in с Long (List<String>)")
    public void test40() {

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(alexToken);

        ResponseEntity<SheetDto<ProjectRD>> response = rest.exchange(
                projectUrl,
                HttpMethod.GET,
                new HttpEntity<>(headers),
                new ParameterizedTypeReference<>() {
                }
        );
        assertNotNull(response);
        assertNotNull(response.getStatusCode());
        assertEquals(HttpStatus.OK, response.getStatusCode());

        SheetDto<ProjectRD> sheet = response.getBody();
        assertNotNull(sheet);
        assertNotNull(sheet.page());

        List<ProjectRD> projects = sheet.content();
        assertNotNull(projects);
        assertFalse(projects.isEmpty());
        assertTrue(projects.size() > 4);

        List<String> list = new ArrayList<>();
        list.add("1");
        list.add("2");
        list.add("5");

        FilterChain filters = FilterChain
                .empty()
                .add(Filter.of("id", Operation.IN, list));

        ResponseEntity<SheetDto<ProjectRD>> response2 = rest.exchange(
                projectUrl + "/search",
                HttpMethod.POST,
                new HttpEntity<>(filters, headers),
                new ParameterizedTypeReference<>() {
                }
        );
        assertNotNull(response2);
        assertNotNull(response2.getStatusCode());
        assertEquals(HttpStatus.OK, response2.getStatusCode());

        SheetDto<ProjectRD> sheet2 = response2.getBody();
        assertNotNull(sheet2);
        assertNotNull(sheet2.page());

        List<ProjectRD> projects2 = sheet2.content();
        assertNotNull(projects2);
        assertFalse(projects2.isEmpty());
        assertEquals(3, projects2.size());

        boolean flag1 = false;
        boolean flag2 = false;
        boolean flag5 = false;
        for (ProjectRD projectRD : projects2) {
            assertNotNull(projectRD);
            assertNotNull(projectRD.id());

            if (projectRD.id().equals(1L)) {
                flag1 = true;
            }

            if (projectRD.id().equals(2L)) {
                flag2 = true;
            }

            if (projectRD.id().equals(5L)) {
                flag5 = true;
            }
        }
        assertTrue(flag1);
        assertTrue(flag2);
        assertTrue(flag5);
    }

    @Test
    @Order(41)
    @DirtiesContext
    @DisplayName("Тестируем in с Long (List<Integer>)")
    public void test41() {

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(alexToken);

        ResponseEntity<SheetDto<ProjectRD>> response = rest.exchange(
                projectUrl,
                HttpMethod.GET,
                new HttpEntity<>(headers),
                new ParameterizedTypeReference<>() {
                }
        );
        assertNotNull(response);
        assertNotNull(response.getStatusCode());
        assertEquals(HttpStatus.OK, response.getStatusCode());

        SheetDto<ProjectRD> sheet = response.getBody();
        assertNotNull(sheet);
        assertNotNull(sheet.page());

        List<ProjectRD> projects = sheet.content();
        assertNotNull(projects);
        assertFalse(projects.isEmpty());
        assertTrue(projects.size() > 4);

        List<Integer> list = new ArrayList<>();
        list.add(1);
        list.add(2);
        list.add(5);

        FilterChain filters = FilterChain
                .empty()
                .add(Filter.of("id", Operation.IN, list));

        ResponseEntity<SheetDto<ProjectRD>> response2 = rest.exchange(
                projectUrl + "/search",
                HttpMethod.POST,
                new HttpEntity<>(filters, headers),
                new ParameterizedTypeReference<>() {
                }
        );
        assertNotNull(response2);
        assertNotNull(response2.getStatusCode());
        assertEquals(HttpStatus.OK, response2.getStatusCode());

        SheetDto<ProjectRD> sheet2 = response2.getBody();
        assertNotNull(sheet2);
        assertNotNull(sheet2.page());

        List<ProjectRD> projects2 = sheet2.content();
        assertNotNull(projects2);
        assertFalse(projects2.isEmpty());
        assertEquals(3, projects2.size());

        boolean flag1 = false;
        boolean flag2 = false;
        boolean flag5 = false;
        for (ProjectRD projectRD : projects2) {
            assertNotNull(projectRD);
            assertNotNull(projectRD.id());

            if (projectRD.id().equals(1L)) {
                flag1 = true;
            }

            if (projectRD.id().equals(2L)) {
                flag2 = true;
            }

            if (projectRD.id().equals(5L)) {
                flag5 = true;
            }
        }
        assertTrue(flag1);
        assertTrue(flag2);
        assertTrue(flag5);
    }

    @Test
    @Order(42)
    @DirtiesContext
    @DisplayName("Тестируем not in с Long (List<Long>)")
    public void test42() {

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(alexToken);

        ResponseEntity<SheetDto<ProjectRD>> response = rest.exchange(
                projectUrl,
                HttpMethod.GET,
                new HttpEntity<>(headers),
                new ParameterizedTypeReference<>() {
                }
        );
        assertNotNull(response);
        assertNotNull(response.getStatusCode());
        assertEquals(HttpStatus.OK, response.getStatusCode());

        SheetDto<ProjectRD> sheet = response.getBody();
        assertNotNull(sheet);
        assertNotNull(sheet.page());

        List<ProjectRD> projects = sheet.content();
        assertNotNull(projects);
        assertFalse(projects.isEmpty());
        assertTrue(projects.size() > 4);

        List<Long> list = new ArrayList<>();
        list.add(1L);
        list.add(2L);
        list.add(5L);

        FilterChain filters = FilterChain
                .empty()
                .add(Filter.of("id", Operation.NOT_IN, list));

        ResponseEntity<SheetDto<ProjectRD>> response2 = rest.exchange(
                projectUrl + "/search",
                HttpMethod.POST,
                new HttpEntity<>(filters, headers),
                new ParameterizedTypeReference<>() {
                }
        );
        assertNotNull(response2);
        assertNotNull(response2.getStatusCode());
        assertEquals(HttpStatus.OK, response2.getStatusCode());

        SheetDto<ProjectRD> sheet2 = response2.getBody();
        assertNotNull(sheet2);
        assertNotNull(sheet2.page());

        List<ProjectRD> projects2 = sheet2.content();
        assertNotNull(projects2);
        assertFalse(projects2.isEmpty());

        boolean flag1 = false;
        boolean flag2 = false;
        boolean flag5 = false;
        for (ProjectRD projectRD : projects2) {
            assertNotNull(projectRD);
            assertNotNull(projectRD.id());

            if (projectRD.id().equals(1L)) {
                flag1 = true;
            }

            if (projectRD.id().equals(2L)) {
                flag2 = true;
            }

            if (projectRD.id().equals(5L)) {
                flag5 = true;
            }
        }
        assertFalse(flag1);
        assertFalse(flag2);
        assertFalse(flag5);
    }

    @Test
    @Order(43)
    @DirtiesContext
    @DisplayName("Тестируем not in с Long (List<String>)")
    public void test43() {

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(alexToken);

        ResponseEntity<SheetDto<ProjectRD>> response = rest.exchange(
                projectUrl,
                HttpMethod.GET,
                new HttpEntity<>(headers),
                new ParameterizedTypeReference<>() {
                }
        );
        assertNotNull(response);
        assertNotNull(response.getStatusCode());
        assertEquals(HttpStatus.OK, response.getStatusCode());

        SheetDto<ProjectRD> sheet = response.getBody();
        assertNotNull(sheet);
        assertNotNull(sheet.page());

        List<ProjectRD> projects = sheet.content();
        assertNotNull(projects);
        assertFalse(projects.isEmpty());
        assertTrue(projects.size() > 4);

        List<String> list = new ArrayList<>();
        list.add("1");
        list.add("2");
        list.add("5");

        FilterChain filters = FilterChain
                .empty()
                .add(Filter.of("id", Operation.NOT_IN, list));

        ResponseEntity<SheetDto<ProjectRD>> response2 = rest.exchange(
                projectUrl + "/search",
                HttpMethod.POST,
                new HttpEntity<>(filters, headers),
                new ParameterizedTypeReference<>() {
                }
        );
        assertNotNull(response2);
        assertNotNull(response2.getStatusCode());
        assertEquals(HttpStatus.OK, response2.getStatusCode());

        SheetDto<ProjectRD> sheet2 = response2.getBody();
        assertNotNull(sheet2);
        assertNotNull(sheet2.page());

        List<ProjectRD> projects2 = sheet2.content();
        assertNotNull(projects2);
        assertFalse(projects2.isEmpty());

        boolean flag1 = false;
        boolean flag2 = false;
        boolean flag5 = false;
        for (ProjectRD projectRD : projects2) {
            assertNotNull(projectRD);
            assertNotNull(projectRD.id());

            if (projectRD.id().equals(1L)) {
                flag1 = true;
            }

            if (projectRD.id().equals(2L)) {
                flag2 = true;
            }

            if (projectRD.id().equals(5L)) {
                flag5 = true;
            }
        }
        assertFalse(flag1);
        assertFalse(flag2);
        assertFalse(flag5);
    }

    @Test
    @Order(44)
    @DirtiesContext
    @DisplayName("Тестируем not in с Long (List<Integer>)")
    public void test44() {

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(alexToken);

        ResponseEntity<SheetDto<ProjectRD>> response = rest.exchange(
                projectUrl,
                HttpMethod.GET,
                new HttpEntity<>(headers),
                new ParameterizedTypeReference<>() {
                }
        );
        assertNotNull(response);
        assertNotNull(response.getStatusCode());
        assertEquals(HttpStatus.OK, response.getStatusCode());

        SheetDto<ProjectRD> sheet = response.getBody();
        assertNotNull(sheet);
        assertNotNull(sheet.page());

        List<ProjectRD> projects = sheet.content();
        assertNotNull(projects);
        assertFalse(projects.isEmpty());
        assertTrue(projects.size() > 4);

        List<Integer> list = new ArrayList<>();
        list.add(1);
        list.add(2);
        list.add(5);

        FilterChain filters = FilterChain
                .empty()
                .add(Filter.of("id", Operation.NOT_IN, list));

        ResponseEntity<SheetDto<ProjectRD>> response2 = rest.exchange(
                projectUrl + "/search",
                HttpMethod.POST,
                new HttpEntity<>(filters, headers),
                new ParameterizedTypeReference<>() {
                }
        );
        assertNotNull(response2);
        assertNotNull(response2.getStatusCode());
        assertEquals(HttpStatus.OK, response2.getStatusCode());

        SheetDto<ProjectRD> sheet2 = response2.getBody();
        assertNotNull(sheet2);
        assertNotNull(sheet2.page());

        List<ProjectRD> projects2 = sheet2.content();
        assertNotNull(projects2);
        assertFalse(projects2.isEmpty());

        boolean flag1 = false;
        boolean flag2 = false;
        boolean flag5 = false;
        for (ProjectRD projectRD : projects2) {
            assertNotNull(projectRD);
            assertNotNull(projectRD.id());

            if (projectRD.id().equals(1L)) {
                flag1 = true;
            }

            if (projectRD.id().equals(2L)) {
                flag2 = true;
            }

            if (projectRD.id().equals(5L)) {
                flag5 = true;
            }
        }
        assertFalse(flag1);
        assertFalse(flag2);
        assertFalse(flag5);
    }

    @Test
    @Order(45)
    @DirtiesContext
    @DisplayName("Тестируем between с Long (Long, Long)")
    public void test45() {

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(alexToken);

        ResponseEntity<SheetDto<ProjectRD>> response = rest.exchange(
                projectUrl,
                HttpMethod.GET,
                new HttpEntity<>(headers),
                new ParameterizedTypeReference<>() {
                }
        );
        assertNotNull(response);
        assertNotNull(response.getStatusCode());
        assertEquals(HttpStatus.OK, response.getStatusCode());

        SheetDto<ProjectRD> sheet = response.getBody();
        assertNotNull(sheet);
        assertNotNull(sheet.page());

        List<ProjectRD> projects = sheet.content();
        assertNotNull(projects);
        assertFalse(projects.isEmpty());
        assertTrue(projects.size() > 4);

        FilterChain filters = FilterChain
                .empty()
                .add(Filter.of("id", Operation.BETWEEN, 2L, 4L));

        ResponseEntity<SheetDto<ProjectRD>> response2 = rest.exchange(
                projectUrl + "/search",
                HttpMethod.POST,
                new HttpEntity<>(filters, headers),
                new ParameterizedTypeReference<>() {
                }
        );
        assertNotNull(response2);
        assertNotNull(response2.getStatusCode());
        assertEquals(HttpStatus.OK, response2.getStatusCode());

        SheetDto<ProjectRD> sheet2 = response2.getBody();
        assertNotNull(sheet2);
        assertNotNull(sheet2.page());

        List<ProjectRD> projects2 = sheet2.content();
        assertNotNull(projects2);
        assertFalse(projects2.isEmpty());

        for (ProjectRD projectRD : projects2) {
            assertNotNull(projectRD);
            assertNotNull(projectRD.id());
            assertTrue(projectRD.id() >= 2);
            assertTrue(projectRD.id() <= 4);
        }
    }

    @Test
    @Order(46)
    @DirtiesContext
    @DisplayName("Тестируем between с Long (String, String)")
    public void test46() {

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(alexToken);

        ResponseEntity<SheetDto<ProjectRD>> response = rest.exchange(
                projectUrl,
                HttpMethod.GET,
                new HttpEntity<>(headers),
                new ParameterizedTypeReference<>() {
                }
        );
        assertNotNull(response);
        assertNotNull(response.getStatusCode());
        assertEquals(HttpStatus.OK, response.getStatusCode());

        SheetDto<ProjectRD> sheet = response.getBody();
        assertNotNull(sheet);
        assertNotNull(sheet.page());

        List<ProjectRD> projects = sheet.content();
        assertNotNull(projects);
        assertFalse(projects.isEmpty());
        assertTrue(projects.size() > 4);

        FilterChain filters = FilterChain
                .empty()
                .add(Filter.of("id", Operation.BETWEEN, "2", "4"));

        ResponseEntity<SheetDto<ProjectRD>> response2 = rest.exchange(
                projectUrl + "/search",
                HttpMethod.POST,
                new HttpEntity<>(filters, headers),
                new ParameterizedTypeReference<>() {
                }
        );
        assertNotNull(response2);
        assertNotNull(response2.getStatusCode());
        assertEquals(HttpStatus.OK, response2.getStatusCode());

        SheetDto<ProjectRD> sheet2 = response2.getBody();
        assertNotNull(sheet2);
        assertNotNull(sheet2.page());

        List<ProjectRD> projects2 = sheet2.content();
        assertNotNull(projects2);
        assertFalse(projects2.isEmpty());

        for (ProjectRD projectRD : projects2) {
            assertNotNull(projectRD);
            assertNotNull(projectRD.id());
            assertTrue(projectRD.id() >= 2);
            assertTrue(projectRD.id() <= 4);
        }
    }

    @Test
    @Order(47)
    @DirtiesContext
    @DisplayName("Тестируем between с Long (Integer, Integer)")
    public void test47() {

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(alexToken);

        ResponseEntity<SheetDto<ProjectRD>> response = rest.exchange(
                projectUrl,
                HttpMethod.GET,
                new HttpEntity<>(headers),
                new ParameterizedTypeReference<>() {
                }
        );
        assertNotNull(response);
        assertNotNull(response.getStatusCode());
        assertEquals(HttpStatus.OK, response.getStatusCode());

        SheetDto<ProjectRD> sheet = response.getBody();
        assertNotNull(sheet);
        assertNotNull(sheet.page());

        List<ProjectRD> projects = sheet.content();
        assertNotNull(projects);
        assertFalse(projects.isEmpty());
        assertTrue(projects.size() > 4);

        FilterChain filters = FilterChain
                .empty()
                .add(Filter.of("id", Operation.BETWEEN, 2, 4));

        ResponseEntity<SheetDto<ProjectRD>> response2 = rest.exchange(
                projectUrl + "/search",
                HttpMethod.POST,
                new HttpEntity<>(filters, headers),
                new ParameterizedTypeReference<>() {
                }
        );
        assertNotNull(response2);
        assertNotNull(response2.getStatusCode());
        assertEquals(HttpStatus.OK, response2.getStatusCode());

        SheetDto<ProjectRD> sheet2 = response2.getBody();
        assertNotNull(sheet2);
        assertNotNull(sheet2.page());

        List<ProjectRD> projects2 = sheet2.content();
        assertNotNull(projects2);
        assertFalse(projects2.isEmpty());

        for (ProjectRD projectRD : projects2) {
            assertNotNull(projectRD);
            assertNotNull(projectRD.id());
            assertTrue(projectRD.id() >= 2);
            assertTrue(projectRD.id() <= 4);
        }
    }

    @Test
    @Order(48)
    @DirtiesContext
    @DisplayName("Тестируем between с Long (Long, String)")
    public void test48() {

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(alexToken);

        ResponseEntity<SheetDto<ProjectRD>> response = rest.exchange(
                projectUrl,
                HttpMethod.GET,
                new HttpEntity<>(headers),
                new ParameterizedTypeReference<>() {
                }
        );
        assertNotNull(response);
        assertNotNull(response.getStatusCode());
        assertEquals(HttpStatus.OK, response.getStatusCode());

        SheetDto<ProjectRD> sheet = response.getBody();
        assertNotNull(sheet);
        assertNotNull(sheet.page());

        List<ProjectRD> projects = sheet.content();
        assertNotNull(projects);
        assertFalse(projects.isEmpty());
        assertTrue(projects.size() > 4);

        FilterChain filters = FilterChain
                .empty()
                .add(Filter.of("id", Operation.BETWEEN, 2L, "4"));

        ResponseEntity<SheetDto<ProjectRD>> response2 = rest.exchange(
                projectUrl + "/search",
                HttpMethod.POST,
                new HttpEntity<>(filters, headers),
                new ParameterizedTypeReference<>() {
                }
        );
        assertNotNull(response2);
        assertNotNull(response2.getStatusCode());
        assertEquals(HttpStatus.OK, response2.getStatusCode());

        SheetDto<ProjectRD> sheet2 = response2.getBody();
        assertNotNull(sheet2);
        assertNotNull(sheet2.page());

        List<ProjectRD> projects2 = sheet2.content();
        assertNotNull(projects2);
        assertFalse(projects2.isEmpty());

        for (ProjectRD projectRD : projects2) {
            assertNotNull(projectRD);
            assertNotNull(projectRD.id());
            assertTrue(projectRD.id() >= 2);
            assertTrue(projectRD.id() <= 4);
        }
    }

    @Test
    @Order(49)
    @DirtiesContext
    @DisplayName("Тестируем not between с Long (Long, Long)")
    public void test49() {

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(alexToken);

        ResponseEntity<SheetDto<ProjectRD>> response = rest.exchange(
                projectUrl,
                HttpMethod.GET,
                new HttpEntity<>(headers),
                new ParameterizedTypeReference<>() {
                }
        );
        assertNotNull(response);
        assertNotNull(response.getStatusCode());
        assertEquals(HttpStatus.OK, response.getStatusCode());

        SheetDto<ProjectRD> sheet = response.getBody();
        assertNotNull(sheet);
        assertNotNull(sheet.page());

        List<ProjectRD> projects = sheet.content();
        assertNotNull(projects);
        assertFalse(projects.isEmpty());
        assertTrue(projects.size() > 4);

        FilterChain filters = FilterChain
                .empty()
                .add(Filter.of("id", Operation.NOT_BETWEEN, 2L, 4L));

        ResponseEntity<SheetDto<ProjectRD>> response2 = rest.exchange(
                projectUrl + "/search",
                HttpMethod.POST,
                new HttpEntity<>(filters, headers),
                new ParameterizedTypeReference<>() {
                }
        );
        assertNotNull(response2);
        assertNotNull(response2.getStatusCode());
        assertEquals(HttpStatus.OK, response2.getStatusCode());

        SheetDto<ProjectRD> sheet2 = response2.getBody();
        assertNotNull(sheet2);
        assertNotNull(sheet2.page());

        List<ProjectRD> projects2 = sheet2.content();
        assertNotNull(projects2);
        assertFalse(projects2.isEmpty());

        for (ProjectRD projectRD : projects2) {
            assertNotNull(projectRD);
            assertNotNull(projectRD.id());
            assertTrue(projectRD.id() < 2 || projectRD.id() > 4);
        }
    }

    @Test
    @Order(50)
    @DirtiesContext
    @DisplayName("Тестируем not between с Long (String, String)")
    public void test50() {

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(alexToken);

        ResponseEntity<SheetDto<ProjectRD>> response = rest.exchange(
                projectUrl,
                HttpMethod.GET,
                new HttpEntity<>(headers),
                new ParameterizedTypeReference<>() {
                }
        );
        assertNotNull(response);
        assertNotNull(response.getStatusCode());
        assertEquals(HttpStatus.OK, response.getStatusCode());

        SheetDto<ProjectRD> sheet = response.getBody();
        assertNotNull(sheet);
        assertNotNull(sheet.page());

        List<ProjectRD> projects = sheet.content();
        assertNotNull(projects);
        assertFalse(projects.isEmpty());
        assertTrue(projects.size() > 4);

        FilterChain filters = FilterChain
                .empty()
                .add(Filter.of("id", Operation.NOT_BETWEEN, "2", "4"));

        ResponseEntity<SheetDto<ProjectRD>> response2 = rest.exchange(
                projectUrl + "/search",
                HttpMethod.POST,
                new HttpEntity<>(filters, headers),
                new ParameterizedTypeReference<>() {
                }
        );
        assertNotNull(response2);
        assertNotNull(response2.getStatusCode());
        assertEquals(HttpStatus.OK, response2.getStatusCode());

        SheetDto<ProjectRD> sheet2 = response2.getBody();
        assertNotNull(sheet2);
        assertNotNull(sheet2.page());

        List<ProjectRD> projects2 = sheet2.content();
        assertNotNull(projects2);
        assertFalse(projects2.isEmpty());

        for (ProjectRD projectRD : projects2) {
            assertNotNull(projectRD);
            assertNotNull(projectRD.id());
            assertTrue(projectRD.id() < 2 || projectRD.id() > 4);
        }
    }

    @Test
    @Order(51)
    @DirtiesContext
    @DisplayName("Тестируем not between с Long (String, Long)")
    public void test51() {

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(alexToken);

        ResponseEntity<SheetDto<ProjectRD>> response = rest.exchange(
                projectUrl,
                HttpMethod.GET,
                new HttpEntity<>(headers),
                new ParameterizedTypeReference<>() {
                }
        );
        assertNotNull(response);
        assertNotNull(response.getStatusCode());
        assertEquals(HttpStatus.OK, response.getStatusCode());

        SheetDto<ProjectRD> sheet = response.getBody();
        assertNotNull(sheet);
        assertNotNull(sheet.page());

        List<ProjectRD> projects = sheet.content();
        assertNotNull(projects);
        assertFalse(projects.isEmpty());
        assertTrue(projects.size() > 4);

        FilterChain filters = FilterChain
                .empty()
                .add(Filter.of("id", Operation.NOT_BETWEEN, "2", 4L));

        ResponseEntity<SheetDto<ProjectRD>> response2 = rest.exchange(
                projectUrl + "/search",
                HttpMethod.POST,
                new HttpEntity<>(filters, headers),
                new ParameterizedTypeReference<>() {
                }
        );
        assertNotNull(response2);
        assertNotNull(response2.getStatusCode());
        assertEquals(HttpStatus.OK, response2.getStatusCode());

        SheetDto<ProjectRD> sheet2 = response2.getBody();
        assertNotNull(sheet2);
        assertNotNull(sheet2.page());

        List<ProjectRD> projects2 = sheet2.content();
        assertNotNull(projects2);
        assertFalse(projects2.isEmpty());

        for (ProjectRD projectRD : projects2) {
            assertNotNull(projectRD);
            assertNotNull(projectRD.id());
            assertTrue(projectRD.id() < 2 || projectRD.id() > 4);
        }
    }

    @Test
    @Order(52)
    @DirtiesContext
    @DisplayName("Тестируем not between с Long (Integer, Integer)")
    public void test52() {

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(alexToken);

        ResponseEntity<SheetDto<ProjectRD>> response = rest.exchange(
                projectUrl,
                HttpMethod.GET,
                new HttpEntity<>(headers),
                new ParameterizedTypeReference<>() {
                }
        );
        assertNotNull(response);
        assertNotNull(response.getStatusCode());
        assertEquals(HttpStatus.OK, response.getStatusCode());

        SheetDto<ProjectRD> sheet = response.getBody();
        assertNotNull(sheet);
        assertNotNull(sheet.page());

        List<ProjectRD> projects = sheet.content();
        assertNotNull(projects);
        assertFalse(projects.isEmpty());
        assertTrue(projects.size() > 4);

        FilterChain filters = FilterChain
                .empty()
                .add(Filter.of("id", Operation.NOT_BETWEEN, 2, 4));

        ResponseEntity<SheetDto<ProjectRD>> response2 = rest.exchange(
                projectUrl + "/search",
                HttpMethod.POST,
                new HttpEntity<>(filters, headers),
                new ParameterizedTypeReference<>() {
                }
        );
        assertNotNull(response2);
        assertNotNull(response2.getStatusCode());
        assertEquals(HttpStatus.OK, response2.getStatusCode());

        SheetDto<ProjectRD> sheet2 = response2.getBody();
        assertNotNull(sheet2);
        assertNotNull(sheet2.page());

        List<ProjectRD> projects2 = sheet2.content();
        assertNotNull(projects2);
        assertFalse(projects2.isEmpty());

        for (ProjectRD projectRD : projects2) {
            assertNotNull(projectRD);
            assertNotNull(projectRD.id());
            assertTrue(projectRD.id() < 2 || projectRD.id() > 4);
        }
    }

    @Test
    @Order(53)
    @DirtiesContext
    @DisplayName("Тестируем is true с Long")
    public void test53() {

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(alexToken);

        FilterChain filters = FilterChain
                .empty()
                .add(Filter.of("id", Operation.IS_TRUE));

        try {
            rest.exchange(
                    projectUrl + "/search",
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
            System.out.println(errorResponse);
            assertNotNull(errorResponse);
            assertNotNull(errorResponse.path());
            assertEquals(projectUrl + "/search", errorResponse.path());
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
    @Order(54)
    @DirtiesContext
    @DisplayName("Тестируем is false с Long")
    public void test54() {

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(alexToken);

        FilterChain filters = FilterChain
                .empty()
                .add(Filter.of("id", Operation.IS_FALSE));

        try {
            rest.exchange(
                    projectUrl + "/search",
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
            System.out.println(errorResponse);
            assertNotNull(errorResponse);
            assertNotNull(errorResponse.path());
            assertEquals(projectUrl + "/search", errorResponse.path());
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
    @Order(55)
    @DirtiesContext
    @DisplayName("Тестируем is empty с Long")
    public void test55() {

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(alexToken);

        FilterChain filters = FilterChain
                .empty()
                .add(Filter.of("id", Operation.IS_EMPTY));

        try {
            rest.exchange(
                    projectUrl + "/search",
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
            System.out.println(errorResponse);
            assertNotNull(errorResponse);
            assertNotNull(errorResponse.path());
            assertEquals(projectUrl + "/search", errorResponse.path());
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
    @Order(56)
    @DirtiesContext
    @DisplayName("Тестируем is not empty с Long")
    public void test56() {

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(alexToken);

        FilterChain filters = FilterChain
                .empty()
                .add(Filter.of("id", Operation.IS_NOT_EMPTY));

        try {
            rest.exchange(
                    projectUrl + "/search",
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
            System.out.println(errorResponse);
            assertNotNull(errorResponse);
            assertNotNull(errorResponse.path());
            assertEquals(projectUrl + "/search", errorResponse.path());
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
    @Order(57)
    @DirtiesContext
    @DisplayName("Тестируем before с Long")
    public void test57() {

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(alexToken);

        FilterChain filters = FilterChain
                .empty()
                .add(Filter.of("id", Operation.BEFORE, 3));

        try {
            rest.exchange(
                    projectUrl + "/search",
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
            System.out.println(errorResponse);
            assertNotNull(errorResponse);
            assertNotNull(errorResponse.path());
            assertEquals(projectUrl + "/search", errorResponse.path());
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
    @Order(58)
    @DirtiesContext
    @DisplayName("Тестируем after с Long")
    public void test58() {

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(alexToken);

        FilterChain filters = FilterChain
                .empty()
                .add(Filter.of("id", Operation.AFTER, 3));

        try {
            rest.exchange(
                    projectUrl + "/search",
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
            System.out.println(errorResponse);
            assertNotNull(errorResponse);
            assertNotNull(errorResponse.path());
            assertEquals(projectUrl + "/search", errorResponse.path());
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
}
