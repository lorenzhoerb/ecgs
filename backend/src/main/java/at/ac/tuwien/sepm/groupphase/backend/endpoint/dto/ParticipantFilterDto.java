package at.ac.tuwien.sepm.groupphase.backend.endpoint.dto;

import at.ac.tuwien.sepm.groupphase.backend.entity.ApplicationUser;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ParticipantFilterDto {
    public Long competitionId;
    public Boolean accepted;
    public String firstName;
    public String lastName;
    public ApplicationUser.Gender gender;
    public Long gradingGroupId;
    public Long flagId;
}

