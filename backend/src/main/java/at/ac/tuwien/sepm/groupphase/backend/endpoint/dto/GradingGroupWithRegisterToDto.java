package at.ac.tuwien.sepm.groupphase.backend.endpoint.dto;

import java.util.Set;

public class GradingGroupWithRegisterToDto {
    private Long id;

    private String title;

    private GradingSystemDetailDto gradingSystem;

    private Set<ParticipantDetailDto> registrations;

    public GradingGroupWithRegisterToDto() {
    }

    public GradingGroupWithRegisterToDto(Long id, String title, GradingSystemDetailDto gradingSystem, Set<ParticipantDetailDto> registrations) {
        this.id = id;
        this.title = title;
        this.gradingSystem = gradingSystem;
        this.registrations = registrations;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public GradingSystemDetailDto getGradingSystem() {
        return gradingSystem;
    }

    public void setGradingSystem(GradingSystemDetailDto gradingSystem) {
        this.gradingSystem = gradingSystem;
    }

    public Set<ParticipantDetailDto> getRegistrations() {
        return registrations;
    }

    public void setRegistrations(Set<ParticipantDetailDto> registrations) {
        this.registrations = registrations;
    }
}
