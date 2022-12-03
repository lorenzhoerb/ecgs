package at.ac.tuwien.sepm.groupphase.backend.endpoint.dto;

import at.ac.tuwien.sepm.groupphase.backend.service.validator.annotation.DateBeforeOrEquals;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.Email;
import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

@DateBeforeOrEquals(first = "beginOfRegistration", second = "endOfRegistration", message = "End of registration date must be after begin of registration")
@DateBeforeOrEquals(first = "beginOfCompetition", second = "endOfCompetition", message = "End of competition date must be after begin of competition")
public class CompetitionDetailDto {

    private Long id;

    @Size(max = 4095)
    @NotBlank(message = "Name must be given")
    private String name;
    private String description;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    @FutureOrPresent(message = "Begin of registration must be in the future or now")
    @NotNull
    private LocalDateTime beginOfRegistration;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    @NotNull
    private LocalDateTime endOfRegistration;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    @NotNull
    private LocalDateTime beginOfCompetition;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    @NotNull
    private LocalDateTime endOfCompetition;

    @NotNull
    private boolean isPublic;

    @NotNull
    private boolean draft;

    @Email
    private String email;

    @Pattern(regexp = "^[\\+]?[(]?[0-9]{3}[)]?[-\\s\\.]?[0-9]{3}[-\\s\\.]?[0-9]{4,6}$")
    private String phone;

    public String getName() {
        return name;
    }

    public Long getId() {
        return id;
    }

    public CompetitionDetailDto setId(Long id) {
        this.id = id;
        return this;
    }

    public CompetitionDetailDto setName(String name) {
        this.name = name;
        return this;
    }

    public CompetitionDetailDto setBeginOfRegistration(LocalDateTime beginOfRegistration) {
        this.beginOfRegistration = beginOfRegistration;
        return this;
    }

    public LocalDateTime getEndOfRegistration() {
        return endOfRegistration;
    }

    public CompetitionDetailDto setEndOfRegistration(LocalDateTime endOfRegistration) {
        this.endOfRegistration = endOfRegistration;
        return this;
    }

    public LocalDateTime getBeginOfCompetition() {
        return beginOfCompetition;
    }

    public CompetitionDetailDto setBeginOfCompetition(LocalDateTime beginOfCompetition) {
        this.beginOfCompetition = beginOfCompetition;
        return this;
    }

    public LocalDateTime getEndOfCompetition() {
        return endOfCompetition;
    }

    public CompetitionDetailDto setEndOfCompetition(LocalDateTime endOfCompetition) {
        this.endOfCompetition = endOfCompetition;
        return this;
    }

    public String getDescription() {
        return description;
    }

    public CompetitionDetailDto setDescription(String description) {
        this.description = description;
        return this;
    }

    public boolean isDraft() {
        return draft;
    }

    public CompetitionDetailDto setDraft(boolean draft) {
        this.draft = draft;
        return this;
    }

    public boolean isPublic() {
        return isPublic;
    }

    public CompetitionDetailDto setPublic(boolean isPublic) {
        this.isPublic = isPublic;
        return this;
    }

    public String getEmail() {
        return email;
    }

    public LocalDateTime getBeginOfRegistration() {
        return beginOfRegistration;
    }

    public CompetitionDetailDto setEmail(String email) {
        this.email = email;
        return this;
    }

    public String getPhone() {
        return phone;
    }

    public CompetitionDetailDto setPhone(String phone) {
        this.phone = phone;
        return this;
    }

    @Override
    public String toString() {
        return "name='" + "CompetitionDetailDto{" + name + '\'' + ", description='" + description + '\'' + ", beginOfRegistration=" + beginOfRegistration + ", endOfRegistration=" + endOfRegistration + ", beginOfCompetition="
            + beginOfCompetition + ", endOfCompetition=" + endOfCompetition + ", isPublic=" + isPublic + ", draft=" + draft + ", email='" + email + '\'' + ", phone='" + phone + '\'' + '}';
    }
}
