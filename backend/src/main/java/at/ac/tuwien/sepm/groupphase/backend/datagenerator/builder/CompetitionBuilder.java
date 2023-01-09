package at.ac.tuwien.sepm.groupphase.backend.datagenerator.builder;

import at.ac.tuwien.sepm.groupphase.backend.datagenerator.UserProvider;
import at.ac.tuwien.sepm.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepm.groupphase.backend.entity.Competition;
import at.ac.tuwien.sepm.groupphase.backend.entity.GradingGroup;
import at.ac.tuwien.sepm.groupphase.backend.entity.RegisterTo;
import at.ac.tuwien.sepm.groupphase.backend.gradingsystem.operations.Add;
import at.ac.tuwien.sepm.groupphase.backend.gradingsystem.operations.Constant;
import at.ac.tuwien.sepm.groupphase.backend.gradingsystem.operations.Divide;
import at.ac.tuwien.sepm.groupphase.backend.gradingsystem.operations.VariableRef;
import at.ac.tuwien.sepm.groupphase.backend.gradingsystem.strategys.Mean;
import at.ac.tuwien.sepm.groupphase.backend.gradingsystem.structural.GradingSystem;
import at.ac.tuwien.sepm.groupphase.backend.gradingsystem.structural.Station;
import at.ac.tuwien.sepm.groupphase.backend.gradingsystem.structural.Variable;
import at.ac.tuwien.sepm.groupphase.backend.repository.ApplicationUserRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.CompetitionRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.GradingGroupRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.GradingSystemRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.RegisterToRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Profile("generateData")
@Component
public class CompetitionBuilder {


    private final ApplicationUserRepository applicationUserRepository;
    private final CompetitionRepository competitionRepository;
    private final GradingGroupRepository gradingGroupRepository;
    private final RegisterToRepository registerToRepository;
    private final GradingSystemRepository gradingSystemRepository;

    private final Competition competition;
    private ApplicationUser creator;
    private List<GradingGroup> gradingGroups;
    private int participantCount = 20;
    private static int COMP_COUNT = 1;

    public CompetitionBuilder(ApplicationUserRepository applicationUserRepository,
                              CompetitionRepository competitionRepository,
                              GradingGroupRepository gradingGroupRepository, RegisterToRepository registerToRepository, GradingSystemRepository gradingSystemRepository) {
        this.applicationUserRepository = applicationUserRepository;
        this.competitionRepository = competitionRepository;
        this.gradingGroupRepository = gradingGroupRepository;
        this.registerToRepository = registerToRepository;
        this.gradingSystemRepository = gradingSystemRepository;
        competition = getDefaultCompetition();
        gradingGroups = getDefaultGradingGroups();
        creator = getDefaultAppUser();
    }

    public Competition create() {
        ApplicationUser creator = applicationUserRepository.save(this.creator);
        competition.setCreator(creator);
        Competition tmpCompetition = competitionRepository.save(competition);

        at.ac.tuwien.sepm.groupphase.backend.entity.GradingSystem gs = getDefaultGradingSystem();
        gs = gradingSystemRepository.save(gs);

        for (GradingGroup g : gradingGroups) {
            g.setGradingSystem(gs);
            g.setCompetitions(tmpCompetition);
        }
        gradingGroupRepository.saveAll(gradingGroups);

        assignParticipants();

        return null;
    }

    private void assignParticipants() {
        for (int i = 0; i < gradingGroups.size(); i++) {
            for (int j = 0; j < participantCount; j++) {
                ApplicationUser participant = UserProvider.getRandomAppUser();
                RegisterTo registerTo = new RegisterTo(
                    participant,
                    gradingGroups.get(i),
                    true
                );
                participant.setRegistrations(Set.of(registerTo));
                if (gradingGroups.get(i).getRegistrations() == null) {
                    gradingGroups.get(i).setRegistrations(new HashSet<>());
                }
                gradingGroups.get(i).getRegistrations().add(registerTo);
                applicationUserRepository.save(participant);
                registerToRepository.save(registerTo);
            }
        }
    }

    public CompetitionBuilder withGradingGroups(Set<String> groupTitles) {
        gradingGroups = groupTitles.stream().map(GradingGroup::new).collect(Collectors.toList());
        return this;
    }

