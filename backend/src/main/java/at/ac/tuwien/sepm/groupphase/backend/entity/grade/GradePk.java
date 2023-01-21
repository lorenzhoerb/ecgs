package at.ac.tuwien.sepm.groupphase.backend.entity.grade;

import at.ac.tuwien.sepm.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepm.groupphase.backend.entity.Judge;
import io.micrometer.core.lang.Nullable;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Embeddable;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
@Embeddable
public class GradePk implements Serializable {
    @NotNull
    private Long judgeId;

    @NotNull
    private Long participantId;

    @NotNull
    private Long competitionId;

    @NotNull
    private Long gradingGroupId;

    @NotNull
    private Long stationId;
}
