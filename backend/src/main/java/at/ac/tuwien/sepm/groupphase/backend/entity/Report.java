package at.ac.tuwien.sepm.groupphase.backend.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
public class Report {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDateTime created;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String results;

    @OneToOne(cascade = {CascadeType.MERGE})
    @JoinColumn(referencedColumnName = "id")
    private GradingGroup gradingGroup;

    public Report() {
    }

    public Report(LocalDateTime created, String results, GradingGroup gradingGroup) {
        this.created = created;
        this.results = results;
        this.gradingGroup = gradingGroup;
    }

    @Override
    public String toString() {
        return "Report{"
            + "id=" + id
            + ", created='" + created + '\''
            + ", results='" + results + '\''
            + ", gradingGroup=" + gradingGroup
            + '}';
    }
}
