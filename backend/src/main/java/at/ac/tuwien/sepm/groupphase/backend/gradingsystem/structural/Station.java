package at.ac.tuwien.sepm.groupphase.backend.gradingsystem.structural;

import at.ac.tuwien.sepm.groupphase.backend.exception.ValidationListException;
import at.ac.tuwien.sepm.groupphase.backend.gradingsystem.operations.Operation;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Station that represents a number of variables and a formula of how to calculate it.
 */
public class Station {
    @JsonProperty("id")
    Long id;
    @JsonProperty("displayName")
    String displayName;
    @JsonProperty("variables")
    Variable[] variables;
    @JsonProperty("formula")
    Operation formula;

    public Station(@JsonProperty("id") Long id,
                   @JsonProperty("displayName") String displayName,
                   @JsonProperty("variables") Variable[] variables,
                   @JsonProperty("formula") Operation formula) {
        this.id = id;
        this.displayName = displayName;
        this.variables = variables;
        this.formula = formula;
    }

    public void validate() {
        Set<Long> ids = Arrays.stream(variables).map(v -> v.id).collect(Collectors.toSet());

        if (ids.size() != variables.length) {
            throw new ValidationListException(
                "Station " + displayName + " has a duplicate variable id",
                List.of("Station " + displayName + " has a duplicate variable id"));
        }
    }

    public Double evaluate() {
        for (Variable var : variables) {
            formula.bind(var.id, var.evaluate());
        }

        return formula.evaluate();
    }

    public Long getId() {
        return this.id;
    }

    public String getDisplayName() {
        return this.displayName;
    }

    public Variable[] getVariables() {
        return this.variables;
    }

    public Operation getFormula() {
        return this.formula;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public void setVariables(Variable[] variables) {
        this.variables = variables;
    }

    public void setFormula(Operation formula) {
        this.formula = formula;
    }
}
