package at.ac.tuwien.sepm.groupphase.backend.repository;

import at.ac.tuwien.sepm.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepm.groupphase.backend.entity.GradingSystem;
import at.ac.tuwien.sepm.groupphase.backend.repository.projections.GradingSystemProjectIdAndNameAndIsPublicAndEditable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@Transactional
@Repository
public interface GradingSystemRepository extends CrudRepository<GradingSystem, Long> {
    Optional<GradingSystem> findFirstByNameAndCreatorAndIsTemplateIsTrue(
        String name, ApplicationUser creator);

    @Query(
        value = "select *"
            + "from GRADING_SYSTEM "
            + "where NOT (creator_id != ?1 AND is_public = false) " // Not yours and private
            + "AND id = ?2 "
            + "AND is_template = true "
            + "LIMIT 1",
        nativeQuery = true)
    Optional<GradingSystem> findTemplateByIdIfNotOthersPrivate(Long creatorId, Long gsId);

    @Query(
        value = "select *"
            + "from GRADING_SYSTEM "
            + "WHERE creator_id = ?1 "
            + "AND id = ?2 "
            + "AND is_template = true "
            + "LIMIT 1",
        nativeQuery = true)
    Optional<GradingSystem> findTemplateByIdIfBelongsTo(Long creatorId, Long gsId);


    @Query(
        value = "select id, name, is_public as public, (creator_id = ?1) as editable "
            + "from GRADING_SYSTEM "
            + "where is_template = true "
            + "AND NOT (creator_id != ?1 AND is_public = false)",
        nativeQuery = true)
    List<GradingSystemProjectIdAndNameAndIsPublicAndEditable> findIdsAndNamesAndIsPublicAndEditableOfAllEligibleToViewOrEditDrafts(Long creatorId);
}
