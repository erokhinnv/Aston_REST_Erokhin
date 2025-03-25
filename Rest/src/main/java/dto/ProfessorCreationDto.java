package dto;

import java.util.Date;

@java.lang.SuppressWarnings("java:S1104") // Поля намеренно делаем публичными
public class ProfessorCreationDto {
    public int departmentId;
    public String name;
    public String phoneNumber;
    public String degree;
    public Date birthday;
}
