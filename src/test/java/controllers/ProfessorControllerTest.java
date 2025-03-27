package controllers;

import entities.Professor;
import exceptions.ValidationException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import services.ProfessorService;
import utils.MimeTypes;

import java.io.*;
import java.util.ArrayList;
import java.util.Date;

class ProfessorControllerTest {
    @BeforeEach
    void setUp() throws IOException {
        response = Mockito.mock(HttpServletResponse.class);
        responseStringWriter = new StringWriter();
        responseWriter = new PrintWriter(responseStringWriter);
        Mockito.doReturn(responseWriter).when(response).getWriter();
        Mockito.doAnswer(invocation -> {
            responseContentType = invocation.getArgument(0);
            return null;
        }).when(response).setContentType(Mockito.anyString());
        Mockito.doAnswer(invocation -> {
            responseStatus = invocation.getArgument(0);
            return null;
        }).when(response).setStatus(Mockito.anyInt());
    }

    @Test
    void testGetAll() throws IOException {
        ProfessorService service;
        ProfessorController controller;
        HttpServletRequest request;
        ArrayList<Professor> professors;
        Professor professor;
        String responseJson;
        Date birthdate;

        professors = new ArrayList<>(2);
        professor = new Professor();
        professor.setId(100);
        professor.setName("Ivan");
        professor.setPhoneNumber("+79998884334");
        professor.setDegree("PhD in Computer Science");
        birthdate = new Date();
        birthdate.setTime(0);
        professor.setBirthday(birthdate);
        professor.setDepartmentId(15);
        professors.add(professor);

        professor = new Professor();
        professor.setId(200);
        professor.setName("Petr");
        professor.setPhoneNumber("+79824863265");
        professor.setDegree("PhD in Technical Science");
        birthdate = new Date();
        birthdate.setTime(169344000);
        professor.setBirthday(birthdate);
        professor.setDepartmentId(24);
        professors.add(professor);

        service = Mockito.mock(ProfessorService.class);
        Mockito.doReturn(professors).when(service).get();

        request = Mockito.mock(HttpServletRequest.class);
        Mockito.doReturn(null).when(request).getPathInfo();

        controller = Mockito.spy(ProfessorController.class);
        Mockito.doReturn(service).when(controller).createProfessorService();
        controller.doGet(request, response);
        responseJson = responseStringWriter.toString();

        Assertions.assertEquals(MimeTypes.APPLICATION_JSON, responseContentType);
        Assertions.assertEquals("[{\"id\":100,\"department_id\":15,\"name\":\"Ivan\",\"phone_number\":\"+79998884334\",\"degree\":\"PhD in Computer Science\",\"birthday\":\"1970-01-01\"}"
                + ",{\"id\":200,\"department_id\":24,\"name\":\"Petr\",\"phone_number\":\"+79824863265\",\"degree\":\"PhD in Technical Science\",\"birthday\":\"1970-01-03\"}]", responseJson);
    }

    @Test
    void testGetOne() throws IOException {
        ProfessorService service;
        ProfessorController controller;
        HttpServletRequest request;
        Professor professor;
        String responseJson;
        Date birthdate;

        professor = new Professor();
        professor.setId(200);
        professor.setName("Petr");
        professor.setPhoneNumber("+79824863265");
        professor.setDegree("PhD in Technical Science");
        birthdate = new Date();
        birthdate.setTime(169344000);
        professor.setBirthday(birthdate);
        professor.setDepartmentId(24);

        service = Mockito.mock(ProfessorService.class);
        Mockito.doReturn(professor).when(service).getById(professor.getId());

        request = Mockito.mock(HttpServletRequest.class);
        Mockito.doReturn("/200").when(request).getPathInfo();

        controller = Mockito.spy(ProfessorController.class);
        Mockito.doReturn(service).when(controller).createProfessorService();
        controller.doGet(request, response);
        responseJson = responseStringWriter.toString();

        Assertions.assertEquals(MimeTypes.APPLICATION_JSON, responseContentType);
        Assertions.assertEquals("{\"id\":200,\"department_id\":24,\"name\":\"Petr\",\"phone_number\":\"+79824863265\",\"degree\":\"PhD in Technical Science\",\"birthday\":\"1970-01-03\"}", responseJson);
    }

