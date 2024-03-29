package at.ac.tuwien.sepm.groupphase.backend.repository;

import at.ac.tuwien.sepm.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepm.groupphase.backend.entity.ManagedBy;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ManagedByRepository extends JpaRepository<ManagedBy, Long> {
    Optional<ManagedBy> findByManagerAndMember(ApplicationUser manager, ApplicationUser member);

    List<ManagedBy> findAllByManagerIs(ApplicationUser manager);

    List<ManagedBy> findByManagerId(Long managerId);

    Optional<ManagedBy> findByManagerIdAndMemberId(Long managerId, Long memberId);
}
