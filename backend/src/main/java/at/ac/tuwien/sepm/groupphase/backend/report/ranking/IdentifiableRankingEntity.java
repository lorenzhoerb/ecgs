package at.ac.tuwien.sepm.groupphase.backend.report.ranking;

public class IdentifiableRankingEntity {
    private Long id;
    private String name;

    public IdentifiableRankingEntity(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public IdentifiableRankingEntity() {
    }

    public Long getId() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }
}
