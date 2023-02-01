package at.ac.tuwien.sepm.groupphase.backend.endpoint;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.CompetitionDetailDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.CompetitionListDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.CompetitionSearchDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.AdvanceCompetitionSearchDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.CompetitionViewDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.ExcelReportDownloadResponseDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.ExcelReportGenerationRequestDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.GradingGroupWithRegisterToDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.ParticipantResultDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.PageableDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.ParticipantFilterDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.ParticipantManageDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.ParticipantRegDetailDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.ParticipantRegistrationDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.ResponseMultiParticipantRegistrationDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.UserDetailFilterDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.UserDetailSetFlagDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.SimpleFlagDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper.CompetitionMapper;
import at.ac.tuwien.sepm.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepm.groupphase.backend.service.CompetitionRegistrationService;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.CompetitionListDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.CompetitionSearchDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.AdvanceCompetitionSearchDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.CompetitionDetailDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.CompetitionListDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.CompetitionSearchDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.CompetitionViewDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.GradingGroupWithRegisterToDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.ParticipantResultDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.PageableDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.ParticipantFilterDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.ParticipantManageDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.ParticipantRegDetailDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.ParticipantRegistrationDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.ReportDownloadInclusionRuleOptionsDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.ReportIsDownloadableDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.ResponseMultiParticipantRegistrationDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.SimpleGradingGroupDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.UserDetailDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper.CompetitionMapper;
import at.ac.tuwien.sepm.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepm.groupphase.backend.exception.ConflictException;
import at.ac.tuwien.sepm.groupphase.backend.exception.UnauthorizedException;
import at.ac.tuwien.sepm.groupphase.backend.service.CompetitionRegistrationService;
import at.ac.tuwien.sepm.groupphase.backend.service.CompetitionService;
import at.ac.tuwien.sepm.groupphase.backend.service.GradeService;
import at.ac.tuwien.sepm.groupphase.backend.service.GradingGroupService;
import at.ac.tuwien.sepm.groupphase.backend.service.ReportFileService;
import at.ac.tuwien.sepm.groupphase.backend.service.ReportService;
import at.ac.tuwien.sepm.groupphase.backend.service.PictureService;
import at.ac.tuwien.sepm.groupphase.backend.util.SessionUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.parameters.P;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.security.PermitAll;
import java.lang.invoke.MethodHandles;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@RestController
@RequestMapping(value = CompetitionEndpoint.BASE_PATH)
public class CompetitionEndpoint {
    static final String BASE_PATH = "/api/v1/competitions";
    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private final CompetitionService competitionService;
    private final GradingGroupService gradingGroupService;
    private final CompetitionRegistrationService competitionRegistrationService;
    private final CompetitionMapper mapper;
    private final SessionUtils sessionUtils;
    private final GradeService gradeService;
    private final ReportService reportService;
    private final ReportFileService reportFileService;


    private final PictureService pictureService;

    @Autowired
    public CompetitionEndpoint(
        CompetitionService service,
        GradingGroupService gradingGroupService,
        CompetitionRegistrationService competitionRegistrationService,
        CompetitionMapper mapper,
        SessionUtils sessionUtils, GradeService gradeService, ReportService reportService,
        ReportFileService reportFileService, PictureService pictureService) {
        this.competitionService = service;
        this.gradingGroupService = gradingGroupService;
        this.competitionRegistrationService = competitionRegistrationService;
        this.mapper = mapper;
        this.sessionUtils = sessionUtils;
        this.gradeService = gradeService;
        this.reportService = reportService;
        this.reportFileService = reportFileService;
        this.pictureService = pictureService;
    }

    @PermitAll
    @GetMapping(value = "/{id}")
    @Operation(summary = "Get information about a specific competition", security = @SecurityRequirement(name = "apiKey"))
    public CompetitionViewDto find(@PathVariable Long id) {
        LOGGER.info("GET {}/find/{}", BASE_PATH, id);
        return competitionService.findOne(id);
    }

