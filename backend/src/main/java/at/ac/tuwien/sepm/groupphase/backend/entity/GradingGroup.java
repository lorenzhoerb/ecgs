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
import javax.persistence.OneToOne;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Entity
public class GradingGroup implements Comparable<GradingGroup> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 4095)
    private String title;

    @OneToOne(cascade = CascadeType.MERGE, mappedBy = "gradingGroup")
    private Report report;

    @ManyToOne()
    @JoinColumn(name = "grading_system_id")
    private GradingSystem gradingSystem;

    @ManyToOne()
    @JoinColumn(name = "competition_id")
    private Competition competition;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "gradingGroup")
    private Set<RegisterTo> registrations;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "gradingGroup", fetch = FetchType.EAGER, orphanRemoval = true)
    private List<RegisterConstraint> registerConstraints = new ArrayList<>();

    public GradingGroup() {
    }

    public GradingGroup(String title) {
        this.title = title;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Report getReport() {
        return report;
    }

    public void setReport(Report report) {
        this.report = report;
    }

    public GradingSystem getGradingSystem() {
        return gradingSystem;
    }

    public void setGradingSystem(GradingSystem gradingSystem) {
        this.gradingSystem = gradingSystem;
    }

    public Competition getCompetitions() {
        return competition;
    }

    public void setCompetitions(Competition competition) {
        this.competition = competition;
    }

    public Set<RegisterTo> getRegistrations() {
        return registrations;
    }

    public void setRegistrations(Set<RegisterTo> registrations) {
        this.registrations = registrations;
    }

    public Competition getCompetition() {
        return competition;
    }

    public GradingGroup setCompetition(Competition competition) {
        this.competition = competition;
        return this;
    }

    public List<RegisterConstraint> getRegisterConstraints() {
        return registerConstraints;
    }

    public GradingGroup setRegisterConstraints(List<RegisterConstraint> constraints) {
        this.registerConstraints.clear();
        this.registerConstraints.addAll(constraints);
        return this;
    }

    @Override
    public String toString() {
        return "GradingGroup{"
            + "id=" + id
            // + ", title='" + title + '\''
            // + ", report=" + report
            // + ", gradingSystems=" + gradingSystem
            // + ", competitions=" + competition
            // + ", registrations=" + registrations
            + '}';
    }

    @Override
    public int compareTo(GradingGroup o) {
        return this.id.compareTo(o.id);
    }
}
