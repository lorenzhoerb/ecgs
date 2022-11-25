package at.ac.tuwien.sepm.groupphase.backend.entity;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Column;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;

import java.util.Set;

@Entity
public class ManagedBy {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(referencedColumnName = "id")
    private ApplicationUser manager;

    @ManyToOne
    @JoinColumn(referencedColumnName = "id")
    private ApplicationUser member;

    @ManyToMany(mappedBy = "clubs")
    private Set<Flags> flags;

    @Column(nullable = false, length = 4095)
    private String teamName;

    public ManagedBy() {}

}
