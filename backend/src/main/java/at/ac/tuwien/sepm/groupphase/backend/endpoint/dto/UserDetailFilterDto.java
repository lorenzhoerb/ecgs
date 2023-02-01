package at.ac.tuwien.sepm.groupphase.backend.endpoint.dto;

import at.ac.tuwien.sepm.groupphase.backend.entity.ApplicationUser;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

public class UserDetailFilterDto {
    private String firstName;
    private String lastName;
    private ApplicationUser.Gender gender;
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private Date dateOfBirth;
    private Long flagId;
    private Integer size;
    private Integer page;

    public UserDetailFilterDto(String firstName, String lastName, ApplicationUser.Gender gender, Date dateOfBirth, Long flagId, Integer size, Integer page) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.gender = gender;
        this.dateOfBirth = dateOfBirth;
        this.flagId = flagId;
        this.size = size;
        this.page = page;
    }

    public UserDetailFilterDto() {
    }

    public String getFirstName() {
        return this.firstName;
    }

    public String getLastName() {
        return this.lastName;
    }

    public ApplicationUser.Gender getGender() {
        return this.gender;
    }

    public Date getDateOfBirth() {
        return this.dateOfBirth;
    }

    public Long getFlagId() {
        return this.flagId;
    }

    public Integer getSize() {
        return this.size;
    }

    public Integer getPage() {
        return this.page;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setGender(ApplicationUser.Gender gender) {
        this.gender = gender;
    }

    public void setDateOfBirth(Date dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public void setFlagId(Long flagId) {
        this.flagId = flagId;
    }

    public void setSize(Integer size) {
        this.size = size;
    }

    public void setPage(Integer page) {
        this.page = page;
    }
}
