package services;

import entities.Department;
import entities.Professor;
import exceptions.ValidationException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import repositories.DepartmentRepository;
import repositories.ProfessorRepository;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;

class ProfessorServiceTest {
    @Test
    void testGet() throws SQLException {
        ArrayList<Professor> professors;
        DepartmentRepository departmentRepository;
        ProfessorRepository repository;
        ProfessorService service;

        professors = new ArrayList<>();
        professors.add(new Professor());
        professors.add(new Professor());
        departmentRepository = Mockito.mock(DepartmentRepository.class);
        repository = Mockito.mock(ProfessorRepository.class);
        Mockito.when(repository.get()).thenReturn(professors);
        service = new ProfessorService(repository, departmentRepository);
        Assertions.assertIterableEquals(professors, service.get());
    }

    @Test
    void testErrorGet() throws SQLException {
        DepartmentRepository departmentRepository;
        ProfessorRepository repository;
        ProfessorService service;

        departmentRepository = Mockito.mock(DepartmentRepository.class);
        repository = Mockito.mock(ProfessorRepository.class);
        service = new ProfessorService(repository, departmentRepository);
        Mockito.doThrow(SQLException.class).when(repository).get();
        Assertions.assertThrows(RuntimeException.class, service::get);
    }

    @Test
    void testGetById() throws SQLException {
        Professor professor;
        DepartmentRepository departmentRepository;
        ProfessorRepository repository;
        ProfessorService service;
        Date birthdate;

        professor = new Professor();
        professor.setName("Ivan");
        professor.setPhoneNumber("+79998884334");
        professor.setDegree("PhD in Computer Science");
        birthdate = new Date();
        birthdate.setTime(0);
        professor.setBirthday(birthdate);
        professor.setDepartmentId(1);
        departmentRepository = Mockito.mock(DepartmentRepository.class);
        repository = Mockito.mock(ProfessorRepository.class);
        Mockito.doReturn(professor).when(repository).getById(professor.getId());
        service = new ProfessorService(repository, departmentRepository);
        Assertions.assertNull(service.getById(professor.getId() + 1));
        Assertions.assertEquals(professor, service.getById(professor.getId()));
    }

    @Test
    void testErrorGetById() throws SQLException {
        DepartmentRepository departmentRepository;
        ProfessorRepository repository;
        ProfessorService service;

        departmentRepository = Mockito.mock(DepartmentRepository.class);
        repository = Mockito.mock(ProfessorRepository.class);
        service = new ProfessorService(repository, departmentRepository);
        Mockito.doThrow(SQLException.class).when(repository).getById(10);
        Assertions.assertThrows(RuntimeException.class, () -> {
            service.getById(10);
        });
    }

    @Test
    void testValidAdd() throws SQLException {
        DepartmentRepository departmentRepository;
        ProfessorRepository repository;
        ProfessorService service;
        Professor professor;
        Date birthdate;

        professor = new Professor();
        professor.setName("Ivan");
        professor.setPhoneNumber("+79998884334");
        professor.setDegree("PhD in Computer Science");
        birthdate = new Date();
        birthdate.setTime(0);
        professor.setBirthday(birthdate);
        professor.setDepartmentId(1);
        departmentRepository = Mockito.mock(DepartmentRepository.class);
        Mockito.doReturn(new Department()).when(departmentRepository).getById(professor.getDepartmentId());
        repository = Mockito.mock(ProfessorRepository.class);
        Mockito.doAnswer(invocation -> {
            Professor professorArg;

            professorArg = invocation.getArgument(0);
            professorArg.setId(1);
            return null;
        }).when(repository).add(professor);
        service = new ProfessorService(repository, departmentRepository);
        service.add(professor);
        Assertions.assertEquals(1, professor.getId());
    }

    @Test
    void testInvalidAdd() throws SQLException {
        DepartmentRepository departmentRepository;
        ProfessorRepository repository;
        ProfessorService service;
        Professor professor;
        Date birthdate;

        professor = new Professor();
        professor.setName("Ivan");
        departmentRepository = Mockito.mock(DepartmentRepository.class);
        repository = Mockito.mock(ProfessorRepository.class);
        service = new ProfessorService(repository, departmentRepository);
        Assertions.assertThrows(ValidationException.class, () -> {
            service.add(professor);
        });
        professor.setName(null);
        professor.setPhoneNumber("+79998884334");
        professor.setDegree("PhD in Computer Science");
        birthdate = new Date();
        birthdate.setTime(0);
        professor.setBirthday(birthdate);
        professor.setDepartmentId(1);
        Mockito.doReturn(new Department()).when(departmentRepository).getById(professor.getDepartmentId());
        Assertions.assertThrows(ValidationException.class, () -> {
            service.add(professor);
        });
    }

