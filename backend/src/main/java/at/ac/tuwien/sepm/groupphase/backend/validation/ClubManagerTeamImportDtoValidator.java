package at.ac.tuwien.sepm.groupphase.backend.validation;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.ClubManagerTeamImportDto;
import at.ac.tuwien.sepm.groupphase.backend.exception.ValidationListException;
import org.springframework.stereotype.Component;

import java.util.Collections;

@Component
public class ClubManagerTeamImportDtoValidator extends Validator<ClubManagerTeamImportDto> {
    private final ClubManagerTeamManyMembersImportValidator membersImportValidator;

    protected ClubManagerTeamImportDtoValidator(javax.validation.Validator validator, ClubManagerTeamManyMembersImportValidator membersImportValidator) {
        super(validator);
        this.membersImportValidator = membersImportValidator;
    }

    @Override
    protected void validateCustom(ClubManagerTeamImportDto toValidate) {
        if (toValidate.teamMembers() == null) {
            throw new ValidationListException("Validation failed", Collections.singletonList("Team members are empty!"));
        }

        membersImportValidator.validate(toValidate.teamMembers());
    }
}
