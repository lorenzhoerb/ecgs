package at.ac.tuwien.sepm.groupphase.backend.repository;

import at.ac.tuwien.sepm.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepm.groupphase.backend.repository.projections.ApplicationUserIdAndInitialsProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@Repository
@Transactional
public interface ApplicationUserRepository extends JpaRepository<ApplicationUser, Long>, JpaSpecificationExecutor<ApplicationUser> {
    Optional<ApplicationUser> findApplicationUserByUserEmail(String email);

    List<ApplicationUser> findApplicationUserByFirstNameStartingWithIgnoreCaseAndLastNameStartingWithIgnoreCase(
        String firstName, String lastName
    );

    @Query(value = "SELECT u.id as id, (u.first_name || ' ' || u.last_name || ' (' || TO_CHAR(date_of_birth, 'YYYY-MM-DD') || ')') as initials "
        + "from APPLICATION_USER u "
        + "WHERE u.id IN (?1)", nativeQuery = true)
    List<ApplicationUserIdAndInitialsProjection> findAllIdsAndInitialsById(List<Long> ids);

}
