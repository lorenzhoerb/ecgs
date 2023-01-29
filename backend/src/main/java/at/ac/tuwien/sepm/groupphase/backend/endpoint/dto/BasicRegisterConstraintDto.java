package at.ac.tuwien.sepm.groupphase.backend.endpoint.dto;

import at.ac.tuwien.sepm.groupphase.backend.entity.RegisterConstraint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class BasicRegisterConstraintDto {

    @NotNull(message = "Type must be given")
    private RegisterConstraint.ConstraintType type;

    @NotNull(message = "Operator must be given")
    private RegisterConstraint.Operator operator;

    @NotEmpty(message = "Value must be given")
    @Size(min = 1, max = 256, message = "Length of value must be between 1 and 256")
    private String value;
}
