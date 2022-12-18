package at.ac.tuwien.sepm.groupphase.backend.gradingsystem.structural;

import at.ac.tuwien.sepm.groupphase.backend.gradingsystem.strategys.Mean;
import at.ac.tuwien.sepm.groupphase.backend.gradingsystem.strategys.Strategy;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;

public class Variable {
    @JsonProperty("id") Long id;
    @JsonProperty("displayName") String displayName;
    @JsonProperty("minJudgeCount") Long minJudgeCount;
    @JsonProperty("strategy") Strategy strategy;
    List<Double> values;

    public Variable(@JsonProperty("id") Long id,
                    @JsonProperty("displayName") String displayName,
                    @JsonProperty("minJudgeCount") Long minJudgeCount,
                    @JsonProperty("strategy") Strategy strategy) {
        this.id = id;
        this.displayName = displayName;
        this.minJudgeCount = minJudgeCount;
        this.strategy = strategy;
        this.values = new ArrayList<>();
    }

    public Variable(Long id, String displayName) {
        this.id = id;
        this.displayName = displayName;
        this.strategy = new Mean();
        this.values = new ArrayList<>();
    }

    public Double evaluate() {
        strategy.verify(values, minJudgeCount);
        return strategy.evaluate(values);
    }
}
