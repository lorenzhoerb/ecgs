package at.ac.tuwien.sepm.groupphase.backend.entity;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Column;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.CascadeType;
import javax.persistence.OneToMany;
import java.util.Set;

@Entity
public class GradingSystem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = true, length = 255)
    private String name;

    @Column(nullable = true, length = 4095)
    private String description;

    @Column(nullable = false)
    private Boolean isPublic;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String formula;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "gradingSystem", fetch = FetchType.EAGER)
    private Set<GradingGroup> gradingGroups;

    public GradingSystem() {
    }

    public GradingSystem(String name, String description, Boolean isPublic, String formula, Set<GradingGroup> gradingGroups) {
        this.name = name;
        this.description = description;
        this.isPublic = isPublic;
        this.formula = formula;
        this.gradingGroups = gradingGroups;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Boolean getPublic() {
        return isPublic;
    }

    @Override
    public String toString() {
        return "GradingSystem{"
            + "id=" + id
            + ", name='" + name + '\''
            + ", description='" + description + '\''
            + ", isPublic=" + isPublic
            + ", formula='" + formula + '\''
            + ", gradingGroup=" + gradingGroups
            + '}';
    }

    public void setPublic(Boolean isPublic) {
        this.isPublic = isPublic;
    }

    public String getFormula() {
        return formula;
    }

    public void setFormula(String formula) {
        this.formula = formula;
    }

    public Set<GradingGroup> getGradingGroup() {
        return gradingGroups;
    }

    public void setGradingGroup(Set<GradingGroup> gradingGroups) {
        this.gradingGroups = gradingGroups;
    }
}
