package at.ac.tuwien.sepm.groupphase.backend.validation;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.UserDetailDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.UserDetailSetFlagDto;

import at.ac.tuwien.sepm.groupphase.backend.exception.ValidationListException;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class UserSetFlagValidator extends Validator<UserDetailSetFlagDto> {

    private SimpleFlagValidator simpleFlagValidator;

    protected UserSetFlagValidator(javax.validation.Validator validator,
                                   SimpleFlagValidator simpleFlagValidator) {
        super(validator);
        this.simpleFlagValidator = simpleFlagValidator;
    }

    public void validateCustom(UserDetailSetFlagDto dto) {
        this.simpleFlagValidator.validate(dto.getFlag());

        for (UserDetailDto user : dto.getUsers()) {
            if (user == null) {
                throw new ValidationListException("User was null",
                    List.of("User was null"));
            }

            if (user.id() == null) {
                throw new ValidationListException("User id was null",
                    List.of("User id was null"));
            }
        }
    }
}
