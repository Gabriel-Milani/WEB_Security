package com.exemplo.secrest.service;

import com.exemplo.secrest.dto.EmailDto;
import com.exemplo.secrest.entity.Role;
import com.exemplo.secrest.entity.User;
import com.exemplo.secrest.enums.RoleName;
import com.exemplo.secrest.producer.UserProducer;
import com.exemplo.secrest.repository.UserRepository;
import com.exemplo.secrest.security.service.JwtTokenService;
import com.exemplo.secrest.security.service.UserDetailsImpl;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.UUID;

@Service
public class AuthCodeService {

    private final CodigoCacheService codigoCacheService;
    private final UserProducer userProducer;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenService jwtTokenService;

    public AuthCodeService(
            CodigoCacheService codigoCacheService,
            UserProducer userProducer,
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            JwtTokenService jwtTokenService
    ) {
        this.codigoCacheService = codigoCacheService;
        this.userProducer = userProducer;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenService = jwtTokenService;
    }

    @Transactional
    public void requestCode(String email) {
        String normalizedEmail = normalizeEmail(email);
        User user = userRepository.findByEmail(normalizedEmail)
                .orElseGet(() -> createTemporaryUser(normalizedEmail));

        String code = codigoCacheService.gerarEArmazenarCodigo(normalizedEmail);
        EmailDto emailDto = new EmailDto(
                userUuid(user),
                normalizedEmail,
                "Seu código de acesso",
                "Seu código de acesso é: " + code + ". Ele expira em 5 minutos."
        );
        userProducer.publishMessageEmail(emailDto);
    }

    public boolean verifyCode(String email, String code) {
        return codigoCacheService.verificarCodigo(email, code);
    }

    public String verifyCodeAndGenerateToken(String email, String code) {
        String normalizedEmail = normalizeEmail(email);
        if (!codigoCacheService.verificarCodigo(normalizedEmail, code)) {
            return null;
        }

        return userRepository.findByEmail(normalizedEmail)
                .map(UserDetailsImpl::new)
                .map(jwtTokenService::generateToken)
                .orElse(null);
    }

    private User createTemporaryUser(String email) {
        User temporaryUser = User.builder()
                .email(email)
                .password(passwordEncoder.encode(UUID.randomUUID().toString()))
                .roles(List.of(Role.builder().name(RoleName.ROLE_CUSTOMER).build()))
                .build();
        return userRepository.save(temporaryUser);
    }

    private UUID userUuid(User user) {
        return UUID.nameUUIDFromBytes(("user:" + user.getId()).getBytes(StandardCharsets.UTF_8));
    }

    private String normalizeEmail(String email) {
        return email == null ? "" : email.trim().toLowerCase();
    }
}
