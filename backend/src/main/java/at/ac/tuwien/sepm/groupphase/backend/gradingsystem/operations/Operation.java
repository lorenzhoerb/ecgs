package at.ac.tuwien.sepm.groupphase.backend.gradingsystem.operations;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

/**
 * Abstract class that encapsulates a mathematical operation like add or subtract.
 */
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

    /**
     * Method responsible for carrying out the formula defined by the operation.
     */
    public abstract Double evaluate();

    /**
     * Method that assigns a value to any operation if it is of type VariableRef otherwise passes the value to the children.
     */
    public abstract void bind(Long id, Double value);

    /**
     * Method that ensures the validity of the operation.
     */
    public abstract void validate();
}
