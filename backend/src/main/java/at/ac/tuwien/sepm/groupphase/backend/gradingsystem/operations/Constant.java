package at.ac.tuwien.sepm.groupphase.backend.gradingsystem.operations;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Constant extends Operation {
    public double value;

    public Constant(@JsonProperty("value") double value) {
        this.value = value;
    }

    public Double evaluate() {
        return value;
    }

    public void bind(Long id, Double value) {
    }
}
