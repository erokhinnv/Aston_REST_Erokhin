package entities;

import java.util.Collection;

/**
 * Класс университета с коллекцией кафедр.
 * Без разделения на University и UniversityFull, при загрузке всех университетов
 * из базы данных, у каждого из них будут загружены все кафедры, а у каждой из кафедр - все её профессоры.
 * То есть, будет загружена вся база данных.
 * Во избежание такого сценария был создан этот класс, внутри которого хранится коллекция кафедр.
 * Коллекция кафедр будет загружена только при запросе отдельного взятого университета по id.
 * Аналогичным образом был добавлен класс DepartmentFull.
 */
public class UniversityFull extends University {

    public Collection<Department> getDepartments() {
        return departments;
    }

    public void setDepartments(Collection<Department> departments) {
        this.departments = departments;
    }

    private Collection<Department> departments;
}
