package servlets;

import controllers.ProfessorController;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import repositories.DepartmentRepository;
import repositories.ProfessorRepository;
import services.ProfessorService;
import utils.ConnectionUtils;

import java.io.IOException;
import java.sql.Connection;

@WebServlet({"/professors", "/professors/*"})
public class ProfessorHttpServlet extends HttpServlet {
    @Override
    @SuppressWarnings("java:S1989") // Все необрабатываемые исключения являются Server Internal Error (500)
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        createProfessorController().get(req, resp);
    }

    @Override
    @SuppressWarnings("java:S1989") // Все необрабатываемые исключения являются Server Internal Error (500)
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        createProfessorController().create(req, resp);
    }

    @Override
    @SuppressWarnings("java:S1989") // Все необрабатываемые исключения являются Server Internal Error (500)
    protected void doPatch(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        createProfessorController().update(req, resp);
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) {
        createProfessorController().delete(req, resp);
    }

    @SuppressWarnings("java:S112") // Все необрабатываемые ошибки считаем Server Internal Error (500)
    private static ProfessorController createProfessorController() {
        Connection connection;
        ProfessorRepository repository;
        DepartmentRepository departmentRepository;
        ProfessorService service;
        ProfessorController controller;

        try {
            connection = ConnectionUtils.openConnection();
            repository = new ProfessorRepository(connection);
            departmentRepository = new DepartmentRepository(connection);
            service = new ProfessorService(repository, departmentRepository);
            controller = new ProfessorController(service);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return controller;
    }
}
