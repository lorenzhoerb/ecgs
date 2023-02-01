package at.ac.tuwien.sepm.groupphase.backend.integrationtest;

import at.ac.tuwien.sepm.groupphase.backend.basetest.TestDataProvider;
import at.ac.tuwien.sepm.groupphase.backend.config.properties.SecurityProperties;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.GradeDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.GradeResultDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.LiveResultDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.MessageErrorDto;
import at.ac.tuwien.sepm.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepm.groupphase.backend.entity.Competition;
import at.ac.tuwien.sepm.groupphase.backend.gradingsystem.structural.Grade;
import at.ac.tuwien.sepm.groupphase.backend.gradingsystem.structural.GradeVariable;
import at.ac.tuwien.sepm.groupphase.backend.gradingsystem.structural.GradingSystem;
import at.ac.tuwien.sepm.groupphase.backend.repository.*;
import at.ac.tuwien.sepm.groupphase.backend.security.JwtTokenizer;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.simp.stomp.*;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.socket.WebSocketHttpHeaders;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.*;


@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@ActiveProfiles("test")
public class GradingWebSocketEndpointTest extends TestDataProvider {

    private final String WS_CONNECTION_PATH = "ws://localhost:8080/ws/grading";

    @Autowired
    private ApplicationUserRepository applicationUserRepository;
    @Autowired
    private SecurityUserRepository securityUserRepository;

    @Autowired
    private CompetitionRepository competitionRepository;

    @Autowired
    private GradingGroupRepository gradingGroupRepository;

    @Autowired
    private GradeRepository gradeRepository;

    @Autowired
    private GradingSystemRepository gradingSystemRepository;

    @Autowired
    private RegisterToRepository registerToRepository;

    @Autowired
    private SecurityProperties securityProperties;
    @Autowired
    private JwtTokenizer jwtTokenizer;

    private WebSocketStompClient webSocketStompClient;


    private ApplicationUser judge;
    private ApplicationUser judge2;
    private Competition competition;

    @BeforeEach
    public void beforeEach() throws Exception {
        this.gradeRepository.deleteAll();
        this.applicationUserRepository.deleteAll();
        this.competitionRepository.deleteAll();
        this.gradingGroupRepository.deleteAll();
        this.gradingSystemRepository.deleteAll();
        this.registerToRepository.deleteAll();

        this.webSocketStompClient = new WebSocketStompClient(new StandardWebSocketClient());

        this.judge = createValidJudgeUser(applicationUserRepository, securityUserRepository, "judge1@email.net");
        this.judge2 = createValidJudgeUser(applicationUserRepository, securityUserRepository, "judge2@email.net");

        this.competition = createCompetitionEntity(applicationUserRepository,
            registerToRepository,
            gradingGroupRepository,
            competitionRepository,
            true, false, Set.of(this.judge, this.judge2),
            gradingSystemRepository);

        StandardWebSocketClient client = new StandardWebSocketClient();
    }

    @Test
    public void givenLoggedInUser_whenEnterValidGrade_expectReceivingGradeOverTopic() throws Exception {
        BlockingQueue<GradeDto> blockingQueue = new ArrayBlockingQueue<>(1);

        StompSession session = this.setupConnection(webSocketStompClient, this.judge);
        assertTrue(session.isConnected());

        this.connectToGradesDto(session, blockingQueue, this.judge);

        GradeDto gradeDto = getValidEnterGradeDto();

        StompHeaders msgHeader = getStompHeadersForDest(
            "/app/grade/"
                + this.competition.getId() + "/"
                + this.competition.getGradingGroups().stream().findFirst().get().getId()
                + "/Station 1"
        );
        session.send(msgHeader, gradeDto);

        await()
            .atMost(1, TimeUnit.SECONDS)
            .untilAsserted(() -> assertEquals(gradeDto, blockingQueue.poll()));

        List<at.ac.tuwien.sepm.groupphase.backend.entity.grade.Grade> inDb = new ArrayList<>();
        this.gradeRepository.findAll().forEach(inDb::add);

        assertEquals(1, inDb.size());
    }


