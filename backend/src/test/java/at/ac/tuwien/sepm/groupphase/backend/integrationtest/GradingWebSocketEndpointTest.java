package at.ac.tuwien.sepm.groupphase.backend.integrationtest;

import at.ac.tuwien.sepm.groupphase.backend.basetest.TestDataProvider;
import at.ac.tuwien.sepm.groupphase.backend.config.properties.SecurityProperties;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.GradeDto;
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

        this.judge = createValidJudgeUser(applicationUserRepository, securityUserRepository);
        this.competition = createCompetitionEntity(applicationUserRepository,
            registerToRepository,
            gradingGroupRepository,
            competitionRepository,
            true, false, judge,
            gradingSystemRepository);

        StandardWebSocketClient client = new StandardWebSocketClient();
    }

    @Test
    public void givenLoggedInUser_whenEnterValidGrade_expectReceivingGradeOverTopic() throws Exception {
        BlockingQueue<GradeDto> blockingQueue = new ArrayBlockingQueue<>(1);

        StompHeaders stompHeaders = getStompConnectionHeader();

        webSocketStompClient.setMessageConverter(new MappingJackson2MessageConverter());

        StompSession session = webSocketStompClient
            .connect(
                WS_CONNECTION_PATH,
                new WebSocketHttpHeaders(new HttpHeaders()), stompHeaders,
                new StompSessionHandlerAdapter() {

                }).get(1, TimeUnit.SECONDS);

        assertTrue(session.isConnected());

        StompHeaders subscriptionHeaders = getStompHeadersForDest("/topic/grades/"
            + this.competition.getId() + "/"
            + this.competition.getGradingGroups().stream().findFirst().get().getId()
            + "/Station 1");
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

        watchingWebSocketStompClient.setMessageConverter(new MappingJackson2MessageConverter());

        StompHeaders watchingStompHeaders = getStompConnectionHeader();

        StompSession watchingSession = watchingWebSocketStompClient
            .connect(
                WS_CONNECTION_PATH,
                new WebSocketHttpHeaders(new HttpHeaders()), watchingStompHeaders,
                new StompSessionHandlerAdapter() {

                }).get(1, TimeUnit.SECONDS);

        assertTrue(watchingSession.isConnected());

        StompHeaders watchingSubscriptionHeaders = getStompHeadersForDest("/topic/grades/"
            + this.competition.getId() + "/"
            + this.competition.getGradingGroups().stream().findFirst().get().getId()
            + "/Station 1");
        watchingSession.subscribe(watchingSubscriptionHeaders, new StompFrameHandler() {

            @Override
            public Type getPayloadType(StompHeaders headers) {
                return GradeDto.class;
            }

            @Override
            public void handleFrame(StompHeaders headers, Object payload) {
                watchingBlockingQueue.add((GradeDto) payload);
            }
        });

        StompHeaders watchingErrorSubscriptionHeaders = getStompHeadersForDest("/user/queue/error");
        watchingSession.subscribe(watchingErrorSubscriptionHeaders, new StompFrameHandler() {

            @Override
            public Type getPayloadType(StompHeaders headers) {
                return MessageErrorDto.class;
            }

            @Override
            public void handleFrame(StompHeaders headers, Object payload) {
                watchingErrorBlockingQueue.add((MessageErrorDto) payload);
            }
        });


        //Entering User Setup



        BlockingQueue<GradeDto> blockingQueue = new ArrayBlockingQueue<>(1);
        BlockingQueue<MessageErrorDto> errorBlockingQueue = new ArrayBlockingQueue<>(1);

        StompHeaders stompHeaders = getStompConnectionHeader();

        webSocketStompClient.setMessageConverter(new MappingJackson2MessageConverter());

        StompSession session = webSocketStompClient
            .connect(
                WS_CONNECTION_PATH,
                new WebSocketHttpHeaders(new HttpHeaders()), stompHeaders,
                new StompSessionHandlerAdapter() {

                }).get(1, TimeUnit.SECONDS);

        assertTrue(session.isConnected());

        StompHeaders subscriptionHeaders = getStompHeadersForDest("/topic/grades/"
            + this.competition.getId() + "/"
            + this.competition.getGradingGroups().stream().findFirst().get().getId()
            + "/Station 1");
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

        StompHeaders errorSubscriptionHeaders = getStompHeadersForDest("/user/queue/error");
        session.subscribe(errorSubscriptionHeaders, new StompFrameHandler() {

            @Override
            public Type getPayloadType(StompHeaders headers) {
                return MessageErrorDto.class;
            }

            @Override
            public void handleFrame(StompHeaders headers, Object payload) {
                errorBlockingQueue.add((MessageErrorDto) payload);
            }
        });

        //Enter valid grade

        GradeDto gradeDto = getValidEnterGradeDto();

        StompHeaders msgHeader = getStompHeadersForDest(
            "/app/grade/"
                + this.competition.getId() + "/"
                + this.competition.getGradingGroups().stream().findFirst().get().getId()
                + "/Station 1"
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
    public void givenLoggedInUser_whenJudgeHello_expectReceivingJudgeIdOverTopic() throws Exception {
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

        StompHeaders stompHeaders = getStompConnectionHeaderForUser(user);

        webSocketStompClient.setMessageConverter(new MappingJackson2MessageConverter());

        StompSession session = webSocketStompClient
            .connect(
                WS_CONNECTION_PATH,
                new WebSocketHttpHeaders(new HttpHeaders()), stompHeaders,
                new StompSessionHandlerAdapter() {

                }).get(1, TimeUnit.SECONDS);

        assertTrue(session.isConnected());


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

        StompHeaders errorSubscriptionHeaders = getStompHeadersForDest("/user/queue/error");
        session.subscribe(errorSubscriptionHeaders, new StompFrameHandler() {

            @Override
            public Type getPayloadType(StompHeaders headers) {
                return MessageErrorDto.class;
            }

            @Override
            public void handleFrame(StompHeaders headers, Object payload) {
                errorBlockingQueue.add((MessageErrorDto) payload);
            }
        });

        GradeDto gradeDto = getValidEnterGradeDto().withJudgeId(this.judge.getId() + 1);

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

        StompHeaders stompHeaders = getStompConnectionHeader();

        webSocketStompClient.setMessageConverter(new MappingJackson2MessageConverter());

        StompSession session = webSocketStompClient
            .connect(
                WS_CONNECTION_PATH,
                new WebSocketHttpHeaders(new HttpHeaders()), stompHeaders,
                new StompSessionHandlerAdapter() {

                }).get(1, TimeUnit.SECONDS);

        assertTrue(session.isConnected());


        StompHeaders subscriptionHeaders = getStompHeadersForDest("/topic/grades/"
            + this.competition.getId() + "/"
            + this.competition.getGradingGroups().stream().findFirst().get().getId()
            + "/Station 1");
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

        StompHeaders errorSubscriptionHeaders = getStompHeadersForDest("/user/queue/error");
        session.subscribe(errorSubscriptionHeaders, new StompFrameHandler() {

            @Override
            public Type getPayloadType(StompHeaders headers) {
                return MessageErrorDto.class;
            }

            @Override
            public void handleFrame(StompHeaders headers, Object payload) {
                errorBlockingQueue.add((MessageErrorDto) payload);
            }
        });

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

        StompHeaders stompHeaders = getStompConnectionHeader();

        webSocketStompClient.setMessageConverter(new MappingJackson2MessageConverter());

        StompSession session = webSocketStompClient
            .connect(
                WS_CONNECTION_PATH,
                new WebSocketHttpHeaders(new HttpHeaders()), stompHeaders,
                new StompSessionHandlerAdapter() {

                }).get(1, TimeUnit.SECONDS);

        assertTrue(session.isConnected());

        StompHeaders subscriptionHeaders = getStompHeadersForDest("/topic/grades/"
            + this.competition.getId() + "/"
            + this.competition.getGradingGroups().stream().findFirst().get().getId()
            + "/Station 1");
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

        StompHeaders errorSubscriptionHeaders = getStompHeadersForDest("/user/queue/error");
        session.subscribe(errorSubscriptionHeaders, new StompFrameHandler() {

            @Override
            public Type getPayloadType(StompHeaders headers) {
                return MessageErrorDto.class;
            }

            @Override
            public void handleFrame(StompHeaders headers, Object payload) {
                errorBlockingQueue.add((MessageErrorDto) payload);
            }
        });

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

        StompHeaders stompHeaders = getStompConnectionHeader();

        webSocketStompClient.setMessageConverter(new MappingJackson2MessageConverter());

        StompSession session = webSocketStompClient
            .connect(
                WS_CONNECTION_PATH,
                new WebSocketHttpHeaders(new HttpHeaders()), stompHeaders,
                new StompSessionHandlerAdapter() {

                }).get(1, TimeUnit.SECONDS);

        assertTrue(session.isConnected());

        StompHeaders subscriptionHeaders = getStompHeadersForDest("/topic/grades/"
            + this.competition.getId() + "/"
            + this.competition.getGradingGroups().stream().findFirst().get().getId()
            + "/Station 1");
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

        StompHeaders errorSubscriptionHeaders = getStompHeadersForDest("/user/queue/error");
        session.subscribe(errorSubscriptionHeaders, new StompFrameHandler() {

            @Override
            public Type getPayloadType(StompHeaders headers) {
                return MessageErrorDto.class;
            }

            @Override
            public void handleFrame(StompHeaders headers, Object payload) {
                errorBlockingQueue.add((MessageErrorDto) payload);
            }
        });

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

        StompHeaders stompHeaders = getStompConnectionHeader();

        webSocketStompClient.setMessageConverter(new MappingJackson2MessageConverter());

        StompSession session = webSocketStompClient
            .connect(
                WS_CONNECTION_PATH,
                new WebSocketHttpHeaders(new HttpHeaders()), stompHeaders,
                new StompSessionHandlerAdapter() {

                }).get(1, TimeUnit.SECONDS);

        assertTrue(session.isConnected());

        StompHeaders subscriptionHeaders = getStompHeadersForDest("/topic/grades/"
            + this.competition.getId() + "/"
            + this.competition.getGradingGroups().stream().findFirst().get().getId()
            + "/Station 1");
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

        StompHeaders errorSubscriptionHeaders = getStompHeadersForDest("/user/queue/error");
        session.subscribe(errorSubscriptionHeaders, new StompFrameHandler() {

            @Override
            public Type getPayloadType(StompHeaders headers) {
                return MessageErrorDto.class;
            }

            @Override
            public void handleFrame(StompHeaders headers, Object payload) {
                errorBlockingQueue.add((MessageErrorDto) payload);
            }
        });

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

        watchingWebSocketStompClient.setMessageConverter(new MappingJackson2MessageConverter());

        StompHeaders watchingStompHeaders = getStompConnectionHeader();

        StompSession watchingSession = watchingWebSocketStompClient
            .connect(
                WS_CONNECTION_PATH,
                new WebSocketHttpHeaders(new HttpHeaders()), watchingStompHeaders,
                new StompSessionHandlerAdapter() {

                }).get(1, TimeUnit.SECONDS);

        assertTrue(watchingSession.isConnected());

        StompHeaders watchingSubscriptionHeaders = getStompHeadersForDest("/topic/grades/"
            + this.competition.getId() + "/"
            + this.competition.getGradingGroups().stream().findFirst().get().getId()
            + "/Station 1");
        watchingSession.subscribe(watchingSubscriptionHeaders, new StompFrameHandler() {

            @Override
            public Type getPayloadType(StompHeaders headers) {
                return GradeDto.class;
            }

            @Override
            public void handleFrame(StompHeaders headers, Object payload) {
                watchingBlockingQueue.add((GradeDto) payload);
            }
        });

        StompHeaders watchingErrorSubscriptionHeaders = getStompHeadersForDest("/user/queue/error");
        watchingSession.subscribe(watchingErrorSubscriptionHeaders, new StompFrameHandler() {

            @Override
            public Type getPayloadType(StompHeaders headers) {
                return MessageErrorDto.class;
            }

            @Override
            public void handleFrame(StompHeaders headers, Object payload) {
                watchingErrorBlockingQueue.add((MessageErrorDto) payload);
            }
        });


        //Entering User Setup



        BlockingQueue<GradeDto> blockingQueue = new ArrayBlockingQueue<>(1);
        BlockingQueue<MessageErrorDto> errorBlockingQueue = new ArrayBlockingQueue<>(1);

        StompHeaders stompHeaders = getStompConnectionHeader();

        webSocketStompClient.setMessageConverter(new MappingJackson2MessageConverter());

        StompSession session = webSocketStompClient
            .connect(
                WS_CONNECTION_PATH,
                new WebSocketHttpHeaders(new HttpHeaders()), stompHeaders,
                new StompSessionHandlerAdapter() {

                }).get(1, TimeUnit.SECONDS);

        assertTrue(session.isConnected());

        StompHeaders subscriptionHeaders = getStompHeadersForDest("/topic/grades/"
            + this.competition.getId() + "/"
            + this.competition.getGradingGroups().stream().findFirst().get().getId()
            + "/Station 1");
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

        StompHeaders errorSubscriptionHeaders = getStompHeadersForDest("/user/queue/error");
        session.subscribe(errorSubscriptionHeaders, new StompFrameHandler() {

            @Override
            public Type getPayloadType(StompHeaders headers) {
                return MessageErrorDto.class;
            }

            @Override
            public void handleFrame(StompHeaders headers, Object payload) {
                errorBlockingQueue.add((MessageErrorDto) payload);
            }
        });

        //Enter bad grade

        GradeDto gradeDto = getInvalidGradeDtoWithMissingVariable();

        StompHeaders msgHeader = getStompHeadersForDest(
            "/app/grade/"
                + this.competition.getId() + "/"
                + this.competition.getGradingGroups().stream().findFirst().get().getId()
                + "/Station 1"
        );
        session.send(msgHeader, gradeDto);

        //assertions

        assertNull(blockingQueue.poll(2, TimeUnit.SECONDS));
        assertNull(watchingBlockingQueue.poll(2, TimeUnit.SECONDS));

        MessageErrorDto errorRes = errorBlockingQueue.poll(2, TimeUnit.SECONDS);
        assertNull(watchingErrorBlockingQueue.poll(2, TimeUnit.SECONDS));

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

        StompHeaders stompHeaders = getStompConnectionHeader();

        webSocketStompClient.setMessageConverter(new MappingJackson2MessageConverter());

        StompSession session = webSocketStompClient
            .connect(
                WS_CONNECTION_PATH,
                new WebSocketHttpHeaders(new HttpHeaders()), stompHeaders,
                new StompSessionHandlerAdapter() {

                }).get(1, TimeUnit.SECONDS);

        assertTrue(session.isConnected());

        StompHeaders subscriptionHeaders = getStompHeadersForDest("/topic/grades/"
            + this.competition.getId() + "/"
            + this.competition.getGradingGroups().stream().findFirst().get().getId()
            + "/Station 1");
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

        StompHeaders errorSubscriptionHeaders = getStompHeadersForDest("/user/queue/error");
        session.subscribe(errorSubscriptionHeaders, new StompFrameHandler() {

            @Override
            public Type getPayloadType(StompHeaders headers) {
                return MessageErrorDto.class;
            }

            @Override
            public void handleFrame(StompHeaders headers, Object payload) {
                errorBlockingQueue.add((MessageErrorDto) payload);
            }
        });

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
