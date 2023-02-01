package at.ac.tuwien.sepm.groupphase.backend.endpoint.dto;

import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

public class AdvanceCompetitionSearchDto {

    private String name;
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime endOfRegistrationBefore;
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime endOfRegistrationAfter;
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime beginOfRegistrationBefore;
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime beginOfRegistrationAfter;
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime endOfCompetitionBefore;
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime endOfCompetitionAfter;
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime beginOfCompetitionBefore;
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime beginOfCompetitionAfter;
    private Boolean isPublic;
    private Boolean isRegistrationOpen;
    private Integer size;
    private Integer page;

    public AdvanceCompetitionSearchDto(String name, LocalDateTime endOfRegistrationBefore, LocalDateTime endOfRegistrationAfter,
                                       LocalDateTime beginOfRegistrationBefore, LocalDateTime beginOfRegistrationAfter, LocalDateTime endOfCompetitionBefore,
                                       LocalDateTime endOfCompetitionAfter, LocalDateTime beginOfCompetitionBefore, LocalDateTime beginOfCompetitionAfter,
                                       Boolean isPublic, Boolean isRegistrationOpen, Integer size, Integer page) {
        this.name = name;
        this.endOfRegistrationBefore = endOfRegistrationBefore;
        this.endOfRegistrationAfter = endOfRegistrationAfter;
        this.beginOfRegistrationBefore = beginOfRegistrationBefore;
        this.beginOfRegistrationAfter = beginOfRegistrationAfter;
        this.endOfCompetitionBefore = endOfCompetitionBefore;
        this.endOfCompetitionAfter = endOfCompetitionAfter;
        this.beginOfCompetitionBefore = beginOfCompetitionBefore;
        this.beginOfCompetitionAfter = beginOfCompetitionAfter;
        this.isPublic = isPublic;
        this.isRegistrationOpen = isRegistrationOpen;
        this.size = size;
        this.page = page;
    }

    public AdvanceCompetitionSearchDto() {
    }

    public String getName() {
        return this.name;
    }

    public LocalDateTime getEndOfRegistrationBefore() {
        return this.endOfRegistrationBefore;
    }

    public LocalDateTime getEndOfRegistrationAfter() {
        return this.endOfRegistrationAfter;
    }

    public LocalDateTime getBeginOfRegistrationBefore() {
        return this.beginOfRegistrationBefore;
    }

    public LocalDateTime getBeginOfRegistrationAfter() {
        return this.beginOfRegistrationAfter;
    }

    public LocalDateTime getEndOfCompetitionBefore() {
        return this.endOfCompetitionBefore;
    }

    public LocalDateTime getEndOfCompetitionAfter() {
        return this.endOfCompetitionAfter;
    }

    public LocalDateTime getBeginOfCompetitionBefore() {
        return this.beginOfCompetitionBefore;
    }

    public LocalDateTime getBeginOfCompetitionAfter() {
        return this.beginOfCompetitionAfter;
    }

    public Boolean getIsPublic() {
        return this.isPublic;
    }

    public Boolean getIsRegistrationOpen() {
        return this.isRegistrationOpen;
    }

    public Integer getSize() {
        return this.size;
    }

    public Integer getPage() {
        return this.page;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setEndOfRegistrationBefore(LocalDateTime endOfRegistrationBefore) {
        this.endOfRegistrationBefore = endOfRegistrationBefore;
    }

    public void setEndOfRegistrationAfter(LocalDateTime endOfRegistrationAfter) {
        this.endOfRegistrationAfter = endOfRegistrationAfter;
    }

    public void setBeginOfRegistrationBefore(LocalDateTime beginOfRegistrationBefore) {
        this.beginOfRegistrationBefore = beginOfRegistrationBefore;
    }

    public void setBeginOfRegistrationAfter(LocalDateTime beginOfRegistrationAfter) {
        this.beginOfRegistrationAfter = beginOfRegistrationAfter;
    }

    public void setEndOfCompetitionBefore(LocalDateTime endOfCompetitionBefore) {
        this.endOfCompetitionBefore = endOfCompetitionBefore;
    }

    public void setEndOfCompetitionAfter(LocalDateTime endOfCompetitionAfter) {
        this.endOfCompetitionAfter = endOfCompetitionAfter;
    }

    public void setBeginOfCompetitionBefore(LocalDateTime beginOfCompetitionBefore) {
        this.beginOfCompetitionBefore = beginOfCompetitionBefore;
    }

    public void setBeginOfCompetitionAfter(LocalDateTime beginOfCompetitionAfter) {
        this.beginOfCompetitionAfter = beginOfCompetitionAfter;
    }

    public void setIsPublic(Boolean isPublic) {
        this.isPublic = isPublic;
    }

    public void setIsRegistrationOpen(Boolean isRegistrationOpen) {
        this.isRegistrationOpen = isRegistrationOpen;
    }

    public void setSize(Integer size) {
        this.size = size;
    }

    public void setPage(Integer page) {
        this.page = page;
    }
}
