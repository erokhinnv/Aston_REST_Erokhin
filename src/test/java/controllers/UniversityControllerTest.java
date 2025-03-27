package controllers;

import entities.University;
import exceptions.ValidationException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import services.UniversityService;
import utils.MimeTypes;

import java.io.*;
import java.util.ArrayList;

class UniversityControllerTest {
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
        UniversityService service;
        UniversityController controller;
        HttpServletRequest request;
        ArrayList<University> universities;
        University university;
        String responseJson;

        universities = new ArrayList<>(2);
        university = new University();
        university.setId(1);
        university.setName("PSTU");
        university.setCity("PERM");
        universities.add(university);

        university = new University();
        university.setId(2);
        university.setName("SPBSU");
        university.setCity("SPB");
        universities.add(university);

        service = Mockito.mock(UniversityService.class);
        Mockito.doReturn(universities).when(service).get();

        request = Mockito.mock(HttpServletRequest.class);
        Mockito.doReturn(null).when(request).getPathInfo();

        controller = Mockito.spy(UniversityController.class);
        Mockito.doReturn(service).when(controller).createUniversityService();
        controller.doGet(request, response);
        responseJson = responseStringWriter.toString();

        Assertions.assertEquals(MimeTypes.APPLICATION_JSON, responseContentType);
        Assertions.assertEquals("[{\"id\":1,\"name\":\"PSTU\",\"city\":\"PERM\"},{\"id\":2,\"name\":\"SPBSU\",\"city\":\"SPB\"}]", responseJson);
    }

    @Test
    void testGetOne() throws IOException {
        UniversityService service;
        UniversityController controller;
        HttpServletRequest request;
        University university;
        String responseJson;

        university = new University();
        university.setId(1);
        university.setName("PSTU");
        university.setCity("PERM");

        service = Mockito.mock(UniversityService.class);
        Mockito.doReturn(university).when(service).getById(university.getId());

        request = Mockito.mock(HttpServletRequest.class);
        Mockito.doReturn("/1").when(request).getPathInfo();

        controller = Mockito.spy(UniversityController.class);
        Mockito.doReturn(service).when(controller).createUniversityService();
        controller.doGet(request, response);
        responseJson = responseStringWriter.toString();

        Assertions.assertEquals(MimeTypes.APPLICATION_JSON, responseContentType);
        Assertions.assertEquals("{\"id\":1,\"name\":\"PSTU\",\"city\":\"PERM\"}", responseJson);
    }

    @Test
    void testGetBadRequest() throws IOException {
        UniversityService service;
        UniversityController controller;
        HttpServletRequest request;

        service = Mockito.mock(UniversityService.class);

        request = Mockito.mock(HttpServletRequest.class);
        Mockito.doReturn("/1a").when(request).getPathInfo();

        controller = Mockito.spy(UniversityController.class);
        Mockito.doReturn(service).when(controller).createUniversityService();
        controller.doGet(request, response);

        Assertions.assertEquals(HttpServletResponse.SC_BAD_REQUEST, responseStatus);
    }

    @Test
    void testGetNotFound() throws IOException {
        UniversityService service;
        UniversityController controller;
        HttpServletRequest request;

        service = Mockito.mock(UniversityService.class);
        Mockito.doReturn(null).when(service).getById(Mockito.anyInt());

        request = Mockito.mock(HttpServletRequest.class);
        Mockito.doReturn("/1").when(request).getPathInfo();

        controller = Mockito.spy(UniversityController.class);
        Mockito.doReturn(service).when(controller).createUniversityService();
        controller.doGet(request, response);

        Assertions.assertEquals(HttpServletResponse.SC_NOT_FOUND, responseStatus);
    }

    @Test
    void testCreate() throws IOException {
        UniversityService service;
        UniversityController controller;
        HttpServletRequest request;
        String requestJson;
        StringReader requestStringReader;
        BufferedReader requestReader;
        String responseJson;

        service = Mockito.mock(UniversityService.class);
        Mockito.doAnswer(invocation -> {
            University arg;

            arg = invocation.getArgument(0);
            arg.setId(10);
            return null;
        }).when(service).add(Mockito.any(University.class));

        requestJson = "{\"name\":\"PSTU\",\"city\":\"PERM\"}";
        requestStringReader = new StringReader(requestJson);
        requestReader = new BufferedReader(requestStringReader);
        request = Mockito.mock(HttpServletRequest.class);
        Mockito.doReturn(null).when(request).getPathInfo();
        Mockito.doReturn(requestReader).when(request).getReader();

        controller = Mockito.spy(UniversityController.class);
        Mockito.doReturn(service).when(controller).createUniversityService();
        controller.doPost(request, response);
        responseJson = responseStringWriter.toString();

        Assertions.assertEquals(HttpServletResponse.SC_CREATED, responseStatus);
        Assertions.assertEquals(MimeTypes.APPLICATION_JSON, responseContentType);
        Assertions.assertEquals("{\"id\":10,\"name\":\"PSTU\",\"city\":\"PERM\"}", responseJson);
    }

    @Test
    void testCreateBadJson() throws IOException {
        UniversityService service;
        UniversityController controller;
        HttpServletRequest request;
        String requestJson;
        StringReader requestStringReader;
        BufferedReader requestReader;
        String responseText;

        service = Mockito.mock(UniversityService.class);

        requestJson = "{\"name\":\"PSTU\",\"city\":\"PERM\"";
        requestStringReader = new StringReader(requestJson);
        requestReader = new BufferedReader(requestStringReader);
        request = Mockito.mock(HttpServletRequest.class);
        Mockito.doReturn(null).when(request).getPathInfo();
        Mockito.doReturn(requestReader).when(request).getReader();

        controller = Mockito.spy(UniversityController.class);
        Mockito.doReturn(service).when(controller).createUniversityService();
        controller.doPost(request, response);
        responseText = responseStringWriter.toString();

        Assertions.assertEquals(HttpServletResponse.SC_BAD_REQUEST, responseStatus);
        Assertions.assertEquals(MimeTypes.TEXT_PLAIN, responseContentType);
        Assertions.assertEquals("Invalid JSON", responseText);
    }

    @Test
    void testCreateBadRequest() throws IOException {
        UniversityService service;
        UniversityController controller;
        HttpServletRequest request;

        service = Mockito.mock(UniversityService.class);

        request = Mockito.mock(HttpServletRequest.class);
        Mockito.doReturn("/aaa").when(request).getPathInfo();

        controller = Mockito.spy(UniversityController.class);
        Mockito.doReturn(service).when(controller).createUniversityService();
        controller.doPost(request, response);

        Assertions.assertEquals(HttpServletResponse.SC_BAD_REQUEST, responseStatus);
    }

    @Test
    void testCreateInvalidUniversity() throws IOException {
        UniversityService service;
        UniversityController controller;
        HttpServletRequest request;
        String requestJson;
        StringReader requestStringReader;
        BufferedReader requestReader;
        String responseText;

        service = Mockito.mock(UniversityService.class);
        Mockito.doThrow(new ValidationException("Test error")).when(service).add(Mockito.any(University.class));

        requestJson = "{}";
        requestStringReader = new StringReader(requestJson);
        requestReader = new BufferedReader(requestStringReader);
        request = Mockito.mock(HttpServletRequest.class);
        Mockito.doReturn(null).when(request).getPathInfo();
        Mockito.doReturn(requestReader).when(request).getReader();

        controller = Mockito.spy(UniversityController.class);
        Mockito.doReturn(service).when(controller).createUniversityService();
        controller.doPost(request, response);
        responseText = responseStringWriter.toString();

        Assertions.assertEquals(HttpServletResponse.SC_BAD_REQUEST, responseStatus);
        Assertions.assertEquals(MimeTypes.TEXT_PLAIN, responseContentType);
        Assertions.assertEquals("Test error", responseText);
    }

    @Test
    void testUpdate() throws IOException {
        UniversityService service;
        UniversityController controller;
        University university;
        HttpServletRequest request;
        String requestJson;
        StringReader requestStringReader;
        BufferedReader requestReader;
        String responseJson;

        university = new University();
        university.setId(12);

        service = Mockito.mock(UniversityService.class);
        Mockito.doReturn(university).when(service).getById(university.getId());
        Mockito.doReturn(true).when(service).update(Mockito.any(University.class));

        requestJson = "{\"name\":\"SPBSU\",\"city\":\"SPB\"}";
        requestStringReader = new StringReader(requestJson);
        requestReader = new BufferedReader(requestStringReader);
        request = Mockito.mock(HttpServletRequest.class);
        Mockito.doReturn("/12").when(request).getPathInfo();
        Mockito.doReturn(requestReader).when(request).getReader();

        controller = Mockito.spy(UniversityController.class);
        Mockito.doReturn(service).when(controller).createUniversityService();
        controller.doPatch(request, response);
        responseJson = responseStringWriter.toString();

        Assertions.assertEquals(MimeTypes.APPLICATION_JSON, responseContentType);
        Assertions.assertEquals("{\"id\":12,\"name\":\"SPBSU\",\"city\":\"SPB\"}", responseJson);
    }

    @Test
    void testUpdateBadJson() throws IOException {
        UniversityService service;
        UniversityController controller;
        HttpServletRequest request;
        String requestJson;
        StringReader requestStringReader;
        BufferedReader requestReader;
        String responseText;

        service = Mockito.mock(UniversityService.class);

        requestJson = "{\"name\":\"SPBSU\",\"city\":\"SPB\"";
        requestStringReader = new StringReader(requestJson);
        requestReader = new BufferedReader(requestStringReader);
        request = Mockito.mock(HttpServletRequest.class);
        Mockito.doReturn("/12").when(request).getPathInfo();
        Mockito.doReturn(requestReader).when(request).getReader();

        controller = Mockito.spy(UniversityController.class);
        Mockito.doReturn(service).when(controller).createUniversityService();
        controller.doPatch(request, response);
        responseText = responseStringWriter.toString();

        Assertions.assertEquals(HttpServletResponse.SC_BAD_REQUEST, responseStatus);
        Assertions.assertEquals(MimeTypes.TEXT_PLAIN, responseContentType);
        Assertions.assertEquals("Invalid JSON", responseText);
    }

    @Test
    void testUpdateBadRequest() throws IOException {
        UniversityService service;
        UniversityController controller;
        HttpServletRequest request;
        Object[] pathInfos;

        service = Mockito.mock(UniversityService.class);

        request = Mockito.mock(HttpServletRequest.class);

        controller = Mockito.spy(UniversityController.class);
        Mockito.doReturn(service).when(controller).createUniversityService();

        pathInfos = new Object[2];
        pathInfos[0] = "/aaa";
        for (Object pathInfo : pathInfos) {
            Mockito.doReturn(pathInfo).when(request).getPathInfo();
            controller.doPatch(request, response);
            Assertions.assertEquals(HttpServletResponse.SC_BAD_REQUEST, responseStatus);
        }
    }

    @Test
    void testUpdateInvalidUniversity() throws IOException {
        UniversityService service;
        UniversityController controller;
        HttpServletRequest request;
        String requestJson;
        StringReader requestStringReader;
        BufferedReader requestReader;
        String responseText;

        service = Mockito.mock(UniversityService.class);
        Mockito.doReturn(new University()).when(service).getById(12);
        Mockito.doThrow(new ValidationException("Test error")).when(service).update(Mockito.any(University.class));

        requestJson = "{}";
        requestStringReader = new StringReader(requestJson);
        requestReader = new BufferedReader(requestStringReader);
        request = Mockito.mock(HttpServletRequest.class);
        Mockito.doReturn("/12").when(request).getPathInfo();
        Mockito.doReturn(requestReader).when(request).getReader();

        controller = Mockito.spy(UniversityController.class);
        Mockito.doReturn(service).when(controller).createUniversityService();
        controller.doPatch(request, response);
        responseText = responseStringWriter.toString();

        Assertions.assertEquals(HttpServletResponse.SC_BAD_REQUEST, responseStatus);
        Assertions.assertEquals(MimeTypes.TEXT_PLAIN, responseContentType);
        Assertions.assertEquals("Test error", responseText);
    }

    @Test
    void testUpdateNotFound() throws IOException {
        UniversityService service;
        UniversityController controller;
        HttpServletRequest request;
        String requestJson;
        StringReader requestStringReader;
        BufferedReader requestReader;

        service = Mockito.mock(UniversityService.class);
        Mockito.doReturn(null).when(service).getById(12);

        requestJson = "{}";
        requestStringReader = new StringReader(requestJson);
        requestReader = new BufferedReader(requestStringReader);
        request = Mockito.mock(HttpServletRequest.class);
        Mockito.doReturn("/12").when(request).getPathInfo();
        Mockito.doReturn(requestReader).when(request).getReader();

        controller = Mockito.spy(UniversityController.class);
        Mockito.doReturn(service).when(controller).createUniversityService();
        controller.doPatch(request, response);

        Assertions.assertEquals(HttpServletResponse.SC_NOT_FOUND, responseStatus);
    }

    @Test
    void testDelete() {
        UniversityService service;
        UniversityController controller;
        HttpServletRequest request;

        service = Mockito.mock(UniversityService.class);
        Mockito.doReturn(true).when(service).delete(12);

        request = Mockito.mock(HttpServletRequest.class);
        Mockito.doReturn("/12").when(request).getPathInfo();

        controller = Mockito.spy(UniversityController.class);
        Mockito.doReturn(service).when(controller).createUniversityService();
        controller.doDelete(request, response);

        Assertions.assertTrue(responseStatus < 400);
    }

    @Test
    void testDeleteBadRequest() {
        UniversityService service;
        UniversityController controller;
        HttpServletRequest request;
        Object[] pathInfos;

        service = Mockito.mock(UniversityService.class);

        request = Mockito.mock(HttpServletRequest.class);

        controller = Mockito.spy(UniversityController.class);
        Mockito.doReturn(service).when(controller).createUniversityService();

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
        UniversityService service;
        UniversityController controller;
        HttpServletRequest request;

        service = Mockito.mock(UniversityService.class);
        Mockito.doReturn(null).when(service).getById(12);

        request = Mockito.mock(HttpServletRequest.class);
        Mockito.doReturn("/12").when(request).getPathInfo();

        controller = Mockito.spy(UniversityController.class);
        Mockito.doReturn(service).when(controller).createUniversityService();
        controller.doDelete(request, response);

        Assertions.assertEquals(HttpServletResponse.SC_NOT_FOUND, responseStatus);
    }

    HttpServletResponse response;
    PrintWriter responseWriter;
    StringWriter responseStringWriter;
    String responseContentType;
    int responseStatus;
}
