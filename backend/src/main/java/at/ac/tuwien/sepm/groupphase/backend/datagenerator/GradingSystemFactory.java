package at.ac.tuwien.sepm.groupphase.backend.datagenerator;

import at.ac.tuwien.sepm.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepm.groupphase.backend.gradingsystem.operations.Add;
import at.ac.tuwien.sepm.groupphase.backend.gradingsystem.operations.Sub;
import at.ac.tuwien.sepm.groupphase.backend.gradingsystem.operations.VariableRef;
import at.ac.tuwien.sepm.groupphase.backend.gradingsystem.strategys.Mean;
import at.ac.tuwien.sepm.groupphase.backend.gradingsystem.structural.GradingSystem;
import at.ac.tuwien.sepm.groupphase.backend.gradingsystem.structural.Station;
import at.ac.tuwien.sepm.groupphase.backend.gradingsystem.structural.Variable;
import at.ac.tuwien.sepm.groupphase.backend.repository.GradingSystemRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;


@Component
public class GradingSystemFactory {

    private final GradingSystemRepository gradingSystemRepository;

    public GradingSystemFactory(GradingSystemRepository gradingSystemRepository) {
        this.gradingSystemRepository = gradingSystemRepository;
    }

    public at.ac.tuwien.sepm.groupphase.backend.entity.GradingSystem getGradingSystem(String gradingSystem, String title, boolean isPublic, boolean isTemplate, ApplicationUser creator) {
        String formula = null;
        if (gradingSystem == null) {
            return null;
        }
        if (gradingSystem.equalsIgnoreCase("turn10M")) {
            formula = tun10FormulaMale();
        }

        if (gradingSystem.equalsIgnoreCase("turn10W")) {
            formula = turn10FormulaFemale();
        }


        at.ac.tuwien.sepm.groupphase.backend.entity.GradingSystem gsEntity = new at.ac.tuwien.sepm.groupphase.backend.entity.GradingSystem();
        gsEntity.setName(title);
        gsEntity.setPublic(isPublic);
        gsEntity.setTemplate(isTemplate);
        gsEntity.setCreator(creator);
        gsEntity.setFormula(formula);
        return gsEntity;
    }

    public at.ac.tuwien.sepm.groupphase.backend.entity.GradingSystem createGradingSystem(String gradingSystem, String title, boolean isPublic, boolean isTemplate, ApplicationUser creator) {
        at.ac.tuwien.sepm.groupphase.backend.entity.GradingSystem gs = getGradingSystem(gradingSystem, title, isPublic, isTemplate, creator);
        return gradingSystemRepository.save(gs);
    }

    private String tun10FormulaMale() {
        return "{\"stations\":[{\"displayName\":\"Barren\",\"id\":1,\"variables\":[{\"displayName\":\"A-Note\",\"strategy\":{\"type\":\"mean\"},\"minJudgeCount\":2,\"id\":1},{\"displayName\":\"B-Note\",\"strategy\":{\"type\":\"mean\"},\"minJudgeCount\":2,\"id\":2},{\"displayName\":\"Abzug\",\"strategy\":{\"type\":\"mean\"},\"minJudgeCount\":2,\"id\":3}],\"formula\":{\"name\":\"-\",\"value\":\"subt\",\"typeHint\":\"subt\",\"type\":\"function\",\"spaces\":2,\"priority\":2,\"left\":{\"name\":\"+\",\"value\":\"add\",\"typeHint\":\"add\",\"type\":\"function\",\"spaces\":2,\"priority\":2,\"left\":{\"name\":\"A-Note\",\"type\":\"variable\",\"typeHint\":\"variableRef\",\"strategy\":{\"type\":\"mean\"},\"value\":1},\"right\":{\"name\":\"B-Note\",\"type\":\"variable\",\"typeHint\":\"variableRef\",\"strategy\":{\"type\":\"mean\"},\"value\":2}},\"right\":{\"name\":\"Abzug\",\"type\":\"variable\",\"typeHint\":\"variableRef\",\"strategy\":{\"type\":\"mean\"},\"value\":3}}},{\"displayName\":\"Ringe\",\"id\":2,\"variables\":[{\"displayName\":\"A-Note\",\"strategy\":{\"type\":\"mean\"},\"minJudgeCount\":1,\"id\":1},{\"displayName\":\"B-Note\",\"strategy\":{\"type\":\"mean\"},\"minJudgeCount\":1,\"id\":2},{\"displayName\":\"Abzug\",\"strategy\":{\"type\":\"mean\"},\"minJudgeCount\":1,\"id\":3}],\"formula\":{\"name\":\"-\",\"value\":\"subt\",\"typeHint\":\"subt\",\"type\":\"function\",\"spaces\":2,\"priority\":2,\"left\":{\"name\":\"+\",\"value\":\"add\",\"typeHint\":\"add\",\"type\":\"function\",\"spaces\":2,\"priority\":2,\"left\":{\"name\":\"A-Note\",\"type\":\"variable\",\"typeHint\":\"variableRef\",\"strategy\":{\"type\":\"mean\"},\"value\":1},\"right\":{\"name\":\"B-Note\",\"type\":\"variable\",\"typeHint\":\"variableRef\",\"strategy\":{\"type\":\"mean\"},\"value\":2}},\"right\":{\"name\":\"Abzug\",\"type\":\"variable\",\"typeHint\":\"variableRef\",\"strategy\":{\"type\":\"mean\"},\"value\":3}}}],\"formula\":{\"name\":\"+\",\"value\":\"add\",\"typeHint\":\"add\",\"type\":\"function\",\"spaces\":2,\"priority\":2,\"left\":{\"name\":\"Barren\",\"value\":1,\"type\":\"variable\",\"typeHint\":\"variableRef\"},\"right\":{\"name\":\"Ringe\",\"value\":2,\"type\":\"variable\",\"typeHint\":\"variableRef\"}}}";
    }