    @Test
    public void givenLoggedInUserWatchingTopic_whenOtherUserEnterValidGrade_expectReceivingGrade() throws Exception {
        //Watching User setup
        WebSocketStompClient watchingWebSocketStompClient = new WebSocketStompClient(new StandardWebSocketClient());

        BlockingQueue<GradeDto> watchingBlockingQueue = new ArrayBlockingQueue<>(1);
        BlockingQueue<MessageErrorDto> watchingErrorBlockingQueue = new ArrayBlockingQueue<>(1);

        StompSession watchingSession = this.setupConnection(watchingWebSocketStompClient, this.judge);

        this.connectToGradesDto(watchingSession, watchingBlockingQueue, this.judge);
        this.connectToMessageErrorQueue(watchingSession, watchingErrorBlockingQueue, this.judge);


        //Entering User Setup
        BlockingQueue<GradeDto> blockingQueue = new ArrayBlockingQueue<>(1);
        BlockingQueue<MessageErrorDto> errorBlockingQueue = new ArrayBlockingQueue<>(1);

        StompSession session = this.setupConnection(this.webSocketStompClient, this.judge2);

        this.connectToGradesDto(session, blockingQueue, this.judge2);
        this.connectToMessageErrorQueue(session, errorBlockingQueue, this.judge2);


        //Enter valid grade
        GradeDto gradeDto = getValidEnterGradeDto().withJudgeId(this.judge2.getId());

        StompHeaders msgHeader = getStompHeadersForDestForUser(
            "/app/grade/"
                + this.competition.getId() + "/"
                + this.competition.getGradingGroups().stream().findFirst().get().getId()
                + "/Station 1", this.judge2
        );
        session.send(msgHeader, gradeDto);

        //assertions

        await()
            .atMost(1, TimeUnit.SECONDS)
            .untilAsserted(() -> assertEquals(gradeDto, blockingQueue.poll()));

        await()
            .atMost(1, TimeUnit.SECONDS)
            .untilAsserted(() -> assertEquals(gradeDto, watchingBlockingQueue.poll()));

        assertNull(errorBlockingQueue.poll(2, TimeUnit.SECONDS));
        assertNull(watchingErrorBlockingQueue.poll(2, TimeUnit.SECONDS));

        List<at.ac.tuwien.sepm.groupphase.backend.entity.grade.Grade> inDb = new ArrayList<>();
        this.gradeRepository.findAll().forEach(inDb::add);

        assertEquals(1, inDb.size());
    }

    @Test
    public void givenLoggedInUserWatchingTopic_whenOtherUserEnterValidGradeAndGradeIsFinished_expectReceivingGradeWithResult() throws Exception {
        //Watching User setup
        WebSocketStompClient watchingWebSocketStompClient = new WebSocketStompClient(new StandardWebSocketClient());

        BlockingQueue<GradeResultDto> watchingBlockingQueue = new ArrayBlockingQueue<>(1);
        BlockingQueue<MessageErrorDto> watchingErrorBlockingQueue = new ArrayBlockingQueue<>(1);

        StompSession watchingSession = this.setupConnection(watchingWebSocketStompClient, this.judge);

        this.connectToGradeResultDto(watchingSession, watchingBlockingQueue, this.judge);
        this.connectToMessageErrorQueue(watchingSession, watchingErrorBlockingQueue, this.judge);

        GradeDto gradeDto = getValidEnterGradeDto();

        StompHeaders msgHeader = getStompHeadersForDestForUser(
            "/app/grade/"
                + this.competition.getId() + "/"
                + this.competition.getGradingGroups().stream().findFirst().get().getId()
                + "/Station 1", this.judge
        );
        watchingSession.send(msgHeader, gradeDto);

        assertNull(watchingErrorBlockingQueue.poll(2, TimeUnit.SECONDS));

        await()
            .atMost(1, TimeUnit.SECONDS)
            .until(() -> {
                GradeResultDto resultDto = watchingBlockingQueue.poll();
                assertEquals(gradeDto.uuid(), resultDto.uuid());
                assertFalse(resultDto.isValid());
                assertTrue(resultDto.result().isNaN());
                return true;
            });



        //Entering User Setup
        BlockingQueue<GradeResultDto> blockingQueue = new ArrayBlockingQueue<>(1);
        BlockingQueue<MessageErrorDto> errorBlockingQueue = new ArrayBlockingQueue<>(1);

        StompSession session = this.setupConnection(this.webSocketStompClient, this.judge2);

        this.connectToGradeResultDto(session, blockingQueue, this.judge2);
        this.connectToMessageErrorQueue(session, errorBlockingQueue, this.judge2);



        //Enter valid grade
        GradeDto gradeDto2 = getValidEnterGradeDto().withJudgeId(this.judge2.getId());

        StompHeaders msgHeader2 = getStompHeadersForDestForUser(
            "/app/grade/"
                + this.competition.getId() + "/"
                + this.competition.getGradingGroups().stream().findFirst().get().getId()
                + "/Station 1", this.judge2
        );
        session.send(msgHeader2, gradeDto2);



        //assertions
        MessageErrorDto errorDto = errorBlockingQueue.poll(2, TimeUnit.SECONDS);

        assertNull(errorDto);
        assertNull(watchingErrorBlockingQueue.poll(2, TimeUnit.SECONDS));

        await()
            .atMost(6, TimeUnit.SECONDS)
            .until(() -> {
                GradeResultDto resultDto = blockingQueue.poll(5, TimeUnit.SECONDS);
                assertEquals(gradeDto2.uuid(), resultDto.uuid());
                assertTrue(resultDto.isValid());
                assertEquals(10.0, resultDto.result());
                return true;
            });

        assertNull(watchingErrorBlockingQueue.poll(2, TimeUnit.SECONDS));

        await()
            .atMost(6, TimeUnit.SECONDS)
            .until(() -> {
                GradeResultDto resultDto = watchingBlockingQueue.poll(5, TimeUnit.SECONDS);
                assertEquals(gradeDto2.uuid(), resultDto.uuid());
                assertTrue(resultDto.isValid());
                assertEquals(10.0, resultDto.result());
                return true;
            });

        List<at.ac.tuwien.sepm.groupphase.backend.entity.grade.Grade> inDb = new ArrayList<>();
        this.gradeRepository.findAll().forEach(inDb::add);

        assertEquals(2, inDb.size());
    }