    @Test
    void testGetBadRequest() throws IOException {
        ProfessorService service;
        ProfessorController controller;
        HttpServletRequest request;

        service = Mockito.mock(ProfessorService.class);

        request = Mockito.mock(HttpServletRequest.class);
        Mockito.doReturn("/1a").when(request).getPathInfo();

        controller = Mockito.spy(ProfessorController.class);
        Mockito.doReturn(service).when(controller).createProfessorService();
        controller.doGet(request, response);

        Assertions.assertEquals(HttpServletResponse.SC_BAD_REQUEST, responseStatus);
    }

    @Test
    void testGetNotFound() throws IOException {
        ProfessorService service;
        ProfessorController controller;
        HttpServletRequest request;

        service = Mockito.mock(ProfessorService.class);
        Mockito.doReturn(null).when(service).getById(Mockito.anyInt());

        request = Mockito.mock(HttpServletRequest.class);
        Mockito.doReturn("/1").when(request).getPathInfo();

        controller = Mockito.spy(ProfessorController.class);
        Mockito.doReturn(service).when(controller).createProfessorService();
        controller.doGet(request, response);

        Assertions.assertEquals(HttpServletResponse.SC_NOT_FOUND, responseStatus);
    }

    @Test
    void testCreate() throws IOException {
        ProfessorService service;
        ProfessorController controller;
        HttpServletRequest request;
        String requestJson;
        StringReader requestStringReader;
        BufferedReader requestReader;
        String responseJson;

        service = Mockito.mock(ProfessorService.class);
        Mockito.doAnswer(invocation -> {
            Professor arg;

            arg = invocation.getArgument(0);
            arg.setId(200);
            return null;
        }).when(service).add(Mockito.any(Professor.class));

        requestJson = "{\"department_id\":24,\"name\":\"Petr\",\"phone_number\":\"+79824863265\",\"degree\":\"PhD in Technical Science\",\"birthday\":\"1970-01-03\"}";
        requestStringReader = new StringReader(requestJson);
        requestReader = new BufferedReader(requestStringReader);
        request = Mockito.mock(HttpServletRequest.class);
        Mockito.doReturn(null).when(request).getPathInfo();
        Mockito.doReturn(requestReader).when(request).getReader();

        controller = Mockito.spy(ProfessorController.class);
        Mockito.doReturn(service).when(controller).createProfessorService();
        controller.doPost(request, response);
        responseJson = responseStringWriter.toString();

        Assertions.assertEquals(HttpServletResponse.SC_CREATED, responseStatus);
        Assertions.assertEquals(MimeTypes.APPLICATION_JSON, responseContentType);
        Assertions.assertEquals("{\"id\":200,\"department_id\":24,\"name\":\"Petr\",\"phone_number\":\"+79824863265\",\"degree\":\"PhD in Technical Science\",\"birthday\":\"1970-01-03\"}", responseJson);
    }

    @Test
    void testCreateBadJson() throws IOException {
        ProfessorService service;
        ProfessorController controller;
        HttpServletRequest request;
        String requestJson;
        StringReader requestStringReader;
        BufferedReader requestReader;
        String responseText;

        service = Mockito.mock(ProfessorService.class);

        requestJson = "{\"department_id\":24,\"name\":\"Petr\",\"phone_number\":\"+79824863265\",\"degree\":\"PhD in Technical Science\",\"birthday\":\"1970-01-03\"";
        requestStringReader = new StringReader(requestJson);
        requestReader = new BufferedReader(requestStringReader);
        request = Mockito.mock(HttpServletRequest.class);
        Mockito.doReturn(null).when(request).getPathInfo();
        Mockito.doReturn(requestReader).when(request).getReader();

        controller = Mockito.spy(ProfessorController.class);
        Mockito.doReturn(service).when(controller).createProfessorService();
        controller.doPost(request, response);
        responseText = responseStringWriter.toString();

        Assertions.assertEquals(HttpServletResponse.SC_BAD_REQUEST, responseStatus);
        Assertions.assertEquals(MimeTypes.TEXT_PLAIN, responseContentType);
        Assertions.assertEquals("Invalid JSON", responseText);
    }

    @Test
    void testCreateBadRequest() throws IOException {
        ProfessorService service;
        ProfessorController controller;
        HttpServletRequest request;

        service = Mockito.mock(ProfessorService.class);

        request = Mockito.mock(HttpServletRequest.class);
        Mockito.doReturn("/aaa").when(request).getPathInfo();

        controller = Mockito.spy(ProfessorController.class);
        Mockito.doReturn(service).when(controller).createProfessorService();
        controller.doPost(request, response);

        Assertions.assertEquals(HttpServletResponse.SC_BAD_REQUEST, responseStatus);
    }

