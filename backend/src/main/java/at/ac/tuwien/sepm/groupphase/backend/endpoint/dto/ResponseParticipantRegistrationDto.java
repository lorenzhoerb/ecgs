package at.ac.tuwien.sepm.groupphase.backend.endpoint.dto;

public class ResponseParticipantRegistrationDto {
    private Long competitionId;
    private Long userId;
    private Long groupPreference;

    public ResponseParticipantRegistrationDto(Long competitionId, Long userId, Long groupPreference) {
        this.competitionId = competitionId;
        this.userId = userId;
        this.groupPreference = groupPreference;
    }

    public ResponseParticipantRegistrationDto() {
    }

    public Long getCompetitionId() {
        return this.competitionId;
    }

    public Long getUserId() {
        return this.userId;
    }

    public Long getGroupPreference() {
        return this.groupPreference;
    }

    public void setCompetitionId(Long competitionId) {
        this.competitionId = competitionId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public void setGroupPreference(Long groupPreference) {
        this.groupPreference = groupPreference;
    }
}
