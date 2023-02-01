package at.ac.tuwien.sepm.groupphase.backend.endpoint.dto;


public class ParticipantSelfRegistrationDto {
    private Long groupPreference;

    public ParticipantSelfRegistrationDto(Long groupPreference) {
        this.groupPreference = groupPreference;
    }

    public ParticipantSelfRegistrationDto() {
    }

    public Long getGroupPreference() {
        return this.groupPreference;
    }

    public void setGroupPreference(Long groupPreference) {
        this.groupPreference = groupPreference;
    }
}