    @Test
    void testCreateInvalidProfessor() throws IOException {
        ProfessorService service;
        ProfessorController controller;
        HttpServletRequest request;
        String requestJson;
        StringReader requestStringReader;
        BufferedReader requestReader;
        String responseText;

        service = Mockito.mock(ProfessorService.class);
        Mockito.doThrow(new ValidationException("Test error")).when(service).add(Mockito.any(Professor.class));

        requestJson = "{}";
        requestStringReader = new StringReader(requestJson);
        requestReader = new BufferedReader(requestStringReader);
        request = Mockito.mock(HttpServletRequest.class);
        Mockito.doReturn(null).when(request).getPathInfo();
        Mockito.doReturn(requestReader).when(request).getReader();

        controller = Mockito.spy(ProfessorController.class);
        Mockito.doReturn(service).when(controller).createProfessorService();
        controller.doPost(request, response);
        responseText = responseStringWriter.toString();

        Assertions.assertEquals(HttpServletResponse.SC_BAD_REQUEST, responseStatus);
        Assertions.assertEquals(MimeTypes.TEXT_PLAIN, responseContentType);
        Assertions.assertEquals("Test error", responseText);
    }

    @Test
    void testUpdate() throws IOException {
        ProfessorService service;
        ProfessorController controller;
        Professor professor;
        HttpServletRequest request;
        String requestJson;
        StringReader requestStringReader;
        BufferedReader requestReader;
        String responseJson;

        professor = new Professor();
        professor.setId(200);

        service = Mockito.mock(ProfessorService.class);
        Mockito.doReturn(professor).when(service).getById(professor.getId());
        Mockito.doReturn(true).when(service).update(Mockito.any(Professor.class));

        requestJson = "{\"department_id\":30,\"name\":\"Petr\",\"phone_number\":\"+79824863265\",\"degree\":\"PhD in Technical Science\",\"birthday\":\"1970-01-03\"}";
        requestStringReader = new StringReader(requestJson);
        requestReader = new BufferedReader(requestStringReader);
        request = Mockito.mock(HttpServletRequest.class);
        Mockito.doReturn("/200").when(request).getPathInfo();
        Mockito.doReturn(requestReader).when(request).getReader();

        controller = Mockito.spy(ProfessorController.class);
        Mockito.doReturn(service).when(controller).createProfessorService();
        controller.doPatch(request, response);
        responseJson = responseStringWriter.toString();

        Assertions.assertEquals(MimeTypes.APPLICATION_JSON, responseContentType);
        Assertions.assertEquals("{\"id\":200,\"department_id\":30,\"name\":\"Petr\",\"phone_number\":\"+79824863265\",\"degree\":\"PhD in Technical Science\",\"birthday\":\"1970-01-03\"}", responseJson);
    }

    @Test
    void testUpdateBadJson() throws IOException {
        ProfessorService service;
        ProfessorController controller;
        HttpServletRequest request;
        String requestJson;
        StringReader requestStringReader;
        BufferedReader requestReader;
        String responseText;

        service = Mockito.mock(ProfessorService.class);

        requestJson = "{\"department_id\":30,\"name\":\"Petr\",\"phone_number\":\"+79824863265\",\"degree\":\"PhD in Technical Science\",\"birthday\":\"1970-01-03\"";
        requestStringReader = new StringReader(requestJson);
        requestReader = new BufferedReader(requestStringReader);
        request = Mockito.mock(HttpServletRequest.class);
        Mockito.doReturn("/200").when(request).getPathInfo();
        Mockito.doReturn(requestReader).when(request).getReader();

        controller = Mockito.spy(ProfessorController.class);
        Mockito.doReturn(service).when(controller).createProfessorService();
        controller.doPatch(request, response);
        responseText = responseStringWriter.toString();

        Assertions.assertEquals(HttpServletResponse.SC_BAD_REQUEST, responseStatus);
        Assertions.assertEquals(MimeTypes.TEXT_PLAIN, responseContentType);
        Assertions.assertEquals("Invalid JSON", responseText);
    }

    @Test
    void testUpdateBadRequest() throws IOException {
        ProfessorService service;
        ProfessorController controller;
        HttpServletRequest request;
        Object[] pathInfos;

        service = Mockito.mock(ProfessorService.class);

        request = Mockito.mock(HttpServletRequest.class);

        controller = Mockito.spy(ProfessorController.class);
        Mockito.doReturn(service).when(controller).createProfessorService();

        pathInfos = new Object[2];
        pathInfos[0] = "/aaa";
        for (Object pathInfo : pathInfos) {
            Mockito.doReturn(pathInfo).when(request).getPathInfo();
            controller.doPatch(request, response);
            Assertions.assertEquals(HttpServletResponse.SC_BAD_REQUEST, responseStatus);
        }
    }

