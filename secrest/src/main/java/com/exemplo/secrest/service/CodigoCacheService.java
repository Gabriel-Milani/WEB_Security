package com.exemplo.secrest.service;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class CodigoCacheService {

    private static final Duration EXPIRATION_TIME = Duration.ofMinutes(5);
    private static final SecureRandom RANDOM = new SecureRandom();

    private final Map<String, CodigoCacheEntry> cache = new ConcurrentHashMap<>();

    public String gerarEArmazenarCodigo(String email) {
        String normalizedEmail = normalizeEmail(email);
        String code = String.format("%06d", RANDOM.nextInt(1_000_000));
        cache.put(normalizedEmail, new CodigoCacheEntry(code, Instant.now().plus(EXPIRATION_TIME)));
        return code;
    }

    public boolean verificarCodigo(String email, String code) {
        String normalizedEmail = normalizeEmail(email);
        return Optional.ofNullable(cache.get(normalizedEmail))
                .filter(entry -> !entry.isExpired())
                .filter(entry -> entry.code().equals(code))
                .map(entry -> {
                    cache.remove(normalizedEmail);
                    return true;
                })
                .orElse(false);
    }

    @Scheduled(fixedRate = 60_000)
    public void removerCodigosExpirados() {
        cache.entrySet().removeIf(entry -> entry.getValue().isExpired());
    }

    private String normalizeEmail(String email) {
        return email == null ? "" : email.trim().toLowerCase();
    }

    private record CodigoCacheEntry(String code, Instant expiresAt) {
        private boolean isExpired() {
            return Instant.now().isAfter(expiresAt);
        }
    }
}
