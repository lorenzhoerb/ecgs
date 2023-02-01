package at.ac.tuwien.sepm.groupphase.backend.endpoint.dto;

import javax.validation.constraints.NotNull;

public class ParticipantManageDto {
    @NotNull
    private Long userId;
    private Long groupId;
    private Boolean accepted;

    public ParticipantManageDto(@NotNull Long userId, Long groupId, Boolean accepted) {
        this.userId = userId;
        this.groupId = groupId;
        this.accepted = accepted;
    }

    public ParticipantManageDto() {
    }

    public @NotNull Long getUserId() {
        return this.userId;
    }

    public Long getGroupId() {
        return this.groupId;
    }

    public Boolean getAccepted() {
        return this.accepted;
    }

    public void setUserId(@NotNull Long userId) {
        this.userId = userId;
    }

    public void setGroupId(Long groupId) {
        this.groupId = groupId;
    }

    public void setAccepted(Boolean accepted) {
        this.accepted = accepted;
    }
}
