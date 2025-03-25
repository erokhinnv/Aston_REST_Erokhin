package services;

import entities.Professor;
import exceptions.ValidationException;
import repositories.DepartmentRepository;
import repositories.ProfessorRepository;

import java.sql.SQLException;
import java.util.Collection;

public class ProfessorService {

    public ProfessorService(ProfessorRepository repository, DepartmentRepository departmentRepository) {
        this.repository = repository;
        this.departmentRepository = departmentRepository;
    }

    @SuppressWarnings("java:S112") // Все необрабатываемые исключения считаем Internal Server Error (500)
    public void add(Professor professor) {
        try {
            validate(professor);
            repository.add(professor);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @SuppressWarnings("java:S112") // Все необрабатываемые исключения считаем Internal Server Error (500)
    public boolean delete(int id) {
        try {
            return repository.delete(id);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @SuppressWarnings("java:S112") // Все необрабатываемые исключения считаем Internal Server Error (500)
    public boolean update(Professor professor) {
        try {
            validate(professor);
            return repository.update(professor);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @SuppressWarnings("java:S112") // Все необрабатываемые исключения считаем Internal Server Error (500)
    public Professor getById(int id) {
        try {
            return repository.getById(id);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @SuppressWarnings("java:S112") // Все необрабатываемые исключения считаем Internal Server Error (500)
    public Collection<Professor> get() {
        try {
            return repository.get();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private void validate(Professor professor) throws SQLException {
        if (professor.getName() == null) {
            throw new ValidationException("Professor's name cannot be null");
        }
        if (professor.getPhoneNumber() == null) {
            throw new ValidationException("Professor's phone number cannot be null");
        }
        if (professor.getDegree() == null) {
            throw new ValidationException("Professor's degree cannot be null");
        }
        if (professor.getBirthday() == null) {
            throw new ValidationException("Professor's birthday cannot be null");
        }
        if (departmentRepository.getById(professor.getDepartmentId()) == null) {
            throw new ValidationException("Department does not exist");
        }
    }

    private final ProfessorRepository repository;
    private final DepartmentRepository departmentRepository;
}
