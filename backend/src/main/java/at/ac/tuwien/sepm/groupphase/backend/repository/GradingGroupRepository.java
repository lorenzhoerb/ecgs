package at.ac.tuwien.sepm.groupphase.backend.repository;

import at.ac.tuwien.sepm.groupphase.backend.entity.Competition;
import at.ac.tuwien.sepm.groupphase.backend.entity.GradingGroup;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@Repository
public interface GradingGroupRepository extends CrudRepository<GradingGroup, Long> {
    List<GradingGroup> findAllByCompetitionId(Long competitionId);

    Optional<GradingGroup> findFirstByCompetitionIdOrderByIdAsc(Long competitionId);

    Optional<GradingGroup> findByIdAndCompetitionId(Long groupId, Long competitionId);
}
