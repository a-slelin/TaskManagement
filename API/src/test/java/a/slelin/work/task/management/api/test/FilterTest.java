package a.slelin.work.task.management.api.test;

import a.slelin.work.task.management.core.dto.SheetDto;
import a.slelin.work.task.management.core.dto.api.ProjectRD;
import a.slelin.work.task.management.core.dto.api.ProjectWD;
import a.slelin.work.task.management.core.dto.api.TaskRD;
import a.slelin.work.task.management.core.dto.api.TaskWD;
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

    @LocalServerPort
    private int port;

    private String projectUrl;

    @BeforeEach
    void beforeEach() {
        String baseUrl = "http://localhost:%d".formatted(port);
        projectUrl = baseUrl + "/api/projects";
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

    @Test
    @Order(59)
    @DirtiesContext
    @DisplayName("Тестируем equals с String")
    public void test59() {

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
                .add(Filter.of("name", Operation.EQ, newProject.name()));

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
    @Order(60)
    @DirtiesContext
    @DisplayName("Тестируем not equals с String")
    public void test60() {

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
                .add(Filter.of("name", Operation.NEQ, newProject.name()));

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

        for (ProjectRD projectRD : projects) {
            assertNotNull(projectRD);
            assertNotNull(projectRD.id());
            assertNotNull(projectRD.name());
            assertNotEquals(projectRD.name(), newProject.name());
        }
    }

    @Test
    @Order(61)
    @DirtiesContext
    @DisplayName("Тестируем is null с String")
    public void test61() {

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
                .add(Filter.of("description", Operation.IS_NULL));

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

        for (ProjectRD projectRD : projects) {
            assertNotNull(projectRD);
            assertNull(projectRD.description());
        }
    }

    @Test
    @Order(62)
    @DirtiesContext
    @DisplayName("Тестируем is not null с String")
    public void test62() {

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
                .add(Filter.of("description", Operation.IS_NOT_NULL));

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

        for (ProjectRD projectRD : projects) {
            assertNotNull(projectRD);
            assertNotNull(projectRD.description());
            assertNotNull(projectRD.name());
            assertNotEquals(projectRD.name(), newProject.name());
        }
    }

    @Test
    @Order(63)
    @DirtiesContext
    @DisplayName("Тестируем greater с String")
    public void test63() {

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(alexToken);

        FilterChain filters = FilterChain
                .empty()
                .add(Filter.of("name", Operation.GT, 4));

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
    @Order(64)
    @DirtiesContext
    @DisplayName("Тестируем greater or equals с String")
    public void test64() {

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(alexToken);

        FilterChain filters = FilterChain
                .empty()
                .add(Filter.of("name", Operation.GE, 3));

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
    @Order(65)
    @DirtiesContext
    @DisplayName("Тестируем less с String")
    public void test65() {

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(alexToken);

        FilterChain filters = FilterChain
                .empty()
                .add(Filter.of("name", Operation.LT, 3));

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
    @Order(66)
    @DirtiesContext
    @DisplayName("Тестируем less or equals с String")
    public void test66() {

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(alexToken);

        FilterChain filters = FilterChain
                .empty()
                .add(Filter.of("name", Operation.LE, 3));

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
    @Order(67)
    @DirtiesContext
    @DisplayName("Тестируем like с String")
    public void test67() {

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
                .add(Filter.of("name", Operation.LIKE, "pro"));

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

        for (ProjectRD projectRD : projects) {
            assertNotNull(projectRD);
            assertNotNull(projectRD.name());
            assertTrue(projectRD.name().contains("pro"));
        }
    }

    @Test
    @Order(68)
    @DirtiesContext
    @DisplayName("Тестируем not like с String")
    public void test68() {

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
                .add(Filter.of("name", Operation.NOT_LIKE, "pro"));

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

        for (ProjectRD projectRD : projects) {
            assertNotNull(projectRD);
            assertNotNull(projectRD.name());
            assertFalse(projectRD.name().contains("pro"));
        }
    }

    @Test
    @Order(69)
    @DirtiesContext
    @DisplayName("Тестируем starts with с String")
    public void test69() {

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
                .add(Filter.of("name", Operation.STARTS_WITH, "tmp_"));

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

        for (ProjectRD projectRD : projects) {
            assertNotNull(projectRD);
            assertNotNull(projectRD.name());
            assertTrue(projectRD.name().startsWith("tmp_"));
        }
    }

    @Test
    @Order(70)
    @DirtiesContext
    @DisplayName("Тестируем not starts with с String")
    public void test70() {

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
                .add(Filter.of("name", Operation.NOT_STARTS_WITH, "tmp_"));

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

        for (ProjectRD projectRD : projects) {
            assertNotNull(projectRD);
            assertNotNull(projectRD.name());
            assertFalse(projectRD.name().startsWith("tmp_"));
        }
    }

    @Test
    @Order(71)
    @DirtiesContext
    @DisplayName("Тестируем ends with с String")
    public void test71() {

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
                .add(Filter.of("name", Operation.ENDS_WITH, "ect"));

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

        for (ProjectRD projectRD : projects) {
            assertNotNull(projectRD);
            assertNotNull(projectRD.name());
            assertTrue(projectRD.name().endsWith("ect"));
        }
    }

    @Test
    @Order(72)
    @DirtiesContext
    @DisplayName("Тестируем not ends with с String")
    public void test72() {

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
                .add(Filter.of("name", Operation.NOT_ENDS_WITH, "ect"));

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

        for (ProjectRD projectRD : projects) {
            assertNotNull(projectRD);
            assertNotNull(projectRD.name());
            assertFalse(projectRD.name().endsWith("ect"));
        }
    }

    @Test
    @Order(73)
    @DirtiesContext
    @DisplayName("Тестируем is empty с String")
    public void test73() {

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(alexToken);

        FilterChain filters = FilterChain
                .empty()
                .add(Filter.of("name", Operation.IS_EMPTY));

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
    @Order(74)
    @DirtiesContext
    @DisplayName("Тестируем is not empty с String")
    public void test74() {

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(alexToken);

        FilterChain filters = FilterChain
                .empty()
                .add(Filter.of("name", Operation.IS_NOT_EMPTY));

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
    @Order(75)
    @DirtiesContext
    @DisplayName("Тестируем is true с String")
    public void test75() {

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(alexToken);

        FilterChain filters = FilterChain
                .empty()
                .add(Filter.of("name", Operation.IS_TRUE));

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
    @Order(76)
    @DirtiesContext
    @DisplayName("Тестируем is false с String")
    public void test76() {

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(alexToken);

        FilterChain filters = FilterChain
                .empty()
                .add(Filter.of("name", Operation.IS_FALSE));

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
    @Order(77)
    @DirtiesContext
    @DisplayName("Тестируем in с String")
    public void test77() {

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

        List<String> list = new ArrayList<>();
        list.add("tmp_project");
        list.add("Интернет-магазин \"Электроника\"");

        FilterChain filters = FilterChain
                .empty()
                .add(Filter.of("name", Operation.IN, list));

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
        assertEquals(2, projects.size());

        for (ProjectRD projectRD : projects) {
            assertNotNull(projectRD);
            assertNotNull(projectRD.name());
            assertTrue(projectRD.name().equals("tmp_project")
                    || projectRD.name().equals("Интернет-магазин \"Электроника\""));
        }
    }

    @Test
    @Order(78)
    @DirtiesContext
    @DisplayName("Тестируем not in с String")
    public void test78() {

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

        List<String> list = new ArrayList<>();
        list.add("tmp_project");
        list.add("Интернет-магазин \"Электроника\"");

        FilterChain filters = FilterChain
                .empty()
                .add(Filter.of("name", Operation.NOT_IN, list));

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

        for (ProjectRD projectRD : projects) {
            assertNotNull(projectRD);
            assertNotNull(projectRD.name());
            assertTrue(!projectRD.name().equals("tmp_project")
                    && !projectRD.name().equals("Интернет-магазин \"Электроника\""));
        }
    }

    @Test
    @Order(79)
    @DirtiesContext
    @DisplayName("Тестируем between с String")
    public void test79() {

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(alexToken);

        FilterChain filters = FilterChain
                .empty()
                .add(Filter.of("name", Operation.BETWEEN, 3, 4));

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
    @Order(80)
    @DirtiesContext
    @DisplayName("Тестируем not between с String")
    public void test80() {

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(alexToken);

        FilterChain filters = FilterChain
                .empty()
                .add(Filter.of("name", Operation.NOT_BETWEEN, 3, 4));

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
    @Order(81)
    @DirtiesContext
    @DisplayName("Тестируем before с String")
    public void test81() {

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(alexToken);

        FilterChain filters = FilterChain
                .empty()
                .add(Filter.of("name", Operation.BEFORE, 3));

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
    @Order(82)
    @DirtiesContext
    @DisplayName("Тестируем after с String")
    public void test82() {

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(alexToken);

        FilterChain filters = FilterChain
                .empty()
                .add(Filter.of("name", Operation.AFTER, 3));

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
    @Order(83)
    @DirtiesContext
    @DisplayName("Тестируем equals с Collection")
    public void test83() {

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(alexToken);

        FilterChain filters = FilterChain
                .empty()
                .add(Filter.of("tasks", Operation.EQ, 3));

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
    @Order(84)
    @DirtiesContext
    @DisplayName("Тестируем not equals с Collection")
    public void test84() {

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(alexToken);

        FilterChain filters = FilterChain
                .empty()
                .add(Filter.of("tasks", Operation.NEQ, 3));

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
    @Order(85)
    @DirtiesContext
    @DisplayName("Тестируем greater с Collection")
    public void test85() {

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(alexToken);

        FilterChain filters = FilterChain
                .empty()
                .add(Filter.of("tasks", Operation.GT, 3));

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
    @Order(86)
    @DirtiesContext
    @DisplayName("Тестируем greater or equals с Collection")
    public void test86() {

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(alexToken);

        FilterChain filters = FilterChain
                .empty()
                .add(Filter.of("tasks", Operation.GE, 3));

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
    @Order(87)
    @DirtiesContext
    @DisplayName("Тестируем less с Collection")
    public void test87() {

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(alexToken);

        FilterChain filters = FilterChain
                .empty()
                .add(Filter.of("tasks", Operation.LT, 3));

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
    @Order(88)
    @DirtiesContext
    @DisplayName("Тестируем less or equals с Collection")
    public void test88() {

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(alexToken);

        FilterChain filters = FilterChain
                .empty()
                .add(Filter.of("tasks", Operation.LE, 3));

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
    @Order(89)
    @DirtiesContext
    @DisplayName("Тестируем like с Collection")
    public void test89() {

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(alexToken);

        FilterChain filters = FilterChain
                .empty()
                .add(Filter.of("tasks", Operation.LIKE, "3"));

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
    @Order(90)
    @DirtiesContext
    @DisplayName("Тестируем not like с Collection")
    public void test90() {

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(alexToken);

        FilterChain filters = FilterChain
                .empty()
                .add(Filter.of("tasks", Operation.NOT_LIKE, "3"));

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
    @Order(91)
    @DirtiesContext
    @DisplayName("Тестируем starts with с Collection")
    public void test91() {

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(alexToken);

        FilterChain filters = FilterChain
                .empty()
                .add(Filter.of("tasks", Operation.STARTS_WITH, "3"));

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
    @Order(92)
    @DirtiesContext
    @DisplayName("Тестируем not starts with с Collection")
    public void test92() {

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(alexToken);

        FilterChain filters = FilterChain
                .empty()
                .add(Filter.of("tasks", Operation.NOT_STARTS_WITH, "3"));

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
    @Order(93)
    @DirtiesContext
    @DisplayName("Тестируем ends with с Collection")
    public void test93() {

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(alexToken);

        FilterChain filters = FilterChain
                .empty()
                .add(Filter.of("tasks", Operation.ENDS_WITH, "3"));

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
    @Order(94)
    @DirtiesContext
    @DisplayName("Тестируем not ends with с Collection")
    public void test94() {

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(alexToken);

        FilterChain filters = FilterChain
                .empty()
                .add(Filter.of("tasks", Operation.NOT_ENDS_WITH, "3"));

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
    @Order(95)
    @DirtiesContext
    @DisplayName("Тестируем is true с Collection")
    public void test95() {

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(alexToken);

        FilterChain filters = FilterChain
                .empty()
                .add(Filter.of("tasks", Operation.IS_TRUE));

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
    @Order(96)
    @DirtiesContext
    @DisplayName("Тестируем is false с Collection")
    public void test96() {

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(alexToken);

        FilterChain filters = FilterChain
                .empty()
                .add(Filter.of("tasks", Operation.IS_FALSE));

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
    @Order(97)
    @DirtiesContext
    @DisplayName("Тестируем in с Collection")
    public void test97() {

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(alexToken);

        List<String> list = new ArrayList<>();
        list.add("dkljf");

        FilterChain filters = FilterChain
                .empty()
                .add(Filter.of("tasks", Operation.IN, list));

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
    @Order(98)
    @DirtiesContext
    @DisplayName("Тестируем not in с Collection")
    public void test98() {

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(alexToken);

        List<String> list = new ArrayList<>();
        list.add("dkljf");

        FilterChain filters = FilterChain
                .empty()
                .add(Filter.of("tasks", Operation.NOT_IN, list));

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
    @Order(99)
    @DirtiesContext
    @DisplayName("Тестируем between с Collection")
    public void test99() {

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(alexToken);

        FilterChain filters = FilterChain
                .empty()
                .add(Filter.of("tasks", Operation.BETWEEN, 1, 2));

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
    @Order(100)
    @DirtiesContext
    @DisplayName("Тестируем not between с Collection")
    public void test100() {

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(alexToken);

        FilterChain filters = FilterChain
                .empty()
                .add(Filter.of("tasks", Operation.NOT_BETWEEN, 1, 2));

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
    @Order(102)
    @DirtiesContext
    @DisplayName("Тестируем before с Collection")
    public void test102() {

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(alexToken);

        FilterChain filters = FilterChain
                .empty()
                .add(Filter.of("tasks", Operation.BEFORE, 1));

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
    @Order(103)
    @DirtiesContext
    @DisplayName("Тестируем after с Collection")
    public void test103() {

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(alexToken);

        FilterChain filters = FilterChain
                .empty()
                .add(Filter.of("tasks", Operation.AFTER, 1));

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
    @Order(104)
    @DirtiesContext
    @DisplayName("Тестируем is null с Collection")
    public void test104() {

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(alexToken);

        FilterChain filters = FilterChain
                .empty()
                .add(Filter.of("tasks", Operation.IS_NULL));

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
    @Order(105)
    @DirtiesContext
    @DisplayName("Тестируем is not null с Collection")
    public void test105() {

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(alexToken);

        FilterChain filters = FilterChain
                .empty()
                .add(Filter.of("tasks", Operation.IS_NOT_NULL));

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
    @Order(106)
    @DirtiesContext
    @DisplayName("Тестируем is empty с Collection")
    public void test106() {

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

        FilterChain filters = FilterChain
                .empty()
                .add(Filter.of("tasks", Operation.IS_EMPTY));

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
        assertTrue(projects.contains(project));
    }

    @Test
    @Order(107)
    @DirtiesContext
    @DisplayName("Тестируем is not empty с Collection")
    public void test107() {

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
        Long projectId = project.id();
        assertNotNull(projectId);

        TaskWD newTask = new TaskWD("tmp_task", "begin");

        ResponseEntity<TaskRD> response2 = rest.exchange(
                projectUrl + "/{id}/tasks",
                HttpMethod.POST,
                new HttpEntity<>(newTask, headers),
                TaskRD.class,
                projectId
        );
        assertNotNull(response2);
        assertNotNull(response2.getStatusCode());
        assertEquals(HttpStatus.CREATED, response2.getStatusCode());

        TaskRD task = response2.getBody();
        assertNotNull(task);

        FilterChain filters = FilterChain
                .empty()
                .add(Filter.of("tasks", Operation.IS_NOT_EMPTY));

        ResponseEntity<SheetDto<ProjectRD>> response3 = rest.exchange(
                projectUrl + "/search",
                HttpMethod.POST,
                new HttpEntity<>(filters, headers),
                new ParameterizedTypeReference<>() {
                }
        );
        assertNotNull(response3);
        assertNotNull(response3.getStatusCode());
        assertEquals(HttpStatus.OK, response3.getStatusCode());

        SheetDto<ProjectRD> sheet = response3.getBody();
        assertNotNull(sheet);
        assertNotNull(sheet.page());

        List<ProjectRD> projects = sheet.content();
        assertNotNull(projects);
        assertFalse(projects.isEmpty());
        assertTrue(projects.contains(project));
    }
}
