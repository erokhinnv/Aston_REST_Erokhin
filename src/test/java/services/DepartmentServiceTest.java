package services;

import entities.Department;
import entities.DepartmentFull;
import entities.UniversityFull;
import exceptions.ValidationException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import repositories.DepartmentRepository;
import repositories.UniversityRepository;

import java.sql.SQLException;
import java.util.ArrayList;

class DepartmentServiceTest {
    @Test
    void testGet() throws SQLException {
        ArrayList<Department> departments;
        UniversityRepository universityRepository;
        DepartmentRepository repository;
        DepartmentService service;

        departments = new ArrayList<>();
        departments.add(new Department());
        departments.add(new Department());
        universityRepository = Mockito.mock(UniversityRepository.class);
        repository = Mockito.mock(DepartmentRepository.class);
        Mockito.when(repository.get()).thenReturn(departments);
        service = new DepartmentService(repository, universityRepository);
        Assertions.assertIterableEquals(departments, service.get());
    }

    @Test
    void testErrorGet() throws SQLException {
        UniversityRepository universityRepository;
        DepartmentRepository repository;
        DepartmentService service;

        universityRepository = Mockito.mock(UniversityRepository.class);
        repository = Mockito.mock(DepartmentRepository.class);
        service = new DepartmentService(repository, universityRepository);
        Mockito.doThrow(SQLException.class).when(repository).get();
        Assertions.assertThrows(RuntimeException.class, service::get);
    }

    @Test
    void testGetById() throws SQLException {
        DepartmentFull department;
        UniversityFull university;
        UniversityRepository universityRepository;
        DepartmentRepository repository;
        DepartmentService service;

        university = new UniversityFull();
        university.setId(1);
        university.setName("PSTU");
        university.setCity("Perm");
        department = new DepartmentFull();
        department.setName("PSTU");
        department.setUniversity(university);
        department.setId(1);
        universityRepository = Mockito.mock(UniversityRepository.class);
        repository = Mockito.mock(DepartmentRepository.class);
        Mockito.doReturn(department).when(repository).getById(department.getId());
        service = new DepartmentService(repository, universityRepository);
        Assertions.assertNull(service.getById(department.getId() + 1));
        Assertions.assertEquals(department, service.getById(department.getId()));
    }

    @Test
    void testErrorGetById() throws SQLException {
        UniversityRepository universityRepository;
        DepartmentRepository repository;
        DepartmentService service;

        universityRepository = Mockito.mock(UniversityRepository.class);
        repository = Mockito.mock(DepartmentRepository.class);
        service = new DepartmentService(repository, universityRepository);
        Mockito.doThrow(SQLException.class).when(repository).getById(10);
        Assertions.assertThrows(RuntimeException.class, () -> {
            service.getById(10);
        });
    }

    @Test
    void testValidAdd() throws SQLException {
        UniversityRepository universityRepository;
        DepartmentRepository repository;
        DepartmentService service;
        DepartmentFull department;
        UniversityFull university;

        university = new UniversityFull();
        university.setId(5);
        university.setName("PSTU");
        university.setCity("Perm");
        department = new DepartmentFull();
        department.setName("ITAS");
        department.setUniversity(university);
        universityRepository = Mockito.mock(UniversityRepository.class);
        Mockito.doReturn(university).when(universityRepository).getById(department.getUniversity().getId());
        repository = Mockito.mock(DepartmentRepository.class);
        Mockito.doAnswer(invocation -> {
            Department departmentArg;

            departmentArg = invocation.getArgument(0);
            departmentArg.setId(1);
            return null;
        }).when(repository).add(department);
        service = new DepartmentService(repository, universityRepository);
        service.add(department);
        Assertions.assertEquals(1, department.getId());
    }

    @Test
    void testInvalidAdd() throws SQLException {
        UniversityRepository universityRepository;
        DepartmentRepository repository;
        DepartmentService service;
        DepartmentFull department;
        UniversityFull university;

        university = new UniversityFull();
        university.setId(2);
        university.setName("PSU");
        university.setCity("Perm");
        department = new DepartmentFull();
        department.setName("ITAS");
        universityRepository = Mockito.mock(UniversityRepository.class);
        repository = Mockito.mock(DepartmentRepository.class);
        service = new DepartmentService(repository, universityRepository);
        Assertions.assertThrows(ValidationException.class, () -> {
            service.add(department);
        });
        department.setName(null);
        department.setUniversity(university);
        Mockito.doReturn(university).when(universityRepository).getById(department.getUniversity().getId());
        Assertions.assertThrows(ValidationException.class, () -> {
            service.add(department);
        });
    }

