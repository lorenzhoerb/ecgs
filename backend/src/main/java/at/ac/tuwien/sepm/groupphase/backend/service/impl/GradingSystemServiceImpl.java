package at.ac.tuwien.sepm.groupphase.backend.service.impl;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.GradingSystemDetailDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper.GradingSystemMapper;
import at.ac.tuwien.sepm.groupphase.backend.entity.GradingSystem;
import at.ac.tuwien.sepm.groupphase.backend.exception.ForbiddenException;
import at.ac.tuwien.sepm.groupphase.backend.repository.GradingSystemRepository;
import at.ac.tuwien.sepm.groupphase.backend.service.GradingSystemService;
import at.ac.tuwien.sepm.groupphase.backend.util.SessionUtils;
import at.ac.tuwien.sepm.groupphase.backend.validation.GradingSystemValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.lang.invoke.MethodHandles;

@Service
@Transactional
public class GradingSystemServiceImpl implements GradingSystemService {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private final GradingSystemRepository gradingSystemRepository;
    private final GradingSystemMapper gradingSystemMapper;
    private final GradingSystemValidator gradingSystemValidator;
    private final SessionUtils sessionUtils;

    public GradingSystemServiceImpl(
        GradingSystemRepository gradingSystemRepository,
        GradingSystemMapper gradingSystemMapper,
        GradingSystemValidator gradingSystemValidator,
        SessionUtils sessionUtils
    ) {
        this.gradingSystemRepository = gradingSystemRepository;
        this.gradingSystemMapper     = gradingSystemMapper;
        this.gradingSystemValidator = gradingSystemValidator;
        this.sessionUtils = sessionUtils;
    }

    @Override
    public GradingSystemDetailDto createGradingSystem(
        GradingSystemDetailDto gradingSystemDetailDto
    ) {
        LOGGER.debug("Create grading system {}", gradingSystemDetailDto);
        if (!sessionUtils.isCompetitionManager()) {
            throw new ForbiddenException("No Permission to create a competition");
        }
        gradingSystemValidator.validate(gradingSystemDetailDto);

        GradingSystem given =
            gradingSystemMapper.gradingSystemDetailDtoToGradingSystem(gradingSystemDetailDto);

        GradingSystem persisted = gradingSystemRepository.save(given);

        return gradingSystemMapper.gradingSystemToGradingSystemDetailDto(persisted);
    }
}
