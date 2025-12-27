package com.weatherapi.filter;

import com.weatherapi.exception.RateLimitExceededException;
import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Refill;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class RateLimitFilter implements Filter {

    private static final Logger log = LoggerFactory.getLogger(RateLimitFilter.class);

    @Value("${rate.limit.capacity}")
    private int capacity;

    @Value("${rate.limit.refill.tokens}")
    private int refillTokens;

    @Value("${rate.limit.refill.duration}")
    private int refillDurationSeconds;

    // Store buckets per IP address
    private final Map<String, Bucket> buckets = new ConcurrentHashMap<>();

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        String clientIp = getClientIp(httpRequest);
        
        Bucket bucket = resolveBucket(clientIp);
        
        if (bucket.tryConsume(1)) {
            // Request allowed
            log.debug("Request from {} allowed. Remaining tokens: {}", clientIp, bucket.getAvailableTokens());
            chain.doFilter(request, response);
        } else {
            // Rate limit exceeded
            log.warn("Rate limit exceeded for IP: {}", clientIp);
            throw new RateLimitExceededException("Too many requests. Please try again later.");
        }
    }

    /**
     * Get or create a bucket for the given client IP
     */
    private Bucket resolveBucket(String clientIp) {
        return buckets.computeIfAbsent(clientIp, k -> createNewBucket());
    }

    /**
     * Create a new rate limit bucket
     */
    private Bucket createNewBucket() {
        Bandwidth limit = Bandwidth.classic(
                capacity,
                Refill.intervally(refillTokens, Duration.ofSeconds(refillDurationSeconds))
        );
        return Bucket.builder()
                .addLimit(limit)
                .build();
    }

    /**
     * Extract client IP address from request
     */
    private String getClientIp(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            return xForwardedFor.split(",")[0].trim();
        }
        
        String xRealIp = request.getHeader("X-Real-IP");
        if (xRealIp != null && !xRealIp.isEmpty()) {
            return xRealIp;
        }
        
        return request.getRemoteAddr();
    }
}
