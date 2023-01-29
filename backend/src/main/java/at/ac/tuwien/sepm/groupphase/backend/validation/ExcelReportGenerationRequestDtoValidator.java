package at.ac.tuwien.sepm.groupphase.backend.validation;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.ExcelReportGenerationRequestDto;
import org.springframework.stereotype.Component;

@Component
public class ExcelReportGenerationRequestDtoValidator extends Validator<ExcelReportGenerationRequestDto> {
    protected ExcelReportGenerationRequestDtoValidator(javax.validation.Validator validator) {
        super(validator);
    }

    @Override
    protected void validateCustom(ExcelReportGenerationRequestDto toValidate) {

    }
}
