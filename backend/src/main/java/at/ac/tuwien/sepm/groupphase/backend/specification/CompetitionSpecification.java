package at.ac.tuwien.sepm.groupphase.backend.specification;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.AdvanceCompetitionSearchDto;
import at.ac.tuwien.sepm.groupphase.backend.entity.Competition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.jpa.domain.Specification;

import java.lang.invoke.MethodHandles;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.springframework.data.jpa.domain.Specification.not;

public class CompetitionSpecification {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    public static Specification<Competition> getSpecs(AdvanceCompetitionSearchDto searchDto) {
        LOGGER.debug("getSpecs({})", searchDto);
        if (searchDto == null) {
            return null;
        }

        List<Specification<Competition>> specs = new ArrayList<>();

        if (searchDto.getBeginOfCompetitionAfter() != null) {
            specs.add(CompetitionSpecification
                .dateIsAfterOrEquals("beginOfCompetition", searchDto.getBeginOfCompetitionAfter()));
        }

        if (searchDto.getBeginOfCompetitionBefore() != null) {
            specs.add(CompetitionSpecification
                .dateIsBeforeOrEquals("beginOfCompetition", searchDto.getBeginOfCompetitionBefore()));
        }

        if (searchDto.getEndOfCompetitionAfter() != null) {
            specs.add(CompetitionSpecification
                .dateIsAfterOrEquals("endOfCompetition", searchDto.getEndOfCompetitionAfter()));
        }

        if (searchDto.getEndOfCompetitionBefore() != null) {
            specs.add(CompetitionSpecification
                .dateIsBeforeOrEquals("endOfCompetition", searchDto.getBeginOfCompetitionBefore()));
        }

        if (searchDto.getBeginOfRegistrationAfter() != null) {
            specs.add(CompetitionSpecification
                .dateIsAfterOrEquals("beginOfRegistration", searchDto.getBeginOfRegistrationAfter()));
        }

        if (searchDto.getBeginOfRegistrationBefore() != null) {
            specs.add(CompetitionSpecification
                .dateIsBeforeOrEquals("beginOfRegistration", searchDto.getBeginOfRegistrationBefore()));
        }

        if (searchDto.getEndOfRegistrationAfter() != null) {
            specs.add(CompetitionSpecification
                .dateIsAfterOrEquals("endOfRegistration", searchDto.getEndOfRegistrationAfter()));
        }

        if (searchDto.getEndOfRegistrationBefore() != null) {
            specs.add(CompetitionSpecification
                .dateIsBeforeOrEquals("endOfRegistration", searchDto.getEndOfRegistrationBefore()));
        }

        if (searchDto.getIsPublic() != null) {
            specs.add(CompetitionSpecification
                .isPublic(searchDto.getIsPublic()));
        }

        if (searchDto.getIsRegistrationOpen() != null) {
            specs.add(CompetitionSpecification
                .registrationOpen(searchDto.getIsRegistrationOpen()));
        }

        if (searchDto.getName() != null) {
            specs.add(CompetitionSpecification
                .nameLike(searchDto.getName()));
        }

        if (specs.isEmpty()) {
            return null;
        }

        if (specs.size() == 1) {
            return specs.get(0);
        }

        Specification<Competition> spec = specs.get(0);

        for (int i = 1; i < specs.size(); i++) {
            spec = spec.and(specs.get(i));
        }
        return spec;
    }

    public static Specification<Competition> dateIsBeforeOrEquals(String key, LocalDateTime date) {
        LOGGER.debug("dateIsBeforeOrEquals({}, {})", key, date);
        return (root, query, cb) -> cb.lessThanOrEqualTo(root.get(key), date);
    }

    public static Specification<Competition> dateIsAfterOrEquals(String key, LocalDateTime date) {
        LOGGER.debug("dateIsAfterOrEquals({}, {})", key, date);
        return (root, query, cb) -> cb.greaterThanOrEqualTo(root.get(key), date);
    }

    public static Specification<Competition> isPublic(boolean isPublic) {
        LOGGER.debug("isPublic({})", isPublic);
        return (root, query, cb) -> cb.equal(root.get("isPublic"), isPublic);
    }

    public static Specification<Competition> isDraft(boolean isDraft) {
        LOGGER.debug("isDraft({})", isDraft);
        return (root, query, cb) -> cb.equal(root.get("draft"), isDraft);
    }

    public static Specification<Competition> nameLike(String name) {
        LOGGER.debug("nameLike({})", name);
        return (root, query, cb) -> cb.like(cb.lower(root.get("name")), "%" + name.toLowerCase() + "%");
    }

    public static Specification<Competition> registrationOpen(boolean isRegistrationOpen) {
        LOGGER.debug("registrationOpen({})", isRegistrationOpen);
        LocalDateTime now = LocalDateTime.now();
        Specification<Competition> spec = dateIsAfterOrEquals("endOfRegistration", now)
            .and(dateIsBeforeOrEquals("beginOfRegistration", now));
        if (!isRegistrationOpen) {
            spec = not(spec);
        }
        return spec;
    }
}
