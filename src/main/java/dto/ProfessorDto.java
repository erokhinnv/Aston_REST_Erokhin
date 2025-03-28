package dto;

import java.util.Date;

@SuppressWarnings("java:S1104") // Поля намеренно делаем публичными
public class ProfessorDto {
    public int id;
    public DepartmentDto department;
    public String name;
    public String phoneNumber;
    public String degree;
    public Date birthday;
}
