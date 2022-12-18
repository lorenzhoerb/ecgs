package at.ac.tuwien.sepm.groupphase.backend.validation;

import at.ac.tuwien.sepm.groupphase.backend.exception.ValidationException;
import at.ac.tuwien.sepm.groupphase.backend.exception.ValidationListException;
import org.springframework.stereotype.Component;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.ClubManagerTeamMemberImportDto;

import java.util.ArrayList;
import java.util.List;

@Component
public class ClubManagerTeamManyMembersImportValidator extends Validator<List<ClubManagerTeamMemberImportDto>> {
    private final ClubManagerTeamMemberImportDtoValidator teamMemberValidator;

    protected ClubManagerTeamManyMembersImportValidator(javax.validation.Validator validator, ClubManagerTeamMemberImportDtoValidator teamMemberValidator) {
        super(validator);
        this.teamMemberValidator = teamMemberValidator;
    }

    @Override
    protected void validateCustom(List<ClubManagerTeamMemberImportDto> clubMembers) {
        List<String> errors = new ArrayList<>();
        int currentUserToValidate = 1;
        for (ClubManagerTeamMemberImportDto teamMember : clubMembers) {
            try {
                if (teamMember == null) {
                    throw new ValidationException("Member is empty!");
                }
                teamMemberValidator.validate(teamMember);
            } catch (ValidationListException listException) {
                errors.add(
                    String.format(
                        "User #%d has some issues.\n%s",
                        currentUserToValidate,
                        String.join(
                            "\n",
                            listException.errors().stream().map(e -> "- " + e).toList()))
                );
            } catch (ValidationException e) {
                errors.add(
                    String.format(
                        "User #%d has some issues.\n- %s",
                        currentUserToValidate,
                        e.getMessage()
                    )
                );
            }

            currentUserToValidate++;
        }

        if (!errors.isEmpty()) {
            throw new ValidationListException("Validation failed.", errors);
        }
    }
}
