package at.ac.tuwien.sepm.groupphase.backend.service;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.CompetitionDetailDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.CompetitionViewDto;
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
     * @return the competition found
     */
    CompetitionViewDto findOne(Long id);

    /**
     * Find a single competition entry by id and retrieve detailed information only
     * meant for the creator of the competition.
     *
     * @param id the id of the competition entry
     * @return the competition found
     */
    CompetitionDetailDto findOneDetail(Long id);

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
