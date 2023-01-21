package at.ac.tuwien.sepm.groupphase.backend.repository;


import at.ac.tuwien.sepm.groupphase.backend.entity.Competition;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;


@Repository
@Transactional
public interface CompetitionRepository extends CrudRepository<Competition, Long>, JpaSpecificationExecutor<Competition> {
    List<Competition> findAllByBeginOfCompetitionAfterAndEndOfCompetitionAfterAndBeginOfRegistrationAfterAndEndOfRegistrationAfterAndNameContainingIgnoreCaseAndIsPublicIsTrue(
        LocalDateTime begin,
        LocalDateTime end,
        LocalDateTime beginRegistration,
        LocalDateTime endRegistration,
        String name);

    Optional<Competition> findByIdAndCreatorId(Long competitionId, Long creatorId);
}