    private String turn10FormulaFemale() {
        return "{\"stations\":[{\"displayName\":\"Balken\",\"id\":1,\"variables\":[{\"displayName\":\"A-Note\",\"strategy\":{\"type\":\"mean\"},\"minJudgeCount\":2,\"id\":1},{\"displayName\":\"B-Note\",\"strategy\":{\"type\":\"mean\"},\"minJudgeCount\":2,\"id\":2},{\"displayName\":\"Abzug\",\"strategy\":{\"type\":\"mean\"},\"minJudgeCount\":2,\"id\":3}],\"formula\":{\"name\":\"-\",\"value\":\"subt\",\"typeHint\":\"subt\",\"type\":\"function\",\"spaces\":2,\"priority\":2,\"left\":{\"name\":\"+\",\"value\":\"add\",\"typeHint\":\"add\",\"type\":\"function\",\"spaces\":2,\"priority\":2,\"left\":{\"name\":\"A-Note\",\"type\":\"variable\",\"typeHint\":\"variableRef\",\"strategy\":{\"type\":\"mean\"},\"value\":1},\"right\":{\"name\":\"B-Note\",\"type\":\"variable\",\"typeHint\":\"variableRef\",\"strategy\":{\"type\":\"mean\"},\"value\":2}},\"right\":{\"name\":\"Abzug\",\"type\":\"variable\",\"typeHint\":\"variableRef\",\"strategy\":{\"type\":\"mean\"},\"value\":3}}},{\"displayName\":\"Stufenbarren\",\"id\":2,\"variables\":[{\"displayName\":\"A-Note\",\"strategy\":{\"type\":\"mean\"},\"minJudgeCount\":1,\"id\":1},{\"displayName\":\"B-Note\",\"strategy\":{\"type\":\"mean\"},\"minJudgeCount\":1,\"id\":2},{\"displayName\":\"Abzug\",\"strategy\":{\"type\":\"mean\"},\"minJudgeCount\":1,\"id\":3}],\"formula\":{\"name\":\"-\",\"value\":\"subt\",\"typeHint\":\"subt\",\"type\":\"function\",\"spaces\":2,\"priority\":2,\"left\":{\"name\":\"+\",\"value\":\"add\",\"typeHint\":\"add\",\"type\":\"function\",\"spaces\":2,\"priority\":2,\"left\":{\"name\":\"A-Note\",\"type\":\"variable\",\"typeHint\":\"variableRef\",\"strategy\":{\"type\":\"mean\"},\"value\":1},\"right\":{\"name\":\"B-Note\",\"type\":\"variable\",\"typeHint\":\"variableRef\",\"strategy\":{\"type\":\"mean\"},\"value\":2}},\"right\":{\"name\":\"Abzug\",\"type\":\"variable\",\"typeHint\":\"variableRef\",\"strategy\":{\"type\":\"mean\"},\"value\":3}}}],\"formula\":{\"name\":\"+\",\"value\":\"add\",\"typeHint\":\"add\",\"type\":\"function\",\"spaces\":2,\"priority\":2,\"left\":{\"name\":\"Barren\",\"value\":1,\"type\":\"variable\",\"typeHint\":\"variableRef\"},\"right\":{\"name\":\"Ringe\",\"value\":2,\"type\":\"variable\",\"typeHint\":\"variableRef\"}}}";
    }
}
