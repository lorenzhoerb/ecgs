package at.ac.tuwien.sepm.groupphase.backend.entity;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.ManyToOne;
import javax.persistence.CascadeType;
import java.util.Set;

@Entity
public class Judge {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(referencedColumnName = "id")
    private ApplicationUser participant;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(referencedColumnName = "id")
    private Competition competition;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "judging")
    private Set<Grade> grades;

    public Judge() {
    }

    public Judge(ApplicationUser participant, Competition competition) {
        this.participant = participant;
        this.competition = competition;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ApplicationUser getParticipant() {
        return participant;
    }

    public void setParticipant(ApplicationUser participant) {
        this.participant = participant;
    }

    public Competition getCompetition() {
        return competition;
    }

    public void setCompetition(Competition competition) {
        this.competition = competition;
    }

    public Set<Grade> getGrades() {
        return grades;
    }

    public void setGrades(Set<Grade> grades) {
        this.grades = grades;
    }

    @Override
    public String toString() {
        return "Judge{"
            + "id=" + id
            + ", participant=" + participant
            + ", competition=" + competition
            + ", grades=" + grades
            + '}';
    }
}
