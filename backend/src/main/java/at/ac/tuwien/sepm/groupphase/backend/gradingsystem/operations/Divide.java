package at.ac.tuwien.sepm.groupphase.backend.gradingsystem.operations;

import at.ac.tuwien.sepm.groupphase.backend.exception.EvaluationException;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Divide extends Operation {
    public Operation left;
    public Operation right;

    public Divide(@JsonProperty("left") Operation left,
                  @JsonProperty("right") Operation right) {
        this.left = left;
        this.right = right;
    }

    public Double evaluate() {
        Double denom = right.evaluate();

        if (-1e-7 < denom && denom < 1e-7) {
            throw new EvaluationException("Division by Zero");
        }

        return left.evaluate() / denom;
    }

    public void bind(Long id, Double value) {
        left.bind(id, value);
        right.bind(id, value);
    }

    @Override
    public void validate() {
        left.validate();
        right.validate();
    }
}
