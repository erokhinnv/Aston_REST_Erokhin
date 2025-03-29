package controllers;

import com.google.gson.Gson;
import dto.ProfessorCreationDto;
import dto.ProfessorDto;
import dto.ProfessorUpdateDto;
import entities.Department;
import entities.Professor;
import exceptions.ValidationException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import repositories.DepartmentRepository;
import repositories.ProfessorRepository;
import services.ProfessorService;
import utils.MimeTypes;
import utils.ParseUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collection;

@WebServlet({"/professors", "/professors/*"})
public class ProfessorController extends HttpServlet {
    @Override
    @SuppressWarnings("java:S1989") // Все необрабатываемые исключения являются Server Internal Error (500)
    public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Gson parser;
        Object body;
        String pathInfo;
        PrintWriter respWriter;

        pathInfo = req.getPathInfo();
        if (pathInfo == null) {
            body = getProfessors();
        } else {
            String professorIdStr;
            int professorId;

            professorIdStr = pathInfo.substring(1);
            try {
                professorId = Integer.parseInt(professorIdStr);
            } catch (NumberFormatException nfe) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                return;
            }

            body = getProfessor(professorId);
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
        ProfessorCreationDto creationDto;
        ProfessorDto professorDto;
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
            creationDto = parser.fromJson(reqReader, ProfessorCreationDto.class);
        } catch (RuntimeException re) {
            respWriter.write("Invalid JSON");
            resp.setContentType(MimeTypes.TEXT_PLAIN);
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }
        try {
            professorDto = createProfessor(creationDto);
        } catch (ValidationException ve) {
            respWriter.write(ve.getMessage());
            resp.setContentType(MimeTypes.TEXT_PLAIN);
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        parser.toJson(professorDto, respWriter);
        resp.setContentType(MimeTypes.APPLICATION_JSON);
        resp.setStatus(HttpServletResponse.SC_CREATED);
    }

    @Override
    @SuppressWarnings("java:S1989") // Все необрабатываемые исключения являются Server Internal Error (500)
    public void doPatch(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String pathInfo;
        String professorIdStr;
        int professorId;
        ProfessorUpdateDto updateDto;
        ProfessorDto professorDto;
        Gson parser;
        BufferedReader reqReader;
        PrintWriter respWriter;

        pathInfo = req.getPathInfo();
        if (pathInfo == null) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        professorIdStr = pathInfo.substring(1);
        try {
            professorId = Integer.parseInt(professorIdStr);
        } catch (NumberFormatException nfe) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        parser = ParseUtils.createParser();
        reqReader = req.getReader();
        respWriter = resp.getWriter();
        try {
            updateDto = parser.fromJson(reqReader, ProfessorUpdateDto.class);
        } catch (RuntimeException re) {
            respWriter.write("Invalid JSON");
            resp.setContentType(MimeTypes.TEXT_PLAIN);
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }
        try {
            professorDto = updateProfessor(professorId, updateDto);
        } catch (ValidationException ve) {
            respWriter.write(ve.getMessage());
            resp.setContentType(MimeTypes.TEXT_PLAIN);
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        if (professorDto == null) {
            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        parser.toJson(professorDto, respWriter);
        resp.setContentType(MimeTypes.APPLICATION_JSON);
    }

    @Override
    public void doDelete(HttpServletRequest req, HttpServletResponse resp) {
        String pathInfo;
        String professorIdStr;
        int professorId;

        pathInfo = req.getPathInfo();
        if (pathInfo == null) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        professorIdStr = pathInfo.substring(1);
        try {
            professorId = Integer.parseInt(professorIdStr);
        } catch (NumberFormatException nfe) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }
        if (!deleteProfessor(professorId)) {
            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
        }
    }

    private Collection<ProfessorDto> getProfessors() {
        Collection<Professor> professors;
        ArrayList<ProfessorDto> result;
        ProfessorService service;

        result = new ArrayList<>();
        service = createProfessorService();
        professors = service.get();
        for (Professor professor : professors) {
            ProfessorDto dto;

            dto = toDto(professor);
            result.add(dto);
        }

        return result;
    }

    private ProfessorDto getProfessor(int id) {
        ProfessorDto result;
        Professor professor;
        ProfessorService service;

        service = createProfessorService();
        professor = service.getById(id);
        result = toDto(professor);

        return result;
    }

    private ProfessorDto createProfessor(ProfessorCreationDto creationDto) {
        ProfessorDto result;
        Professor professor;
        ProfessorService service;
        Department department;

        professor = new Professor();
        department = new Department();
        department.setId(creationDto.departmentId);
        professor.setDepartment(department);
        professor.setName(creationDto.name);
        professor.setPhoneNumber(creationDto.phoneNumber);
        professor.setDegree(creationDto.degree);
        professor.setBirthday(creationDto.birthday);
        service = createProfessorService();
        professor = service.add(professor);
        result = toDto(professor);
        return result;
    }

    @java.lang.SuppressWarnings("squid:S2789") // Optional может быть null намеренно
    private ProfessorDto updateProfessor(int id, ProfessorUpdateDto updateDto) {
        Professor professor;
        ProfessorDto result;
        ProfessorService service;

        result = null;
        service = createProfessorService();
        professor = service.getById(id);
        if (professor != null) {
            if (updateDto.departmentId != null) {
                Department department;

                department = new Department();
                department.setId(updateDto.departmentId.orElse(0));
                professor.setDepartment(department);
            }
            if (updateDto.name != null) {
                professor.setName(updateDto.name.orElse(null));
            }
            if (updateDto.phoneNumber != null) {
                professor.setPhoneNumber(updateDto.phoneNumber.orElse(null));
            }
            if (updateDto.degree != null) {
                professor.setDegree(updateDto.degree.orElse(null));
            }
            if (updateDto.birthday != null) {
                professor.setBirthday(updateDto.birthday.orElse(null));
            }
            if (service.update(professor)) {
                result = toDto(professor);
            }
        }

        return result;
    }

    private boolean deleteProfessor(int id) {
        ProfessorService service;

        service = createProfessorService();
        return service.delete(id);
    }

    static ProfessorDto toDto(Professor professor) {
        ProfessorDto dto;

        if (professor != null) {
            Department department;

            dto = new ProfessorDto();
            dto.id = professor.getId();
            department = professor.getDepartment();
            dto.department = DepartmentController.toDto(department);
            dto.name = professor.getName();
            dto.phoneNumber = professor.getPhoneNumber();
            dto.degree = professor.getDegree();
            dto.birthday = professor.getBirthday();
        } else {
            dto = null;
        }

        return dto;
    }

    @SuppressWarnings("java:S112") // Все необрабатываемые ошибки считаем Server Internal Error (500)
    ProfessorService createProfessorService() {
        ProfessorRepository repository;
        DepartmentRepository departmentRepository;
        ProfessorService service;

        try {
            repository = new ProfessorRepository();
            departmentRepository = new DepartmentRepository();
            service = new ProfessorService(repository, departmentRepository);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return service;
    }
}
