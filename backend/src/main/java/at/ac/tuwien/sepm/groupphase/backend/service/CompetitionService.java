package at.ac.tuwien.sepm.groupphase.backend.service;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.CompetitionDetailDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.CompetitionListDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.CompetitionSearchDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.UserDetailDto;
import at.ac.tuwien.sepm.groupphase.backend.entity.Competition;

import java.util.List;
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
     * Get participants for competition.
     *
     * @param id id of competition
     * @return Users registered to competition
     */
    Set<UserDetailDto> getParticipants(Long id);

    List<CompetitionListDto> searchCompetitions(CompetitionSearchDto compoCompetitionSearchDto);
}
