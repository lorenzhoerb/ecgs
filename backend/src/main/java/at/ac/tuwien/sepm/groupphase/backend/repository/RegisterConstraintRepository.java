package at.ac.tuwien.sepm.groupphase.backend.repository;

import at.ac.tuwien.sepm.groupphase.backend.entity.RegisterConstraint;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface RegisterConstraintRepository extends CrudRepository<RegisterConstraint, Long> {

    List<RegisterConstraint> findAllByGradingGroup_Id(Long gradingGroupId);

    void deleteAllByGradingGroupId(Long gradingGroupId);

}