    @Test
    public void givenLoggedInUserWatchingLiveResults_whenJudgesEnterValidGrade_expectReceivingLiveResult() throws Exception {
        //Watching User setup
        ApplicationUser user = createValidParticipantUser(this.applicationUserRepository, this.securityUserRepository);

        WebSocketStompClient liveWebSocketStompClient = new WebSocketStompClient(new StandardWebSocketClient());

        BlockingQueue<LiveResultDto> liveBlockingQueue = new ArrayBlockingQueue<>(1);
        BlockingQueue<MessageErrorDto> liveErrorBlockingQueue = new ArrayBlockingQueue<>(1);


        StompSession liveSession = this.setupConnection(liveWebSocketStompClient, user);

        this.connectToLiveResultsDto(liveSession, liveBlockingQueue, user);
        this.connectToMessageErrorQueue(liveSession, liveErrorBlockingQueue, user);


        //First Judge
        WebSocketStompClient watchingWebSocketStompClient = new WebSocketStompClient(new StandardWebSocketClient());
        BlockingQueue<GradeResultDto> watchingBlockingQueue = new ArrayBlockingQueue<>(1);
        BlockingQueue<MessageErrorDto> watchingErrorBlockingQueue = new ArrayBlockingQueue<>(1);

        StompSession watchingSession = this.setupConnection(watchingWebSocketStompClient, this.judge);

        this.connectToGradeResultDto(watchingSession, watchingBlockingQueue, this.judge);
        this.connectToMessageErrorQueue(watchingSession, watchingErrorBlockingQueue, this.judge);

        GradeDto gradeDto = getValidEnterGradeDto();

        StompHeaders msgHeader = getStompHeadersForDestForUser(
            "/app/grade/"
                + this.competition.getId() + "/"
                + this.competition.getGradingGroups().stream().findFirst().get().getId()
                + "/Station 1", this.judge
        );
        watchingSession.send(msgHeader, gradeDto);

        assertNull(watchingErrorBlockingQueue.poll(2, TimeUnit.SECONDS));
        assertNull(liveBlockingQueue.poll(2, TimeUnit.SECONDS));

        await()
            .atMost(1, TimeUnit.SECONDS)
            .until(() -> {
                GradeResultDto resultDto = watchingBlockingQueue.poll();
                assertEquals(gradeDto.uuid(), resultDto.uuid());
                assertFalse(resultDto.isValid());
                assertTrue(resultDto.result().isNaN());
                return true;
            });


        //Entering User Setup
        BlockingQueue<GradeResultDto> blockingQueue = new ArrayBlockingQueue<>(1);
        BlockingQueue<MessageErrorDto> errorBlockingQueue = new ArrayBlockingQueue<>(1);

        StompSession session = this.setupConnection(this.webSocketStompClient, this.judge2);

        this.connectToGradeResultDto(session, blockingQueue, this.judge2);
        this.connectToMessageErrorQueue(session, errorBlockingQueue, this.judge2);

        //Enter valid grade

        GradeDto gradeDto2 = getValidEnterGradeDto().withJudgeId(this.judge2.getId());

        StompHeaders msgHeader2 = getStompHeadersForDestForUser(
            "/app/grade/"
                + this.competition.getId() + "/"
                + this.competition.getGradingGroups().stream().findFirst().get().getId()
                + "/Station 1", this.judge2
        );
        session.send(msgHeader2, gradeDto2);

        //assertions

        MessageErrorDto errorDto = errorBlockingQueue.poll(2, TimeUnit.SECONDS);

        assertNull(errorDto);
        assertNull(watchingErrorBlockingQueue.poll(2, TimeUnit.SECONDS));
        assertNull(liveErrorBlockingQueue.poll(2, TimeUnit.SECONDS));

        await()
            .atMost(6, TimeUnit.SECONDS)
            .until(() -> {
                GradeResultDto resultDto = blockingQueue.poll(5, TimeUnit.SECONDS);
                assertEquals(gradeDto2.uuid(), resultDto.uuid());
                assertTrue(resultDto.isValid());
                assertEquals(10.0, resultDto.result());
                return true;
            });

        await()
            .atMost(6, TimeUnit.SECONDS)
            .until(() -> {
                GradeResultDto resultDto = watchingBlockingQueue.poll(5, TimeUnit.SECONDS);
                assertEquals(gradeDto2.uuid(), resultDto.uuid());
                assertTrue(resultDto.isValid());
                assertEquals(10.0, resultDto.result());
                return true;
            });

        await()
            .atMost(6, TimeUnit.SECONDS)
            .until(() -> {
                LiveResultDto resultDto = liveBlockingQueue.poll(5, TimeUnit.SECONDS);
                assertNotNull(resultDto);
                assertEquals(2, resultDto.grades().size());
                assertEquals(10.0, resultDto.grades().get(0).result());
                return true;
            });

        List<at.ac.tuwien.sepm.groupphase.backend.entity.grade.Grade> inDb = new ArrayList<>();
        this.gradeRepository.findAll().forEach(inDb::add);

        assertEquals(2, inDb.size());
    }

