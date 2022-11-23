package at.ac.tuwien.sepm.groupphase.backend.entity;

import javax.persistence.*;

@Entity
public class GradingSystem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = true, length=255)
    private String name;

    @Column(nullable=true, length=4095)
    private String description;

    @Column(nullable = false)
    private Boolean is_public;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String formula;

    @ManyToOne
    @JoinColumn(nullable = false)
    private GradingGroup gradingGroup;

    public GradingSystem() {}
}
