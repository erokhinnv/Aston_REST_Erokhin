package repositories;

import entities.Department;
import entities.University;
import org.junit.jupiter.api.*;
import org.testcontainers.containers.PostgreSQLContainer;
import utils.ConnectionUtils;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
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
            Connection connection;

            connection = ConnectionUtils.openConnection(
                    postgres.getJdbcUrl(),
                    postgres.getUsername(),
                    postgres.getPassword()
            );
            universityRepository = new UniversityRepository(connection);
            repository = new DepartmentRepository(connection);
            university = new University();
            university.setName("PSTU");
            university.setCity("Perm");
            universityRepository.add(university);
            connection.setAutoCommit(false);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void testGet() throws SQLException {
        Department first;
        Department second;
        Collection<Department> departments;

        first = new Department();
        first.setName("ITAS");
        first.setUniversityId(university.getId());
        second = new Department();
        second.setName("AT");
        second.setUniversityId(university.getId());
        repository.add(first);
        repository.add(second);
        departments = repository.get();
        Assertions.assertEquals(2, departments.size());
    }

    @Test
    void testGetById() throws SQLException {
        Department department;
        int id;
        ArrayList<Department> list;

        department = new Department();
        department.setUniversityId(university.getId());
        department.setName("ITAS");
        repository.add(department);
        list = new ArrayList<>();
        id = department.getId();
        list.add(repository.getById(id));
        Assertions.assertEquals(1, list.size());
    }

    @Test
    void testAdd() throws SQLException {
        Department department;
        Collection<Department> departments;
        department = new Department();
        department.setName("ITAS");
        department.setUniversityId(university.getId());
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
        department.setUniversityId(university.getId());
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
        department.setUniversityId(university.getId());
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
