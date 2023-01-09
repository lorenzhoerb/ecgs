package at.ac.tuwien.sepm.groupphase.backend.gradingsystem.operations;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME,
    include = JsonTypeInfo.As.PROPERTY,
    property = "typeHint")
@JsonSubTypes({
    @JsonSubTypes.Type(value = Add.class, name = "add"),
    @JsonSubTypes.Type(value = Sub.class, name = "subt"),
    @JsonSubTypes.Type(value = Constant.class, name = "const"),
    @JsonSubTypes.Type(value = Divide.class, name = "div"),
    @JsonSubTypes.Type(value = Multiply.class, name = "mult"),
    @JsonSubTypes.Type(value = Mean.class, name = "mean"),
    @JsonSubTypes.Type(value = VariableRef.class, name = "variableRef")
})
@JsonIgnoreProperties(ignoreUnknown = true)
public abstract class Operation {

    public abstract Double evaluate();

    public abstract void bind(Long id, Double value);
}
