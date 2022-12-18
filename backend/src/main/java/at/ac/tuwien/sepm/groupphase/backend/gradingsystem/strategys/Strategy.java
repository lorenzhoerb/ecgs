package at.ac.tuwien.sepm.groupphase.backend.gradingsystem.strategys;

import at.ac.tuwien.sepm.groupphase.backend.exception.StrategyException;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import java.util.List;

@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME,
    include = JsonTypeInfo.As.PROPERTY,
    property = "type")
@JsonSubTypes({
    @JsonSubTypes.Type(value = Mean.class, name = "mean"),
    @JsonSubTypes.Type(value = Equal.class, name = "equal")
})
public abstract class Strategy {

    public abstract void verify(List<Double> values,
                                Long minJudeCount) throws StrategyException;

    public abstract Double evaluate(List<Double> values);
}
