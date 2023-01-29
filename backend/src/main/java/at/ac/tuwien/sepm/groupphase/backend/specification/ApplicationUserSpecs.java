package at.ac.tuwien.sepm.groupphase.backend.specification;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.ParticipantFilterDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.UserDetailFilterDto;
import at.ac.tuwien.sepm.groupphase.backend.entity.ManagedBy;
import at.ac.tuwien.sepm.groupphase.backend.entity.Flags;
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
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
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

        if (criteria.getFlagId() != null && criteria.getCompetitionId() != null) {
            listSpecs.add(hasRegisterFlag(criteria.getCompetitionId(), criteria.getFlagId()));
        }

        Specification<ApplicationUser> specs;
        specs = listSpecs.stream().reduce(Specification::and).orElse(null);

        return specs;
    }

    public static Specification<ApplicationUser> specs(Long id, UserDetailFilterDto criteria) {
        LOGGER.debug("specs({})", criteria);
        List<Specification<ApplicationUser>> listSpecs = new ArrayList<>();

        listSpecs.add(isRegisteredToAndAccepted(id));

        if (criteria.getFirstName() != null) {
            listSpecs.add(hasFirstName(criteria.getFirstName()));
        }

        if (criteria.getLastName() != null) {
            listSpecs.add(hasLastName(criteria.getLastName()));
        }

        if (criteria.getGender() != null) {
            listSpecs.add(hasGender(criteria.getGender()));
        }

        if (criteria.getDateOfBirth() != null) {
            listSpecs.add(dateIsBeforeOrEquals("dateOfBirth", criteria.getDateOfBirth()));
        }

        Specification<ApplicationUser> specs;
        specs = listSpecs.stream().reduce(Specification::and).orElse(null);

        return specs;
    }

    public static Specification<ApplicationUser> groupSpecs(Long id, UserDetailFilterDto criteria) {
        LOGGER.debug("groupSpecs({})", criteria);
        List<Specification<ApplicationUser>> listSpecs = new ArrayList<>();

        listSpecs.add(isRegisteredToGroup(id));

        if (criteria.getFirstName() != null) {
            listSpecs.add(hasFirstName(criteria.getFirstName()));
        }

        if (criteria.getLastName() != null) {
            listSpecs.add(hasLastName(criteria.getLastName()));
        }

        if (criteria.getGender() != null) {
            listSpecs.add(hasGender(criteria.getGender()));
        }

        if (criteria.getDateOfBirth() != null) {
            listSpecs.add(dateIsBeforeOrEquals("dateOfBirth", criteria.getDateOfBirth()));
        }

        Specification<ApplicationUser> specs;
        specs = listSpecs.stream().reduce(Specification::and).orElse(null);

        return specs;
    }

    public static Specification<ApplicationUser> specsForMembers(Long managerId, UserDetailFilterDto criteria) {
        LOGGER.debug("specs({})", criteria);
        List<Specification<ApplicationUser>> listSpecs = new ArrayList<>();

        listSpecs.add(isMember(managerId));

        if (criteria.getFirstName() != null) {
            listSpecs.add(hasFirstName(criteria.getFirstName()));
        }

        if (criteria.getLastName() != null) {
            listSpecs.add(hasLastName(criteria.getLastName()));
        }

        if (criteria.getGender() != null) {
            listSpecs.add(hasGender(criteria.getGender()));
        }

        if (criteria.getDateOfBirth() != null) {
            listSpecs.add(dateIsBeforeOrEquals("dateOfBirth", criteria.getDateOfBirth()));
        }

        if (criteria.getFlagId() != null) {
            listSpecs.add(hasMemberFlag(managerId, criteria.getFlagId()));
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

    public static Specification<ApplicationUser> isRegisteredToAndAccepted(Long competitionId) {
        LOGGER.debug("isRegisteredTo({})", competitionId);
        return (root, query, cb) -> {
            Join<ApplicationUser, RegisterTo> registerToJoin = root.join("registrations", JoinType.LEFT);
            Join<RegisterTo, GradingGroup> gradingGroupJoin = registerToJoin.join("gradingGroup", JoinType.LEFT);
            Join<GradingGroup, Competition> competition = gradingGroupJoin.join("competition", JoinType.LEFT);
            return cb.and(cb.equal(competition.get("id"), competitionId), cb.equal(registerToJoin.get("accepted"), true));
        };
    }

    public static Specification<ApplicationUser> isRegisteredToGroup(Long groupId) {
        LOGGER.debug("isRegisteredToGroup({})", groupId);
        return (root, query, cb) -> {
            Join<ApplicationUser, RegisterTo> registerToJoin = root.join("registrations", JoinType.LEFT);
            Join<RegisterTo, GradingGroup> gradingGroupJoin = registerToJoin.join("gradingGroup", JoinType.LEFT);
            return cb.and(cb.equal(gradingGroupJoin.get("id"), groupId), cb.equal(registerToJoin.get("accepted"), true));
        };
    }

    public static Specification<ApplicationUser> isMember(Long managerId) {
        LOGGER.debug("isMember({})", managerId);
        return (root, query, cb) -> {
            Join<ApplicationUser, ManagedBy> registerToJoin = root.join("managers", JoinType.LEFT);
            return cb.equal(registerToJoin.get("manager"), managerId);
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

    public static Specification<ApplicationUser> dateIsBeforeOrEquals(String key, Date date) {
        LOGGER.debug("dateIsBeforeOrEquals({}, {})", key, date);
        return (root, query, cb) -> cb.lessThanOrEqualTo(root.get(key), date);
    }

    public static Specification<ApplicationUser> hasMemberFlag(Long managerId, Long flagId) {
        LOGGER.debug("hasMemberFlag({}, {})", managerId, flagId);
        return (root, query, cb) -> {
            Join<ApplicationUser, ManagedBy> managedByJoin = root.join("managers", JoinType.LEFT);
            Join<ManagedBy, Flags> flagsJoin = managedByJoin.join("flags", JoinType.LEFT);
            return cb.and(cb.equal(managedByJoin.get("manager"), managerId), cb.equal(flagsJoin.get("id"), flagId));
        };
    }

    public static Specification<ApplicationUser> hasRegisterFlag(Long competitionId, Long flagId) {
        LOGGER.debug("hasRegisterFlag({}, {})", competitionId, flagId);
        return (root, query, cb) -> {
            Join<ApplicationUser, RegisterTo> registerToJoin = root.join("registrations", JoinType.LEFT);
            Join<RegisterTo, Flags> flagsJoin = registerToJoin.join("flags", JoinType.LEFT);
            return cb.and(cb.equal(registerToJoin.get("gradingGroup").get("competition"), competitionId), cb.equal(flagsJoin.get("id"), flagId));
        };
    }
}
