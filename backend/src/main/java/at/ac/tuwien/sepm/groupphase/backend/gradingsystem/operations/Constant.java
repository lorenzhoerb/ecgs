package at.ac.tuwien.sepm.groupphase.backend.gradingsystem.operations;

import at.ac.tuwien.sepm.groupphase.backend.exception.ValidationListException;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Constant extends Operation {
    public Double value;

    public Constant(@JsonProperty("value") double value) {
        this.value = value;
    }

    public Double evaluate() {
        return value;
    }

    public void bind(Long id, Double value) {
    }

    @Override
    public void validate() {
        if (value == null) {
            throw new ValidationListException("Incomplete formula", "Some values are missing");
        }
    }
}