    @Test
    public void givenLoggedInUser_whenJudgeHello_expectReceivingJudgeIdOverTopic() throws Exception {
        BlockingQueue<Long> blockingQueue = new ArrayBlockingQueue<>(1);

        StompSession session = this.setupConnection(webSocketStompClient, this.judge);


        StompHeaders subscriptionHeaders = getStompHeadersForDest("/topic/judge/"
            + this.competition.getId() + "/"
            + this.competition.getGradingGroups().stream().findFirst().get().getId()
            + "/1");
        session.subscribe(subscriptionHeaders, new StompFrameHandler() {

            @Override
            public Type getPayloadType(StompHeaders headers) {
                return Long.class;
            }

            @Override
            public void handleFrame(StompHeaders headers, Object payload) {
                blockingQueue.add((Long) payload);
            }
        });

        StompHeaders msgHeader = getStompHeadersForDest(
            "/app/judge/"
                + this.competition.getId() + "/"
                + this.competition.getGradingGroups().stream().findFirst().get().getId()
                + "/1"
        );
        session.send(msgHeader, "");

        await()
            .atMost(1, TimeUnit.SECONDS)
            .untilAsserted(() -> assertEquals(this.judge.getId(), blockingQueue.poll()));
    }


    @Test
    public void givenLoggedInUser_whenGoodbyeJudge_expectReceivingJudgeIdOverGoodbyeTopic() throws Exception {
        BlockingQueue<Long> blockingQueue = new ArrayBlockingQueue<>(1);

        StompHeaders stompHeaders = getStompConnectionHeader();

        webSocketStompClient.setMessageConverter(new MappingJackson2MessageConverter());

        StompSession session = webSocketStompClient
            .connect(
                WS_CONNECTION_PATH,
                new WebSocketHttpHeaders(new HttpHeaders()), stompHeaders,
                new StompSessionHandlerAdapter() {

                }).get(1, TimeUnit.SECONDS);

        assertTrue(session.isConnected());

        StompHeaders subscriptionHeaders = getStompHeadersForDest("/topic/goodbye-judge/"
            + this.competition.getId() + "/"
            + this.competition.getGradingGroups().stream().findFirst().get().getId()
            + "/1");
        session.subscribe(subscriptionHeaders, new StompFrameHandler() {

            @Override
            public Type getPayloadType(StompHeaders headers) {
                return Long.class;
            }

            @Override
            public void handleFrame(StompHeaders headers, Object payload) {
                blockingQueue.add((Long) payload);
            }
        });

        StompHeaders msgHeader = getStompHeadersForDest(
            "/app/goodbye-judge/"
                + this.competition.getId() + "/"
                + this.competition.getGradingGroups().stream().findFirst().get().getId()
                + "/1"
        );
        session.send(msgHeader, "");

        await()
            .atMost(1, TimeUnit.SECONDS)
            .untilAsserted(() -> assertEquals(this.judge.getId(), blockingQueue.poll()));
    }


    @Test
    public void givenLoggedInUserWhoIsNotAJudge_whenEnterGrade_expectReceivingErrorUnauthorized() throws Exception {
        ApplicationUser user = createValidParticipantUser(this.applicationUserRepository, this.securityUserRepository);

        BlockingQueue<GradeDto> blockingQueue = new ArrayBlockingQueue<>(1);
        BlockingQueue<MessageErrorDto> errorBlockingQueue = new ArrayBlockingQueue<>(1);

        StompSession session = this.setupConnection(webSocketStompClient, user);

        this.connectToGradesDto(session, blockingQueue, user);
        this.connectToMessageErrorQueue(session, errorBlockingQueue, user);

        GradeDto gradeDto = getValidEnterGradeDto().withJudgeId(user.getId());

        StompHeaders msgHeader = getStompHeadersForDestForUser(
            "/app/grade/"
                + this.competition.getId() + "/"
                + this.competition.getGradingGroups().stream().findFirst().get().getId()
                + "/Station 1", user
        );
        session.send(msgHeader, gradeDto);

        assertNull(blockingQueue.poll(2, TimeUnit.SECONDS));

        MessageErrorDto errorRes = errorBlockingQueue.poll(2, TimeUnit.SECONDS);

        assertNotNull(errorRes);
        assertEquals(MessageErrorDto.MessageErrorType.UNAUTHORIZED, errorRes.getType());
        assertEquals(gradeDto.uuid(), errorRes.getUuid());

        List<at.ac.tuwien.sepm.groupphase.backend.entity.grade.Grade> inDb = new ArrayList<>();
        this.gradeRepository.findAll().forEach(inDb::add);

        assertEquals(0, inDb.size());
    }

