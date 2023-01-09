package at.ac.tuwien.sepm.groupphase.backend.endpoint.dto;

import java.time.LocalDateTime;

public class CompetitionSearchDto {
    private String name;
    private LocalDateTime beginDate;
    private LocalDateTime endDate;
    private LocalDateTime beginRegistrationDate;
    private LocalDateTime endRegistrationDate;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LocalDateTime getBeginDate() {
        return beginDate;
    }

    public void setBeginDate(LocalDateTime beginDate) {
        this.beginDate = beginDate;
    }

    public CompetitionSearchDto() {
    }

    public LocalDateTime getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDateTime endDate) {
        this.endDate = endDate;
    }

    public LocalDateTime getBeginRegistrationDate() {
        return beginRegistrationDate;
    }

    public void setBeginRegistrationDate(LocalDateTime beginRegistrationDate) {
        this.beginRegistrationDate = beginRegistrationDate;
    }

    public LocalDateTime getEndRegistrationDate() {
        return endRegistrationDate;
    }

    public void setEndRegistrationDate(LocalDateTime endRegistrationDate) {
        this.endRegistrationDate = endRegistrationDate;
    }

    public CompetitionSearchDto(String name, LocalDateTime beginDate, LocalDateTime endDate, LocalDateTime beginRegistrationDate,
                                LocalDateTime endRegistrationDate) {
        this.name = name;
        this.beginDate = beginDate;
        this.endDate = endDate;
        this.beginRegistrationDate = beginRegistrationDate;
        this.endRegistrationDate = endRegistrationDate;
    }
}
