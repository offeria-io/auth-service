package offeria.auth_service.security;

import offeria.auth_service.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

class JwtUtilsTest {

    private JwtUtils jwtUtils;

    @BeforeEach
    void setUp() {
        jwtUtils = new JwtUtils();

        ReflectionTestUtils.setField(
                jwtUtils,
                "secret",
                "test-only-jwt-secret-key-that-is-at-least-32-bytes-long"
        );

        ReflectionTestUtils.setField(
                jwtUtils,
                "jwtExpirationMs",
                3600000L
        );
    }

    @Test
    void generateToken_IncludesUsernameAndRoles() {
        User user = User.builder()
                .username("testuser")
                .password("encodedPassword")
                .roles(Set.of("ROLE_USER", "ROLE_MANAGER"))
                .build();

        String token = jwtUtils.generateToken(user);

        assertEquals("testuser", jwtUtils.extractUsername(token));
        assertEquals(
                List.of("ROLE_MANAGER", "ROLE_USER"),
                jwtUtils.extractRoles(token)
        );
    }
}
