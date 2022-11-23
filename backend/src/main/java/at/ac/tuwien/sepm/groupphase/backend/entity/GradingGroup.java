package at.ac.tuwien.sepm.groupphase.backend.entity;

import javax.persistence.*;
import java.util.Set;

@Entity
public class GradingGroup {

    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;

    @Column(nullable=false, length=4095)
    private String title;

    @OneToOne(mappedBy = "gradingGroup")
    private Report report;

    @OneToMany(mappedBy = "gradingGroup")
    private Set<GradingSystem> gradingSystems;

    @ManyToMany(mappedBy = "competition")
    private Set<Competition> competitions;
}
