package com.courier;

import com.nimbusds.jose.jwk.source.ImmutableSecret;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.test.web.servlet.MockMvc;

import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.time.Instant;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(properties = {
        "security.enabled=true",
        "security.hmac-secret=this-is-a-very-long-hmac-secret-for-tests-1234567890",
        "app.exitAfterStartup=false"
})
@AutoConfigureMockMvc
class SecurityEnabledIntegrationTest {

    @Autowired
    MockMvc mockMvc;

    private String token(String secret) {
        var key = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
        JwtEncoder encoder = new NimbusJwtEncoder(new ImmutableSecret<>(key));
        Instant now = Instant.now();
        JwtClaimsSet claims = JwtClaimsSet.builder()
                .subject("test-user")
                .issuedAt(now)
                .expiresAt(now.plusSeconds(3600))
                .build();
        return encoder.encode(JwtEncoderParameters.from(
                JwsHeader.with(MacAlgorithm.HS256).build(),
                claims
        )).getTokenValue();
    }

    @Test
    void rejectsWithoutTokenAndAcceptsWithValidToken() throws Exception {
        mockMvc.perform(post("/locations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"courierId":"c1","timeMillis":1723900000000,"lat":40.0,"lng":29.0}
                                """))
                .andExpect(status().isUnauthorized());

        String jwt = token("this-is-a-very-long-hmac-secret-for-tests-1234567890");

        mockMvc.perform(post("/locations")
                        .header("Authorization", "Bearer " + jwt)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"courierId":"c1","timeMillis":1723900001000,"lat":40.0,"lng":29.0}
                                """))
                .andExpect(status().isAccepted());
    }
}

