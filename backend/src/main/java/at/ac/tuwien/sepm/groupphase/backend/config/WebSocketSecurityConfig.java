package at.ac.tuwien.sepm.groupphase.backend.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.messaging.MessageSecurityMetadataSourceRegistry;
import org.springframework.security.config.annotation.web.socket.AbstractSecurityWebSocketMessageBrokerConfigurer;

@Configuration
public class WebSocketSecurityConfig extends AbstractSecurityWebSocketMessageBrokerConfigurer {
    @Override
    protected void configureInbound(
        MessageSecurityMetadataSourceRegistry messages) {
        messages
            .simpDestMatchers("/grade/**", "/judge/**", "/goodbye-judge/**").authenticated()
            .anyMessage().authenticated();
    }

    @Override
    protected boolean sameOriginDisabled() {
        //disable CSRF for websockets for now...
        return true;
    }
}
