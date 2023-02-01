package at.ac.tuwien.sepm.groupphase.backend.report.ranking;

public class GradedIdentifiableRankingEntity extends IdentifiableRankingEntity {
    private Double results;

    public GradedIdentifiableRankingEntity(Long id, String name, Double results) {
        super(id, name);
        this.results = results;
    }

    public GradedIdentifiableRankingEntity() {
    }

    public Double getResults() {
        return this.results;
    }

    public void setResults(Double results) {
        this.results = results;
    }
}
