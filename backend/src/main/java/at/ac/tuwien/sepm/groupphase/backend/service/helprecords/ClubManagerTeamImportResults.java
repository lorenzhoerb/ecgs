package at.ac.tuwien.sepm.groupphase.backend.service.helprecords;

public class ClubManagerTeamImportResults {
    private int newParticipantsCount;
    private int oldParticipantsCount;

    public int getNewParticipantsCount() {
        return newParticipantsCount;
    }

    public int getOldParticipantsCount() {
        return oldParticipantsCount;
    }

    public void incrementNewParticipantsCount() {
        this.newParticipantsCount += 1;
    }

    public void incrementOldParticipantsCount() {
        this.oldParticipantsCount += 1;
    }

    public ClubManagerTeamImportResults(int newParticipantsCount, int oldParticipantsCount) {
        this.newParticipantsCount = newParticipantsCount;
        this.oldParticipantsCount = oldParticipantsCount;
    }

    public ClubManagerTeamImportResults() {
        this(0, 0);
    }


}
