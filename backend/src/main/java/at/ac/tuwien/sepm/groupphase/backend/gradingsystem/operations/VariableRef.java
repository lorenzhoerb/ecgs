package at.ac.tuwien.sepm.groupphase.backend.gradingsystem.operations;

import com.fasterxml.jackson.annotation.JsonProperty;

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
}
