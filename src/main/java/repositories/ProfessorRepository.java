package repositories;

import entities.Department;
import entities.Professor;
import entities.University;

import java.sql.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

public class ProfessorRepository extends Repository {
    public ProfessorRepository() throws SQLException {
        super();
    }

    public void add(Professor professor) throws SQLException {
        Connection connection;
        ResultSet resultSet;

        connection = openConnection();
        try (PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO professors (department_id, name, phone_number, degree, birthday)" +
                "VALUES (?, ?, ?, ?, ?) returning id")) {
            preparedStatement.setInt(1, professor.getDepartment().getId());
            preparedStatement.setString(2, professor.getName());
            preparedStatement.setString(3, professor.getPhoneNumber());
            preparedStatement.setString(4, professor.getDegree());
            preparedStatement.setDate(5, new java.sql.Date(professor.getBirthday().getTime()));
            resultSet = preparedStatement.executeQuery();
            resultSet.next();
            professor.setId(resultSet.getInt(1));
        } finally {
            connection.close();
        }
    }

    public boolean delete(int id) throws SQLException {
        Connection connection;
        ResultSet resultSet;
        boolean deleted;

        connection = openConnection();
        try (PreparedStatement preparedStatement = connection.prepareStatement("DELETE FROM professors WHERE id = ? returning id")) {
            preparedStatement.setInt(1, id);
            resultSet = preparedStatement.executeQuery();
            deleted = resultSet.next();
        } finally {
            connection.close();
        }
        return deleted;
    }

    public boolean update(Professor professor) throws SQLException {
        Connection connection;
        ResultSet resultSet;
        boolean updated;

        connection = openConnection();
        try (PreparedStatement preparedStatement = connection.prepareStatement("UPDATE professors SET department_id = ?, name = ?, phone_number = ?, degree = ?, birthday = ? " +
                "WHERE id =? returning id")) {
            preparedStatement.setInt(1, professor.getDepartment().getId());
            preparedStatement.setString(2, professor.getName());
            preparedStatement.setString(3, professor.getPhoneNumber());
            preparedStatement.setString(4, professor.getDegree());
            preparedStatement.setDate(5, new java.sql.Date(professor.getBirthday().getTime()));
            preparedStatement.setInt(6, professor.getId());
            resultSet = preparedStatement.executeQuery();
            updated = resultSet.next();
        } finally {
            connection.close();
        }
        return updated;
    }

    public Professor getById(int id) throws SQLException {
        Connection connection;
        ResultSet resultSet;
        Professor professor;

        connection = openConnection();
        try (PreparedStatement preparedStatement = connection.prepareStatement("SELECT p.name, p.phone_number," +
                " p.degree, p.birthday, d.id, d.name, u.id, u.name, u.city" +
                " FROM professors p INNER JOIN departments d ON p.department_id = d.id" +
                " INNER JOIN universities u ON d.university_id = u.id WHERE p.id = ?")) {
            preparedStatement.setInt(1, id);
            resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                University university;
                Department department;

                professor = new Professor();
                professor.setId(id);
                professor.setName(resultSet.getString(1));
                professor.setPhoneNumber(resultSet.getString(2));
                professor.setDegree(resultSet.getString(3));
                professor.setBirthday(resultSet.getDate(4));
                department = new Department();
                department.setId(resultSet.getInt(5));
                department.setName(resultSet.getString(6));
                university = new University();
                university.setId(resultSet.getInt(7));
                university.setName(resultSet.getString(8));
                university.setCity(resultSet.getString(9));

                department.setUniversity(university);
                professor.setDepartment(department);

            } else {
                professor = null;
            }
        }
        connection.close();
        return professor;
    }

    public Collection<Professor> get() throws SQLException {
        Connection connection;
        ResultSet resultSet;
        ArrayList<Professor> professors;
        HashMap<Integer, Department> departments;
        HashMap<Integer, University> universities;

        connection = openConnection();
        universities = new HashMap<>();
        departments = new HashMap<>();
        professors = new ArrayList<>();
        try (Statement statement = connection.createStatement()) {
            resultSet = statement.executeQuery("SELECT p.id, p.name, p.phone_number," +
                    " p.degree, p.birthday, d.id, d.name, u.id, u.name, u.city" +
                    " FROM professors p INNER JOIN departments d ON p.department_id = d.id" +
                    " INNER JOIN universities u ON d.university_id = u.id");
            while (resultSet.next()) {
                University university;
                Department department;
                Professor professor;
                int departmentId;
                int universityId;

                professor = new Professor();
                professor.setId(resultSet.getInt(1));
                professor.setName(resultSet.getString(2));
                professor.setPhoneNumber(resultSet.getString(3));
                professor.setDegree(resultSet.getString(4));
                professor.setBirthday(resultSet.getDate(5));
                universityId = resultSet.getInt(8);
                university = universities.get(universityId);
                if (university == null) {
                    university = new University();
                    university.setId(universityId);
                    university.setName(resultSet.getString(9));
                    university.setCity(resultSet.getString(10));
                    universities.put(universityId, university);
                }
                departmentId = resultSet.getInt(6);
                department = departments.get(departmentId);
                if (department == null) {
                    department = new Department();
                    department.setId(departmentId);
                    department.setName(resultSet.getString(7));
                    department.setUniversity(university);
                    departments.put(departmentId, department);
                }
                professor.setDepartment(department);
                professors.add(professor);

            }
        } finally {
            connection.close();
        }
        return professors;
    }
}
