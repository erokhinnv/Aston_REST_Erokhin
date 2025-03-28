package dto;

import java.util.Collection;

@SuppressWarnings("java:S1104") // Поля намеренно делаем публичными
public class DepartmentFullDto extends DepartmentDto {
    public Collection<ProfessorDto> professors;
}
