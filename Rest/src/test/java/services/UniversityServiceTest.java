package services;

import entities.University;
import exceptions.ValidationException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import repositories.UniversityRepository;

import java.sql.SQLException;
import java.util.ArrayList;

class UniversityServiceTest {
    @Test
    void testGet() throws SQLException {
        ArrayList<University> universities;
        UniversityRepository repository;
        UniversityService service;

        universities = new ArrayList<>();
        universities.add(new University());
        universities.add(new University());
        repository = Mockito.mock(UniversityRepository.class);
        Mockito.when(repository.get()).thenReturn(universities);
        service = new UniversityService(repository);
        Assertions.assertIterableEquals(universities, service.get());
    }

    @Test
    void testErrorGet() throws SQLException {
        UniversityRepository repository;
        UniversityService service;

        repository = Mockito.mock(UniversityRepository.class);
        service = new UniversityService(repository);
        Mockito.doThrow(SQLException.class).when(repository).get();
        Assertions.assertThrows(RuntimeException.class, service::get);
    }

    @Test
    void testGetById() throws SQLException {
        University university;
        UniversityRepository repository;
        UniversityService service;

        university = new University();
        university.setName("PSTU");
        university.setCity("Perm");
        university.setId(1);
        repository = Mockito.mock(UniversityRepository.class);
        Mockito.doReturn(university).when(repository).getById(university.getId());
        service = new UniversityService(repository);
        Assertions.assertNull(service.getById(university.getId() + 1));
        Assertions.assertEquals(university, service.getById(university.getId()));
    }

    @Test
    void testErrorGetById() throws SQLException {
        UniversityRepository repository;
        UniversityService service;

        repository = Mockito.mock(UniversityRepository.class);
        service = new UniversityService(repository);
        Mockito.doThrow(SQLException.class).when(repository).getById(10);
        Assertions.assertThrows(RuntimeException.class, () -> {
            service.getById(10);
        });
    }

    @Test
    void testValidAdd() throws SQLException {
        UniversityRepository repository;
        UniversityService service;
        University university;

        university = new University();
        university.setName("PSTU");
        university.setCity("Perm");
        repository = Mockito.mock(UniversityRepository.class);
        Mockito.doAnswer(invocation -> {
            University universityArg;

            universityArg = invocation.getArgument(0);
            universityArg.setId(1);
            return null;
        }).when(repository).add(university);
        service = new UniversityService(repository);
        service.add(university);
        Assertions.assertEquals(1, university.getId());
    }

    @Test
    void testInvalidAdd() {
        UniversityRepository repository;
        UniversityService service;
        University university;

        university = new University();
        university.setCity("Perm");
        repository = Mockito.mock(UniversityRepository.class);
        service = new UniversityService(repository);
        Assertions.assertThrows(ValidationException.class, () -> {
           service.add(university);
        });
        university.setName("PSTU");
        university.setCity(null);
        Assertions.assertThrows(ValidationException.class, () -> {
            service.add(university);
        });
    }

    @Test
    void testErrorAdd() throws SQLException {
        UniversityRepository repository;
        UniversityService service;
        University university;

        repository = Mockito.mock(UniversityRepository.class);
        service = new UniversityService(repository);
        Assertions.assertThrows(RuntimeException.class, () -> {
            service.add(null);
        });
        university = new University();
        university.setCity("Perm");
        university.setName("PSTU");
        Mockito.doThrow(SQLException.class).when(repository).add(university);
        Assertions.assertThrows(RuntimeException.class, () -> {
            service.add(university);
        });
    }

    @Test
    void testValidUpdate() throws SQLException {
        UniversityRepository repository;
        UniversityService service;
        University university;

        university = new University();
        university.setName("PSTU");
        university.setCity("Perm");
        repository = Mockito.mock(UniversityRepository.class);
        Mockito.doAnswer(invocation -> {
            University universityArg;

            universityArg = invocation.getArgument(0);
            return universityArg.getId() == 10;
        }).when(repository).update(university);
        service = new UniversityService(repository);
        university.setId(10);
        Assertions.assertTrue(service.update(university));
        university.setId(11);
        Assertions.assertFalse(service.update(university));
    }

    @Test
    void testInvalidUpdate() {
        UniversityRepository repository;
        UniversityService service;
        University university;

        university = new University();
        university.setCity("Perm");
        repository = Mockito.mock(UniversityRepository.class);
        service = new UniversityService(repository);
        Assertions.assertThrows(ValidationException.class, () -> {
            service.update(university);
        });
        university.setName("PSTU");
        university.setCity(null);
        Assertions.assertThrows(ValidationException.class, () -> {
            service.update(university);
        });
    }

    @Test
    void testErrorUpdate() throws SQLException {
        UniversityRepository repository;
        UniversityService service;
        University university;

        repository = Mockito.mock(UniversityRepository.class);
        service = new UniversityService(repository);
        Assertions.assertThrows(RuntimeException.class, () -> {
            service.update(null);
        });
        university = new University();
        university.setCity("Perm");
        university.setName("PSTU");
        Mockito.doThrow(SQLException.class).when(repository).update(university);
        Assertions.assertThrows(RuntimeException.class, () -> {
            service.update(university);
        });
    }

    @Test
    void testDelete() throws SQLException {
        UniversityRepository repository;
        UniversityService service;

        repository = Mockito.mock(UniversityRepository.class);
        Mockito.doReturn(true).when(repository).delete(10);
        service = new UniversityService(repository);
        Assertions.assertTrue(service.delete(10));
        Assertions.assertFalse(service.delete(11));
    }

    @Test
    void testErrorDelete() throws SQLException {
        UniversityRepository repository;
        UniversityService service;

        repository = Mockito.mock(UniversityRepository.class);
        service = new UniversityService(repository);

        Mockito.doThrow(SQLException.class).when(repository).delete(10);
        Assertions.assertThrows(RuntimeException.class, () -> {
            service.delete(10);
        });
    }

}
