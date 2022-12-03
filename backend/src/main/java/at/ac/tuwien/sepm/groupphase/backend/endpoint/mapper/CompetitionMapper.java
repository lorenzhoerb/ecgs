package at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.CompetitionDetailDto;
import at.ac.tuwien.sepm.groupphase.backend.entity.Competition;
import org.mapstruct.Mapper;

@Mapper
public interface CompetitionMapper {

    CompetitionDetailDto competitionToCompetitionDetailDto(Competition competition);

    Competition competitionDetailDtoToCompetition(CompetitionDetailDto competitionDetailDto);
}
