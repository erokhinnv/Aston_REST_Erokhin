package dto;

import java.util.Optional;
import java.util.OptionalInt;

@java.lang.SuppressWarnings("java:S1104") // Поля намеренно делаем публичными
public class DepartmentUpdateDto {
    public OptionalInt universityId;
    public Optional<String> name;
}