    @Test
    public void givenLoggedInUser_whenEnterGradeWithDifferentJudgeId_expectReceivingErrorUnauthorized() throws Exception {
        BlockingQueue<GradeDto> blockingQueue = new ArrayBlockingQueue<>(1);
        BlockingQueue<MessageErrorDto> errorBlockingQueue = new ArrayBlockingQueue<>(1);

        StompSession session = this.setupConnection(webSocketStompClient, this.judge);

        this.connectToGradesDto(session, blockingQueue, this.judge);
        this.connectToMessageErrorQueue(session, errorBlockingQueue, this.judge);

        GradeDto gradeDto = getValidEnterGradeDto().withJudgeId(this.judge.getId() + 1);

        StompHeaders msgHeader = getStompHeadersForDest(
            "/app/grade/"
                + this.competition.getId() + "/"
                + this.competition.getGradingGroups().stream().findFirst().get().getId()
                + "/Station 1"
        );
        session.send(msgHeader, gradeDto);

        assertNull(blockingQueue.poll(2, TimeUnit.SECONDS));

        MessageErrorDto errorRes = errorBlockingQueue.poll(2, TimeUnit.SECONDS);

        assertNotNull(errorRes);
        assertEquals(MessageErrorDto.MessageErrorType.UNAUTHORIZED, errorRes.getType());
        assertEquals(gradeDto.uuid(), errorRes.getUuid());

        List<at.ac.tuwien.sepm.groupphase.backend.entity.grade.Grade> inDb = new ArrayList<>();
        this.gradeRepository.findAll().forEach(inDb::add);

        assertEquals(0, inDb.size());
    }

    @Test
    public void givenLoggedInUser_whenEnterGradeWithBadStationId_expectReceivingErrorNotFound() throws Exception {
        BlockingQueue<GradeDto> blockingQueue = new ArrayBlockingQueue<>(1);
        BlockingQueue<MessageErrorDto> errorBlockingQueue = new ArrayBlockingQueue<>(1);

        StompSession session = this.setupConnection(webSocketStompClient, this.judge);

        this.connectToGradesDto(session, blockingQueue, this.judge);
        this.connectToMessageErrorQueue(session, errorBlockingQueue, this.judge);

        GradeDto gradeDto = getValidEnterGradeDto().withStationId(10L);

        StompHeaders msgHeader = getStompHeadersForDest(
            "/app/grade/"
                + this.competition.getId() + "/"
                + this.competition.getGradingGroups().stream().findFirst().get().getId()
                + "/Station 1"
        );
        session.send(msgHeader, gradeDto);

        assertNull(blockingQueue.poll(2, TimeUnit.SECONDS));

        MessageErrorDto errorRes = errorBlockingQueue.poll(2, TimeUnit.SECONDS);

        assertNotNull(errorRes);
        assertEquals(MessageErrorDto.MessageErrorType.NOT_FOUND, errorRes.getType());
        assertEquals(gradeDto.uuid(), errorRes.getUuid());

        List<at.ac.tuwien.sepm.groupphase.backend.entity.grade.Grade> inDb = new ArrayList<>();
        this.gradeRepository.findAll().forEach(inDb::add);

        assertEquals(0, inDb.size());
    }


    @Test
    public void givenLoggedInUser_whenEnterGradeWithNullValue_expectReceivingErrorValidation() throws Exception {
        BlockingQueue<GradeDto> blockingQueue = new ArrayBlockingQueue<>(1);
        BlockingQueue<MessageErrorDto> errorBlockingQueue = new ArrayBlockingQueue<>(1);

        StompSession session = this.setupConnection(webSocketStompClient, this.judge);

        this.connectToGradesDto(session, blockingQueue, this.judge);
        this.connectToMessageErrorQueue(session, errorBlockingQueue, this.judge);

        GradeDto gradeDto = getInvalidGradeDtoWithNullValue();

        StompHeaders msgHeader = getStompHeadersForDest(
            "/app/grade/"
                + this.competition.getId() + "/"
                + this.competition.getGradingGroups().stream().findFirst().get().getId()
                + "/Station 1"
        );
        session.send(msgHeader, gradeDto);

        assertNull(blockingQueue.poll(2, TimeUnit.SECONDS));

        MessageErrorDto errorRes = errorBlockingQueue.poll(2, TimeUnit.SECONDS);

        assertNotNull(errorRes);
        assertEquals(MessageErrorDto.MessageErrorType.VALIDATION, errorRes.getType());
        assertEquals(gradeDto.uuid(), errorRes.getUuid());

        List<at.ac.tuwien.sepm.groupphase.backend.entity.grade.Grade> inDb = new ArrayList<>();
        this.gradeRepository.findAll().forEach(inDb::add);

        assertEquals(0, inDb.size());
    }

