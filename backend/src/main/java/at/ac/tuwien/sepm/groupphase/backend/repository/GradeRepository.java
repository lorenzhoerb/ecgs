package at.ac.tuwien.sepm.groupphase.backend.repository;

import at.ac.tuwien.sepm.groupphase.backend.entity.Judge;
import at.ac.tuwien.sepm.groupphase.backend.entity.grade.Grade;
import at.ac.tuwien.sepm.groupphase.backend.entity.grade.GradePk;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GradeRepository extends CrudRepository<Grade, GradePk> {
    List<Grade> findAllByGradePkParticipantIdAndGradePkCompetitionIdAndGradePkGradingGroupIdAndGradePkStationId(Long participantId, Long competitionId, Long gradingGroupId, Long stationId);

    List<Grade> findAllByGradePkCompetitionIdAndGradePkGradingGroupIdAndGradePkStationId(Long competitionId, Long gradingGroupId, Long stationId);
}
