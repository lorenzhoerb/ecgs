package at.ac.tuwien.sepm.groupphase.backend.security;

import at.ac.tuwien.sepm.groupphase.backend.config.properties.SecurityProperties;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.lang.invoke.MethodHandles;
import java.util.Date;
import java.util.List;

@Component
public class JwtTokenizer {
    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private final SecurityProperties securityProperties;

    public JwtTokenizer(SecurityProperties securityProperties) {
        this.securityProperties = securityProperties;
    }

    /**
     * Build jwt auth token.
     *
     * @param user  the user to create the auth token forj
     * @param roles the roles the user has
     * @return The resulting auth token as a string
     */
    public String getAuthToken(String user, List<String> roles) {
        LOGGER.debug("getAuthToken()");

        byte[] signingKey = securityProperties.getJwtSecret().getBytes();
        String token = Jwts.builder()
            .signWith(Keys.hmacShaKeyFor(signingKey), SignatureAlgorithm.HS512)
            .setHeaderParam("typ", securityProperties.getJwtType())
            .setIssuer(securityProperties.getJwtIssuer())
            .setAudience(securityProperties.getJwtAudience())
            .setSubject(user)
            .setExpiration(new Date(System.currentTimeMillis() + securityProperties.getJwtExpirationTime()))
            .claim("rol", roles)
            .compact();
        return securityProperties.getAuthTokenPrefix() + token;
    }
}
