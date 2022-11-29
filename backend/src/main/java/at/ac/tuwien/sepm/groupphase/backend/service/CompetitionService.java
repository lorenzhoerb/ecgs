package at.ac.tuwien.sepm.groupphase.backend.service;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.CompetitionDetailDto;

public interface CompetitionService {

    CompetitionDetailDto create(CompetitionDetailDto competitionDetailDto);

}
