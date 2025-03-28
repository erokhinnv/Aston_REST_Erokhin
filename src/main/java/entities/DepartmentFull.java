package entities;

import java.util.Collection;

public class DepartmentFull extends Department {

    public Collection<Professor> getProfessors() {
        return professors;
    }

    public void setProfessors(Collection<Professor> professors) {
        this.professors = professors;
    }

    private Collection<Professor> professors;
}
