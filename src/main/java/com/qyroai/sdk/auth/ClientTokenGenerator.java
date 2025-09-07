package com.qyroai.sdk.auth;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

public class ClientTokenGenerator {
    private final String apiKeyId;
    private final String apiKeySecret;
    private static final ObjectMapper MAPPER = new ObjectMapper();

    public ClientTokenGenerator(String apiKeyId, String apiKeySecret) {
        this.apiKeyId = apiKeyId;
        this.apiKeySecret = apiKeySecret;
    }

    public String generate(Map<String, Object> context) {
        try {
            String subject = MAPPER.writeValueAsString(context);
            long now = Instant.now().getEpochSecond();
            long exp = now + 24L * 30L * 3600L; // 30 days

            Algorithm alg = Algorithm.HMAC256(apiKeySecret);

            return JWT.create()
                    .withHeader(Map.of("kid", apiKeyId))
                    .withSubject(subject)
                    .withIssuedAt(java.util.Date.from(Instant.ofEpochSecond(now)))
                    .withExpiresAt(java.util.Date.from(Instant.ofEpochSecond(exp)))
                    .withClaim("type", "client")
                    .withIssuer(apiKeyId)
                    .withAudience("qyro")
                    .withJWTId(UUID.randomUUID().toString())
                    .sign(alg);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("Failed to serialize context to JSON", e);
        }
    }
}