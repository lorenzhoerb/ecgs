package at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.CompetitionViewDto;
import at.ac.tuwien.sepm.groupphase.backend.entity.Competition;
import org.mapstruct.Mapper;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.CompetitionDetailDto;
import org.mapstruct.Mapping;
import org.springframework.context.annotation.Bean;

@Mapper
public interface CompetitionMapper {
    @Mapping(source = "public", target = "isPublic")
    CompetitionViewDto competitionToCompetitionViewDto(Competition competition);

    CompetitionDetailDto competitionToCompetitionDetailDto(Competition competition);

    Competition competitionDetailDtoToCompetition(CompetitionDetailDto competitionDetailDto);
}
