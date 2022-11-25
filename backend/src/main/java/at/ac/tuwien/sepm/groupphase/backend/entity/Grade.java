package at.ac.tuwien.sepm.groupphase.backend.entity;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Column;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Entity
public class Grade {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // @TODO: change name in the ER-Diagram as value is a keyword in h2
    @Column(nullable = false)
    private String grading;

    @ManyToOne
    @JoinColumn(referencedColumnName = "id")
    private Judge judging;
}