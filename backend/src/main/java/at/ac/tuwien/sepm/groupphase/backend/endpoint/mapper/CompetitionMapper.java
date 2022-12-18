package at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.CalenderViewCompetitionDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.CompetitionDetailDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.CompetitionDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.CompetitionViewDto;
import at.ac.tuwien.sepm.groupphase.backend.entity.Competition;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.context.annotation.Bean;

import java.util.Set;

@Mapper
public interface CompetitionMapper {
    @Mapping(source = "public", target = "isPublic")
    CompetitionViewDto competitionToCompetitionViewDto(Competition competition);

    CompetitionDetailDto competitionToCompetitionDetailDto(Competition competition);

    Competition competitionDetailDtoToCompetition(CompetitionDetailDto competitionDetailDto);

    CompetitionDto competitionToCompetitionDto(Competition competition);

    Set<CompetitionDto> competitionSetToCompetitionDtoSet(Set<Competition> competitionSet);

    Set<CalenderViewCompetitionDto> competitionSetToCalenderViewCompetitionDtoSet(Set<Competition> competitionSet);
}
