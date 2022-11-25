package at.ac.tuwien.sepm.groupphase.backend.entity;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Column;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import java.io.Serializable;

@Entity
public class RegisterTo implements Serializable {

    @Id
    @ManyToOne
    @JoinColumn(referencedColumnName = "id")
    private ApplicationUser participant;

    @Id
    @ManyToOne
    @JoinColumn(referencedColumnName = "id")
    private GradingGroup gradingGroup;

    @Column(nullable = false)
    private Boolean accepted;
}
