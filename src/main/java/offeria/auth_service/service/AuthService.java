package offeria.auth_service.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import offeria.auth_service.dto.AuthResponse;
import offeria.auth_service.dto.LoginRequest;
import offeria.auth_service.dto.RegisterRequest;
import offeria.auth_service.entity.User;
import offeria.auth_service.exception.ApiException;
import offeria.auth_service.repository.UserRepository;
import offeria.auth_service.security.JwtUtils;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private static final String DEFAULT_ROLE = "ROLE_USER";

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;
    private final AuthenticationManager authenticationManager;

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        log.info("Registering user: {}", request.getUsername());

        if (userRepository.existsByUsername(request.getUsername())) {
            throw new ApiException(
                    "Username already taken",
                    HttpStatus.BAD_REQUEST
            );
        }

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new ApiException(
                    "Email already taken",
                    HttpStatus.BAD_REQUEST
            );
        }

        User user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .roles(Set.of(DEFAULT_ROLE))
                .build();

        userRepository.save(user);

        String jwtToken = jwtUtils.generateToken(user);

        return buildAuthResponse(user, jwtToken);
    }

    public AuthResponse login(LoginRequest request) {
        log.info("Login attempt for user: {}", request.getUsername());

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()
                )
        );

        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new ApiException(
                        "User not found",
                        HttpStatus.NOT_FOUND
                ));

        String jwtToken = jwtUtils.generateToken(user);

        return buildAuthResponse(user, jwtToken);
    }

    private AuthResponse buildAuthResponse(
            User user,
            String jwtToken
    ) {
        return AuthResponse.builder()
                .token(jwtToken)
                .username(user.getUsername())
                .roles(user.getRoles())
                .type("Bearer")
                .build();
    }
}
