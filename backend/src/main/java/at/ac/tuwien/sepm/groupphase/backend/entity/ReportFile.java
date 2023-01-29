package at.ac.tuwien.sepm.groupphase.backend.entity;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.enums.ExcelReportGenerationRequestInclusionRule;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Setter
@Getter
public class ReportFile {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 511)
    private String name;

    @Column(nullable = false)
    private LocalDateTime generationDate;

    @Column(nullable = false)
    private LocalDateTime deleteAfter;

    @ManyToOne()
    @JoinColumn(referencedColumnName = "id")
    private ApplicationUser creator;

    @Column(nullable = false)
    private ExcelReportGenerationRequestInclusionRule inclusionRule;

    @ManyToOne()
    @JoinColumn(referencedColumnName = "id")
    private Competition competition;

    public ReportFile(String name, LocalDateTime generationDate, LocalDateTime deleteAfter, ApplicationUser creator, ExcelReportGenerationRequestInclusionRule inclusionRule, Competition competition) {
        this.name = name;
        this.generationDate = generationDate;
        this.deleteAfter = deleteAfter;
        this.creator = creator;
        this.inclusionRule = inclusionRule;
        this.competition = competition;
    }

    public ReportFile() {

    }
}
