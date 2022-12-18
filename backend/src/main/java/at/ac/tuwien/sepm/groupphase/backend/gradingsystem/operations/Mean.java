package at.ac.tuwien.sepm.groupphase.backend.gradingsystem.operations;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Mean extends Operation {
    public Operation[] operations;

    public Mean(@JsonProperty("operations") Operation[] operations) {
        this.operations = operations;
    }

    public Double evaluate() {
        Double result = 0.0;
        Double inverse = 1.0 / operations.length;

        for (Operation op : operations) {
            result += op.evaluate() * inverse;
        }

        return result;
    }

    public void bind(Long id, Double value) {
        for (Operation op : operations) {
            op.bind(id, value);
        }
    }
}
