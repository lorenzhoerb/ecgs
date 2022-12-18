package at.ac.tuwien.sepm.groupphase.backend.repository;


import at.ac.tuwien.sepm.groupphase.backend.entity.Competition;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import at.ac.tuwien.sepm.groupphase.backend.entity.Competition;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import javax.transaction.Transactional;

@Repository
@Transactional
public interface CompetitionRepository extends CrudRepository<Competition, Long> {
    //TODO SEARCH ADAPTION FOR DRAFT OR NEW METHOD
    List<Competition> findAllByBeginOfCompetitionAfterAndEndOfCompetitionAfterAndBeginOfRegistrationAfterAndEndOfRegistrationAfterAndNameContainingIgnoreCaseAndIsPublicIsTrue(
        LocalDateTime begin,
        LocalDateTime end,
        LocalDateTime beginRegistration,
        LocalDateTime endRegistration,
        String name);
}
