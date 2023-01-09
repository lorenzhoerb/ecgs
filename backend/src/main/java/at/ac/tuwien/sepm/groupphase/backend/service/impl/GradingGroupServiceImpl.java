package at.ac.tuwien.sepm.groupphase.backend.service.impl;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.SimpleGradingGroupDto;
import at.ac.tuwien.sepm.groupphase.backend.entity.Competition;
import at.ac.tuwien.sepm.groupphase.backend.entity.GradingGroup;
import at.ac.tuwien.sepm.groupphase.backend.exception.NotFoundException;
import at.ac.tuwien.sepm.groupphase.backend.exception.ValidationListException;
import at.ac.tuwien.sepm.groupphase.backend.repository.CompetitionRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.GradingGroupRepository;
import at.ac.tuwien.sepm.groupphase.backend.service.GradingGroupService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.lang.invoke.MethodHandles;
import java.util.List;

@Service
public class GradingGroupServiceImpl implements GradingGroupService {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private final GradingGroupRepository gradingGroupRepository;
    private final CompetitionRepository competitionRepository;

    public GradingGroupServiceImpl(GradingGroupRepository gradingGroupRepository, CompetitionRepository competitionRepository) {
        this.gradingGroupRepository = gradingGroupRepository;
        this.competitionRepository = competitionRepository;
    }

    @Override
    public List<SimpleGradingGroupDto> getAllByCompetition(Long competitionId) {
        LOGGER.debug("getAllByCompetition {}", competitionId);
        if (competitionId == null) {
            throw new ValidationListException("Error while getting grading group", "No componentId given");
        }

        Competition competition = competitionRepository.findById(competitionId)
            .orElseThrow(() -> new NotFoundException("Unknown competition " + competitionId));

        List<GradingGroup> gradingGroup = gradingGroupRepository.findAllByCompetitionId(competitionId);
        return gradingGroup
            .stream()
            .map((g) -> new SimpleGradingGroupDto(g.getId(), g.getTitle()))
            .toList();
    }
}
