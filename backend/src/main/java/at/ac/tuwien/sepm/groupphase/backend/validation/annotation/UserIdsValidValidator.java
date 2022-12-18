package at.ac.tuwien.sepm.groupphase.backend.validation.annotation;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.UserDetailDto;
import at.ac.tuwien.sepm.groupphase.backend.repository.ApplicationUserRepository;
import org.springframework.beans.factory.annotation.Autowired;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class UserIdsValidValidator implements ConstraintValidator<UserIdsValid, UserDetailDto[]> {

    @Autowired
    private ApplicationUserRepository applicationUserRepository;

    @Override
    public void initialize(UserIdsValid constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(UserDetailDto[] users, ConstraintValidatorContext constraintValidatorContext) {
        if (users == null || users.length == 0) {
            return true;
        }

        for (UserDetailDto user : users) {
            if (user.id() == null) {
                return false;
            }

            if (applicationUserRepository.findById(user.id()).isEmpty()) {
                return false;
            }
        }

        return true;
    }
}
