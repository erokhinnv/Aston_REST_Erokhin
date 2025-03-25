package servlets;

import controllers.DepartmentController;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import repositories.DepartmentRepository;
import repositories.UniversityRepository;
import services.DepartmentService;
import utils.ConnectionUtils;

import java.io.IOException;
import java.sql.Connection;

@WebServlet({"/departments", "/departments/*"})
public class DepartmentHttpServlet extends HttpServlet {
    @Override
    @SuppressWarnings("java:S1989") // Все необрабатываемые исключения являются Server Internal Error (500)
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        createDepartmentController().get(req, resp);
    }

    @Override
    @SuppressWarnings("java:S1989") // Все необрабатываемые исключения являются Server Internal Error (500)
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        createDepartmentController().create(req, resp);
    }

    @Override
    @SuppressWarnings("java:S1989") // Все необрабатываемые исключения являются Server Internal Error (500)
    protected void doPatch(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        createDepartmentController().update(req, resp);
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) {
        createDepartmentController().delete(req, resp);
    }

    @SuppressWarnings("java:S112") // Все необрабатываемые исключения являются Server Internal Error (500)
    private static DepartmentController createDepartmentController() {
        Connection connection;
        DepartmentRepository repository;
        UniversityRepository universityRepository;
        DepartmentService service;
        DepartmentController controller;

        try {
            connection = ConnectionUtils.openConnection();
            repository = new DepartmentRepository(connection);
            universityRepository = new UniversityRepository(connection);
            service = new DepartmentService(repository, universityRepository);
            controller = new DepartmentController(service);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return controller;
    }
}
