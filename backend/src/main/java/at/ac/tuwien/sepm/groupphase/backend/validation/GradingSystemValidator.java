package at.ac.tuwien.sepm.groupphase.backend.validation;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.GradingSystemDetailDto;
import at.ac.tuwien.sepm.groupphase.backend.gradingsystem.structural.GradingSystem;
import org.springframework.stereotype.Component;

@Component
public class GradingSystemValidator extends Validator<GradingSystemDetailDto> {

    protected GradingSystemValidator(javax.validation.Validator validator) {
        super(validator);
    }

    @Override
    protected void validateCustom(GradingSystemDetailDto toValidate) {
        // Throws Validation Exception on parse error
        GradingSystem system = new GradingSystem(toValidate.formula());
        // Throws Non unique id error
        system.validate();
    }
}
