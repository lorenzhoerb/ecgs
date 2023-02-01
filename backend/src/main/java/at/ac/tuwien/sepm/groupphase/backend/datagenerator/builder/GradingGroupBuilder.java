package at.ac.tuwien.sepm.groupphase.backend.datagenerator.builder;

import at.ac.tuwien.sepm.groupphase.backend.entity.Competition;
import at.ac.tuwien.sepm.groupphase.backend.entity.GradingGroup;
import at.ac.tuwien.sepm.groupphase.backend.entity.GradingSystem;
import at.ac.tuwien.sepm.groupphase.backend.entity.RegisterConstraint;
import at.ac.tuwien.sepm.groupphase.backend.repository.GradingGroupRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.RegisterConstraintRepository;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class GradingGroupBuilder {
    private final GradingGroupRepository gradingGroupRepository;
    private final RegisterConstraintRepository registerConstraintRepository;

    public GradingGroupBuilder(GradingGroupRepository gradingGroupRepository, RegisterConstraintRepository registerConstraintRepository) {
        this.gradingGroupRepository = gradingGroupRepository;
        this.registerConstraintRepository = registerConstraintRepository;
    }

    public GradingGroupConstruct builder() {
        return new GradingGroupConstruct();
    }

    public class GradingGroupConstruct {
        private String title;
        private List<RegisterConstraint> registerConstraints;
        private GradingSystem gradingSystem;
        private Competition competition;

        public GradingGroupConstruct() {
            title = "Grading Group 1";
            registerConstraints = new ArrayList<>();
        }

        public GradingGroupConstruct withTitle(String title) {
            this.title = title;
            return this;
        }

        public GradingGroupConstruct withConstraints(List<RegisterConstraint> registerConstraints) {
            this.registerConstraints.clear();
            this.registerConstraints.addAll(registerConstraints);
            return this;
        }

        public GradingGroupConstruct withGradingSystem(GradingSystem gradingSystem) {
            this.gradingSystem = gradingSystem;
            return this;
        }

        public GradingGroupConstruct withCompetition(Competition competition) {
            this.competition = competition;
            return this;
        }

        public GradingGroup create() {
            GradingGroup gradingGroup = new GradingGroup();
            gradingGroup.setTitle(title);
            gradingGroup.setCompetitions(competition);
            gradingGroup.setGradingSystem(gradingSystem);
            GradingGroup gradingGroupSaved = gradingGroupRepository.save(gradingGroup);

            for (RegisterConstraint gc : registerConstraints) {
                gc.setGradingGroup(gradingGroupSaved);
                registerConstraintRepository.save(gc);
            }
            return gradingGroupSaved;
        }
    }
}
