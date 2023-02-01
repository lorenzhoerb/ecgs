package at.ac.tuwien.sepm.groupphase.backend.endpoint.dto;

import java.util.List;

public class ResponseMultiParticipantRegistrationDto {
    private Long competitionId;
    private List<ParticipantRegistrationDto> registeredParticipants;

    public ResponseMultiParticipantRegistrationDto(Long competitionId, List<ParticipantRegistrationDto> registeredParticipants) {
        this.competitionId = competitionId;
        this.registeredParticipants = registeredParticipants;
    }

    public ResponseMultiParticipantRegistrationDto() {
    }

    public Long getCompetitionId() {
        return this.competitionId;
    }

    public List<ParticipantRegistrationDto> getRegisteredParticipants() {
        return this.registeredParticipants;
    }

    public void setCompetitionId(Long competitionId) {
        this.competitionId = competitionId;
    }

    public void setRegisteredParticipants(List<ParticipantRegistrationDto> registeredParticipants) {
        this.registeredParticipants = registeredParticipants;
    }
}
