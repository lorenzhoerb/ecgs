package at.ac.tuwien.sepm.groupphase.backend.entity;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Column;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

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

    @ManyToOne
    @JoinColumn(referencedColumnName = "id")
    private GradingGroup gradingGroup;

    public GradingSystem() {}
}
