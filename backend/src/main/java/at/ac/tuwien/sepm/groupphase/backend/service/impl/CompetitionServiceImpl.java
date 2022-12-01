package at.ac.tuwien.sepm.groupphase.backend.service.impl;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.CompetitionDetailDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper.CompetitionMapper;
import at.ac.tuwien.sepm.groupphase.backend.entity.Competition;
import at.ac.tuwien.sepm.groupphase.backend.repository.CompetitionRepository;
import at.ac.tuwien.sepm.groupphase.backend.service.CompetitionService;
import at.ac.tuwien.sepm.groupphase.backend.validation.CompetitionValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.lang.invoke.MethodHandles;

@Service
public class CompetitionServiceImpl implements CompetitionService {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private final CompetitionRepository competitionRepository;
    private final CompetitionMapper competitionMapper;
    private final CompetitionValidator competitionValidator;

    public CompetitionServiceImpl(CompetitionRepository competitionRepository, CompetitionMapper competitionMapper, CompetitionValidator competitionValidator) {
        this.competitionRepository = competitionRepository;
        this.competitionMapper = competitionMapper;
        this.competitionValidator = competitionValidator;
    }

    @Override
    public CompetitionDetailDto create(CompetitionDetailDto competitionDetailDto) {
        LOGGER.debug("Create competition {}", competitionDetailDto);
        //ToDo: Check user
        competitionValidator.validate(competitionDetailDto);
        Competition competition = competitionMapper
            .competitionDetailDtoToCompetition(competitionDetailDto);
        return competitionMapper
            .competitionToCompetitionDetailDto(competitionRepository.save(competition));
    }
}
