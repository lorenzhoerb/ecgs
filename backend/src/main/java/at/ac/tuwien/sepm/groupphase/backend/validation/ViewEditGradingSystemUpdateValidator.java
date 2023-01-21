package at.ac.tuwien.sepm.groupphase.backend.validation;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.ViewEditGradingSystemDto;
import org.springframework.stereotype.Component;

@Component
public class ViewEditGradingSystemUpdateValidator extends Validator<ViewEditGradingSystemDto> {
    protected ViewEditGradingSystemUpdateValidator(javax.validation.Validator validator) {
        super(validator);
    }

    @Override
    protected void validateCustom(ViewEditGradingSystemDto toValidate) {

    }
}
