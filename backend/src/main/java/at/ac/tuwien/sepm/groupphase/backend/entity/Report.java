package at.ac.tuwien.sepm.groupphase.backend.entity;

import javax.persistence.*;

@Entity
public class Report {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable=false)
    private String created;

    @Column(nullable=false, columnDefinition = "TEXT")
    private String results;

    @OneToOne
    @JoinColumn(referencedColumnName = "id")
    private GradingGroup gradingGroup;
}
