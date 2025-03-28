package services;

import entities.Department;
import entities.DepartmentFull;
import entities.Professor;
import entities.University;
import exceptions.ValidationException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import repositories.DepartmentRepository;
import repositories.ProfessorRepository;
import repositories.UniversityRepository;

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
        Department department;
        University university;
        DepartmentRepository departmentRepository;
        ProfessorRepository repository;
        ProfessorService service;
        Date birthdate;

        university = new University();
        university.setId(1);
        university.setName("PSTU");
        university.setCity("Perm");
        department = new Department();
        department.setId(1);
        department.setName("ITAS");
        department.setUniversity(university);
        professor = new Professor();
        professor.setName("Ivan");
        professor.setPhoneNumber("+79998884334");
        professor.setDegree("PhD in Computer Science");
        birthdate = new Date();
        birthdate.setTime(0);
        professor.setBirthday(birthdate);
        professor.setDepartment(department);
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
        DepartmentFull department;
        University university;
        Date birthdate;

        university = new University();
        university.setId(1);
        university.setName("PSTU");
        university.setCity("Perm");
        department = new DepartmentFull();
        department.setId(1);
        department.setName("ITAS");
        department.setUniversity(university);
        professor = new Professor();
        professor.setName("Ivan");
        professor.setPhoneNumber("+79998884334");
        professor.setDegree("PhD in Computer Science");
        birthdate = new Date();
        birthdate.setTime(0);
        professor.setBirthday(birthdate);
        professor.setDepartment(department);
        departmentRepository = Mockito.mock(DepartmentRepository.class);
        Mockito.doReturn(department).when(departmentRepository).getById(professor.getDepartment().getId());
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
        DepartmentFull department;
        University university;
        Date birthdate;

        professor = new Professor();
        professor.setName("Ivan");
        departmentRepository = Mockito.mock(DepartmentRepository.class);
        repository = Mockito.mock(ProfessorRepository.class);
        service = new ProfessorService(repository, departmentRepository);
        Assertions.assertThrows(ValidationException.class, () -> {
            service.add(professor);
        });
        university = new University();
        university.setId(1);
        university.setName("PSTU");
        university.setCity("Perm");
        department = new DepartmentFull();
        department.setId(1);
        department.setName("ITAS");
        department.setUniversity(university);
        professor.setName(null);
        professor.setPhoneNumber("+79998884334");
        professor.setDegree("PhD in Computer Science");
        birthdate = new Date();
        birthdate.setTime(0);
        professor.setBirthday(birthdate);
        professor.setDepartment(department);
        Mockito.doReturn(department).when(departmentRepository).getById(professor.getDepartment().getId());
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
        DepartmentFull department;
        University university;
        Date birthdate;

        departmentRepository = Mockito.mock(DepartmentRepository.class);
        repository = Mockito.mock(ProfessorRepository.class);
        service = new ProfessorService(repository, departmentRepository);
        Assertions.assertThrows(RuntimeException.class, () -> {
            service.add(null);
        });
        university = new University();
        university.setId(1);
        university.setName("PSTU");
        university.setCity("Perm");
        department = new DepartmentFull();
        department.setId(1);
        department.setName("ITAS");
        department.setUniversity(university);
        professor = new Professor();
        professor.setName("Ivan");
        professor.setPhoneNumber("+79998884334");
        professor.setDegree("PhD in Computer Science");
        birthdate = new Date();
        birthdate.setTime(0);
        professor.setBirthday(birthdate);
        professor.setDepartment(department);
        Mockito.doReturn(department).when(departmentRepository).getById(professor.getDepartment().getId());
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
        DepartmentFull department;
        University university;
        Date birthdate;

        university = new University();
        university.setId(1);
        university.setName("PSTU");
        university.setCity("Perm");
        department = new DepartmentFull();
        department.setId(1);
        department.setName("ITAS");
        department.setUniversity(university);
        professor = new Professor();
        professor.setName("Ivan");
        professor.setPhoneNumber("+79998884334");
        professor.setDegree("PhD in Computer Science");
        birthdate = new Date();
        birthdate.setTime(0);
        professor.setBirthday(birthdate);
        professor.setDepartment(department);
        departmentRepository = Mockito.mock(DepartmentRepository.class);
        Mockito.doReturn(department).when(departmentRepository).getById(professor.getDepartment().getId());
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
        DepartmentFull department;
        University university;
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
        university = new University();
        university.setId(1);
        university.setName("PSTU");
        university.setCity("Perm");
        department = new DepartmentFull();
        department.setId(1);
        department.setName("ITAS");
        department.setUniversity(university);
        professor = new Professor();
        professor.setName(null);
        professor.setPhoneNumber("+79998884334");
        professor.setDegree("PhD in Computer Science");
        birthdate = new Date();
        birthdate.setTime(0);
        professor.setBirthday(birthdate);
        professor.setDepartment(department);
        Mockito.doReturn(department).when(departmentRepository).getById(professor.getDepartment().getId());
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
        DepartmentFull department;
        University university;
        Date birthdate;

        departmentRepository = Mockito.mock(DepartmentRepository.class);
        repository = Mockito.mock(ProfessorRepository.class);
        service = new ProfessorService(repository, departmentRepository);
        Assertions.assertThrows(RuntimeException.class, () -> {
            service.update(null);
        });
        university = new University();
        university.setId(1);
        university.setName("PSTU");
        university.setCity("Perm");
        department = new DepartmentFull();
        department.setId(1);
        department.setName("ITAS");
        department.setUniversity(university);
        professor = new Professor();
        professor.setName("Ivan");
        professor.setPhoneNumber("+79998884334");
        professor.setDegree("PhD in Computer Science");
        birthdate = new Date();
        birthdate.setTime(0);
        professor.setBirthday(birthdate);
        professor.setDepartment(department);
        Mockito.doReturn(department).when(departmentRepository).getById(professor.getDepartment().getId());
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
