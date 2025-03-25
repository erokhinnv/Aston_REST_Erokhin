package dto;

import java.util.Date;
import java.util.Optional;
import java.util.OptionalInt;

@java.lang.SuppressWarnings("java:S1104") // Поля намеренно делаем публичными
public class ProfessorUpdateDto {
    public OptionalInt departmentId;
    public Optional<String> name;
    public Optional<String> phoneNumber;
    public Optional<String> degree;
    public Optional<Date> birthday;
}
