package controllers;

import com.google.gson.Gson;
import dto.*;
import entities.Department;
import entities.University;
import entities.UniversityFull;
import exceptions.ValidationException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import repositories.UniversityRepository;
import services.UniversityService;
import utils.ConnectionUtils;
import utils.MimeTypes;
import utils.ParseUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Collection;

@WebServlet({"/universities", "/universities/*"})
public class UniversityController extends HttpServlet {

    /**
     * @GET/universities
     * Получить список университетов.
     * @param req запрос на получение списка dto-университетов.
     * @param resp ответ, в который будет отдан список dto-университетов в формате JSON.
     * Возможные ошибки: 400 Bad Request - ошибка в запросе
     *                   404 Not Found - запрашиваемый ресурс не найден
     *
     * @GET/universities/*
     * Получить университет по идентификатору.
     * @param req запрос на получение dto-университета.
     * @param resp ответ, в который будет отдан dto-университет с указанным id.
     * Возможные ошибки: 400 Bad Request - ошибка в запросе
     *                   404 Not Found - запрашиваемый ресурс не найден
     */
    @Override
    @SuppressWarnings("java:S1989") // Все необрабатываемые исключения являются Server Internal Error (500)
    public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
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

    /**
     * Добавить новый университет.
     *
     * @param req запрос на добавление университета. Внутри запроса в формате JSON приходит
     *           DTO-объект, из которого будет собрана сущность и добавлена в базу данных.
     * @param resp объект для создания ответа по операции.
     * @throws IOException
     */
    @Override
    @SuppressWarnings("java:S1989") // Все необрабатываемые исключения являются Server Internal Error (500)
    public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Gson parser;
        UniversityCreationDto creationDto;
        UniversityFullDto universityDto;
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

    @Override
    @SuppressWarnings("java:S1989") // Все необрабатываемые исключения являются Server Internal Error (500)
    public void doPatch(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String pathInfo;
        String universityIdStr;
        int universityId;
        UniversityUpdateDto updateDto;
        UniversityFullDto universityDto;
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

    @Override
    @SuppressWarnings("java:S1989") // Все необрабатываемые исключения являются Server Internal Error (500)
    public void doDelete(HttpServletRequest req, HttpServletResponse resp) {
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

    @SuppressWarnings("java:S112") // Все необрабатываемые ошибки считаем Server Internal Error (500)
    UniversityService createUniversityService() {
        Connection connection;
        UniversityRepository repository;
        UniversityService service;

        try {
            connection = ConnectionUtils.openConnection();
            repository = new UniversityRepository(connection);
            service = new UniversityService(repository);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return service;
    }

    private Collection<UniversityDto> getUniversities() {
        Collection<University> universities;
        ArrayList<UniversityDto> result;
        UniversityService service;

        service = createUniversityService();
        universities = service.get();
        result = new ArrayList<>(universities.size());
        for (University university : universities) {
            UniversityDto dto;

            dto = toDto(university);
            result.add(dto);
        }
        return result;
    }

    private UniversityFullDto getUniversity(int id) {
        UniversityFull university;
        UniversityFullDto result;
        UniversityService service;

        service = createUniversityService();
        university = service.getById(id);
        result = toFullDto(university);
        return result;
    }

    private UniversityFullDto createUniversity(UniversityCreationDto creationDto) {
        University university;
        UniversityFull universityFull;
        UniversityFullDto result;
        UniversityService service;

        university = new University();
        university.setName(creationDto.name);
        university.setCity(creationDto.city);
        service = createUniversityService();
        universityFull = service.add(university);
        result = toFullDto(universityFull);
        return result;
    }

    @java.lang.SuppressWarnings("squid:S2789") // Optional может быть null намеренно
    private UniversityFullDto updateUniversity(int id, UniversityUpdateDto updateDto) {
        UniversityFull university;
        UniversityFullDto result;
        UniversityService service;

        result = null;
        service = createUniversityService();
        university = service.getById(id);
        if (university != null) {
            if (updateDto.name != null) {
                university.setName(updateDto.name.orElse(null));
            }
            if (updateDto.city != null) {
                university.setCity(updateDto.city.orElse(null));
            }
            if (service.update(university)) {
                result = toFullDto(university);
            }
        }

        return result;
    }

    private boolean deleteUniversity(int id) {
        UniversityService service;

        service = createUniversityService();
        return service.delete(id);
    }

    static UniversityDto toDto(University university) {
        UniversityDto dto;

        if (university != null) {
            dto = new UniversityDto();
            fillDto(dto, university);
        } else {
            dto = null;
        }

        return dto;
    }

    private static void fillDto(UniversityDto dto, University university) {
        dto.id = university.getId();
        dto.name = university.getName();
        dto.city = university.getCity();
    }

    private static UniversityFullDto toFullDto(UniversityFull universityFull) {
        UniversityFullDto fullDto;

        if (universityFull != null) {
            Collection<Department> departments;

            fullDto = new UniversityFullDto();
            fillDto(fullDto, universityFull);
            departments = universityFull.getDepartments();
            if (departments != null) {
                ArrayList<DepartmentDto> departmentDtos;

                departmentDtos = new ArrayList<>(departments.size());
                for (Department department : departments) {
                    DepartmentDto departmentDto;

                    departmentDto = DepartmentController.toDto(department);
                    departmentDto.university = null;
                    departmentDtos.add(departmentDto);
                }
                fullDto.departments = departmentDtos;
            }
        } else {
            fullDto = null;
        }
        return fullDto;
    }
}