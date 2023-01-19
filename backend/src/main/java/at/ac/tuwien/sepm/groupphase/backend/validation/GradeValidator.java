package at.ac.tuwien.sepm.groupphase.backend.validation;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.GradeDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.GradingSystemDetailDto;
import at.ac.tuwien.sepm.groupphase.backend.gradingsystem.structural.Grade;
import at.ac.tuwien.sepm.groupphase.backend.gradingsystem.structural.GradingSystem;
import org.springframework.stereotype.Component;

@Component
public class GradeValidator extends Validator<GradeDto> {

    protected GradeValidator(javax.validation.Validator validator) {
        super(validator);
    }

    @Override
    protected void validateCustom(GradeDto toValidate) {
        // Throws Validation Exception on parse error
        Grade grade = new Grade(toValidate.grade());
        // Throws Non unique id error
        grade.validate();
    }
}