    @Secured("ROLE_TOURNAMENT_MANAGER")
    @GetMapping(value = "/{id}/detail")
    @Operation(summary = "Get detailed information about a specific competition", security = @SecurityRequirement(name = "apiKey"))
    public CompetitionDetailDto findDetail(@PathVariable Long id) {
        LOGGER.info("GET {}/findDetail/{}", BASE_PATH, id);
        return competitionService.findOneDetail(id);
    }

    @GetMapping
    @PermitAll
    @Operation(summary = "Searches all competitions which are not drafts")
    public Page<CompetitionListDto> searchCompetitions(AdvanceCompetitionSearchDto searchQuery) {
        LOGGER.info("GET {}", BASE_PATH);
        LOGGER.trace("searchCompetitions({})", searchQuery);
        return competitionService
            .searchCompetitionsAdvanced(Objects.requireNonNullElseGet(searchQuery, AdvanceCompetitionSearchDto::new));
    }

    @Secured("ROLE_TOURNAMENT_MANAGER")
    @PostMapping
    @Operation(summary = "Create a competition", security = @SecurityRequirement(name = "apiKey"))
    @ResponseStatus(code = HttpStatus.CREATED)
    public CompetitionDetailDto create(@RequestBody CompetitionDetailDto competitionDetailDto) {
        LOGGER.info("POST {}", BASE_PATH);
        LOGGER.trace("POST {}, value: {}", BASE_PATH, competitionDetailDto);
        return competitionService.create(competitionDetailDto);
    }


    @PermitAll
    @GetMapping("/search")
    @Operation(summary = "Search a competition list")
    @ResponseStatus(code = HttpStatus.OK)
    public List<CompetitionListDto> search(@RequestParam("name") String name,
                                           @RequestParam("begin") String begin, @RequestParam("end") String end,
                                           @RequestParam(value = "beginRegistration") String beginRegistration,
                                           @RequestParam(value = "endRegistration") String endRegistration) {
        LOGGER.info("GET {}", BASE_PATH);
        CompetitionSearchDto competitionSearchDto = new CompetitionSearchDto(
            name,
            LocalDateTime.parse(begin),
            LocalDateTime.parse(end),
            LocalDateTime.parse(beginRegistration),
            LocalDateTime.parse(endRegistration));
        return competitionService.searchCompetitions(competitionSearchDto);
    }


    @Secured({"ROLE_PARTICIPANT", "ROLE_CLUB_MANAGER", "ROLE_TOURNAMENT_MANAGER"})
    @GetMapping(value = "/{id}/participants")
    @Operation(summary = "Get participants of competition", security = @SecurityRequirement(name = "apiKey"))
    public Page<UserDetailDto> getParticipants(@PathVariable Long id, UserDetailFilterDto filter) {
        LOGGER.info("GET {}", BASE_PATH);
        return competitionService.getParticipants(id, filter);
    }

    @Secured({"ROLE_TOURNAMENT_MANAGER"})
    @GetMapping(value = "/{competitionId}/participants/registrations")
    @Operation(summary = "Get participants of competition with details about registration.", security = @SecurityRequirement(name = "apiKey"))
    public Page<ParticipantRegDetailDto> getParticipantsRegistrationDetails(
        @PathVariable Long competitionId,
        @RequestParam(required = false) Boolean accepted,
        @RequestParam(required = false) String firstName,
        @RequestParam(required = false) String lastName,
        @RequestParam(required = false) ApplicationUser.Gender gender,
        @RequestParam(required = false) Long gradingGroup,
        @RequestParam(required = false) Long flagId,
        @RequestParam(required = false, defaultValue = "15") Integer pageSize,
        @RequestParam(required = false, defaultValue = "0") Integer page
    ) {
        LOGGER.info("GET {}/{}/participants/registrations", BASE_PATH, competitionId);
        return competitionService
            .getParticipantsRegistrationDetails(new PageableDto<>(
                new ParticipantFilterDto(
                    competitionId,
                    accepted,
                    firstName,
                    lastName,
                    gender,
                    gradingGroup,
                flagId
                    ),
                page,
                pageSize
            ));
    }

