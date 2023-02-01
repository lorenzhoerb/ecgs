package at.ac.tuwien.sepm.groupphase.backend.gradingsystem.structural;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Class that represents a grade from a specific judge.
 */
public class GradeVariable {
    @JsonProperty("id") Long id;
    @JsonProperty("value") Double value;

    public GradeVariable(@JsonProperty("id") Long id,
                         @JsonProperty("value") Double value) {
        this.id = id;
        this.value = value;
    }

    public Long getId() {
        return this.id;
    }

    public Double getValue() {
        return this.value;
    }

    @JsonProperty("id")
    public void setId(Long id) {
        this.id = id;
    }

    @JsonProperty("value")
    public void setValue(Double value) {
        this.value = value;
    }
}
