package at.ac.tuwien.sepm.groupphase.backend.gradingsystem.strategys;

import at.ac.tuwien.sepm.groupphase.backend.exception.StrategyException;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import java.util.List;

/**
 * Class that encapsulates how to deal with inputs from multiple judges.
 */
@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME,
    include = JsonTypeInfo.As.PROPERTY,
    property = "type")
@JsonSubTypes({
    @JsonSubTypes.Type(value = Mean.class, name = "mean"),
    @JsonSubTypes.Type(value = Equal.class, name = "equal")
})
public abstract class Strategy {

    /**
     * Verifys the validity of the given values under the strategy.
     * Throws a StrategyException if the values are not valid under the strategy.
     */
    public abstract void verify(List<Double> values,
                                Long minJudeCount) throws StrategyException;

    /**
     * Calculates the value according to the strategy. For example the mean for the mean strategy.
     */
    public abstract Double evaluate(List<Double> values);
}
