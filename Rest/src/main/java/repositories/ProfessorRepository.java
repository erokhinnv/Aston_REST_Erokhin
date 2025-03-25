package repositories;

import entities.Professor;

import java.sql.*;
import java.util.ArrayList;
import java.util.Collection;

public class ProfessorRepository {
    public ProfessorRepository(Connection connection) throws SQLException {
        this.connection = connection;
        RepositoryUtils.createTablesIfNotExist(connection);
    }

    public void add(Professor professor) throws SQLException {
        ResultSet resultSet;

        try (PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO professors (department_id, name, phone_number, degree, birthday)" +
                "VALUES (?, ?, ?, ?, ?) returning id")) {
            preparedStatement.setInt(1, professor.getDepartmentId());
            preparedStatement.setString(2, professor.getName());
            preparedStatement.setString(3, professor.getPhoneNumber());
            preparedStatement.setString(4, professor.getDegree());
            preparedStatement.setDate(5, new java.sql.Date(professor.getBirthday().getTime()));
            resultSet = preparedStatement.executeQuery();
            resultSet.next();
            professor.setId(resultSet.getInt(1));
        }
    }

    public boolean delete(int id) throws SQLException {
        ResultSet resultSet;
        boolean deleted;

        try (PreparedStatement preparedStatement = connection.prepareStatement("DELETE FROM professors WHERE id = ? returning id")) {
            preparedStatement.setInt(1, id);
            resultSet = preparedStatement.executeQuery();
            deleted = resultSet.next();
        }
        return deleted;
    }

    public boolean update(Professor professor) throws SQLException {
        ResultSet resultSet;
        boolean updated;

        try (PreparedStatement preparedStatement = connection.prepareStatement("UPDATE professors SET department_id = ?, name = ?, phone_number = ?, degree = ?, birthday = ? " +
                "WHERE id =? returning id")) {
            preparedStatement.setInt(1, professor.getDepartmentId());
            preparedStatement.setString(2, professor.getName());
            preparedStatement.setString(3, professor.getPhoneNumber());
            preparedStatement.setString(4, professor.getDegree());
            preparedStatement.setDate(5, new java.sql.Date(professor.getBirthday().getTime()));
            preparedStatement.setInt(6, professor.getId());
            resultSet = preparedStatement.executeQuery();
            updated = resultSet.next();
        }
        return updated;
    }

    public Professor getById(int id) throws SQLException {
        ResultSet resultSet;
        Professor professor;

        try (PreparedStatement preparedStatement = connection.prepareStatement("SELECT department_id, name, phone_number," +
                " degree, birthday FROM professors WHERE id = ?")) {
            preparedStatement.setInt(1, id);
            resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                professor = new Professor();
                professor.setId(id);
                professor.setDepartmentId(resultSet.getInt(1));
                professor.setName(resultSet.getString(2));
                professor.setPhoneNumber(resultSet.getString(3));
                professor.setDegree(resultSet.getString(4));
                professor.setBirthday(resultSet.getDate(5));
            } else {
                professor = null;
            }
        }
        return professor;
    }

    public Collection<Professor> get() throws SQLException {
        ResultSet resultSet;
        ArrayList<Professor> professors;

        professors = new ArrayList<>();
        try (Statement statement = connection.createStatement()) {
            resultSet = statement.executeQuery("SELECT department_id, name, phone_number, degree, birthday, id FROM professors");
            while (resultSet.next()) {
                Professor professor;

                professor = new Professor();
                professor.setId(resultSet.getInt(6));
                professor.setDepartmentId(resultSet.getInt(1));
                professor.setName(resultSet.getString(2));
                professor.setPhoneNumber(resultSet.getString(3));
                professor.setDegree(resultSet.getString(4));
                professor.setBirthday(resultSet.getDate(5));
                professors.add(professor);
            }
        }
        return professors;
    }

    private final Connection connection;

}
