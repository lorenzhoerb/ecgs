package at.ac.tuwien.sepm.groupphase.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import at.ac.tuwien.sepm.groupphase.backend.entity.Competition;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;

@Repository
@Transactional
public interface CompetitionRepository extends JpaRepository<Competition, Long> {
}
