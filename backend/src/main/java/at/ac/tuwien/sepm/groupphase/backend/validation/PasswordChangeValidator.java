package at.ac.tuwien.sepm.groupphase.backend.validation;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.UserCredentialUpdateDto;
import org.springframework.stereotype.Component;

@Component
public class PasswordChangeValidator extends Validator<UserCredentialUpdateDto> {

    public PasswordChangeValidator(javax.validation.Validator validator) {
        super(validator);
    }

    @Override
    protected void validateCustom(UserCredentialUpdateDto toValidate) {
    }
}
