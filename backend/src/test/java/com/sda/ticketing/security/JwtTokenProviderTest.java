package com.sda.ticketing.security;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class JwtTokenProviderTest {

    @Test
    void tokenRoundTrip() {
        String secret = "ZmFrZV9zZWNyZXRfY2hhbmdlX2xhdGVyCg==";
        JwtTokenProvider provider = new JwtTokenProvider(secret, 3600000);

        String token = provider.createToken("user@example.com", "ROLE_ADMIN");
        assertThat(provider.validateToken(token)).isTrue();
        assertThat(provider.getUsername(token)).isEqualTo("user@example.com");
        assertThat(provider.getRole(token)).isEqualTo("ROLE_ADMIN");
    }
}

