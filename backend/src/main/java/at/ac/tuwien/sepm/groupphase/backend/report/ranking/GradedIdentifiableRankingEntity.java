package at.ac.tuwien.sepm.groupphase.backend.report.ranking;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class GradedIdentifiableRankingEntity extends IdentifiableRankingEntity {
    private Double results;

    public GradedIdentifiableRankingEntity(Long id, String name, Double results) {
        super(id, name);
        this.results = results;
    }

    public GradedIdentifiableRankingEntity() {
    }
}
