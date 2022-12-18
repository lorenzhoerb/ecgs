package at.ac.tuwien.sepm.groupphase.backend.repository;

import at.ac.tuwien.sepm.groupphase.backend.entity.ApplicationUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@Repository
@Transactional
public interface ApplicationUserRepository extends JpaRepository<ApplicationUser, Long> {
    Optional<ApplicationUser> findApplicationUserByUserEmail(String email);

    List<ApplicationUser>
        findApplicationUserByFirstNameStartingWithIgnoreCaseAndLastNameStartingWithIgnoreCase(
            String firstName, String lastName
    );

    //TODO Delete because it already exists
    void deleteAll();

}
