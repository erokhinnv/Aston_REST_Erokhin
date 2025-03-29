package repositories;

import entities.Department;
import entities.University;
import org.junit.jupiter.api.*;
import org.testcontainers.containers.PostgreSQLContainer;
import utils.DatabaseSettings;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collection;

class DepartmentRepositoryTest {

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
            UniversityRepository universityRepository;

            DatabaseSettings.URL = postgres.getJdbcUrl();
            DatabaseSettings.USERNAME = postgres.getUsername();
            DatabaseSettings.PASSWORD = postgres.getPassword();
            universityRepository = new UniversityRepository();
            repository = new DepartmentRepository();
            university = new University();
            university.setName("PSTU");
            university.setCity("Perm");
            universityRepository.add(university);
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
        Department first;
        Department second;
        Collection<Department> departments;

        first = new Department();
        first.setName("ITAS");
        first.setUniversity(university);
        second = new Department();
        second.setName("AT");
        second.setUniversity(university);
        repository.add(first);
        repository.add(second);
        departments = repository.get();
        Assertions.assertEquals(2, departments.size());
    }

    @Test
    void testGetById() throws SQLException {
        Department department, dbDepartment;
        int id;

        department = new Department();
        department.setUniversity(university);
        department.setName("ITAS");
        repository.add(department);
        id = department.getId();
        dbDepartment = repository.getById(id);
        Assertions.assertEquals(department.getId(), dbDepartment.getId());
        Assertions.assertEquals(department.getName(), dbDepartment.getName());
        Assertions.assertEquals(department.getUniversity().getId(), dbDepartment.getUniversity().getId());
    }

    @Test
    void testAdd() throws SQLException {
        Department department;
        Collection<Department> departments;
        department = new Department();
        department.setName("ITAS");
        department.setUniversity(university);
        repository.add(department);
        departments = repository.get();
        Assertions.assertFalse(departments.isEmpty());
    }

    @Test
    void testUpdate() throws SQLException {
        Department department;
        boolean updated;

        department = new Department();
        department.setName("ITAS");
        department.setUniversity(university);
        repository.add(department);
        department.setName("AT");
        updated = repository.update(department);
        Assertions.assertTrue(updated);
    }

    @Test
    void testDelete() throws SQLException {
        Department department;
        boolean deleted;

        department = new Department();
        department.setName("ITAS");
        department.setUniversity(university);
        repository.add(department);
        deleted = repository.delete(department.getId());
        Assertions.assertTrue(deleted);
    }

    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>(
            "postgres:16-alpine"
    );
    DepartmentRepository repository;
    University university;
}
