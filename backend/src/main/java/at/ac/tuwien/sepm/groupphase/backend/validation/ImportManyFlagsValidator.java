package at.ac.tuwien.sepm.groupphase.backend.validation;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.ImportFlag;
import at.ac.tuwien.sepm.groupphase.backend.exception.ValidationListException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class ImportManyFlagsValidator {
    private final ImportFlagValidator importFlagValidator;

    @Autowired
    public ImportManyFlagsValidator(ImportFlagValidator importFlagValidator) {
        this.importFlagValidator = importFlagValidator;
    }

    public void validate(List<ImportFlag> flagList) {
        var errors = new ArrayList<String>();
        for (int i = 0; i < flagList.size(); i++) {
            try {
                importFlagValidator.validate(flagList.get(i));
            } catch (ValidationListException e) {
                errors.add(String.format("Flag #%d has some issues:\n%s", i + 1, String.join("\n", e.errors())));
            }
        }

        if (!errors.isEmpty()) {
            throw new ValidationListException("Validation failure", errors);
        }
    }
}