    @Test
    public void givenLoggedInUser_whenEnterGradeWithMissingVariable_expectReceivingErrorBadRequest() throws Exception {
        BlockingQueue<GradeDto> blockingQueue = new ArrayBlockingQueue<>(1);
        BlockingQueue<MessageErrorDto> errorBlockingQueue = new ArrayBlockingQueue<>(1);

        StompSession session = this.setupConnection(webSocketStompClient, this.judge);

        this.connectToGradesDto(session, blockingQueue, this.judge);
        this.connectToMessageErrorQueue(session, errorBlockingQueue, this.judge);

        GradeDto gradeDto = getInvalidGradeDtoWithMissingVariable();

        StompHeaders msgHeader = getStompHeadersForDest(
            "/app/grade/"
                + this.competition.getId() + "/"
                + this.competition.getGradingGroups().stream().findFirst().get().getId()
                + "/Station 1"
        );
        session.send(msgHeader, gradeDto);

        assertNull(blockingQueue.poll(2, TimeUnit.SECONDS));

        MessageErrorDto errorRes = errorBlockingQueue.poll(2, TimeUnit.SECONDS);

        assertNotNull(errorRes);
        assertEquals(MessageErrorDto.MessageErrorType.BAD_REQUEST, errorRes.getType());
        assertEquals(gradeDto.uuid(), errorRes.getUuid());

        List<at.ac.tuwien.sepm.groupphase.backend.entity.grade.Grade> inDb = new ArrayList<>();
        this.gradeRepository.findAll().forEach(inDb::add);

        assertEquals(0, inDb.size());
    }


    @Test
    public void givenLoggedInUserWatchingTopic_whenOtherUserEnterBadGrade_expectReceivingNothing() throws Exception {
        //Watching User setup
        WebSocketStompClient watchingWebSocketStompClient = new WebSocketStompClient(new StandardWebSocketClient());
        BlockingQueue<GradeDto> watchingBlockingQueue = new ArrayBlockingQueue<>(1);
        BlockingQueue<MessageErrorDto> watchingErrorBlockingQueue = new ArrayBlockingQueue<>(1);

        StompSession watchingSession = this.setupConnection(watchingWebSocketStompClient, this.judge2);

        assertTrue(watchingSession.isConnected());

        this.connectToGradesDto(watchingSession, watchingBlockingQueue, this.judge2);
        this.connectToMessageErrorQueue(watchingSession, watchingErrorBlockingQueue, this.judge2);


        //Entering User Setup



        BlockingQueue<GradeDto> blockingQueue = new ArrayBlockingQueue<>(1);
        BlockingQueue<MessageErrorDto> errorBlockingQueue = new ArrayBlockingQueue<>(1);

        StompSession session = this.setupConnection(webSocketStompClient, this.judge);

        this.connectToGradesDto(session, blockingQueue, this.judge);
        this.connectToMessageErrorQueue(session, errorBlockingQueue, this.judge);

        //Enter bad grade

        GradeDto gradeDto = getInvalidGradeDtoWithMissingVariable();

        StompHeaders msgHeader = getStompHeadersForDestForUser(
            "/app/grade/"
                + this.competition.getId() + "/"
                + this.competition.getGradingGroups().stream().findFirst().get().getId()
                + "/Station 1", this.judge
        );
        session.send(msgHeader, gradeDto);

        //assertions

        assertNull(blockingQueue.poll(2, TimeUnit.SECONDS));
        assertNull(watchingBlockingQueue.poll(2, TimeUnit.SECONDS));

        MessageErrorDto errorRes = errorBlockingQueue.poll(2, TimeUnit.SECONDS);
        MessageErrorDto noMessage = watchingErrorBlockingQueue.poll(2, TimeUnit.SECONDS);
        assertNull(noMessage);

        assertNotNull(errorRes);
        assertEquals(MessageErrorDto.MessageErrorType.BAD_REQUEST, errorRes.getType());
        assertEquals(gradeDto.uuid(), errorRes.getUuid());

        List<at.ac.tuwien.sepm.groupphase.backend.entity.grade.Grade> inDb = new ArrayList<>();
        this.gradeRepository.findAll().forEach(inDb::add);

        assertEquals(0, inDb.size());
    }


    @Test
    public void givenLoggedInUser_whenEnterGradeWithWrongVariable_expectReceivingErrorBadRequest() throws Exception {
        BlockingQueue<GradeDto> blockingQueue = new ArrayBlockingQueue<>(1);
        BlockingQueue<MessageErrorDto> errorBlockingQueue = new ArrayBlockingQueue<>(1);

        StompSession session = this.setupConnection(webSocketStompClient, this.judge);

        this.connectToGradesDto(session, blockingQueue, this.judge);
        this.connectToMessageErrorQueue(session, errorBlockingQueue, this.judge);

        GradeDto gradeDto = getInvalidGradeDtoWithWrongVariable();

        StompHeaders msgHeader = getStompHeadersForDest(
            "/app/grade/"
                + this.competition.getId() + "/"
                + this.competition.getGradingGroups().stream().findFirst().get().getId()
                + "/Station 1"
        );
        session.send(msgHeader, gradeDto);

        assertNull(blockingQueue.poll(2, TimeUnit.SECONDS));

        MessageErrorDto errorRes = errorBlockingQueue.poll(2, TimeUnit.SECONDS);

        assertNotNull(errorRes);
        assertEquals(MessageErrorDto.MessageErrorType.BAD_REQUEST, errorRes.getType());
        assertEquals(gradeDto.uuid(), errorRes.getUuid());

        List<at.ac.tuwien.sepm.groupphase.backend.entity.grade.Grade> inDb = new ArrayList<>();
        this.gradeRepository.findAll().forEach(inDb::add);

        assertEquals(0, inDb.size());
    }


