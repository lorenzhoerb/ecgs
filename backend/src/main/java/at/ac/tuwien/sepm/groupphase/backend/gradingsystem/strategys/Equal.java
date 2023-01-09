package at.ac.tuwien.sepm.groupphase.backend.gradingsystem.strategys;

import at.ac.tuwien.sepm.groupphase.backend.exception.StrategyException;

import java.util.List;

public class Equal extends Strategy {

    public void verify(List<Double> values,
                       Long minJudgeCount) throws StrategyException {
        if (values.size() != 0) {

            for (Double value : values) {
                if (Math.abs(value - values.get(0)) > 1e-5) {
                    throw new StrategyException("Not all grades are equal!");
                }
            }
        }

        if (minJudgeCount != null && values.size() < minJudgeCount) {
            throw new StrategyException("Not enough grades received!");
        }
    }

    public Double evaluate(List<Double> values) {
        return values.get(0);
    }
}
