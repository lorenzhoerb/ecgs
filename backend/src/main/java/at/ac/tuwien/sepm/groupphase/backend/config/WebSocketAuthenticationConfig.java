package at.ac.tuwien.sepm.groupphase.backend.config;

import at.ac.tuwien.sepm.groupphase.backend.config.properties.SecurityProperties;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.StompErrorDto;
import at.ac.tuwien.sepm.groupphase.backend.exception.UnauthorizedException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.micrometer.core.lang.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.MessagingException;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.csrf.MissingCsrfTokenException;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.messaging.StompSubProtocolErrorHandler;

import java.lang.invoke.MethodHandles;
import java.nio.file.AccessDeniedException;
import java.util.List;

@Configuration
@EnableWebSocketMessageBroker
@Order(Ordered.HIGHEST_PRECEDENCE + 99)
public class WebSocketAuthenticationConfig implements WebSocketMessageBrokerConfigurer {

    private final SecurityProperties securityProperties;

    private static final ObjectMapper mapper = new ObjectMapper();

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    public WebSocketAuthenticationConfig(SecurityProperties securityProperties) {
        super();
        this.securityProperties = securityProperties;
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws/grading").setAllowedOriginPatterns("*");
        registry.setErrorHandler(new StompSubProtocolErrorHandler() {
            //TODO: create different RuntimeExceptions and handle response that way
            @Override
            public Message<byte[]> handleInternal(StompHeaderAccessor errorHeaderAccessor, byte[] errorPayload, @Nullable java.lang.Throwable cause,
                                                  @Nullable StompHeaderAccessor clientHeaderAccessor) {
                if (cause == null || cause.getCause() == null) {
                    //Unknown error
                    LOGGER.error("Unknown error in Stomp Handshake Services: {}, {}, {}, {}", errorHeaderAccessor, errorPayload, cause, clientHeaderAccessor);
                    return MessageBuilder.createMessage(new byte[0], errorHeaderAccessor.getMessageHeaders());
                }

                if (cause instanceof MessagingException) {
                    Throwable ogCause = cause.getCause();
                    if (ogCause instanceof UnauthorizedException) {
                        errorHeaderAccessor.setMessage(cause.getCause().getMessage());
                        StompErrorDto error = new StompErrorDto()
                            .withMessage(cause.getCause().getMessage())
                            .withType(StompErrorDto.StompErrorType.Unauthorized);
                        try {
                            return MessageBuilder.createMessage(mapper.writeValueAsString(error).getBytes(), errorHeaderAccessor.getMessageHeaders());
                        } catch (JsonProcessingException e) {
                            LOGGER.error("Object: {} produced JSON PROCESSING ERROR: {}", error, e);
                            return MessageBuilder.createMessage(new byte[0], errorHeaderAccessor.getMessageHeaders());
                        }
                    } else {
                        //Unknown error
                        LOGGER.error("Unknown error in Stomp Handshake Services: {}, {}, {}, {}", errorHeaderAccessor, errorPayload, cause,
                            clientHeaderAccessor);
                        return MessageBuilder.createMessage(new byte[0], errorHeaderAccessor.getMessageHeaders());
                    }
                } else {
                    //Unknown error
                    LOGGER.error("Unknown error in Stomp Handshake Services: {}, {}, {}, {}", errorHeaderAccessor, errorPayload, cause, clientHeaderAccessor);
                    return MessageBuilder.createMessage(new byte[0], errorHeaderAccessor.getMessageHeaders());
                }


            }
        });
    }

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(new ChannelInterceptor() {
            @Override
            public Message<?> preSend(Message<?> message, MessageChannel channel) {
                StompHeaderAccessor accessor =
                    MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

                if (StompCommand.CONNECT.equals(accessor.getCommand())
                    || StompCommand.MESSAGE.equals(accessor.getCommand())
                    || StompCommand.SEND.equals(accessor.getCommand())
                    || StompCommand.SUBSCRIBE.equals(accessor.getCommand())) {
                    List<String> tokenList = accessor.getNativeHeader("Authorization");
                    accessor.removeNativeHeader("Authorization");

                    String token = null;
                    if (tokenList != null && tokenList.size() > 0) {
                        token = tokenList.get(0);
                    }

                    // validate and convert to a Principal based on your own requirements e.g.
                    // authenticationManager.authenticate(JwtAuthentication(token))

                    UsernamePasswordAuthenticationToken authToken;
                    try {
                        authToken = token == null ? null : getAuthToken(token);
                        if (authToken != null) {
                            SecurityContextHolder.getContext().setAuthentication(authToken);
                        } else {
                            throw new UnauthorizedException("Unauthorized");
                        }
                    } catch (IllegalArgumentException | JwtException e) {
                        throw new UnauthorizedException(e.getMessage());
                    }

                    accessor.setUser(authToken);
                }

                return message;
            }
        });
    }

    private UsernamePasswordAuthenticationToken getAuthToken(String token)
        throws JwtException, IllegalArgumentException {
        if (token == null || token.isEmpty()) {
            return null;
        }

        if (!token.startsWith(securityProperties.getAuthTokenPrefix())) {
            throw new IllegalArgumentException("Authorization header is malformed or missing");
        }

        byte[] signingKey = securityProperties.getJwtSecret().getBytes();

        if (!token.startsWith("Bearer ")) {
            throw new IllegalArgumentException("Token must start with 'Bearer'");
        }
        Claims claims = Jwts.parserBuilder().setSigningKey(signingKey).build()
            .parseClaimsJws(token.replace(securityProperties.getAuthTokenPrefix(), ""))
            .getBody();

        String username = claims.getSubject();

        List<SimpleGrantedAuthority> authorities = ((List<?>) claims
            .get("rol")).stream()
            .map(authority -> new SimpleGrantedAuthority((String) authority))
            .toList();

        if (username == null || username.isEmpty()) {
            throw new IllegalArgumentException("Token contains no user");
        }

        MDC.put("u", username);

        return new UsernamePasswordAuthenticationToken(username, null, authorities);
    }
}
