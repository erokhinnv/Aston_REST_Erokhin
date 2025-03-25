package controllers;

import com.google.gson.Gson;
import dto.UniversityCreationDto;
import dto.UniversityDto;
import dto.UniversityUpdateDto;
import entities.University;
import exceptions.ValidationException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import services.UniversityService;
import utils.MimeTypes;
import utils.ParseUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collection;

public class UniversityController {
    public UniversityController(UniversityService service) {
        this.service = service;
    }

    public void get(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Gson parser;
        Object body;
        String pathInfo;
        PrintWriter respWriter;

        pathInfo = req.getPathInfo();
        if (pathInfo == null) {
            body = getUniversities();
        } else {
            String universityIdStr;
            int universityId;

            universityIdStr = pathInfo.substring(1);
            try {
                universityId = Integer.parseInt(universityIdStr);
            } catch (NumberFormatException nfe) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                return;
            }

            body = getUniversity(universityId);
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
        UniversityCreationDto creationDto;
        UniversityDto universityDto;
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
            creationDto = parser.fromJson(reqReader, UniversityCreationDto.class);
        } catch (RuntimeException re) {
            respWriter.write("Invalid JSON");
            resp.setContentType(MimeTypes.TEXT_PLAIN);
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }
        try {
            universityDto = createUniversity(creationDto);
        } catch (ValidationException ve) {
            respWriter.write(ve.getMessage());
            resp.setContentType(MimeTypes.TEXT_PLAIN);
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        parser.toJson(universityDto, respWriter);
        resp.setContentType(MimeTypes.APPLICATION_JSON);
        resp.setStatus(HttpServletResponse.SC_CREATED);
    }

    public void update(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String pathInfo;
        String universityIdStr;
        int universityId;
        UniversityUpdateDto updateDto;
        UniversityDto universityDto;
        Gson parser;
        BufferedReader reqReader;
        PrintWriter respWriter;

        pathInfo = req.getPathInfo();
        if (pathInfo == null) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        universityIdStr = pathInfo.substring(1);
        try {
            universityId = Integer.parseInt(universityIdStr);
        } catch (NumberFormatException nfe) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        parser = ParseUtils.createParser();
        reqReader = req.getReader();
        respWriter = resp.getWriter();
        try {
            updateDto = parser.fromJson(reqReader, UniversityUpdateDto.class);
        } catch (RuntimeException re) {
            respWriter.write("Invalid JSON");
            resp.setContentType(MimeTypes.TEXT_PLAIN);
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }
        try {
            universityDto = updateUniversity(universityId, updateDto);
        } catch (ValidationException ve) {
            respWriter.write(ve.getMessage());
            resp.setContentType(MimeTypes.TEXT_PLAIN);
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        if (universityDto == null) {
            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        parser.toJson(universityDto, respWriter);
        resp.setContentType(MimeTypes.APPLICATION_JSON);
    }

    public void delete(HttpServletRequest req, HttpServletResponse resp) {
        String pathInfo;
        String universityIdStr;
        int universityId;

        pathInfo = req.getPathInfo();
        if (pathInfo == null) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        universityIdStr = pathInfo.substring(1);
        try {
            universityId = Integer.parseInt(universityIdStr);
        } catch (NumberFormatException nfe) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }
        if (!deleteUniversity(universityId)) {
            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
        }
    }

    private Collection<UniversityDto> getUniversities() {
        Collection<University> universities;
        ArrayList<UniversityDto> result;

        universities = service.get();
        result = new ArrayList<>(universities.size());
        for (University university : universities) {
            UniversityDto dto;

            dto = toDto(university);
            result.add(dto);
        }
        return result;
    }

    private UniversityDto getUniversity(int id) {
        University university;
        UniversityDto result;

        university = service.getById(id);
        result = toDto(university);
        return result;
    }

    private UniversityDto createUniversity(UniversityCreationDto creationDto) {
        University university;
        UniversityDto result;

        university = new University();
        university.setName(creationDto.name);
        university.setCity(creationDto.city);
        service.add(university);
        result = toDto(university);
        return result;
    }

    @java.lang.SuppressWarnings("squid:S2789") // Optional может быть null намеренно
    private UniversityDto updateUniversity(int id, UniversityUpdateDto updateDto) {
        University university;
        UniversityDto result;

        result = null;
        university = service.getById(id);
        if (university != null) {
            if (updateDto.name != null) {
                university.setName(updateDto.name.orElse(null));
            }
            if (updateDto.city != null) {
                university.setCity(updateDto.city.orElse(null));
            }
            if (service.update(university)) {
                result = toDto(university);
            }
        }

        return result;
    }

    private boolean deleteUniversity(int id) {
        return service.delete(id);
    }

    private static UniversityDto toDto(University university) {
        UniversityDto dto;

        if (university != null) {
            dto = new UniversityDto();
            dto.id = university.getId();
            dto.name = university.getName();
            dto.city = university.getCity();
        } else {
            dto = null;
        }

        return dto;
    }

    private final UniversityService service;
}