    @Test
    void testErrorAdd() throws SQLException {
        UniversityRepository universityRepository;
        DepartmentRepository repository;
        DepartmentService service;
        DepartmentFull department;
        UniversityFull university;

        universityRepository = Mockito.mock(UniversityRepository.class);
        repository = Mockito.mock(DepartmentRepository.class);
        service = new DepartmentService(repository, universityRepository);
        Assertions.assertThrows(RuntimeException.class, () -> {
            service.add(null);
        });
        university = new UniversityFull();
        university.setId(1);
        university.setName("PSTU");
        university.setCity("Perm");
        department = new DepartmentFull();
        department.setUniversity(university);
        department.setName("ITAS");
        Mockito.doReturn(university).when(universityRepository).getById(department.getUniversity().getId());
        Mockito.doThrow(SQLException.class).when(repository).add(department);
        Assertions.assertThrows(RuntimeException.class, () -> {
            service.add(department);
        });
    }

    @Test
    void testValidUpdate() throws SQLException {
        UniversityRepository universityRepository;
        DepartmentRepository repository;
        DepartmentService service;
        DepartmentFull department;
        UniversityFull university;

        university = new UniversityFull();
        university.setId(1);
        university.setName("PSTU");
        university.setCity("Perm");
        department = new DepartmentFull();
        department.setName("ITAS");
        department.setUniversity(university);
        universityRepository = Mockito.mock(UniversityRepository.class);
        repository = Mockito.mock(DepartmentRepository.class);
        Mockito.doReturn(university).when(universityRepository).getById(department.getUniversity().getId());
        Mockito.doAnswer(invocation -> {
            Department departmentArg;

            departmentArg = invocation.getArgument(0);
            return departmentArg.getId() == 10;
        }).when(repository).update(department);
        service = new DepartmentService(repository, universityRepository);
        department.setId(10);
        Assertions.assertTrue(service.update(department));
        department.setId(11);
        Assertions.assertFalse(service.update(department));
    }

    @Test
    void testInvalidUpdate() throws SQLException {
        UniversityRepository universityRepository;
        DepartmentRepository repository;
        DepartmentService service;
        DepartmentFull department;
        UniversityFull university;

        university = new UniversityFull();
        university.setId(2);
        university.setName("PSU");
        university.setCity("Perm");
        department = new DepartmentFull();
        department.setName("IT");
        universityRepository = Mockito.mock(UniversityRepository.class);
        repository = Mockito.mock(DepartmentRepository.class);
        service = new DepartmentService(repository, universityRepository);
        Assertions.assertThrows(ValidationException.class, () -> {
            service.update(department);
        });
        department.setName(null);
        department.setUniversity(university);
        Mockito.doReturn(university).when(universityRepository).getById(department.getUniversity().getId());
        Assertions.assertThrows(ValidationException.class, () -> {
            service.update(department);
        });
    }

    @Test
    void testErrorUpdate() throws SQLException {
        UniversityRepository universityRepository;
        DepartmentRepository repository;
        DepartmentService service;
        Department department;
        UniversityFull university;

        universityRepository = Mockito.mock(UniversityRepository.class);
        repository = Mockito.mock(DepartmentRepository.class);
        service = new DepartmentService(repository, universityRepository);
        Assertions.assertThrows(RuntimeException.class, () -> {
            service.update(null);
        });
        university = new UniversityFull();
        university.setId(1);
        university.setName("PSTU");
        university.setCity("Perm");
        department = new Department();
        department.setUniversity(university);
        department.setName("ITAS");
        Mockito.doReturn(university).when(universityRepository).getById(department.getUniversity().getId());
        Mockito.doThrow(SQLException.class).when(repository).update(department);
        Assertions.assertThrows(RuntimeException.class, () -> {
            service.update(department);
        });
    }

    @Test
    void testDelete() throws SQLException {
        UniversityRepository universityRepository;
        DepartmentRepository repository;
        DepartmentService service;

        universityRepository = Mockito.mock(UniversityRepository.class);
        repository = Mockito.mock(DepartmentRepository.class);
        Mockito.doReturn(true).when(repository).delete(10);
        service = new DepartmentService(repository, universityRepository);
        Assertions.assertTrue(service.delete(10));
        Assertions.assertFalse(service.delete(11));
    }

    @Test
    void testErrorDelete() throws SQLException {
        UniversityRepository universityRepository;
        DepartmentRepository repository;
        DepartmentService service;

        universityRepository = Mockito.mock(UniversityRepository.class);
        repository = Mockito.mock(DepartmentRepository.class);
        service = new DepartmentService(repository, universityRepository);

        Mockito.doThrow(SQLException.class).when(repository).delete(10);
        Assertions.assertThrows(RuntimeException.class, () -> {
            service.delete(10);
        });
    }
}
