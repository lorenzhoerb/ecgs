package at.ac.tuwien.sepm.groupphase.backend.report;

import lombok.Data;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
public class GradableEntityInfo {
    private String name = "";
    private Double results;

    public GradableEntityInfo(Double results) {
        this.results = results;
    }

    public GradableEntityInfo(String name) {
        this.name = name;
    }

    public GradableEntityInfo() {

    }

    public GradableEntityInfo(String name, Double results) {
        this.name = name;
        this.results = results;
    }
}
