package at.ac.tuwien.sepm.groupphase.backend.repository.projections;

public interface GradingSystemProjectIdAndNameAndIsPublicAndEditable {
    Long getId();

    String getName();

    Boolean getPublic();

    Boolean getEditable();
}
