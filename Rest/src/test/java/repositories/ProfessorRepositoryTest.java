package repositories;

import entities.Department;
import entities.Professor;
import entities.University;
import org.junit.jupiter.api.*;
import org.testcontainers.containers.PostgreSQLContainer;
import utils.ConnectionUtils;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
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
            Connection connection = ConnectionUtils.openConnection(
                    postgres.getJdbcUrl(),
                    postgres.getUsername(),
                    postgres.getPassword()
            );
            universityRepository = new UniversityRepository(connection);
            departmentRepository = new DepartmentRepository(connection);
            repository = new ProfessorRepository(connection);
            university = new University();
            university.setName("PSTU");
            university.setCity("Perm");
            universityRepository.add(university);
            department = new Department();
            department.setName("ITAS");
            department.setUniversityId(university.getId());
            departmentRepository.add(department);
            connection.setAutoCommit(false);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
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
        first.setDepartmentId(department.getId());

        second = new Professor();
        second.setName("Petr");
        second.setPhoneNumber("+79824863265");
        second.setDegree("PhD in Technical Science");
        secondBirthdate = new Date();
        secondBirthdate.setTime(169344000);
        second.setBirthday(secondBirthdate);
        second.setDepartmentId(department.getId());
        repository.add(first);
        repository.add(second);
        professors = repository.get();
        Assertions.assertEquals(2, professors.size());
    }

    @Test
    void testGetById() throws SQLException {
        Professor professor;
        Date birthdate;
        int id;
        ArrayList<Professor> list;

        professor = new Professor();
        professor.setName("Ivan");
        professor.setPhoneNumber("+79998884334");
        professor.setDegree("PhD in Computer Science");
        birthdate = new Date();
        birthdate.setTime(0);
        professor.setBirthday(birthdate);
        professor.setDepartmentId(department.getId());
        repository.add(professor);
        list = new ArrayList<>();
        id = professor.getId();
        list.add(repository.getById(id));
        Assertions.assertEquals(1, list.size());
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
        professor.setDepartmentId(department.getId());

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
        professor.setDepartmentId(department.getId());

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
        professor.setDepartmentId(department.getId());

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
