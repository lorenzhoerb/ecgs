package at.ac.tuwien.sepm.groupphase.backend.endpoint.dto;

import at.ac.tuwien.sepm.groupphase.backend.entity.ApplicationUser;

public class ParticipantFilterDto {
    public Long competitionId;
    public Boolean accepted;
    public String firstName;
    public String lastName;
    public ApplicationUser.Gender gender;
    public Long gradingGroupId;
    public Long flagId;

    public ParticipantFilterDto(Long competitionId, Boolean accepted, String firstName, String lastName, ApplicationUser.Gender gender, Long gradingGroupId,
                                Long flagId) {
        this.competitionId = competitionId;
        this.accepted = accepted;
        this.firstName = firstName;
        this.lastName = lastName;
        this.gender = gender;
        this.gradingGroupId = gradingGroupId;
        this.flagId = flagId;
    }

    public ParticipantFilterDto() {
    }

    public Long getCompetitionId() {
        return this.competitionId;
    }

    public Boolean getAccepted() {
        return this.accepted;
    }

    public String getFirstName() {
        return this.firstName;
    }

    public String getLastName() {
        return this.lastName;
    }

    public ApplicationUser.Gender getGender() {
        return this.gender;
    }

    public Long getGradingGroupId() {
        return this.gradingGroupId;
    }

    public Long getFlagId() {
        return this.flagId;
    }

    public void setCompetitionId(Long competitionId) {
        this.competitionId = competitionId;
    }

    public void setAccepted(Boolean accepted) {
        this.accepted = accepted;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setGender(ApplicationUser.Gender gender) {
        this.gender = gender;
    }

    public void setGradingGroupId(Long gradingGroupId) {
        this.gradingGroupId = gradingGroupId;
    }

    public void setFlagId(Long flagId) {
        this.flagId = flagId;
    }
}

