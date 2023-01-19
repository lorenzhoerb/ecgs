package at.ac.tuwien.sepm.groupphase.backend.repository;

import at.ac.tuwien.sepm.groupphase.backend.entity.Judge;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface JudgeRepository extends CrudRepository<Judge, Long> {

    Optional<Judge> findJudgeByJudgeIdAndCompetitionId(Long judgeId, Long competitionId);

}
