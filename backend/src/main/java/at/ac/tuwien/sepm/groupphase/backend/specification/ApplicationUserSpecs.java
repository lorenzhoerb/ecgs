package at.ac.tuwien.sepm.groupphase.backend.specification;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.ParticipantFilterDto;
import at.ac.tuwien.sepm.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepm.groupphase.backend.entity.Competition;
import at.ac.tuwien.sepm.groupphase.backend.entity.GradingGroup;
import at.ac.tuwien.sepm.groupphase.backend.entity.RegisterTo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;
import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.List;

public class ApplicationUserSpecs {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    public static Specification<ApplicationUser> specs(ParticipantFilterDto criteria) {
        LOGGER.debug("specs({})", criteria);
        List<Specification<ApplicationUser>> listSpecs = new ArrayList<>();

        listSpecs.add(isRegisteredTo(criteria.getCompetitionId()));

        if (criteria.getFirstName() != null) {
            listSpecs.add(hasFirstName(criteria.getFirstName()));
        }

        if (criteria.getLastName() != null) {
            listSpecs.add(hasLastName(criteria.getLastName()));
        }

        if (criteria.getGender() != null) {
            listSpecs.add(hasGender(criteria.getGender()));
        }

        if (criteria.getAccepted() != null && criteria.getCompetitionId() != null) {
            listSpecs.add(isActive(criteria.getAccepted(), criteria.getCompetitionId()));
        }

        if (criteria.getGradingGroupId() != null && criteria.getCompetitionId() != null) {
            listSpecs.add(assignedToGradingGroup(criteria.getGradingGroupId()));
        }

        Specification<ApplicationUser> specs;
        specs = listSpecs.stream().reduce(Specification::and).orElse(null);

        return specs;
    }

    public static Specification<ApplicationUser> assignedToGradingGroup(Long gradingGroup) {
        LOGGER.debug("assignedToGradingGroup({})", gradingGroup);
        return (root, query, cb) -> {
            Join<ApplicationUser, RegisterTo> registerToJoin = root.join("registrations", JoinType.LEFT);
            Join<RegisterTo, GradingGroup> gradingGroupJoin = registerToJoin.join("gradingGroup", JoinType.LEFT);
            return cb.equal(gradingGroupJoin.get("id"), gradingGroup);
        };
    }

    public static Specification<ApplicationUser> isRegisteredTo(Long competitionId) {
        LOGGER.debug("isRegisteredTo({})", competitionId);
        return (root, query, cb) -> {
            Join<ApplicationUser, RegisterTo> registerToJoin = root.join("registrations", JoinType.LEFT);
            Join<RegisterTo, GradingGroup> gradingGroupJoin = registerToJoin.join("gradingGroup", JoinType.LEFT);
            Join<GradingGroup, Competition> competition = gradingGroupJoin.join("competition", JoinType.LEFT);
            return cb.equal(competition.get("id"), competitionId);
        };
    }

    public static Specification<ApplicationUser> hasFirstName(String firstName) {
        LOGGER.debug("hasFirstName({})", firstName);
        return (root, query, cb) -> cb.like(cb.lower(root.get("firstName")), "%" + firstName.toLowerCase() + "%");
    }

    public static Specification<ApplicationUser> hasLastName(String lastName) {
        LOGGER.debug("hastLastName({})", lastName);
        return (root, query, cb) -> cb.like(cb.lower(root.get("lastName")), "%" + lastName.toLowerCase() + "%");
    }

    public static Specification<ApplicationUser> hasGender(ApplicationUser.Gender gender) {
        LOGGER.debug("hasGender({})", gender);
        return (root, query, cb) -> cb.equal(root.get("gender"), gender);
    }

    public static Specification<ApplicationUser> isActive(boolean active, long competitionId) {
        LOGGER.debug("isActive({}, {})", active, competitionId);
        return (root, query, cb) -> {
            Join<ApplicationUser, RegisterTo> registerToJoin = root.join("registrations", JoinType.LEFT);
            Join<RegisterTo, GradingGroup> gradingGroupJoin = registerToJoin.join("gradingGroup", JoinType.LEFT);
            Join<GradingGroup, Competition> competitionJoin = gradingGroupJoin.join("competition", JoinType.LEFT);
            Predicate accepted = cb.equal(registerToJoin.get("accepted"), active);
            Predicate competitionIdEqual = cb.equal(competitionJoin.get("id"), competitionId);
            return cb.and(accepted, competitionIdEqual);
        };
    }

    private static Specification<ApplicationUser> competitionIdSpecs(Long competitionId) {
        LOGGER.debug("competitionIdSpecs({})", competitionId);
        return (root, query, cb) -> {
            Join<ApplicationUser, Competition> usersComp = root.join("competitions");
            return cb.equal(usersComp.get("id"), competitionId);
        };
    }
}
