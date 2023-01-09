package at.ac.tuwien.sepm.groupphase.backend.entity;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;

@Entity
public class Report {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String created;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String results;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(referencedColumnName = "id")
    private GradingGroup gradingGroup;

    public Report() {
    }

    public Report(String created, String results, GradingGroup gradingGroup) {
        this.created = created;
        this.results = results;
        this.gradingGroup = gradingGroup;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCreated() {
        return created;
    }

    public void setCreated(String created) {
        this.created = created;
    }

    public String getResults() {
        return results;
    }

    public void setResults(String results) {
        this.results = results;
    }

    public GradingGroup getGradingGroup() {
        return gradingGroup;
    }

    public void setGradingGroup(GradingGroup gradingGroup) {
        this.gradingGroup = gradingGroup;
    }

    @Override
    public String toString() {
        return "Report{"
            + "id=" + id
            + ", created='" + created + '\''
            + ", results='" + results + '\''
            + ", gradingGroup=" + gradingGroup
            + '}';
    }
}
