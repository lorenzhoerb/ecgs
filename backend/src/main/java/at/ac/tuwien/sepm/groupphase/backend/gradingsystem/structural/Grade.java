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

/**
 * Class that represents a number of grades from a specific judge.
 */
public class Grade {
    @JsonProperty("grades")
    public GradeVariable[] grades;

    public Grade() {
    }

    public Grade(String json) throws ValidationException {
        ObjectMapper mapper = new ObjectMapper();

        try {
            Grade parsed = mapper.readValue(json, Grade.class);
            grades = parsed.grades;
        } catch (JsonProcessingException e) {
            throw new ValidationListException(
                "parsing formula",
                List.of("parsing formula"));
        }
    }

    public void validate() {

        Set<Long> ids = Arrays.stream(grades).map(s -> s.id).collect(Collectors.toSet());

        if (ids.size() != grades.length) {
            throw new ValidationListException(
                "Grades have a duplicate variable id",
                List.of("Grades have a duplicate variable id"));
        }

        for (GradeVariable grade : grades) {
            if (grade.value == null) {
                throw new ValidationException("Value of Variable must not be null!");
            }
        }
    }
}
