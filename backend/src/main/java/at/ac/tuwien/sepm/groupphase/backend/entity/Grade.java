package at.ac.tuwien.sepm.groupphase.backend.entity;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Column;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.CascadeType;

@Entity
public class Grade {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // @TODO: change name in the ER-Diagram as value is a keyword in h2
    @Column(nullable = false)
    private String grading;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(referencedColumnName = "id")
    private Judge judging;

    public Grade() {
    }

    public Grade(String grading, Judge judging) {
        this.grading = grading;
        this.judging = judging;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getGrading() {
        return grading;
    }

    public void setGrading(String grading) {
        this.grading = grading;
    }

    public Judge getJudging() {
        return judging;
    }

    public void setJudging(Judge judging) {
        this.judging = judging;
    }

    @Override
    public String toString() {
        return "Grade{"
            + "id=" + id
            + ", grading='" + grading + '\''
            + ", judging=" + judging
            + '}';
    }
}