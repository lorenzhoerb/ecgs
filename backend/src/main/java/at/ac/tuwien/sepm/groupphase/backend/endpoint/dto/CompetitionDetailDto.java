package at.ac.tuwien.sepm.groupphase.backend.endpoint.dto;

import at.ac.tuwien.sepm.groupphase.backend.validation.annotation.DateBeforeOrEquals;
import at.ac.tuwien.sepm.groupphase.backend.validation.annotation.HasOnlyUniqueProperty;
import at.ac.tuwien.sepm.groupphase.backend.validation.annotation.UserIdsValid;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.Valid;
import javax.validation.constraints.Email;
import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

@DateBeforeOrEquals(first = "beginOfRegistration", second = "endOfRegistration", message = "End of registration date must be after begin of registration")
@DateBeforeOrEquals(first = "beginOfCompetition", second = "endOfCompetition", message = "End of competition date must be after begin of competition")
@DateBeforeOrEquals(first = "endOfRegistration", second = "beginOfCompetition", message = "Begin of competition date must be after end of registration")
public class CompetitionDetailDto {

    private Long id;

    @Size(max = 4095)
    @NotBlank(message = "Name must be given")
    private String name;

    @Size(max = 8191)
    private String description;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    @FutureOrPresent(message = "Begin of registration must be in the future or now")
    @NotNull(message = "Begin of registration must be given")
    private LocalDateTime beginOfRegistration;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    @NotNull(message = "End of registration must be given")
    private LocalDateTime endOfRegistration;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    @FutureOrPresent(message = "Begin of competition must be in the future or now")
    @NotNull(message = "Begin of competition must be given")
    private LocalDateTime beginOfCompetition;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    @NotNull(message = "End of competition must be given")
    private LocalDateTime endOfCompetition;

    @NotNull(message = "isPublic must be given")
    @JsonProperty("public")
    private boolean isPublic;

    @NotNull(message = "draft must be given")
    private boolean draft;

    @Email(regexp = "[a-z0-9._%+-]+@[a-z0-9.-]+\\.[a-z]{2,63}",
        flags = Pattern.Flag.CASE_INSENSITIVE)
    private String email;

    @Pattern(regexp = "^[\\+]?[(]?[0-9]{3}[)]?[-\\s\\.]?[0-9]{3}[-\\s\\.]?[0-9]{4,6}$", message = "Invalid phone number")
    private String phone;

    @Valid
    @HasOnlyUniqueProperty(property = "title", message = "Grading groups must have unique names")
    private GradingGroupDto[] gradingGroups;

    public UserDetailDto[] getJudges() {
        return judges;
    }

    public CompetitionDetailDto setJudges(UserDetailDto[] judges) {
        this.judges = judges;
        return this;
    }

    @Valid
    @HasOnlyUniqueProperty(property = "id", message = "A judge can not be assigned twice to a competition")
    @UserIdsValid(message = "Id of a judge invalid")
    private UserDetailDto[] judges;

    // is optional
    private UserDetailDto creator;

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

    public GradingGroupDto[] getGradingGroups() {
        return gradingGroups;
    }

    public CompetitionDetailDto setGradingGroups(GradingGroupDto[] gradingGroups) {
        this.gradingGroups = gradingGroups;
        return this;
    }

    public String getPhone() {
        return phone;
    }

    public CompetitionDetailDto setPhone(String phone) {
        this.phone = phone;
        return this;
    }

    public UserDetailDto getCreator() {
        return creator;
    }

    public void setCreator(UserDetailDto creator) {
        this.creator = creator;
    }

    @Override
    public String toString() {
        return "name='" + "CompetitionDetailDto{" + name + '\'' + ", description='" + description + '\'' + ", beginOfRegistration=" + beginOfRegistration
            + ", endOfRegistration=" + endOfRegistration + ", beginOfCompetition="
            + beginOfCompetition + ", endOfCompetition=" + endOfCompetition + ", isPublic=" + isPublic + ", draft=" + draft + ", email='" + email + '\''
            + ", phone='" + phone + '\'' + '}';
    }
}
