package at.ac.tuwien.sepm.groupphase.backend.validation;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.ImportFlag;
import org.springframework.stereotype.Component;

@Component
public class ImportFlagValidator extends Validator<ImportFlag> {
    protected ImportFlagValidator(javax.validation.Validator validator) {
        super(validator);
    }

    @Override
    protected void validateCustom(ImportFlag toValidate) {

    }
}
