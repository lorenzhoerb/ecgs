package at.ac.tuwien.sepm.groupphase.backend.validation;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.UserPasswordResetRequestDto;
import org.springframework.stereotype.Component;

@Component
public class ForgotPasswordValidator extends Validator<UserPasswordResetRequestDto> {

    public ForgotPasswordValidator(javax.validation.Validator validator) {
        super(validator);
    }

    @Override
    protected void validateCustom(UserPasswordResetRequestDto toValidate) {
    }

}
