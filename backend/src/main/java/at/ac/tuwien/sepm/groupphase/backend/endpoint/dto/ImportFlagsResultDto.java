package at.ac.tuwien.sepm.groupphase.backend.endpoint.dto;

public class ImportFlagsResultDto {
    int newImportedFlags;

    public ImportFlagsResultDto(int newImportedFlags) {
        this.newImportedFlags = newImportedFlags;
    }

    public int getNewImportedFlags() {
        return newImportedFlags;
    }

    public ImportFlagsResultDto() {

    }

    public void addNewImportedFlags(int newFlagsNumber) {
        this.newImportedFlags += newFlagsNumber;
    }
}
