package at.ac.tuwien.sepm.groupphase.backend.endpoint.dto;

import java.time.LocalDateTime;


public class CompetitionListDto {

    private Long id;

    private String name;
    private String description;
    private LocalDateTime beginOfRegistration;

    private LocalDateTime endOfRegistration;

    private LocalDateTime beginOfCompetition;

    private LocalDateTime endOfCompetition;

    private String email;

    private String phone;

    public String getName() {
        return name;
    }

    public Long getId() {
        return id;
    }

    public CompetitionListDto setId(Long id) {
        this.id = id;
        return this;
    }

    public CompetitionListDto setName(String name) {
        this.name = name;
        return this;
    }

    public CompetitionListDto setBeginOfRegistration(LocalDateTime beginOfRegistration) {
        this.beginOfRegistration = beginOfRegistration;
        return this;
    }

    public LocalDateTime getEndOfRegistration() {
        return endOfRegistration;
    }

    public CompetitionListDto setEndOfRegistration(LocalDateTime endOfRegistration) {
        this.endOfRegistration = endOfRegistration;
        return this;
    }

    public LocalDateTime getBeginOfCompetition() {
        return beginOfCompetition;
    }

    public CompetitionListDto setBeginOfCompetition(LocalDateTime beginOfCompetition) {
        this.beginOfCompetition = beginOfCompetition;
        return this;
    }

    public LocalDateTime getEndOfCompetition() {
        return endOfCompetition;
    }

    public CompetitionListDto setEndOfCompetition(LocalDateTime endOfCompetition) {
        this.endOfCompetition = endOfCompetition;
        return this;
    }

    public String getDescription() {
        return description;
    }

    public CompetitionListDto setDescription(String description) {
        this.description = description;
        return this;
    }


    public String getEmail() {
        return email;
    }

    public LocalDateTime getBeginOfRegistration() {
        return beginOfRegistration;
    }

    public CompetitionListDto setEmail(String email) {
        this.email = email;
        return this;
    }

    public String getPhone() {
        return phone;
    }

    public CompetitionListDto setPhone(String phone) {
        this.phone = phone;
        return this;
    }

    @Override
    public String toString() {
        return "name='" + "CompetitionDetailDto{" + name + '\'' + ", description='" + description + '\'' + ", beginOfRegistration=" + beginOfRegistration
            + ", endOfRegistration=" + endOfRegistration + ", beginOfCompetition="
            + beginOfCompetition + ", endOfCompetition=" + endOfCompetition + ", email='" + email + '\'' + ", phone='" + phone + '\'' + '}';
    }
}