    @Secured({"ROLE_CLUB_MANAGER", "ROLE_TOURNAMENT_MANAGER"})
    @PostMapping(value = "/{id}/participants")
    @Operation(summary = "Register participants to a competition", security = @SecurityRequirement(name = "apiKey"))
    @ResponseStatus(code = HttpStatus.CREATED)
    public ResponseMultiParticipantRegistrationDto registerParticipants(
        @PathVariable Long id,
        @RequestBody List<ParticipantRegistrationDto> registrations) {
        LOGGER.info("POST {}/{}/participants", BASE_PATH, id);
        return competitionRegistrationService.registerParticipants(id, registrations);
    }

    @Secured({"ROLE_TOURNAMENT_MANAGER"})
    @PatchMapping(value = "/{id}/participants")
    @Operation(summary = "Updates participants", security = @SecurityRequirement(name = "apiKey"))
    @ResponseStatus(code = HttpStatus.OK)
    public List<ParticipantManageDto> updateRegisteredParticipants(
        @PathVariable Long id,
        @RequestBody List<ParticipantManageDto> participants
    ) {
        LOGGER.info("PATCH {}/{}/participants", BASE_PATH, id);
        return competitionRegistrationService.updateParticipants(id, participants);
    }

    @Secured({"ROLE_PARTICIPANT", "ROLE_CLUB_MANAGER", "ROLE_TOURNAMENT_MANAGER"})
    @GetMapping(value = "/{id}/groups")
    @Operation(summary = "Get groups of a competition", security = @SecurityRequirement(name = "apiKey"))
    public List<SimpleGradingGroupDto> getGroups(@PathVariable Long id) {
        LOGGER.info("GET {}/{}/groups", BASE_PATH, id);
        return gradingGroupService.getAllByCompetition(id);
    }

    @Secured({"ROLE_PARTICIPANT", "ROLE_CLUB_MANAGER", "ROLE_TOURNAMENT_MANAGER"})
    @GetMapping(value = "/{id}/group-registrations")
    @Operation(summary = "Get groups of a competition containing the registrations", security = @SecurityRequirement(name = "apiKey"))
    public List<GradingGroupWithRegisterToDto> getGroupsWithRegistrations(@PathVariable Long id) {
        LOGGER.info("GET {}/{}/group-registrations", BASE_PATH, id);
        return competitionService.getCompetitionGradingGroupsWithParticipants(id);
    }

    @Secured({"ROLE_PARTICIPANT", "ROLE_CLUB_MANAGER", "ROLE_TOURNAMENT_MANAGER"})
    @PostMapping(value = "/{id}/group-registrations")
    @Operation(summary = "Saves the judgings for a competition", security = @SecurityRequirement(name = "apiKey"))
    public void updateCompetitionResults(@PathVariable Long id, @RequestBody List<ParticipantResultDto> participantResultDtos) {
        LOGGER.info("POST {}/{}/group-registrations", BASE_PATH, id);
        // competitionService.updateCompetitionResults(participantResultDtos, id);
    }

    @Secured({"ROLE_TOURNAMENT_MANAGER"})
    @PostMapping("/{competitionId}/report")
    @Operation(summary = "Generate report for grading groups of a competition", security = @SecurityRequirement(name = "apiKey"))
    public void calculateResultsOfCompetition(
        @PathVariable Long competitionId
    ) {
        LOGGER.info("POST {}/{}/report", BASE_PATH, competitionId);
        reportService.calculateResultsOfCompetition(competitionId);
    }

    @Secured({"ROLE_PARTICIPANT", "ROLE_CLUB_MANAGER", "ROLE_TOURNAMENT_MANAGER"})
    @PostMapping("/{competitionId}/report/download")
    @Operation(summary = "Download report for grading groups of a competition", security = @SecurityRequirement(name = "apiKey"))
    public ExcelReportDownloadResponseDto downloadExcelReport(
        @PathVariable Long competitionId,
        @RequestBody ExcelReportGenerationRequestDto requestDto
    ) {
        LOGGER.info("POST {}/{}/report/download {}", BASE_PATH, competitionId, requestDto);
        requestDto.setCompetitionId(competitionId);
        return reportFileService.downloadExcelReport(requestDto);
    }

