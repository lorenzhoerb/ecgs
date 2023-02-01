package at.ac.tuwien.sepm.groupphase.backend.endpoint.websocket;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.GradeDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.GradeResultDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.LiveResultDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.MessageErrorDto;
import at.ac.tuwien.sepm.groupphase.backend.exception.BadWebSocketRequestException;
import at.ac.tuwien.sepm.groupphase.backend.exception.NotFoundException;
import at.ac.tuwien.sepm.groupphase.backend.exception.UnauthorizedException;
import at.ac.tuwien.sepm.groupphase.backend.exception.ValidationException;
import at.ac.tuwien.sepm.groupphase.backend.exception.ValidationListException;
import at.ac.tuwien.sepm.groupphase.backend.exception.WebSocketMessageException;
import at.ac.tuwien.sepm.groupphase.backend.service.GradeService;
import at.ac.tuwien.sepm.groupphase.backend.util.SessionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageExceptionHandler;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.stereotype.Controller;

import java.lang.invoke.MethodHandles;

@Controller
public class GradeController {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private final SimpMessagingTemplate messagingTemplate;
    private final SessionUtils sessionUtils;
    private final GradeService gradeService;

    @Autowired
    public GradeController(SimpMessagingTemplate messagingTemplate, SessionUtils sessionUtils, GradeService gradeService) {
        this.messagingTemplate = messagingTemplate;
        this.sessionUtils = sessionUtils;
        this.gradeService = gradeService;
    }

    @MessageMapping("/judge/{competitionId}/{groupId}/{stationId}")
    @SendTo("/topic/judge/{competitionId}/{groupId}/{stationId}")
    public Long judgeHello(@DestinationVariable Long competitionId, @DestinationVariable Long groupId, @DestinationVariable Long stationId) throws Exception {
        try {
            LOGGER.trace("WS Request to: /judge/{}/{}/{}", competitionId, groupId, stationId);
            return this.gradeService.verifyJudgeAndReturnId(competitionId, groupId, stationId);
        } catch (Exception e) {
            throw new WebSocketMessageException(e.getMessage(), null, e);
        }
    }

    @MessageMapping("/goodbye-judge/{competitionId}/{groupId}/{stationId}")
    @SendTo("/topic/goodbye-judge/{competitionId}/{groupId}/{stationId}")
    public Long judgeGracefulGoodbye(@DestinationVariable Long competitionId, @DestinationVariable Long groupId, @DestinationVariable Long stationId) throws Exception {
        try {
            LOGGER.trace("WS Request to: /goodbye-judge/{}/{}/{}", competitionId, groupId, stationId);
            return this.gradeService.verifyJudgeAndReturnId(competitionId, groupId, stationId);
        } catch (Exception e) {
            throw new WebSocketMessageException(e.getMessage(), null, e);
        }
    }

    @MessageMapping("/grade/{competitionId}/{groupId}/{stationName}")
    @SendTo("/topic/grades/{competitionId}/{groupId}/{stationName}")
    public GradeResultDto grade(@DestinationVariable Long competitionId, @DestinationVariable Long groupId, @DestinationVariable String stationName,
                                @Payload GradeDto gradeDto) throws Exception {

        try {
            LOGGER.trace("WS Request to: /judge/{}/{}/{} Payload: {}", competitionId, groupId, stationName, gradeDto);
            LOGGER.info("Grade: {}", gradeDto);
            GradeResultDto result = this.gradeService.updateCompetitionResults(competitionId, groupId, stationName, gradeDto);
            if (result.isValid() && result.result() != null && !result.result().isNaN()) {
                LiveResultDto liveResult = this.gradeService.getAllResultsForParticipantAtStation(competitionId, groupId, result.stationId(), result.participantId(), result.result());
                LOGGER.trace("Sending to /topic/live-results/{} with Grade: {}", competitionId, gradeDto);
                this.messagingTemplate.convertAndSend("/topic/live-results/" + competitionId, liveResult);
            }
            return result;
        } catch (Exception e) {
            throw new WebSocketMessageException(e.getMessage(), gradeDto.uuid(), e);
        }
    }


    @MessageExceptionHandler
    @SendToUser(value = "/queue/error", broadcast = false)
    public MessageErrorDto handleException(Throwable message) {
        LOGGER.trace("handleException {}", message);

        if (!(message instanceof  WebSocketMessageException)) {
            //should never happen
            return new MessageErrorDto()
                .withType(MessageErrorDto.MessageErrorType.UNKNOWN_SERVER_ERROR)
                .withMessage("What the actual..." + message.getMessage());
        }

        WebSocketMessageException wsException = (WebSocketMessageException) message;

        MessageErrorDto.MessageErrorType errorType = MessageErrorDto.MessageErrorType.UNKNOWN_SERVER_ERROR;

        if (wsException.getCause() instanceof BadWebSocketRequestException) {
            errorType = MessageErrorDto.MessageErrorType.BAD_REQUEST;
        } else if (wsException.getCause() instanceof NotFoundException) {
            errorType = MessageErrorDto.MessageErrorType.NOT_FOUND;
        } else if (wsException.getCause() instanceof UnauthorizedException) {
            errorType = MessageErrorDto.MessageErrorType.UNAUTHORIZED;
        } else if (wsException.getCause() instanceof ValidationException || wsException.getCause() instanceof ValidationListException) {
            errorType = MessageErrorDto.MessageErrorType.VALIDATION;
        }

        return new MessageErrorDto()
            .withType(errorType)
            .withUuid(wsException.getUuid())
            .withMessage(wsException.getMessage());

    }

}
