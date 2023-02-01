package at.ac.tuwien.sepm.groupphase.backend.datagenerator;

import at.ac.tuwien.sepm.groupphase.backend.datagenerator.builder.CompetitionBuilder;
import at.ac.tuwien.sepm.groupphase.backend.datagenerator.builder.UserBuilder;
import at.ac.tuwien.sepm.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepm.groupphase.backend.entity.GradingGroup;
import at.ac.tuwien.sepm.groupphase.backend.entity.GradingSystem;
import at.ac.tuwien.sepm.groupphase.backend.gradingsystem.operations.Add;
import at.ac.tuwien.sepm.groupphase.backend.gradingsystem.operations.Constant;
import at.ac.tuwien.sepm.groupphase.backend.gradingsystem.operations.Divide;
import at.ac.tuwien.sepm.groupphase.backend.gradingsystem.operations.VariableRef;
import at.ac.tuwien.sepm.groupphase.backend.gradingsystem.strategys.Equal;
import at.ac.tuwien.sepm.groupphase.backend.gradingsystem.strategys.Mean;
import at.ac.tuwien.sepm.groupphase.backend.gradingsystem.structural.Station;
import at.ac.tuwien.sepm.groupphase.backend.gradingsystem.structural.Variable;
import at.ac.tuwien.sepm.groupphase.backend.repository.GradeRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.GradingSystemRepository;
import at.ac.tuwien.sepm.groupphase.backend.service.impl.CustomUserDetailService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.lang.invoke.MethodHandles;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Profile("generateData")
@Component
public class GradingSystemGenerator {
    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    UserBuilder userBuilder;
    GradingSystemRepository gradingSystemRepository;

    public GradingSystemGenerator(UserBuilder userBuilder,
                                  GradingSystemRepository gradingSystemRepository) {
        this.userBuilder = userBuilder;
        this.gradingSystemRepository = gradingSystemRepository;
    }

    @PostConstruct
    private void generateGradingSystems() {
        LOGGER.debug("generateGradingSystems");

        ApplicationUser user = userBuilder.builder()
            .withLogin("tm99@email.com", "12345678")
            .withName("Franz", "Fischer")
            .withRole(ApplicationUser.Role.TOURNAMENT_MANAGER)
            .withGender(ApplicationUser.Gender.MALE)
            .create();

        GradingSystem sys = new GradingSystem();
        sys.setName("Turnen");
        sys.setDescription("Bewertungssystem für ein Turnier mit Reck und Barren.");
        sys.setCreator(user);
        sys.setGradingGroup(getDefaultGradingGroups());
        sys.setFormula(getGymnasticsFormula());
        sys.setPublic(true);
        sys.setTemplate(true);
        gradingSystemRepository.save(sys);

        GradingSystem sys2 = new GradingSystem();
        sys2.setName("Leichtathletik");
        sys2.setDescription("Bewertungssystem für ein Turnier mit Dreisprung, 100m Sprint und 200m Sprint.");
        sys2.setCreator(user);
        sys2.setGradingGroup(getDefaultGradingGroups());
        sys2.setFormula(getAthleticsFormula());
        sys2.setPublic(true);
        sys2.setTemplate(true);
        gradingSystemRepository.save(sys2);

        GradingSystem sys3 = new GradingSystem();
        sys3.setName("Triathlon");
        sys3.setDescription("Bewertungssystem für ein Triathlonturnier.");
        sys3.setCreator(user);
        sys3.setGradingGroup(getDefaultGradingGroups());
        sys3.setFormula(getTriathlonFormula());
        sys3.setPublic(true);
        sys3.setTemplate(true);
        gradingSystemRepository.save(sys3);
    }

    private Set<GradingGroup> getDefaultGradingGroups() {
        return new HashSet<>(List.of(
            new GradingGroup("AK10M"),
            new GradingGroup("AK10W"),
            new GradingGroup("AK14M"),
            new GradingGroup("AK14W"),
            new GradingGroup("Oberstufe M"),
            new GradingGroup("Unterstufe W")
        ));
    }

    private String getGymnasticsFormula() {
        ObjectMapper mapper = new ObjectMapper();
        at.ac.tuwien.sepm.groupphase.backend.gradingsystem.structural.GradingSystem system =
            new at.ac.tuwien.sepm.groupphase.backend.gradingsystem.structural.GradingSystem();
        system.stations = new Station[]{
            new Station(1L, "Barren", new Variable[]{
                new Variable(1L, "Haltung", 2L, new Mean()),
                new Variable(2L, "Schwierigkeit", 1L, new Mean()),
            }, new Add(new VariableRef(1L), new VariableRef(2L))),
            new Station(2L, "Reck", new Variable[]{
                new Variable(1L, "Haltung", 3L, new Mean()),
                new Variable(2L, "Schwierigkeit", 3L, new Mean()),
            }, new Divide(new Add(new VariableRef(1L), new VariableRef(2L)), new Constant(2.0)))
        };
        system.formula = new Add(new VariableRef(1L), new VariableRef(2L));

        try {
            return mapper.writeValueAsString(system);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    private String getAthleticsFormula() {
        ObjectMapper mapper = new ObjectMapper();
        at.ac.tuwien.sepm.groupphase.backend.gradingsystem.structural.GradingSystem system =
            new at.ac.tuwien.sepm.groupphase.backend.gradingsystem.structural.GradingSystem();
        system.stations = new Station[]{
            new Station(1L, "Dreisprung", new Variable[]{
                new Variable(1L, "Hop", 2L, new Equal()),
                new Variable(2L, "Step", 2L, new Equal()),
                new Variable(3L, "Jump", 2L, new Equal()),
            }, new Add(new Add(new VariableRef(1L), new VariableRef(2L)), new VariableRef(3L))),
            new Station(2L, "100m Sprint", new Variable[]{
                new Variable(1L, "Zeit", 2L, new Mean()),
            }, new VariableRef(1L)),
            new Station(3L, "200m Sprint", new Variable[]{
                new Variable(1L, "Zeit", 2L, new Mean()),
            }, new VariableRef(1L))
        };
        system.formula = new Add(new Add(new VariableRef(1L), new Divide(new Constant(1.0), new VariableRef(2L))),
            new Divide(new Constant(1.0), new VariableRef(3L)));

        try {
            return mapper.writeValueAsString(system);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    private String getTriathlonFormula() {
        ObjectMapper mapper = new ObjectMapper();
        at.ac.tuwien.sepm.groupphase.backend.gradingsystem.structural.GradingSystem system =
            new at.ac.tuwien.sepm.groupphase.backend.gradingsystem.structural.GradingSystem();
        system.stations = new Station[]{
            new Station(1L, "Schwimmen", new Variable[]{
                new Variable(1L, "Zeit", 2L, new Equal()),
            }, new VariableRef(1L)),
            new Station(2L, "Radfahren", new Variable[]{
                new Variable(1L, "Zeit", 2L, new Mean()),
            }, new VariableRef(1L)),
            new Station(3L, "Laufen", new Variable[]{
                new Variable(1L, "Zeit", 2L, new Mean()),
            }, new VariableRef(1L))
        };
        system.formula = new Add(new Add(new Divide(new Constant(1.0), new VariableRef(1L)),
            new Divide(new Constant(1.0), new VariableRef(2L))),
            new Divide(new Constant(1.0), new VariableRef(3L)));

        try {
            return mapper.writeValueAsString(system);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
