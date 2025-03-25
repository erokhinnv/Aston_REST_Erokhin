package servlets;

import controllers.UniversityController;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import repositories.UniversityRepository;
import services.UniversityService;
import utils.ConnectionUtils;

import java.io.IOException;
import java.sql.Connection;

@WebServlet({"/universities", "/universities/*"})
public class UniversityHttpServlet extends HttpServlet {

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
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        createUniversityController().get(req, resp);
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
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        createUniversityController().create(req, resp);
    }

    @Override
    @SuppressWarnings("java:S1989") // Все необрабатываемые исключения являются Server Internal Error (500)
    protected void doPatch(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        createUniversityController().update(req, resp);
    }

    @Override
    @SuppressWarnings("java:S1989") // Все необрабатываемые исключения являются Server Internal Error (500)
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) {
        createUniversityController().delete(req, resp);
    }

    @SuppressWarnings("java:S112") // Все необрабатываемые ошибки считаем Server Internal Error (500)
    private static UniversityController createUniversityController() {
        Connection connection;
        UniversityRepository repository;
        UniversityService service;
        UniversityController controller;

        try {
            connection = ConnectionUtils.openConnection();
            repository = new UniversityRepository(connection);
            service = new UniversityService(repository);
            controller = new UniversityController(service);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return controller;
    }
}
