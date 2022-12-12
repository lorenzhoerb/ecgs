package at.ac.tuwien.sepm.groupphase.backend.endpoint.dto;

import at.ac.tuwien.sepm.groupphase.backend.entity.ApplicationUser;

public class UserInfoDto {
    private String firstName;
    private String lastName;
    private ApplicationUser.Role role;

    public UserInfoDto(String firstName, String lastName, ApplicationUser.Role role) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.role = role;
    }

    public UserInfoDto() {
    }

    public static UserInfoDtoBuilder builder() {
        return new UserInfoDtoBuilder();
    }

    public String getFirstName() {
        return this.firstName;
    }

    public String getLastName() {
        return this.lastName;
    }

    public ApplicationUser.Role getRole() {
        return this.role;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setRole(ApplicationUser.Role role) {
        this.role = role;
    }

    public static class UserInfoDtoBuilder {
        private String firstName;
        private String lastName;
        private ApplicationUser.Role role;

        UserInfoDtoBuilder() {
        }

        public UserInfoDtoBuilder firstName(String firstName) {
            this.firstName = firstName;
            return this;
        }

        public UserInfoDtoBuilder lastName(String lastName) {
            this.lastName = lastName;
            return this;
        }

        public UserInfoDtoBuilder role(ApplicationUser.Role role) {
            this.role = role;
            return this;
        }

        public UserInfoDto build() {
            return new UserInfoDto(firstName, lastName, role);
        }

        public String toString() {
            return "UserInfoDto.UserInfoDtoBuilder(firstName=" + this.firstName + ", lastName=" + this.lastName + ", role=" + this.role + ")";
        }
    }
}
