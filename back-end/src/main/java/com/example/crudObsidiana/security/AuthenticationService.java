package com.example.crudObsidiana.security;

import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class AuthenticationService {

    private static final int MAX_ATTEMPTS = 5;
    private final Map<String, LoginAttempt> attempts = new ConcurrentHashMap<>();

    public void checkLoginAttempts(String email, String ip) {

        String key = email + ":" + ip;
        LoginAttempt attempt = attempts.getOrDefault(key, new LoginAttempt());

        if (attempt.getCount() >= MAX_ATTEMPTS) {

            long blockTime = Duration
                    .between(attempt.getLastAttempt(), Instant.now())
                    .toMinutes();

            if (blockTime < 30) {
                throw new RuntimeException("Muitas tentativas. Tente novamente em 30 minutos.");
            } else {
                attempts.remove(key);
            }
        }

        attempt.increment();
        attempts.put(key, attempt);
    }

    private static class LoginAttempt {

        private int count = 0;
        private Instant lastAttempt = Instant.now();

        public int getCount() {
            return count;
        }

        public Instant getLastAttempt() {
            return lastAttempt;
        }

        public void increment() {
            count++;
            lastAttempt = Instant.now();
        }
    }
}