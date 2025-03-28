package dto;

import java.util.Collection;

@SuppressWarnings("java:S1104") // Поля намеренно делаем публичными
public class UniversityFullDto extends UniversityDto {
    public Collection<DepartmentDto> departments;
}
