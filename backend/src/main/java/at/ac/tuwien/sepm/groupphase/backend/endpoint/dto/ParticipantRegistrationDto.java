package at.ac.tuwien.sepm.groupphase.backend.endpoint.dto;

import javax.validation.constraints.NotNull;

public class ParticipantRegistrationDto {
    @NotNull(message = "No user id given")
    private Long userId;
    private Long groupPreference;

    public ParticipantRegistrationDto(@NotNull(message = "No user id given") Long userId, Long groupPreference) {
        this.userId = userId;
        this.groupPreference = groupPreference;
    }

    public ParticipantRegistrationDto() {
    }

    public @NotNull(message = "No user id given") Long getUserId() {
        return this.userId;
    }

    public Long getGroupPreference() {
        return this.groupPreference;
    }

    public void setUserId(@NotNull(message = "No user id given") Long userId) {
        this.userId = userId;
    }

    public void setGroupPreference(Long groupPreference) {
        this.groupPreference = groupPreference;
    }
}
