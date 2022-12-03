package at.ac.tuwien.sepm.groupphase.backend.validation;


import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.CompetitionDetailDto;
import org.springframework.stereotype.Component;


@Component
public class CompetitionValidator extends Validator<CompetitionDetailDto> {

    protected CompetitionValidator(javax.validation.Validator validator) {
        super(validator);
    }

    @Override
    protected void validateCustom(CompetitionDetailDto toValidate) {
    }
}
