package at.ac.tuwien.sepm.groupphase.backend.gradingsystem.structural;

import at.ac.tuwien.sepm.groupphase.backend.gradingsystem.strategys.Mean;
import at.ac.tuwien.sepm.groupphase.backend.gradingsystem.strategys.Strategy;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class GradeVariable {
    @JsonProperty("id") Long id;
    @JsonProperty("value") Double value;

    public GradeVariable(@JsonProperty("id") Long id,
                         @JsonProperty("value") Double value) {
        this.id = id;
        this.value = value;
    }
}
