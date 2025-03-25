package services;

import entities.University;
import exceptions.ValidationException;
import repositories.UniversityRepository;

import java.sql.SQLException;
import java.util.Collection;

public class UniversityService {

    public UniversityService(UniversityRepository repository) {
        this.repository = repository;
    }

    @SuppressWarnings("java:S112") // Все необрабатываемые исключения считаем Internal Server Error (500)
    public void add(University university) {
        validate(university);
        try {
            repository.add(university);
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
    public boolean update(University university) {
        validate(university);
        try {
            return repository.update(university);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @SuppressWarnings("java:S112") // Все необрабатываемые исключения считаем Internal Server Error (500)
    public University getById(int id) {
        try {
            return repository.getById(id);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @SuppressWarnings("java:S112") // Все необрабатываемые исключения считаем Internal Server Error (500)
    public Collection<University> get() {
        try {
            return repository.get();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private static void validate(University university) {
        if (university.getName() == null) {
            throw new ValidationException("Name of university cannot be null");
        }
        if (university.getCity() == null) {
            throw new ValidationException("City of university cannot be null");
        }
    }

    private final UniversityRepository repository;
}
