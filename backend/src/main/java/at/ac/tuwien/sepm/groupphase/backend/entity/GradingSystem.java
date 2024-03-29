package at.ac.tuwien.sepm.groupphase.backend.entity;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
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

    @ManyToOne()
    @JoinColumn(name = "creator_id")
    private ApplicationUser creator;

    @Column(nullable = false)
    private Boolean isTemplate;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String formula;

    @OneToMany(cascade = {CascadeType.MERGE}, mappedBy = "gradingSystem", fetch = FetchType.EAGER)
    private Set<GradingGroup> gradingGroups;

    public GradingSystem() {
    }

    public GradingSystem(String name, String description, Boolean isPublic, Boolean isTemplate, String formula, Set<GradingGroup> gradingGroups) {
        this.name = name;
        this.description = description;
        this.isPublic = isPublic;
        this.isTemplate = isTemplate;
        this.formula = formula;
        this.gradingGroups = gradingGroups;
    }

    public GradingSystem(String name, String description, Boolean isPublic, Boolean isTemplate, String formula, Set<GradingGroup> gradingGroups, ApplicationUser creator) {
        this.name = name;
        this.description = description;
        this.isPublic = isPublic;
        this.isTemplate = isTemplate;
        this.formula = formula;
        this.gradingGroups = gradingGroups;
        this.creator = creator;
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

    public ApplicationUser getCreator() {
        return creator;
    }

    public void setCreator(ApplicationUser creator) {
        this.creator = creator;
    }

    public Set<GradingGroup> getGradingGroups() {
        return gradingGroups;
    }

    public void setGradingGroups(Set<GradingGroup> gradingGroups) {
        this.gradingGroups = gradingGroups;
    }

    public Boolean getPublic() {
        return isPublic;
    }

    public Boolean getTemplate() {
        return isTemplate;
    }

    public void setTemplate(Boolean template) {
        isTemplate = template;
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
