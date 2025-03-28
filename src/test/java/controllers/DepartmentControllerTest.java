package controllers;

import entities.Department;
import entities.DepartmentFull;
import entities.University;
import exceptions.ValidationException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import services.DepartmentService;
import utils.MimeTypes;

import java.io.*;
import java.util.ArrayList;

class DepartmentControllerTest {
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
        DepartmentService service;
        DepartmentController controller;
        HttpServletRequest request;
        ArrayList<Department> departments;
        Department department;
        University university, secondUniversity;
        String responseJson;

        departments = new ArrayList<>(2);
        university = new University();
        university.setId(1);
        university.setName("PSTU");
        university.setCity("Perm");
        department = new Department();
        department.setId(1);
        department.setName("MEHMAT");
        department.setUniversity(university);
        departments.add(department);

        secondUniversity = new University();
        secondUniversity.setId(5);
        secondUniversity.setName("ITMO");
        secondUniversity.setCity("Saint-Petersburg");
        department = new Department();
        department.setId(2);
        department.setName("ITAP");
        department.setUniversity(secondUniversity);
        departments.add(department);

        service = Mockito.mock(DepartmentService.class);
        Mockito.doReturn(departments).when(service).get();

        request = Mockito.mock(HttpServletRequest.class);
        Mockito.doReturn(null).when(request).getPathInfo();

        controller = Mockito.spy(DepartmentController.class);
        Mockito.doReturn(service).when(controller).createDepartmentService();
        controller.doGet(request, response);
        responseJson = responseStringWriter.toString();

