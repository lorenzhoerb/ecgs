package at.ac.tuwien.sepm.groupphase.backend.service;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.CompetitionDetailDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.CompetitionViewDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.CompetitionListDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.CompetitionSearchDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.GradeDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.GradingGroupWithRegisterToDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.ParticipantResultDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.PageableDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.ParticipantFilterDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.ParticipantRegDetailDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.UserDetailDto;
import org.springframework.data.domain.Page;

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

    /**
     * Get the grading groups of a competition with their Participants.
     *
     * @param competitionId to get the grading groups from
     * @return the GradingGroups WithRegistrations
     */
    Set<GradingGroupWithRegisterToDto> getCompetitionGradingGroupsWithParticipants(Long competitionId);


    /**
     * Searches for competitions by Search parameters.
     *
     * @param competitionSearchDto the search params
     * @return a list of CompetitionListDtos to display
     */
    List<CompetitionListDto> searchCompetitions(CompetitionSearchDto competitionSearchDto);

    //TODO: Remove
    //void updateCompetitionResults(List<ParticipantResultDto> results, Long competitionId);
    /** Returns a list of participants of a competition with registration details.
     * Default value of page=0 and size=10.
     *
     * @param filter Filter with pagination
     * @return Paginated list of participants
     */
    Page<ParticipantRegDetailDto> getParticipantsRegistrationDetails(PageableDto<ParticipantFilterDto> filter);
}
