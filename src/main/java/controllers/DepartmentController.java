package controllers;

import com.google.gson.Gson;
import dto.DepartmentCreationDto;
import dto.DepartmentDto;
import dto.DepartmentUpdateDto;
import entities.Department;
import exceptions.ValidationException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import repositories.DepartmentRepository;
import repositories.UniversityRepository;
import services.DepartmentService;
import utils.ConnectionUtils;
import utils.MimeTypes;
import utils.ParseUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Collection;

@WebServlet({"/departments", "/departments/*"})
public class DepartmentController extends HttpServlet {
    @Override
    @SuppressWarnings("java:S1989") // Все необрабатываемые исключения являются Server Internal Error (500)
    public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Gson parser;
        Object body;
        String pathInfo;
        PrintWriter respWriter;

        pathInfo = req.getPathInfo();
        if (pathInfo == null) {
            body = getDepartments();
        } else {
            String departmentIdStr;
            int departmentId;

            departmentIdStr = pathInfo.substring(1);
            try {
                departmentId = Integer.parseInt(departmentIdStr);
            } catch (NumberFormatException nfe) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                return;
            }

            body = getDepartment(departmentId);
            if (body == null) {
                resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                return;
            }
        }

        parser = ParseUtils.createParser();
        respWriter = resp.getWriter();
        parser.toJson(body, respWriter);
        resp.setContentType(MimeTypes.APPLICATION_JSON);
    }

    @Override
    @SuppressWarnings("java:S1989") // Все необрабатываемые исключения являются Server Internal Error (500)
    public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Gson parser;
        DepartmentCreationDto creationDto;
        DepartmentDto departmentDto;
        BufferedReader reqReader;
        PrintWriter respWriter;

        if (req.getPathInfo() != null) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        parser = ParseUtils.createParser();
        reqReader = req.getReader();
        respWriter = resp.getWriter();
        try {
            creationDto = parser.fromJson(reqReader, DepartmentCreationDto.class);
        } catch (RuntimeException re) {
            respWriter.write("Invalid JSON");
            resp.setContentType(MimeTypes.TEXT_PLAIN);
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }
        try {
            departmentDto = createDepartment(creationDto);
        } catch (ValidationException ve) {
            respWriter.write(ve.getMessage());
            resp.setContentType(MimeTypes.TEXT_PLAIN);
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        parser.toJson(departmentDto, respWriter);
        resp.setContentType(MimeTypes.APPLICATION_JSON);
        resp.setStatus(HttpServletResponse.SC_CREATED);
    }

    @Override
    @SuppressWarnings("java:S1989") // Все необрабатываемые исключения являются Server Internal Error (500)
    public void doPatch(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String pathInfo;
        String departmentIdStr;
        int departmentId;
        DepartmentUpdateDto updateDto;
        DepartmentDto departmentDto;
        Gson parser;
        BufferedReader reqReader;
        PrintWriter respWriter;

        pathInfo = req.getPathInfo();
        if (pathInfo == null) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        departmentIdStr = pathInfo.substring(1);
        try {
            departmentId = Integer.parseInt(departmentIdStr);
        } catch (NumberFormatException nfe) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        parser = ParseUtils.createParser();
        reqReader = req.getReader();
        respWriter = resp.getWriter();
        try {
            updateDto = parser.fromJson(reqReader, DepartmentUpdateDto.class);
        } catch (RuntimeException re) {
            respWriter.write("Invalid JSON");
            resp.setContentType(MimeTypes.TEXT_PLAIN);
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }
        try {
            departmentDto = updateDepartment(departmentId, updateDto);
        } catch (ValidationException ve) {
            respWriter.write(ve.getMessage());
            resp.setContentType(MimeTypes.TEXT_PLAIN);
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        if (departmentDto == null) {
            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        parser.toJson(departmentDto, respWriter);
        resp.setContentType(MimeTypes.APPLICATION_JSON);
    }

    @Override
    public void doDelete(HttpServletRequest req, HttpServletResponse resp) {
        String pathInfo;
        String departmentIdStr;
        int departmentId;

        pathInfo = req.getPathInfo();
        if (pathInfo == null) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        departmentIdStr = pathInfo.substring(1);
        try {
            departmentId = Integer.parseInt(departmentIdStr);
        } catch (NumberFormatException nfe) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }
        if (!deleteDepartment(departmentId)) {
            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
        }
    }

    @SuppressWarnings("java:S112") // Все необрабатываемые исключения являются Server Internal Error (500)
    DepartmentService createDepartmentService() {
        Connection connection;
        DepartmentRepository repository;
        UniversityRepository universityRepository;
        DepartmentService service;

        try {
            connection = ConnectionUtils.openConnection();
            repository = new DepartmentRepository(connection);
            universityRepository = new UniversityRepository(connection);
            service = new DepartmentService(repository, universityRepository);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return service;
    }

    private Collection<DepartmentDto> getDepartments() {
        Collection<Department> departments;
        ArrayList<DepartmentDto> result;
        DepartmentService service;

        service = createDepartmentService();
        departments = service.get();
        result = new ArrayList<>(departments.size());
        for (Department department : departments) {
            DepartmentDto dto;

            dto = toDto(department);
            result.add(dto);
        }
        return result;
    }

    private DepartmentDto getDepartment(int id) {
        DepartmentDto result;
        Department department;
        DepartmentService service;

        service = createDepartmentService();
        department = service.getById(id);
        result = toDto(department);

        return result;
    }

    private DepartmentDto createDepartment(DepartmentCreationDto creationDto) {
        DepartmentDto result;
        Department department;
        DepartmentService service;

        department = new Department();
        department.setUniversityId(creationDto.universityId);
        department.setName(creationDto.name);
        service = createDepartmentService();
        service.add(department);
        result = toDto(department);
        return result;
    }

    @java.lang.SuppressWarnings("squid:S2789") // Optional может быть null намеренно
    private DepartmentDto updateDepartment(int id, DepartmentUpdateDto updateDto) {
        Department department;
        DepartmentDto result;
        DepartmentService service;

        result = null;
        service = createDepartmentService();
        department = service.getById(id);
        if (department != null) {
            if (updateDto.universityId != null) {
                department.setUniversityId(updateDto.universityId.orElse(0));
            }
            if (updateDto.name != null) {
                department.setName(updateDto.name.orElse(null));
            }
            if (service.update(department)) {
                result = toDto(department);
            }
        }

        return result;
    }

    private boolean deleteDepartment(int id) {
        DepartmentService service;

        service = createDepartmentService();
        return service.delete(id);
    }

    private static DepartmentDto toDto(Department department) {
        DepartmentDto dto;

        if (department != null) {
            dto = new DepartmentDto();
            dto.id = department.getId();
            dto.universityId = department.getUniversityId();
            dto.name = department.getName();
        } else {
            dto = null;
        }

        return dto;
    }
}