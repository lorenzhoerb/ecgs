package at.ac.tuwien.sepm.groupphase.backend.entity;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Column;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.ManyToMany;

import java.util.Set;

@Entity
public class GradingGroup {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 4095)
    private String title;

    @OneToOne(mappedBy = "gradingGroup")
    private Report report;

    @OneToMany(mappedBy = "gradingGroup")
    private Set<GradingSystem> gradingSystems;

    @ManyToMany(mappedBy = "competition")
    private Set<Competition> competitions;

    @OneToMany(mappedBy = "gradingGroup")
    private Set<RegisterTo> registrations;

    public GradingGroup() {}

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

    public Set<GradingSystem> getGradingSystems() {
        return gradingSystems;
    }

    public void setGradingSystems(Set<GradingSystem> gradingSystems) {
        this.gradingSystems = gradingSystems;
    }

    public Set<Competition> getCompetitions() {
        return competitions;
    }

    public void setCompetitions(Set<Competition> competitions) {
        this.competitions = competitions;
    }

    public Set<RegisterTo> getRegistrations() {
        return registrations;
    }

    public void setRegistrations(Set<RegisterTo> registrations) {
        this.registrations = registrations;
    }

    @Override
    public String toString() {
        return "GradingGroup{"
            + "id=" + id
            + ", title='" + title + '\''
            + ", report=" + report
            + ", gradingSystems=" + gradingSystems
            + ", competitions=" + competitions
            + ", registrations=" + registrations
            + '}';
    }
}
