package repositories;

import entities.Department;

import java.sql.*;
import java.util.ArrayList;
import java.util.Collection;

public class DepartmentRepository {
    public DepartmentRepository(Connection connection) throws SQLException {
        this.connection = connection;
        RepositoryUtils.createTablesIfNotExist(connection);
    }

    public void add(Department department) throws SQLException {
        ResultSet resultSet;

        try (PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO departments (university_id, name)" +
                "VALUES (?, ?) returning id")) {
            preparedStatement.setInt(1, department.getUniversityId());
            preparedStatement.setString(2, department.getName());

            resultSet = preparedStatement.executeQuery();
            resultSet.next();
            department.setId(resultSet.getInt(1));
        }

    }

    public boolean delete(int id) throws SQLException {
        ResultSet resultSet;
        boolean deleted;

        try (PreparedStatement preparedStatement = connection.prepareStatement("DELETE FROM departments WHERE id = ? returning id")) {
            preparedStatement.setInt(1, id);
            resultSet = preparedStatement.executeQuery();
            deleted = resultSet.next();
        }
        return deleted;
    }

    public boolean update(Department department) throws SQLException {
        ResultSet resultSet;
        boolean updated;

        try (PreparedStatement preparedStatement = connection.prepareStatement("UPDATE departments SET university_id = ?, name = ? " +
                "WHERE id = ? returning id")) {
            preparedStatement.setInt(1, department.getUniversityId());
            preparedStatement.setString(2, department.getName());
            preparedStatement.setInt(3, department.getId());
            resultSet = preparedStatement.executeQuery();
            updated = resultSet.next();
        }
        return updated;
    }

    public Department getById(int id) throws SQLException {
        ResultSet resultSet;
        Department department;


        try (PreparedStatement preparedStatement = connection.prepareStatement("SELECT university_id, name FROM departments WHERE id = ?")) {
            preparedStatement.setInt(1, id);
            resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                department = new Department();
                department.setId(id);
                department.setUniversityId(resultSet.getInt(1));
                department.setName(resultSet.getString(2));
            } else {
                department = null;
            }
        }
        return department;
    }

    public Collection<Department> get() throws SQLException {
        ArrayList<Department> departments;
        ResultSet resultSet;

        departments = new ArrayList<>();
        try (Statement statement = connection.createStatement()) {
            resultSet = statement.executeQuery("SELECT id, university_id, name FROM departments");
            while (resultSet.next()) {
                Department department;

                department = new Department();
                department.setId(resultSet.getInt(1));
                department.setUniversityId(resultSet.getInt(2));
                department.setName(resultSet.getString(3));
                departments.add(department);
            }
        }
        return departments;
    }

    private final Connection connection;

}
