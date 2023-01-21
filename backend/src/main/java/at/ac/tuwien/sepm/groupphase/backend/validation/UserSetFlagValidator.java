package at.ac.tuwien.sepm.groupphase.backend.validation;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.UserDetailSetFlagDto;

import org.springframework.stereotype.Component;

@Component
public class UserSetFlagValidator extends Validator<UserDetailSetFlagDto> {

    protected UserSetFlagValidator(javax.validation.Validator validator) {
        super(validator);
    }

    public void validateCustom(UserDetailSetFlagDto dto) {

    }
}
