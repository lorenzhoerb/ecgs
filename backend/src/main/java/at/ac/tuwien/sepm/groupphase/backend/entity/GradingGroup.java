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
}
