package at.ac.tuwien.sepm.groupphase.backend.repository;


import at.ac.tuwien.sepm.groupphase.backend.entity.Competition;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;

@Repository
@Transactional
public interface CompetitionRepository extends CrudRepository<Competition, Long> {
    List<Competition> findAllByBeginOfCompetitionAfterAndEndOfCompetitionAfterAndBeginOfRegistrationAfterAndEndOfRegistrationAfterAndNameContainingIgnoreCaseAndIsPublicIsTrue(
        LocalDateTime begin,
        LocalDateTime end,
        LocalDateTime beginRegistration,
        LocalDateTime endRegistration,
        String name);
}
