package at.ac.tuwien.sepm.groupphase.backend.validation;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.ClubManagerTeamMemberImportDto;
import at.ac.tuwien.sepm.groupphase.backend.exception.ValidationException;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class ClubManagerTeamMemberImportDtoValidator extends Validator<ClubManagerTeamMemberImportDto> {
    protected ClubManagerTeamMemberImportDtoValidator(javax.validation.Validator validator) {
        super(validator);
    }

    @Override
    protected void validateCustom(ClubManagerTeamMemberImportDto toValidate) {
        if (toValidate.dateOfBirth().before(new Date(-1547533357000L))) {
            throw new ValidationException("Date of birth must be after the begin of 1920");
        }
    }
}
