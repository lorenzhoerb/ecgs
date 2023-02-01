package at.ac.tuwien.sepm.groupphase.backend.gradingsystem.structural;

import at.ac.tuwien.sepm.groupphase.backend.gradingsystem.strategys.Mean;
import at.ac.tuwien.sepm.groupphase.backend.gradingsystem.strategys.Strategy;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;

/**
 * Variable that stores assigned values and handles it strategy.
 */
public class Variable {
    @JsonProperty("id")
    Long id;
    @JsonProperty("displayName")
    String displayName;
    @JsonProperty("minJudgeCount")
    Long minJudgeCount;
    @JsonProperty("strategy")
    Strategy strategy;
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
        if (strategy == null) {
            strategy = new Mean();
        }
        strategy.verify(values, minJudgeCount);
        return strategy.evaluate(values);
    }

    public Long getId() {
        return this.id;
    }

    public String getDisplayName() {
        return this.displayName;
    }

    public Long getMinJudgeCount() {
        return this.minJudgeCount;
    }

    public Strategy getStrategy() {
        return this.strategy;
    }

    public List<Double> getValues() {
        return this.values;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public void setMinJudgeCount(Long minJudgeCount) {
        this.minJudgeCount = minJudgeCount;
    }

    public void setStrategy(Strategy strategy) {
        this.strategy = strategy;
    }

    public void setValues(List<Double> values) {
        this.values = values;
    }
}
