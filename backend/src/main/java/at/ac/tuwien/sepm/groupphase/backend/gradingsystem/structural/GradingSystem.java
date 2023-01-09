package at.ac.tuwien.sepm.groupphase.backend.gradingsystem.structural;

import at.ac.tuwien.sepm.groupphase.backend.exception.NotFoundException;
import at.ac.tuwien.sepm.groupphase.backend.exception.ValidationException;
import at.ac.tuwien.sepm.groupphase.backend.exception.ValidationListException;
import at.ac.tuwien.sepm.groupphase.backend.gradingsystem.operations.Operation;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class GradingSystem {
    @JsonProperty("stations")
    public Station[] stations;
    @JsonProperty("formula")
    public Operation formula;

    public GradingSystem() {}

    public GradingSystem(String json) throws ValidationException {
        ObjectMapper mapper = new ObjectMapper();

        try {
            GradingSystem parsed = mapper.readValue(json, GradingSystem.class);
            stations = parsed.stations;
            formula = parsed.formula;
        } catch (JsonProcessingException e) {
            throw new ValidationListException(
                "parsing formula",
                List.of("parsing formula"));
        }
    }

    public void bindVariable(Long stationId, Long variableId, Double value) throws NotFoundException {
        Optional<Station> station = Arrays.stream(stations)
                                        .filter(s -> s.id.equals(stationId)).findFirst();

        if (station.isEmpty()) {
            throw new NotFoundException("station not found");
        }

        Optional<Variable> variable = Arrays.stream(station.get().variables)
                                        .filter(v -> v.id.equals(variableId)).findFirst();

        if (variable.isEmpty()) {
            throw new NotFoundException("variable in station not found");
        }

        variable.get().values.add(value);
    }

    public void validate() {
        for (Station station : stations) {
            station.validate();
        }

        Set<Long> ids = Arrays.stream(stations).map(s -> s.id).collect(Collectors.toSet());

        if (ids.size() != stations.length) {
            throw new ValidationListException(
                "GradingSystem has a duplicate station id",
                List.of("GradingSystem has a duplicate station id"));
        }
    }

    public Double evaluate() {
        for (Station station : stations) {
            formula.bind(station.id, station.evaluate());
        }
        return formula.evaluate();
    }
}