    public CompetitionBuilder withParticipants(int amount) {
        participantCount = amount;
        return this;
    }

    public CompetitionBuilder withName(String name) {
        competition.setName(name);
        return this;
    }

    public CompetitionBuilder withDescription(String description) {
        competition.setDescription(description);
        return this;
    }

    public CompetitionBuilder withCreator(ApplicationUser applicationUser) {
        creator = applicationUser;
        return this;
    }

    public CompetitionBuilder withRegistrationDates(LocalDateTime begin, LocalDateTime end) {
        competition.setBeginOfRegistration(begin);
        competition.setEndOfRegistration(end);
        return this;
    }

    public CompetitionBuilder withCompetitionDates(LocalDateTime begin, LocalDateTime end) {
        competition.setBeginOfCompetition(begin);
        competition.setEndOfCompetition(end);
        return this;
    }

    public CompetitionBuilder setPublic(boolean isPublic) {
        competition.setPublic(isPublic);
        return this;
    }

    public CompetitionBuilder setDraft(boolean isDraft) {
        competition.setDraft(isDraft);
        return this;
    }

    public CompetitionBuilder withCompEmail(String email) {
        competition.setEmail(email);
        return this;
    }

    public CompetitionBuilder withCompPhone(String phone) {
        competition.setPhone(phone);
        return this;
    }

    private ApplicationUser getDefaultAppUser() {
        return new ApplicationUser(
            ApplicationUser.Role.TOURNAMENT_MANAGER,
            "Max",
            "Muster",
            ApplicationUser.Gender.MALE,
            new Date(2000, 10, 10),
            null
        );
    }

    private List<GradingGroup> getDefaultGradingGroups() {
        return List.of(
            new GradingGroup("AK10M"),
            new GradingGroup("AK10W"),
            new GradingGroup("AK14M"),
            new GradingGroup("AK14W"),
            new GradingGroup("Oberstufe M"),
            new GradingGroup("Unterstufe W")
        );
    }

    private at.ac.tuwien.sepm.groupphase.backend.entity.GradingSystem getDefaultGradingSystem() {
        at.ac.tuwien.sepm.groupphase.backend.entity.GradingSystem gs = new at.ac.tuwien.sepm.groupphase.backend.entity.GradingSystem(
            "Test",
            "Test",
            true,
            getGradingSystemFormula(),
            null
        );
        return gs;
    }

    private Competition getDefaultCompetition() {
        LocalDateTime now = LocalDateTime.now();
        return new Competition(
            "Competition " + COMP_COUNT++,
            now.minusDays(2),
            now.plusDays(3),
            now.plusDays(10),
            now.plusDays(11),
            "Lorem ipsum dolor sit amet, consetetur sadipscing elitr, "
                + "sed diam nonumy eirmod tempor invidunt ut labore et dolore magna "
                + "aliquyam erat, sed diam voluptua. At vero eos et accusam et justo duo "
                + "dolores et ea rebum. Stet clita kasd gubergren, no sea takimata sanctus"
                + " est Lorem ipsum dolor sit amet. Lorem ipsum dolor sit amet, consetetur"
                + " sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et "
                + "dolore magna aliquyam erat, sed diam voluptua. At vero eos et accusam "
                + "et justo duo dolores et ea rebum. Stet clita kasd gubergren, no sea "
                + "takimata sanctus est Lorem ipsum dolor sit amet.",
            null,
            true,
            false,
            "test@gmx.at",
            "069917134691");
    }

    private String getGradingSystemFormula() {
        ObjectMapper mapper = new ObjectMapper();
        GradingSystem system = new GradingSystem();
        system.stations = new Station[] {
            new Station(1L, "Station 1", new Variable[] {
                new Variable(1L, "Var 1", 2L, new Mean()),
                new Variable(2L, "Var 2", 1L, new Mean()),
            }, new Add(new VariableRef(1L), new VariableRef(2L))),
            new Station(2L, "Station 2", new Variable[] {
                new Variable(1L, "Var 1", 3L, new Mean()),
            }, new Divide(new VariableRef(1L), new Constant(2.0)))
        };
        system.formula = new Add(new VariableRef(1L), new VariableRef(2L));

        try {
            return mapper.writeValueAsString(system);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
