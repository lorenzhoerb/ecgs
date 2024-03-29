package at.ac.tuwien.sepm.groupphase.backend.gradingsystem.operations;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Sub extends Operation {
    public Operation left;
    public Operation right;

    public Sub(@JsonProperty("left") Operation left,
               @JsonProperty("right") Operation right) {
        this.left = left;
        this.right = right;
    }

    public Double evaluate() {
        return left.evaluate() - right.evaluate();
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
