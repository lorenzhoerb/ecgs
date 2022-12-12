package at.ac.tuwien.sepm.groupphase.backend.service;

import at.ac.tuwien.sepm.groupphase.backend.entity.Competition;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.CompetitionDetailDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.UserDetailDto;

import java.util.Set;

public interface CompetitionService {

    /**
     * Find a single competition entry by id.
     *
     * @param id the id of the competition entry
     * @return the message entry
     */
    Competition findOne(Long id);

    /**
     * Creates a competition.
     *
     * @param competitionDetailDto competitionDetailDto
     * @return created competition
     */
    CompetitionDetailDto create(CompetitionDetailDto competitionDetailDto);

    /**
     * Gets participants of competition.
     *
     * @param id id of the competition
     * @return list of registered competitions
     */
    Set<UserDetailDto> getParticipants(Long id);
}
