package at.ac.tuwien.sepm.groupphase.backend.repository;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.enums.ExcelReportGenerationRequestInclusionRule;
import at.ac.tuwien.sepm.groupphase.backend.entity.ReportFile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface ReportFileRepository extends JpaRepository<ReportFile, Long> {
    @Query(nativeQuery = true, value = "SELECT * FROM REPORT_FILE "
        + "WHERE (CREATOR_ID = ?1 OR INCLUSION_RULE = 2) "
        + "AND COMPETITION_ID = ?2 "
        + "AND INCLUSION_RULE = ?3 "
        + "LIMIT 1")
    Optional<ReportFile> findFirstByCreatorIdAndCompetitionId(
        Long creatorId, Long competitionId, Integer ruleValue);

    List<ReportFile> findAllByDeleteAfterBefore(LocalDateTime localDateTime);
}
