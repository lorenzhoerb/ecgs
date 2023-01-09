package at.ac.tuwien.sepm.groupphase.backend.gradingsystem.strategys;

import at.ac.tuwien.sepm.groupphase.backend.exception.StrategyException;

import java.util.List;

public class Mean extends Strategy {
    public void verify(List<Double> values,
                       Long minJudgeCount) throws StrategyException {

        if (minJudgeCount != null && values.size() < minJudgeCount) {
            throw new StrategyException("Not enough grades received!");
        }
    }

    public Double evaluate(List<Double> values) {
        Double result = 0.0;
        Double inverse = 1.0 / values.size();

        for (Double value : values) {
            result += value * inverse;
        }

        return result;
    }
}
