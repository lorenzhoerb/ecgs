package at.ac.tuwien.sepm.groupphase.backend.validation;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.UserRegisterDto;
import at.ac.tuwien.sepm.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepm.groupphase.backend.exception.ValidationException;
import at.ac.tuwien.sepm.groupphase.backend.exception.ValidationListException;
import org.springframework.stereotype.Component;

import javax.validation.ConstraintViolation;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

@Component
public class RegistrationValidator extends Validator<UserRegisterDto> {

    public RegistrationValidator(javax.validation.Validator validator) {
        super(validator);
    }

    @Override
    protected void validateCustom(UserRegisterDto toValidate) {
        validateEnums(toValidate);
    }

    //TODO SHOW IN RESPONSE
    private void validateEnums(UserRegisterDto toValidate) {
        List<String> errMsgs = new LinkedList<>();
        if (toValidate.getType() == null) {
            errMsgs.add("type must not be null");
        } else {
            if (!(toValidate.getType().equals(ApplicationUser.Role.PARTICIPANT) || toValidate.getType().equals(ApplicationUser.Role.CLUB_MANAGER) || toValidate.getType().equals(ApplicationUser.Role.TOURNAMENT_MANAGER))) {
                errMsgs.add("type must be a valid value: 'PARTICIPANT','CLUB_MANAGER' or 'TOURNAMENT_MANAGER'");
            }
        }
        if (toValidate.getGender() == null) {
            errMsgs.add("gender must not be null");
        } else {
            if (!(toValidate.getGender().equals(ApplicationUser.Gender.MALE) || toValidate.getGender().equals(ApplicationUser.Gender.FEMALE) || toValidate.getGender().equals(ApplicationUser.Gender.OTHER))) {
                errMsgs.add("gender must be a valid value: 'MALE','FEMALE' or 'OTHER'");
            }
        }
        if (!errMsgs.isEmpty()) {
            throw new ValidationListException("Failed to validate user enum", errMsgs);
        }
    }
}
