package repositories;

import entities.Department;
import entities.Professor;
import entities.University;
import org.junit.jupiter.api.*;
import org.testcontainers.containers.PostgreSQLContainer;
import utils.DatabaseSettings;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collection;
import java.util.Date;

class ProfessorRepositoryTest {
    @BeforeAll
    static void beforeAll() {
        postgres.start();
    }

    @AfterAll
    static void afterAll() {
        postgres.stop();
    }

    @BeforeEach
    void setUp() {
        try {
            DatabaseSettings.URL = postgres.getJdbcUrl();
            DatabaseSettings.USERNAME = postgres.getUsername();
            DatabaseSettings.PASSWORD = postgres.getPassword();
            universityRepository = new UniversityRepository();
            departmentRepository = new DepartmentRepository();
            repository = new ProfessorRepository();
            university = new University();
            university.setName("PSTU");
            university.setCity("Perm");
            universityRepository.add(university);
            department = new Department();
            department.setName("ITAS");
            department.setUniversity(university);
            departmentRepository.add(department);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @AfterEach
    void cleanUp() throws SQLException {
        Connection connection;
        Statement statement;

        connection = DriverManager.getConnection(postgres.getJdbcUrl(), postgres.getUsername(), postgres.getPassword());
        statement = connection.createStatement();
        statement.execute("DELETE FROM universities");
    }

    @Test
    void testGet() throws SQLException {
        Professor first;
        Professor second;
        Date firstBirthdate, secondBirthdate;
        Collection<Professor> professors;

        first = new Professor();
        first.setName("Ivan");
        first.setPhoneNumber("+79998884334");
        first.setDegree("PhD in Computer Science");
        firstBirthdate = new Date();
        firstBirthdate.setTime(0);
        first.setBirthday(firstBirthdate);
        first.setDepartment(department);

        second = new Professor();
        second.setName("Petr");
        second.setPhoneNumber("+79824863265");
        second.setDegree("PhD in Technical Science");
        secondBirthdate = new Date();
        secondBirthdate.setTime(169344000);
        second.setBirthday(secondBirthdate);
        second.setDepartment(department);
        repository.add(first);
        repository.add(second);
        professors = repository.get();
        Assertions.assertEquals(2, professors.size());
    }

    @Test
    void testGetById() throws SQLException {
        Professor professor, dbProfessor;
        Date birthdate;
        int id;

        professor = new Professor();
        professor.setName("Ivan");
        professor.setPhoneNumber("+79998884334");
        professor.setDegree("PhD in Computer Science");
        birthdate = new Date();
        birthdate.setTime(0);
        professor.setBirthday(birthdate);
        professor.setDepartment(department);
        repository.add(professor);
        id = professor.getId();
        dbProfessor = repository.getById(id);
        Assertions.assertEquals(professor.getId(), dbProfessor.getId());
        Assertions.assertEquals(professor.getDepartment().getId(), dbProfessor.getDepartment().getId());
        Assertions.assertEquals(professor.getName(), dbProfessor.getName());
        Assertions.assertEquals(professor.getPhoneNumber(), dbProfessor.getPhoneNumber());
        Assertions.assertEquals(professor.getDegree(), dbProfessor.getDegree());
    }

    @Test
    void testAdd() throws SQLException {
        Professor professor;
        Date birthdate;
        Collection<Professor> professors;

        professor = new Professor();
        professor.setName("Ivan");
        professor.setPhoneNumber("+79998884334");
        professor.setDegree("PhD in Computer Science");
        birthdate = new Date();
        birthdate.setTime(0);
        professor.setBirthday(birthdate);
        professor.setDepartment(department);

        repository.add(professor);
        professors = repository.get();
        Assertions.assertFalse(professors.isEmpty());
    }

    @Test
    void testUpdate() throws SQLException {
        Professor professor;
        Date birthdate;
        boolean updated;

        professor = new Professor();
        professor.setName("Ivan");
        professor.setPhoneNumber("+79998884334");
        professor.setDegree("PhD in Computer Science");
        birthdate = new Date();
        birthdate.setTime(0);
        professor.setBirthday(birthdate);
        professor.setDepartment(department);

        repository.add(professor);
        professor.setName("Alex");
        updated = repository.update(professor);
        Assertions.assertTrue(updated);
    }

    @Test
    void testDelete() throws SQLException {
        Professor professor;
        Date birthdate;
        boolean deleted;

        professor = new Professor();
        professor.setName("Ivan");
        professor.setPhoneNumber("+79998884334");
        professor.setDegree("PhD in Computer Science");
        birthdate = new Date();
        birthdate.setTime(0);
        professor.setBirthday(birthdate);
        professor.setDepartment(department);

        repository.add(professor);
        deleted = repository.delete(professor.getId());
        Assertions.assertTrue(deleted);
    }

    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>(
            "postgres:16-alpine"
    );

    UniversityRepository universityRepository;
    DepartmentRepository departmentRepository;
    ProfessorRepository repository;
    University university;
    Department department;
}
