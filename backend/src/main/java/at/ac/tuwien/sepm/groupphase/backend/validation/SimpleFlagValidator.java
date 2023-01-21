package at.ac.tuwien.sepm.groupphase.backend.validation;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.SimpleFlagDto;

import org.springframework.stereotype.Component;

@Component
public class SimpleFlagValidator extends Validator<SimpleFlagDto> {

    protected SimpleFlagValidator(javax.validation.Validator validator) {
        super(validator);
    }

    public void validateCustom(SimpleFlagDto dto) {

    }
}
