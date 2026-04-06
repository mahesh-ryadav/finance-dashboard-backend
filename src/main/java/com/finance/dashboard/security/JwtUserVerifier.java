package com.finance.dashboard.security;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.finance.dashboard.entity.User;
import com.finance.dashboard.enums.UserStatus;
import com.finance.dashboard.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class JwtUserVerifier {

    private static final Duration MIN_TTL = Duration.ofSeconds(5);

    private final UserRepository userRepository;

    @Value("${app.jwt.user-check.cache-max-size:10000}")
    private long cacheMaxSize;

    @Value("${app.jwt.user-check.enabled:true}")
    private boolean enabled;

    @Value("${app.jwt.user-check.cache-ttl:60s}")
    private Duration cacheTtl;

    private volatile Cache<String, CacheEntry> cache;

    public boolean isEnabled() {
        return enabled;
    }

    public boolean isUserAllowed(String email, String roleClaim) {
        if (!enabled) {
            return true;
        }

        Cache<String, CacheEntry> localCache = getCache();
        CacheEntry cached = localCache.getIfPresent(email);
        if (cached != null) {
            if (cached.dbRole != null && roleClaim != null && !cached.dbRole.equals(roleClaim)) {
                return false;
            }
            return cached.allowed;
        }

        Optional<User> userOpt = userRepository.findByEmailAndIsDeletedFalse(email);
        boolean allowed = userOpt
                .filter(u -> u.getStatus() == UserStatus.ACTIVE)
                .isPresent();

        String dbRole = userOpt.map(u -> u.getRole().name()).orElse(null);
        Duration ttl = cacheTtl == null ? Duration.ofSeconds(60) : cacheTtl;
        if (ttl.compareTo(MIN_TTL) < 0) {
            ttl = MIN_TTL;
        }

        localCache.put(email, new CacheEntry(allowed, dbRole));
        return allowed && (dbRole == null || dbRole.equals(roleClaim));
    }

    private Cache<String, CacheEntry> getCache() {
        Cache<String, CacheEntry> current = cache;
        if (current != null) {
            return current;
        }
        synchronized (this) {
            if (cache == null) {
                Duration ttl = cacheTtl == null ? Duration.ofSeconds(60) : cacheTtl;
                if (ttl.compareTo(MIN_TTL) < 0) {
                    ttl = MIN_TTL;
                }

                long max = cacheMaxSize <= 0 ? 10_000 : cacheMaxSize;
                cache = Caffeine.newBuilder()
                        .maximumSize(max)
                        .expireAfterWrite(ttl)
                        .build();
            }
            return cache;
        }
    }

    private record CacheEntry(boolean allowed, String dbRole) {}
}

