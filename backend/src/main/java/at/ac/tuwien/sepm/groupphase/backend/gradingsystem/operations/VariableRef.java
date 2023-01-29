package at.ac.tuwien.sepm.groupphase.backend.gradingsystem.operations;

import at.ac.tuwien.sepm.groupphase.backend.exception.ValidationListException;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class VariableRef extends Operation {
    @JsonProperty("value")
    public Long idRef;
    public Double value;

    public VariableRef(@JsonProperty("value") Long value) {
        idRef = value;
    }

    public Double evaluate() {
        return value;
    }

    public void bind(Long id, Double value) {
        if (idRef.equals(id)) {
            this.value = value;
        }
    }

    @Override
    public void validate() {
        if (value == null) {
            throw new ValidationListException("Incomplete formula", "Some values are missing");
        }
    }
}
