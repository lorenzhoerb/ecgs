package at.ac.tuwien.sepm.groupphase.backend.service;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.ParticipantManageDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.ParticipantRegistrationDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.ResponseMultiParticipantRegistrationDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.ResponseParticipantRegistrationDto;

import java.util.List;

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
     * A Club Manager or a Tournament Manager can register multiple participants if they manage them.
     * For each participant a group preference can be specified. If the group preference is null,
     * the default group preference gets selected.
     *
     * @param competitionId    Competition id
     * @param registrationDtos Registration list
     * @return Registered participants with assigned grading group.
     */
    ResponseMultiParticipantRegistrationDto registerParticipants(Long competitionId,
                                                                 List<ParticipantRegistrationDto> registrationDtos);

    /**
     * Updates accepted or group id for each participant given in the list.
     *
     * @param competitionId Competition id
     * @param participants list of participants to update
     * @return participants with updated values
     */
    List<ParticipantManageDto> updateParticipants(Long competitionId, List<ParticipantManageDto> participants);

    /**
     * Checks if the authenticated user is registered to
     * the competition {@code competitionId}.
     *
     * @param competitionId competition id
     * @return ture if registered else false
     */
    boolean isRegisteredTo(Long competitionId);
}
