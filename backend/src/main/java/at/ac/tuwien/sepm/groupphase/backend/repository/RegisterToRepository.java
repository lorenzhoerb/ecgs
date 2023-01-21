package at.ac.tuwien.sepm.groupphase.backend.repository;

import at.ac.tuwien.sepm.groupphase.backend.entity.RegisterTo;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RegisterToRepository extends CrudRepository<RegisterTo, Long> {
    Optional<RegisterTo> findByGradingGroupCompetitionIdAndParticipantId(Long competitionId, Long participantId);

    List<RegisterTo> findByGradingGroupCompetitionId(Long competitionId);
}
