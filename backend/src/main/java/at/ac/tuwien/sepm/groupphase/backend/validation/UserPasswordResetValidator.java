package at.ac.tuwien.sepm.groupphase.backend.validation;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.UserPasswordResetDto;
import org.springframework.stereotype.Component;

@Component
public class UserPasswordResetValidator extends Validator<UserPasswordResetDto> {

    public UserPasswordResetValidator(javax.validation.Validator validator) {
        super(validator);
    }

    @Override
    protected void validateCustom(UserPasswordResetDto toValidate) {
    }
}
