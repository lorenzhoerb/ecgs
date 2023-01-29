package at.ac.tuwien.sepm.groupphase.backend.service;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.AdvanceCompetitionSearchDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.CompetitionDetailDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.CompetitionViewDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.CompetitionListDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.CompetitionSearchDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.GradingGroupWithRegisterToDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.PageableDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.ParticipantFilterDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.ParticipantRegDetailDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.ReportDownloadInclusionRuleOptionsDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.UserDetailDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.UserDetailFilterDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.SimpleFlagDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.UserDetailSetFlagDto;
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
     * @param filter filter for users
     * @return Users registered to competition
     */
    Page<UserDetailDto> getParticipants(Long id, UserDetailFilterDto filter);

    /**
     * Get the grading groups of a competition with their Participants.
     *
     * @param competitionId to get the grading groups from
     * @return the GradingGroups WithRegistrations
     */
    List<GradingGroupWithRegisterToDto> getCompetitionGradingGroupsWithParticipants(Long competitionId);


    /**
     * Searches for competitions by Search parameters.
     *
     * @param competitionSearchDto the search params
     * @return a list of CompetitionListDtos to display
     */
    List<CompetitionListDto> searchCompetitions(CompetitionSearchDto competitionSearchDto);

    /** Returns a list of participants of a competition with registration details.
     * Default value of page=0 and size=10.
     *
     * @param filter Filter with pagination
     * @return Paginated list of participants
     */
    Page<ParticipantRegDetailDto> getParticipantsRegistrationDetails(PageableDto<ParticipantFilterDto> filter);

    /**
     * Search non-draft competitions with search params. The params are optional
     * and can be left away to search for every competition (exluding drafts).
     * Default result size is 10 and page is 0.
     *
     * @param searchDto search parameters
     * @return Paginated result of competitions
     */
    Page<CompetitionListDto> searchCompetitionsAdvanced(AdvanceCompetitionSearchDto searchDto);

    /**
     * Get flags in use by club manager .
     *
     * @param id id of competition
     * @return flags managed by a club manager
     */
    List<SimpleFlagDto> getManagedFlags(Long id);

    /**
     * Add flags for participants.
     *
     * @param id id of competition
     * @param participants set flags for participants
     */
    void addFlagsForUsers(Long id, UserDetailSetFlagDto participants);

    /**
     * Remove flags for participants.
     *
     * @param id id of competition
     * @param participants set flags for participants
    */
    void removeFlagsForUsers(Long id, UserDetailSetFlagDto participants);


    ReportDownloadInclusionRuleOptionsDto getCurrentUserReportDownloadInclusionRuleOptions(Long competitionId);
}