        Assertions.assertEquals(MimeTypes.APPLICATION_JSON, responseContentType);
        Assertions.assertEquals("[{\"id\":1,\"university\":{\"id\":1,\"name\":\"PSTU\",\"city\":\"Perm\"},\"name\":\"MEHMAT\"},{\"id\":2,\"university\":{\"id\":5,\"name\":\"ITMO\",\"city\":\"Saint-Petersburg\"},\"name\":\"ITAP\"}]", responseJson);
    }

    @Test
    void testGetOne() throws IOException {
        DepartmentService service;
        DepartmentController controller;
        HttpServletRequest request;
        DepartmentFull department;
        University university;
        String responseJson;

        university = new University();
        university.setId(1);
        university.setName("PSTU");
        university.setCity("Perm");
        department = new DepartmentFull();
        department.setId(1);
        department.setName("MEHMAT");
        department.setUniversity(university);

        service = Mockito.mock(DepartmentService.class);
        Mockito.doReturn(department).when(service).getById(department.getId());

        request = Mockito.mock(HttpServletRequest.class);
        Mockito.doReturn("/1").when(request).getPathInfo();

        controller = Mockito.spy(DepartmentController.class);
        Mockito.doReturn(service).when(controller).createDepartmentService();
        controller.doGet(request, response);
        responseJson = responseStringWriter.toString();

        Assertions.assertEquals(MimeTypes.APPLICATION_JSON, responseContentType);
        Assertions.assertEquals("{\"id\":1,\"university\":{\"id\":1,\"name\":\"PSTU\",\"city\":\"Perm\"},\"name\":\"MEHMAT\"}", responseJson);
    }

    @Test
    void testGetBadRequest() throws IOException {
        DepartmentService service;
        DepartmentController controller;
        HttpServletRequest request;

        service = Mockito.mock(DepartmentService.class);

        request = Mockito.mock(HttpServletRequest.class);
        Mockito.doReturn("/1a").when(request).getPathInfo();

        controller = Mockito.spy(DepartmentController.class);
        Mockito.doReturn(service).when(controller).createDepartmentService();
        controller.doGet(request, response);

        Assertions.assertEquals(HttpServletResponse.SC_BAD_REQUEST, responseStatus);
    }

    @Test
    void testGetNotFound() throws IOException {
        DepartmentService service;
        DepartmentController controller;
        HttpServletRequest request;

        service = Mockito.mock(DepartmentService.class);
        Mockito.doReturn(null).when(service).getById(Mockito.anyInt());

        request = Mockito.mock(HttpServletRequest.class);
        Mockito.doReturn("/1").when(request).getPathInfo();

        controller = Mockito.spy(DepartmentController.class);
        Mockito.doReturn(service).when(controller).createDepartmentService();
        controller.doGet(request, response);

        Assertions.assertEquals(HttpServletResponse.SC_NOT_FOUND, responseStatus);
    }

    @Test
    void testCreate() throws IOException {
        DepartmentService service;
        DepartmentController controller;
        HttpServletRequest request;
        String requestJson;
        StringReader requestStringReader;
        BufferedReader requestReader;
        String responseJson;

        service = Mockito.mock(DepartmentService.class);
        Mockito.doAnswer(invocation -> {
            Department arg;
            DepartmentFull departmentFull;

            departmentFull = new DepartmentFull();
            arg = invocation.getArgument(0);
            arg.setId(24);
            departmentFull.setId(arg.getId());
            departmentFull.setName(arg.getName());
            departmentFull.setUniversity(arg.getUniversity());
            return departmentFull;
        }).when(service).add(Mockito.any(Department.class));

        requestJson = "{\"university_id\":1,\"name\":\"MEHMAT\"}";
        requestStringReader = new StringReader(requestJson);
        requestReader = new BufferedReader(requestStringReader);
        request = Mockito.mock(HttpServletRequest.class);
        Mockito.doReturn(null).when(request).getPathInfo();
        Mockito.doReturn(requestReader).when(request).getReader();

        controller = Mockito.spy(DepartmentController.class);
        Mockito.doReturn(service).when(controller).createDepartmentService();
        controller.doPost(request, response);
        responseJson = responseStringWriter.toString();

        Assertions.assertEquals(HttpServletResponse.SC_CREATED, responseStatus);
        Assertions.assertEquals(MimeTypes.APPLICATION_JSON, responseContentType);
        Assertions.assertEquals("{\"id\":24,\"university\":{\"id\":1},\"name\":\"MEHMAT\"}", responseJson);
    }

    @Test
    void testCreateBadJson() throws IOException {
        DepartmentService service;
        DepartmentController controller;
        HttpServletRequest request;
        String requestJson;
        StringReader requestStringReader;
        BufferedReader requestReader;
        String responseText;

        service = Mockito.mock(DepartmentService.class);

        requestJson = "{\"university_id\":1,\"name\":\"MEHMAT\"";
        requestStringReader = new StringReader(requestJson);
        requestReader = new BufferedReader(requestStringReader);
        request = Mockito.mock(HttpServletRequest.class);
        Mockito.doReturn(null).when(request).getPathInfo();
        Mockito.doReturn(requestReader).when(request).getReader();

        controller = Mockito.spy(DepartmentController.class);
        Mockito.doReturn(service).when(controller).createDepartmentService();
        controller.doPost(request, response);
        responseText = responseStringWriter.toString();

        Assertions.assertEquals(HttpServletResponse.SC_BAD_REQUEST, responseStatus);
        Assertions.assertEquals(MimeTypes.TEXT_PLAIN, responseContentType);
        Assertions.assertEquals("Invalid JSON", responseText);
    }

    @Test
    void testCreateBadRequest() throws IOException {
        DepartmentService service;
        DepartmentController controller;
        HttpServletRequest request;

        service = Mockito.mock(DepartmentService.class);

        request = Mockito.mock(HttpServletRequest.class);
        Mockito.doReturn("/aaa").when(request).getPathInfo();

        controller = Mockito.spy(DepartmentController.class);
        Mockito.doReturn(service).when(controller).createDepartmentService();
        controller.doPost(request, response);

        Assertions.assertEquals(HttpServletResponse.SC_BAD_REQUEST, responseStatus);
    }

    @Test
    void testCreateInvalidDepartment() throws IOException {
        DepartmentService service;
        DepartmentController controller;
        HttpServletRequest request;
        String requestJson;
        StringReader requestStringReader;
        BufferedReader requestReader;
        String responseText;

        service = Mockito.mock(DepartmentService.class);
        Mockito.doThrow(new ValidationException("Test error")).when(service).add(Mockito.any(Department.class));

        requestJson = "{}";
        requestStringReader = new StringReader(requestJson);
        requestReader = new BufferedReader(requestStringReader);
        request = Mockito.mock(HttpServletRequest.class);
        Mockito.doReturn(null).when(request).getPathInfo();
        Mockito.doReturn(requestReader).when(request).getReader();

        controller = Mockito.spy(DepartmentController.class);
        Mockito.doReturn(service).when(controller).createDepartmentService();
        controller.doPost(request, response);
        responseText = responseStringWriter.toString();

        Assertions.assertEquals(HttpServletResponse.SC_BAD_REQUEST, responseStatus);
        Assertions.assertEquals(MimeTypes.TEXT_PLAIN, responseContentType);
        Assertions.assertEquals("Test error", responseText);
    }

    @Test
    void testUpdate() throws IOException {
        DepartmentService service;
        DepartmentController controller;
        DepartmentFull department;
        HttpServletRequest request;
        String requestJson;
        StringReader requestStringReader;
        BufferedReader requestReader;
        String responseJson;

        department = new DepartmentFull();
        department.setId(24);

        service = Mockito.mock(DepartmentService.class);
        Mockito.doReturn(department).when(service).getById(department.getId());
        Mockito.doReturn(true).when(service).update(Mockito.any(Department.class));

        requestJson = "{\"university_id\":1,\"name\":\"MEHMAT\"}";
        requestStringReader = new StringReader(requestJson);
        requestReader = new BufferedReader(requestStringReader);
        request = Mockito.mock(HttpServletRequest.class);
        Mockito.doReturn("/24").when(request).getPathInfo();
        Mockito.doReturn(requestReader).when(request).getReader();

        controller = Mockito.spy(DepartmentController.class);
        Mockito.doReturn(service).when(controller).createDepartmentService();
        controller.doPatch(request, response);
        responseJson = responseStringWriter.toString();

        Assertions.assertEquals(MimeTypes.APPLICATION_JSON, responseContentType);
        Assertions.assertEquals("{\"id\":24,\"university\":{\"id\":1},\"name\":\"MEHMAT\"}", responseJson);
    }

    @Test
    void testUpdateBadJson() throws IOException {
        DepartmentService service;
        DepartmentController controller;
        HttpServletRequest request;
        String requestJson;
        StringReader requestStringReader;
        BufferedReader requestReader;
        String responseText;

        service = Mockito.mock(DepartmentService.class);

        requestJson = "{\"university_id\":1,\"name\":\"MEHMAT\"";
        requestStringReader = new StringReader(requestJson);
        requestReader = new BufferedReader(requestStringReader);
        request = Mockito.mock(HttpServletRequest.class);
        Mockito.doReturn("/12").when(request).getPathInfo();
        Mockito.doReturn(requestReader).when(request).getReader();

        controller = Mockito.spy(DepartmentController.class);
        Mockito.doReturn(service).when(controller).createDepartmentService();
        controller.doPatch(request, response);
        responseText = responseStringWriter.toString();

        Assertions.assertEquals(HttpServletResponse.SC_BAD_REQUEST, responseStatus);
        Assertions.assertEquals(MimeTypes.TEXT_PLAIN, responseContentType);
        Assertions.assertEquals("Invalid JSON", responseText);
    }

    @Test
    void testUpdateBadRequest() throws IOException {
        DepartmentService service;
        DepartmentController controller;
        HttpServletRequest request;
        Object[] pathInfos;

        service = Mockito.mock(DepartmentService.class);

        request = Mockito.mock(HttpServletRequest.class);

        controller = Mockito.spy(DepartmentController.class);
        Mockito.doReturn(service).when(controller).createDepartmentService();

        pathInfos = new Object[2];
        pathInfos[0] = "/aaa";
        for (Object pathInfo : pathInfos) {
            Mockito.doReturn(pathInfo).when(request).getPathInfo();
            controller.doPatch(request, response);
            Assertions.assertEquals(HttpServletResponse.SC_BAD_REQUEST, responseStatus);
        }
    }

    @Test
    void testUpdateInvalidDepartment() throws IOException {
        DepartmentService service;
        DepartmentController controller;
        HttpServletRequest request;
        String requestJson;
        StringReader requestStringReader;
        BufferedReader requestReader;
        String responseText;

        service = Mockito.mock(DepartmentService.class);
        Mockito.doReturn(new DepartmentFull()).when(service).getById(24);
        Mockito.doThrow(new ValidationException("Test error")).when(service).update(Mockito.any(Department.class));

        requestJson = "{}";
        requestStringReader = new StringReader(requestJson);
        requestReader = new BufferedReader(requestStringReader);
        request = Mockito.mock(HttpServletRequest.class);
        Mockito.doReturn("/24").when(request).getPathInfo();
        Mockito.doReturn(requestReader).when(request).getReader();

        controller = Mockito.spy(DepartmentController.class);
        Mockito.doReturn(service).when(controller).createDepartmentService();
        controller.doPatch(request, response);
        responseText = responseStringWriter.toString();

        Assertions.assertEquals(HttpServletResponse.SC_BAD_REQUEST, responseStatus);
        Assertions.assertEquals(MimeTypes.TEXT_PLAIN, responseContentType);
        Assertions.assertEquals("Test error", responseText);
    }

    @Test
    void testUpdateNotFound() throws IOException {
        DepartmentService service;
        DepartmentController controller;
        HttpServletRequest request;
        String requestJson;
        StringReader requestStringReader;
        BufferedReader requestReader;

        service = Mockito.mock(DepartmentService.class);
        Mockito.doReturn(null).when(service).getById(24);

        requestJson = "{}";
        requestStringReader = new StringReader(requestJson);
        requestReader = new BufferedReader(requestStringReader);
        request = Mockito.mock(HttpServletRequest.class);
        Mockito.doReturn("/24").when(request).getPathInfo();
        Mockito.doReturn(requestReader).when(request).getReader();

        controller = Mockito.spy(DepartmentController.class);
        Mockito.doReturn(service).when(controller).createDepartmentService();
        controller.doPatch(request, response);

        Assertions.assertEquals(HttpServletResponse.SC_NOT_FOUND, responseStatus);
    }

    @Test
    void testDelete() {
        DepartmentService service;
        DepartmentController controller;
        HttpServletRequest request;

        service = Mockito.mock(DepartmentService.class);
        Mockito.doReturn(true).when(service).delete(24);

        request = Mockito.mock(HttpServletRequest.class);
        Mockito.doReturn("/24").when(request).getPathInfo();

        controller = Mockito.spy(DepartmentController.class);
        Mockito.doReturn(service).when(controller).createDepartmentService();
        controller.doDelete(request, response);

        Assertions.assertTrue(responseStatus < 400);
    }

    @Test
    void testDeleteBadRequest() {
        DepartmentService service;
        DepartmentController controller;
        HttpServletRequest request;
        Object[] pathInfos;

        service = Mockito.mock(DepartmentService.class);

        request = Mockito.mock(HttpServletRequest.class);

        controller = Mockito.spy(DepartmentController.class);
        Mockito.doReturn(service).when(controller).createDepartmentService();

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
        DepartmentService service;
        DepartmentController controller;
        HttpServletRequest request;

        service = Mockito.mock(DepartmentService.class);
        Mockito.doReturn(null).when(service).getById(24);

        request = Mockito.mock(HttpServletRequest.class);
        Mockito.doReturn("/24").when(request).getPathInfo();

        controller = Mockito.spy(DepartmentController.class);
        Mockito.doReturn(service).when(controller).createDepartmentService();
        controller.doDelete(request, response);

        Assertions.assertEquals(HttpServletResponse.SC_NOT_FOUND, responseStatus);
    }

    HttpServletResponse response;
    PrintWriter responseWriter;
    StringWriter responseStringWriter;
    String responseContentType;
    int responseStatus;
}
