package repositories;

import entities.Department;
import entities.University;
import entities.UniversityFull;

import java.sql.*;
import java.util.ArrayList;
import java.util.Collection;

public class UniversityRepository {

    public UniversityRepository(Connection connection) throws SQLException {
        this.connection = connection;
        RepositoryUtils.createTablesIfNotExist(connection);
    }

    public void add(University university) throws SQLException {
        ResultSet resultSet;

        try (PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO universities (name, city)" +
                "VALUES (?, ?) returning id")) {
            preparedStatement.setString(1, university.getName());
            preparedStatement.setString(2, university.getCity());

            resultSet = preparedStatement.executeQuery();
            resultSet.next();
            university.setId(resultSet.getInt(1));
        }
    }

    public boolean delete(int id) throws SQLException {
        ResultSet resultSet;
        boolean deleted;

        try (PreparedStatement preparedStatement = connection.prepareStatement("DELETE FROM universities WHERE id = ? returning id")) {
            preparedStatement.setInt(1, id);
            resultSet = preparedStatement.executeQuery();
            deleted = resultSet.next();
        }
        return deleted;
    }

    public boolean update(University university) throws SQLException {
        ResultSet resultSet;
        boolean updated;

        try (PreparedStatement preparedStatement = connection.prepareStatement("UPDATE universities SET name = ?, city = ? " +
                "WHERE id = ? returning id")) {
            preparedStatement.setString(1, university.getName());
            preparedStatement.setString(2, university.getCity());
            preparedStatement.setInt(3, university.getId());
            resultSet = preparedStatement.executeQuery();
            updated = resultSet.next();
        }
        return updated;
    }

    public UniversityFull getById(int id) throws SQLException {
        UniversityFull university;

        try (PreparedStatement preparedStatement = connection.prepareStatement("SELECT name, city FROM universities WHERE id = ?")) {
            ResultSet resultSet;

            preparedStatement.setInt(1, id);
            resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                university = new UniversityFull();
                university.setId(id);
                university.setName(resultSet.getString(1));
                university.setCity(resultSet.getString(2));
            } else {
                university = null;
            }
        }

        if (university != null) {
            try (PreparedStatement preparedStatement = connection.prepareStatement("SELECT id, name FROM departments WHERE university_id = ?")) {
                ResultSet resultSet;
                ArrayList<Department> departments;

                departments = new ArrayList<>();
                preparedStatement.setInt(1, id);
                resultSet = preparedStatement.executeQuery();
                while (resultSet.next()) {
                    Department department;

                    department = new Department();
                    department.setId(resultSet.getInt(1));
                    department.setUniversity(university);
                    department.setName(resultSet.getString(2));
                    departments.add(department);
                }
                university.setDepartments(departments);
            }
        }

        return university;
    }

    public Collection<University> get() throws SQLException {
        ArrayList<University> universities;
        ResultSet resultSet;

        universities = new ArrayList<>();
        try (Statement statement = connection.createStatement()) {
            resultSet = statement.executeQuery("SELECT id, name, city FROM universities");
            while (resultSet.next()) {
                University university;

                university = new University();
                university.setId(resultSet.getInt(1));
                university.setName(resultSet.getString(2));
                university.setCity(resultSet.getString(3));
                universities.add(university);
            }
        }
        return universities;
    }

    private final Connection connection;

}