    @Test
    void testUpdateInvalidProfessor() throws IOException {
        ProfessorService service;
        ProfessorController controller;
        HttpServletRequest request;
        String requestJson;
        StringReader requestStringReader;
        BufferedReader requestReader;
        String responseText;

        service = Mockito.mock(ProfessorService.class);
        Mockito.doReturn(new Professor()).when(service).getById(200);
        Mockito.doThrow(new ValidationException("Test error")).when(service).update(Mockito.any(Professor.class));

        requestJson = "{}";
        requestStringReader = new StringReader(requestJson);
        requestReader = new BufferedReader(requestStringReader);
        request = Mockito.mock(HttpServletRequest.class);
        Mockito.doReturn("/200").when(request).getPathInfo();
        Mockito.doReturn(requestReader).when(request).getReader();

        controller = Mockito.spy(ProfessorController.class);
        Mockito.doReturn(service).when(controller).createProfessorService();
        controller.doPatch(request, response);
        responseText = responseStringWriter.toString();

        Assertions.assertEquals(HttpServletResponse.SC_BAD_REQUEST, responseStatus);
        Assertions.assertEquals(MimeTypes.TEXT_PLAIN, responseContentType);
        Assertions.assertEquals("Test error", responseText);
    }

    @Test
    void testUpdateNotFound() throws IOException {
        ProfessorService service;
        ProfessorController controller;
        HttpServletRequest request;
        String requestJson;
        StringReader requestStringReader;
        BufferedReader requestReader;

        service = Mockito.mock(ProfessorService.class);
        Mockito.doReturn(null).when(service).getById(200);

        requestJson = "{}";
        requestStringReader = new StringReader(requestJson);
        requestReader = new BufferedReader(requestStringReader);
        request = Mockito.mock(HttpServletRequest.class);
        Mockito.doReturn("/200").when(request).getPathInfo();
        Mockito.doReturn(requestReader).when(request).getReader();

        controller = Mockito.spy(ProfessorController.class);
        Mockito.doReturn(service).when(controller).createProfessorService();
        controller.doPatch(request, response);

        Assertions.assertEquals(HttpServletResponse.SC_NOT_FOUND, responseStatus);
    }

    @Test
    void testDelete() {
        ProfessorService service;
        ProfessorController controller;
        HttpServletRequest request;

        service = Mockito.mock(ProfessorService.class);
        Mockito.doReturn(true).when(service).delete(200);

        request = Mockito.mock(HttpServletRequest.class);
        Mockito.doReturn("/200").when(request).getPathInfo();

        controller = Mockito.spy(ProfessorController.class);
        Mockito.doReturn(service).when(controller).createProfessorService();
        controller.doDelete(request, response);

        Assertions.assertTrue(responseStatus < 400);
    }

    @Test
    void testDeleteBadRequest() {
        ProfessorService service;
        ProfessorController controller;
        HttpServletRequest request;
        Object[] pathInfos;

        service = Mockito.mock(ProfessorService.class);

        request = Mockito.mock(HttpServletRequest.class);

        controller = Mockito.spy(ProfessorController.class);
        Mockito.doReturn(service).when(controller).createProfessorService();

        pathInfos = new Object[2];
        pathInfos[0] = "/aaa";
        for (Object pathInfo : pathInfos) {
            Mockito.doReturn(pathInfo).when(request).getPathInfo();
            controller.doDelete(request, response);
            Assertions.assertEquals(HttpServletResponse.SC_BAD_REQUEST, responseStatus);
        }
    }

    @Test
    void testDeleteNotFound() {
        ProfessorService service;
        ProfessorController controller;
        HttpServletRequest request;

        service = Mockito.mock(ProfessorService.class);
        Mockito.doReturn(null).when(service).getById(200);

        request = Mockito.mock(HttpServletRequest.class);
        Mockito.doReturn("/200").when(request).getPathInfo();

        controller = Mockito.spy(ProfessorController.class);
        Mockito.doReturn(service).when(controller).createProfessorService();
        controller.doDelete(request, response);

        Assertions.assertEquals(HttpServletResponse.SC_NOT_FOUND, responseStatus);
    }

    HttpServletResponse response;
    PrintWriter responseWriter;
    StringWriter responseStringWriter;
    String responseContentType;
    int responseStatus;
}
