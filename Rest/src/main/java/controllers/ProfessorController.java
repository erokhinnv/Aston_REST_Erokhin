package controllers;

import com.google.gson.Gson;
import dto.*;
import entities.Professor;
import exceptions.ValidationException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import services.ProfessorService;
import utils.MimeTypes;
import utils.ParseUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collection;

public class ProfessorController {
    public ProfessorController(ProfessorService service) {
        this.service = service;
    }

    public void get(HttpServletRequest req, HttpServletResponse resp) throws IOException {
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

    public void create(HttpServletRequest req, HttpServletResponse resp) throws IOException {
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

    public void update(HttpServletRequest req, HttpServletResponse resp) throws IOException {
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

    public void delete(HttpServletRequest req, HttpServletResponse resp) {
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

        result = new ArrayList<>();
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

        professor = service.getById(id);
        result = toDto(professor);

        return result;
    }

    private ProfessorDto createProfessor(ProfessorCreationDto creationDto) {
        ProfessorDto result;
        Professor professor;

        professor = new Professor();
        professor.setDepartmentId(creationDto.departmentId);
        professor.setName(creationDto.name);
        professor.setPhoneNumber(creationDto.phoneNumber);
        professor.setDegree(creationDto.degree);
        professor.setBirthday(creationDto.birthday);
        service.add(professor);
        result = toDto(professor);
        return result;
    }

    @java.lang.SuppressWarnings("squid:S2789") // Optional может быть null намеренно
    private ProfessorDto updateProfessor(int id, ProfessorUpdateDto updateDto) {
        Professor professor;
        ProfessorDto result;

        result = null;
        professor = service.getById(id);
        if (professor != null) {
            if (updateDto.departmentId != null) {
                professor.setDepartmentId(updateDto.departmentId.orElse(0));
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
        return service.delete(id);
    }

    private static ProfessorDto toDto(Professor professor) {
        ProfessorDto dto;

        if (professor != null) {
            dto = new ProfessorDto();
            dto.id = professor.getId();
            dto.departmentId = professor.getDepartmentId();
            dto.name = professor.getName();
            dto.phoneNumber = professor.getPhoneNumber();
            dto.degree = professor.getDegree();
            dto.birthday = professor.getBirthday();
        } else {
            dto = null;
        }

        return dto;
    }

    private final ProfessorService service;
}