    @Secured({"ROLE_PARTICIPANT", "ROLE_CLUB_MANAGER", "ROLE_TOURNAMENT_MANAGER"})
    @GetMapping("/{competitionId}/report/download-inclusion-rule-options")
    @Operation(summary = "Generate report for grading groups of a competition", security = @SecurityRequirement(name = "apiKey"))
    public ReportDownloadInclusionRuleOptionsDto getCurrentUserReportDownloadInclusionRuleOptions(
        @PathVariable Long competitionId
    ) {
        LOGGER.info("GET {}/{}/report/download-inclusion-rule-options {}", BASE_PATH, competitionId);
        return competitionService.getCurrentUserReportDownloadInclusionRuleOptions(competitionId);
    }

    @Secured({"ROLE_PARTICIPANT", "ROLE_CLUB_MANAGER", "ROLE_TOURNAMENT_MANAGER"})
    @GetMapping("/{competitionId}/report/downloadable")
    @Operation(summary = "Generate report for grading groups of a competition", security = @SecurityRequirement(name = "apiKey"))
    public ReportIsDownloadableDto checkIfReportsAreDownloadable(
        @PathVariable Long competitionId
    ) {
        LOGGER.info("GET {}/{}/report/downloadable {}", BASE_PATH, competitionId);
        return gradingGroupService.checkAllGradingGroupsHaveReports(competitionId);
    }

    @Secured({"ROLE_TOURNAMENT_MANAGER"})
    @PostMapping("/{id}/picture")
    @Operation(summary = "Changes the picture of the competition with the given id", security = @SecurityRequirement(name = "apiKey"))
    public String updateCompetitionPicture(@PathVariable("id") Long id, @RequestPart(name = "file") MultipartFile file) {
        LOGGER.info("POST {}/{}/picture/?multiPartFile={}", BASE_PATH, id, file);
        pictureService.saveCompetitionImage(id, file);
        return "Picture successfully stored";
    }

    @Secured({"ROLE_TOURNAMENT_MANAGER"})
    @PostMapping("{id}/members/flags")
    @Operation(summary = "add flags for given members of this competition", security = @SecurityRequirement(name = "apiKey"))
    public ResponseEntity<Void> addMemberFlags(
        @PathVariable Long id,
        @RequestBody UserDetailSetFlagDto members) {
        LOGGER.info("POST {}/{}/members/flags", BASE_PATH, id);
        competitionService.addFlagsForUsers(id, members);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Secured({"ROLE_TOURNAMENT_MANAGER"})
    @PatchMapping("{id}/members/flags")
    @Operation(summary = "remove flags for given members of this competition", security = @SecurityRequirement(name = "apiKey"))
    public ResponseEntity<Void> removeMemberFlags(
        @PathVariable Long id,
        @RequestBody UserDetailSetFlagDto members) {
        LOGGER.info("PATCH {}/{}/members/flags", BASE_PATH, id);
        competitionService.removeFlagsForUsers(id, members);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Secured({"ROLE_TOURNAMENT_MANAGER"})
    @GetMapping("{id}/my-flags")
    @Operation(summary = "get the flags for participants of this competition", security = @SecurityRequirement(name = "apiKey"))
    public List<SimpleFlagDto> getManagedFlags(
        @PathVariable Long id) {
        LOGGER.info("GET {}/{}/my-flags", BASE_PATH, id);
        return competitionService.getManagedFlags(id);
    }

    @Secured({"ROLE_PARTICIPANT", "ROLE_CLUB_MANAGER", "ROLE_TOURNAMENT_MANAGER"})
    @GetMapping(value = "/{id}/is-creator")
    @Operation(summary = "check if user is the creator of the competition", security = @SecurityRequirement(name = "apiKey"))
    public Boolean isCreator(@PathVariable Long id) {
        LOGGER.info("GET {}/{}/is-creator", BASE_PATH, id);
        return competitionService.isCreator(id);
    }
}
