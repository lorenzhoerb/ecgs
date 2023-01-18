package at.ac.tuwien.sepm.groupphase.backend.help;

import lombok.Getter;
import lombok.Setter;


@Setter
@Getter
public class GradingSystemProjectIdAndNameAndIsPublicAndEditableAsClass {
    Long id;
    String name;
    Boolean isPublic;
    Boolean editable;

    public GradingSystemProjectIdAndNameAndIsPublicAndEditableAsClass(Long id, String name, Boolean isPublic, Boolean editable) {
        this.id = id;
        this.name = name;
        this.isPublic = isPublic;
        this.editable = editable;
    }

    public GradingSystemProjectIdAndNameAndIsPublicAndEditableAsClass() {

    }

    public void setPublic(Boolean aPublic) {
        isPublic = aPublic;
    }
}