    @Test
    public void givenNoUser_whenSendingMessage_expectConnectionClosed() throws Exception {
        StompHeaders stompHeaders = getStompConnectionHeader();

        webSocketStompClient.setMessageConverter(new MappingJackson2MessageConverter());

        StompSession session = webSocketStompClient
            .connect(
                WS_CONNECTION_PATH,
                new WebSocketHttpHeaders(new HttpHeaders()), stompHeaders,
                new StompSessionHandlerAdapter() {

                }).get(1, TimeUnit.SECONDS);

        assertTrue(session.isConnected());


        await()
            .atMost(1, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                assertThrows(IllegalStateException.class, () -> {
                    try {
                        stompHeaders.clear();
                        stompHeaders.setDestination("/app/grade/1/station1");
                        session.send(stompHeaders, getValidEnterGradeDto());
                    } catch (Exception e) {
                        throw e;
                    }
                });
            });

    }

    @Test
    public void givenNoUser_whenSubscribing_expectConnectionClosed() throws Exception {
        StompHeaders stompHeaders = getStompConnectionHeader();

        webSocketStompClient.setMessageConverter(new MappingJackson2MessageConverter());

        StompSession session = webSocketStompClient
            .connect(
                WS_CONNECTION_PATH,
                new WebSocketHttpHeaders(new HttpHeaders()), stompHeaders,
                new StompSessionHandlerAdapter() {

                }).get(1, TimeUnit.SECONDS);

        assertTrue(session.isConnected());


        await()
            .atMost(1, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                assertThrows(IllegalStateException.class, () -> {

                    stompHeaders.clear();
                    stompHeaders.setDestination("/topic/grades/1/station1");
                    session.subscribe(stompHeaders, new StompFrameHandler() {

                        @Override
                        public Type getPayloadType(StompHeaders headers) {
                            return GradeDto.class;
                        }

                        @Override
                        public void handleFrame(StompHeaders headers, Object payload) {

                        }
                    });


                });
            });
    }

    @Test
    public void givenNoUser_whenConnecting_expectConnectionClosed() throws Exception {
        StompHeaders stompHeaders = getStompConnectionHeader();
        stompHeaders.clear();

        webSocketStompClient.setMessageConverter(new MappingJackson2MessageConverter());

        //It is intended that the .atMost duration is shorter than the .get
        //This let us test if the connection is actually denied or just lost
        //due to wrong configurations.
        await()
            .atMost(1, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                assertThrows(ConnectionLostException.class, () -> {
                    try {
                        webSocketStompClient
                            .connect(
                                WS_CONNECTION_PATH,
                                new WebSocketHttpHeaders(new HttpHeaders()),
                                stompHeaders,
                                new StompSessionHandlerAdapter() {
                                }).get(2, TimeUnit.SECONDS);
                    } catch (ExecutionException e) {
                        //Throw the real cause instead of the async exception
                        throw e.getCause();
                    }
                });
            });

    }


    private StompSession setupConnection(WebSocketStompClient webSocketStompClient, ApplicationUser user) throws Exception {
        webSocketStompClient.setMessageConverter(new MappingJackson2MessageConverter());

        StompHeaders stompHeaders = getStompConnectionHeaderForUser(user);

        webSocketStompClient.setMessageConverter(new MappingJackson2MessageConverter());

        StompSession session = webSocketStompClient
            .connect(
                WS_CONNECTION_PATH,
                new WebSocketHttpHeaders(new HttpHeaders()), stompHeaders,
                new StompSessionHandlerAdapter() {

                }).get(1, TimeUnit.SECONDS);


        return session;
    }

    private void connectToGradesDto(StompSession session, BlockingQueue<GradeDto> blockingQueue, ApplicationUser user) {
        StompHeaders subscriptionHeaders = getStompHeadersForDestForUser("/topic/grades/"
            + this.competition.getId() + "/"
            + this.competition.getGradingGroups().stream().findFirst().get().getId()
            + "/Station 1", user);
        session.subscribe(subscriptionHeaders, new StompFrameHandler() {

            @Override
            public Type getPayloadType(StompHeaders headers) {
                return GradeDto.class;
            }

            @Override
            public void handleFrame(StompHeaders headers, Object payload) {
                blockingQueue.add((GradeDto) payload);
            }
        });
    }

    private void connectToGradeResultDto(StompSession session, BlockingQueue<GradeResultDto> blockingQueue, ApplicationUser user) {
        StompHeaders subscriptionHeaders = getStompHeadersForDestForUser("/topic/grades/"
            + this.competition.getId() + "/"
            + this.competition.getGradingGroups().stream().findFirst().get().getId()
            + "/Station 1", user);
        session.subscribe(subscriptionHeaders, new StompFrameHandler() {

            @Override
            public Type getPayloadType(StompHeaders headers) {
                return GradeResultDto.class;
            }

            @Override
            public void handleFrame(StompHeaders headers, Object payload) {
                blockingQueue.add((GradeResultDto) payload);
            }
        });
    }

    private void connectToLiveResultsDto(StompSession session, BlockingQueue<LiveResultDto> blockingQueue, ApplicationUser user) {
        StompHeaders subscriptionHeaders = getStompHeadersForDestForUser("/topic/live-results/"
            + this.competition.getId(), user);
        session.subscribe(subscriptionHeaders, new StompFrameHandler() {

            @Override
            public Type getPayloadType(StompHeaders headers) {
                return LiveResultDto.class;
            }

            @Override
            public void handleFrame(StompHeaders headers, Object payload) {
                blockingQueue.add((LiveResultDto) payload);
            }
        });
    }

    private void connectToMessageErrorQueue(StompSession session, BlockingQueue<MessageErrorDto> blockingQueue, ApplicationUser user) {
        StompHeaders subscriptionHeaders = getStompHeadersForDestForUser("/user/queue/error", user);
        session.subscribe(subscriptionHeaders, new StompFrameHandler() {

            @Override
            public Type getPayloadType(StompHeaders headers) {
                return MessageErrorDto.class;
            }

            @Override
            public void handleFrame(StompHeaders headers, Object payload) {
                blockingQueue.add((MessageErrorDto) payload);
            }
        });
    }


    private GradeDto getValidEnterGradeDto() throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();

        Grade grade = new Grade();
        grade.grades = new GradeVariable[] {
            new GradeVariable(1L, 7.6),
            new GradeVariable(2L, 2.4)
        };

        return new GradeDto(
            UUID.randomUUID(),
            this.judge.getId(),
            this.competition.getGradingGroups().stream().toList().get(0).getRegistrations().stream().toList().get(0).getParticipant().getId(),
            this.competition.getId(),
            this.competition.getGradingGroups().stream().toList().get(0).getId(),
            1L, mapper.writeValueAsString(grade));
    }

    private GradeDto getInvalidGradeDtoWithNullValue() throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();

        Grade grade = new Grade();
        grade.grades = new GradeVariable[] {
            new GradeVariable(1L, 7.6),
            new GradeVariable(2L, null)
        };

        return new GradeDto(
            UUID.randomUUID(),
            this.judge.getId(),
            this.competition.getGradingGroups().stream().toList().get(0).getRegistrations().stream().toList().get(0).getParticipant().getId(),
            competition.getId(),
            competition.getGradingGroups().stream().toList().get(0).getId(),
            1L, mapper.writeValueAsString(grade));
    }

    private GradeDto getInvalidGradeDtoWithMissingVariable() throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();

        Grade grade = new Grade();
        grade.grades = new GradeVariable[] {
            new GradeVariable(1L, 7.6)
        };

        return new GradeDto(
            UUID.randomUUID(),
            this.judge.getId(),
            this.competition.getGradingGroups().stream().toList().get(0).getRegistrations().stream().toList().get(0).getParticipant().getId(),
            competition.getId(),
            competition.getGradingGroups().stream().toList().get(0).getId(),
            1L, mapper.writeValueAsString(grade));
    }

    private GradeDto getInvalidGradeDtoWithWrongVariable() throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();

        Grade grade = new Grade();
        grade.grades = new GradeVariable[] {
            new GradeVariable(1L, 7.6),
            new GradeVariable(3L, 6.4)
        };

        return new GradeDto(
            UUID.randomUUID(),
            this.judge.getId(),
            this.competition.getGradingGroups().stream().toList().get(0).getRegistrations().stream().toList().get(0).getParticipant().getId(),
            competition.getId(),
            competition.getGradingGroups().stream().toList().get(0).getId(),
            1L, mapper.writeValueAsString(grade));
    }


    private StompHeaders getStompHeadersForDest(String destination) {
        StompHeaders headers = getStompConnectionHeader();
        headers.setDestination(destination);
        return headers;
    }

    private StompHeaders getStompConnectionHeader() {
        StompHeaders stompHeaders = new StompHeaders();

        stompHeaders.add(securityProperties.getAuthHeader(),
            jwtTokenizer.getAuthToken(
                judge.getUser().getEmail(),
                List.of("ROLE_" + ApplicationUser.Role.PARTICIPANT)
            ));

        return stompHeaders;
    }


    private StompHeaders getStompHeadersForDestForUser(String destination, ApplicationUser user) {
        StompHeaders headers = getStompConnectionHeaderForUser(user);
        headers.setDestination(destination);
        return headers;
    }

    private StompHeaders getStompConnectionHeaderForUser(ApplicationUser user) {
        StompHeaders stompHeaders = new StompHeaders();

        stompHeaders.add(securityProperties.getAuthHeader(),
            jwtTokenizer.getAuthToken(
                user.getUser().getEmail(),
                List.of("ROLE_" + ApplicationUser.Role.PARTICIPANT)
            ));

        return stompHeaders;
    }
}
