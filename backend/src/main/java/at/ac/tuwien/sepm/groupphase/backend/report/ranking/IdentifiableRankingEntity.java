package at.ac.tuwien.sepm.groupphase.backend.report.ranking;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class IdentifiableRankingEntity {
    private Long id;
    private String name;

    public IdentifiableRankingEntity(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public IdentifiableRankingEntity() {
    }
}
