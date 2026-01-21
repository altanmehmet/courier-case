package com.courier;

import com.nimbusds.jose.jwk.source.ImmutableSecret;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.security.oauth2.jwt.JwsHeader;

import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        properties = {
                "security.enabled=true",
                "security.hmac-secret=please-change-me-32chars-placeholder",
                "app.exitAfterStartup=false"
        }
)
class SecurityEnabledE2ETest {

    @LocalServerPort
    int port;

    @Autowired
    TestRestTemplate restTemplate;

    private String token() {
        // Use 32+ chars secret to satisfy HS256 key length requirements.
        var secret = "please-change-me-32chars-placeholder";
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
    void endToEndWithJwt() {
        String jwt = token();
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(jwt);
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> entity = new HttpEntity<>("""
                {"courierId":"c-e2e","timeMillis":1723900000000,"lat":40.0,"lng":29.0}
                """, headers);

        ResponseEntity<String> ingestResp = restTemplate.exchange(
                "http://localhost:" + port + "/locations",
                HttpMethod.POST,
                entity,
                String.class
        );
        assertEquals(HttpStatus.ACCEPTED, ingestResp.getStatusCode(), "ingest failed body: " + ingestResp.getBody());

        ResponseEntity<Map> distanceResp = restTemplate.exchange(
                "http://localhost:" + port + "/couriers/c-e2e/distance",
                HttpMethod.GET,
                new HttpEntity<>(headers),
                Map.class
        );
        assertEquals(HttpStatus.OK, distanceResp.getStatusCode());
        assertNotNull(distanceResp.getBody());
        assertEquals("c-e2e", distanceResp.getBody().get("courierId"));
    }
}

