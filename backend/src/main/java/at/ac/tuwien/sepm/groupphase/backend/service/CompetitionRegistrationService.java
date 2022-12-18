package at.ac.tuwien.sepm.groupphase.backend.service;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.ResponseParticipantRegistrationDto;

public interface CompetitionRegistrationService {

    /**
     * An authenticated user can register himself. If the session user is a club manager or a
     * competition manager he can register people he manages.
     *
     * @param competitionId id of the competition
     * @return response participant registration dto
     */
    ResponseParticipantRegistrationDto selfRegisterParticipant(Long competitionId, Long groupPreference);

    /**
     * Checks if the authenticated user is registered to
     * the competition {@code competitionId}.
     *
     * @param competitionId competition id
     * @return ture if registered else false
     */
    boolean isRegisteredTo(Long competitionId);
}