    @Test
    void testErrorAdd() throws SQLException {
        DepartmentRepository departmentRepository;
        ProfessorRepository repository;
        ProfessorService service;
        Professor professor;
        Date birthdate;

        departmentRepository = Mockito.mock(DepartmentRepository.class);
        repository = Mockito.mock(ProfessorRepository.class);
        service = new ProfessorService(repository, departmentRepository);
        Assertions.assertThrows(RuntimeException.class, () -> {
            service.add(null);
        });
        professor = new Professor();
        professor.setName("Ivan");
        professor.setPhoneNumber("+79998884334");
        professor.setDegree("PhD in Computer Science");
        birthdate = new Date();
        birthdate.setTime(0);
        professor.setBirthday(birthdate);
        professor.setDepartmentId(1);
        Mockito.doReturn(new Department()).when(departmentRepository).getById(professor.getDepartmentId());
        Mockito.doThrow(SQLException.class).when(repository).add(professor);
        Assertions.assertThrows(RuntimeException.class, () -> {
            service.add(professor);
        });
    }

    @Test
    void testValidUpdate() throws SQLException {
        DepartmentRepository departmentRepository;
        ProfessorRepository repository;
        ProfessorService service;
        Professor professor;
        Date birthdate;

        professor = new Professor();
        professor.setName("Ivan");
        professor.setPhoneNumber("+79998884334");
        professor.setDegree("PhD in Computer Science");
        birthdate = new Date();
        birthdate.setTime(0);
        professor.setBirthday(birthdate);
        professor.setDepartmentId(1);
        departmentRepository = Mockito.mock(DepartmentRepository.class);
        Mockito.doReturn(new Department()).when(departmentRepository).getById(professor.getDepartmentId());
        repository = Mockito.mock(ProfessorRepository.class);
        Mockito.doAnswer(invocation -> {
            Professor professorArg;

            professorArg = invocation.getArgument(0);
            professorArg.setId(1);
            return null;
        }).when(repository).update(professor);
        service = new ProfessorService(repository, departmentRepository);
        service.update(professor);
        Assertions.assertEquals(1, professor.getId());
    }

    @Test
    void testInvalidUpdate() throws SQLException {
        DepartmentRepository departmentRepository;
        ProfessorRepository repository;
        ProfessorService service;
        Professor professor;
        Date birthdate;

        professor = new Professor();
        professor.setName("Ivan");
        departmentRepository = Mockito.mock(DepartmentRepository.class);
        repository = Mockito.mock(ProfessorRepository.class);
        service = new ProfessorService(repository, departmentRepository);
        Professor finalProfessor = professor;
        Assertions.assertThrows(ValidationException.class, () -> {
            service.update(finalProfessor);
        });
        professor = new Professor();
        professor.setName(null);
        professor.setPhoneNumber("+79998884334");
        professor.setDegree("PhD in Computer Science");
        birthdate = new Date();
        birthdate.setTime(0);
        professor.setBirthday(birthdate);
        professor.setDepartmentId(1);
        Mockito.doReturn(new Department()).when(departmentRepository).getById(professor.getDepartmentId());
        Professor finalProfessor1 = professor;
        Assertions.assertThrows(ValidationException.class, () -> {
            service.update(finalProfessor1);
        });
    }

    @Test
    void testErrorUpdate() throws SQLException {
        DepartmentRepository departmentRepository;
        ProfessorRepository repository;
        ProfessorService service;
        Professor professor;
        Date birthdate;

        departmentRepository = Mockito.mock(DepartmentRepository.class);
        repository = Mockito.mock(ProfessorRepository.class);
        service = new ProfessorService(repository, departmentRepository);
        Assertions.assertThrows(RuntimeException.class, () -> {
            service.update(null);
        });
        professor = new Professor();
        professor.setName("Ivan");
        professor.setPhoneNumber("+79998884334");
        professor.setDegree("PhD in Computer Science");
        birthdate = new Date();
        birthdate.setTime(0);
        professor.setBirthday(birthdate);
        professor.setDepartmentId(1);
        Mockito.doReturn(new Department()).when(departmentRepository).getById(professor.getDepartmentId());
        Mockito.doThrow(SQLException.class).when(repository).update(professor);
        Assertions.assertThrows(RuntimeException.class, () -> {
            service.update(professor);
        });
    }

    @Test
    void testDelete() throws SQLException {
        DepartmentRepository departmentRepository;
        ProfessorRepository repository;
        ProfessorService service;

        departmentRepository = Mockito.mock(DepartmentRepository.class);
        repository = Mockito.mock(ProfessorRepository.class);
        Mockito.doReturn(true).when(repository).delete(10);
        service = new ProfessorService(repository, departmentRepository);
        Assertions.assertTrue(service.delete(10));
        Assertions.assertFalse(service.delete(11));
    }

    @Test
    void testErrorDelete() throws SQLException {
        DepartmentRepository departmentRepository;
        ProfessorRepository repository;
        ProfessorService service;

        departmentRepository = Mockito.mock(DepartmentRepository.class);
        repository = Mockito.mock(ProfessorRepository.class);
        service = new ProfessorService(repository, departmentRepository);

        Mockito.doThrow(SQLException.class).when(repository).delete(10);
        Assertions.assertThrows(RuntimeException.class, () -> {
            service.delete(10);
        });
    }
}
