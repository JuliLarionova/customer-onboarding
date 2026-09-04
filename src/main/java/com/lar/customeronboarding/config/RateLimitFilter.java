package com.lar.customeronboarding.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lar.customeronboarding.exception.error.ApiError;
import io.github.bucket4j.Bucket;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Instant;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class RateLimitFilter extends OncePerRequestFilter {

    private static final String TOO_MANY_REQUESTS = "Too many requests";
    private static final String API_PREFIX = "/api/";

    private final RateLimitProperties properties;
    private final ObjectMapper objectMapper;
    private final Bucket bucket;

    public RateLimitFilter(RateLimitProperties properties, ObjectMapper objectMapper) {
        this.properties = properties;
        this.objectMapper = objectMapper;


        this.bucket = Bucket.builder()
                .addLimit(limit -> limit.capacity(properties.capacity())
                        .refillGreedy(properties.refillTokens(), properties.refillPeriod()))
                .build();
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        return !properties.enabled() || !request.getRequestURI().startsWith(API_PREFIX);
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        var probe = bucket.tryConsumeAndReturnRemaining(1);

        if (probe.isConsumed()) {
            filterChain.doFilter(request, response);
            return;
        }

        var secondsToWait = Math.max(1,
                TimeUnit.NANOSECONDS.toSeconds(probe.getNanosToWaitForRefill()));
        response.setHeader(HttpHeaders.RETRY_AFTER, String.valueOf(secondsToWait));
        rejectWithTooManyRequests(response);
    }

    private void rejectWithTooManyRequests(HttpServletResponse response) throws IOException {
        var traceId = UUID.randomUUID().toString();
        log.warn("[{}] {} - request dropped by rate limiter", traceId, HttpStatus.TOO_MANY_REQUESTS.value());

        var body = ApiError.builder()
                .timestamp(Instant.now())
                .status(HttpStatus.TOO_MANY_REQUESTS.value())
                .error(TOO_MANY_REQUESTS)
                .traceId(traceId)
                .build();

        response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        objectMapper.writeValue(response.getWriter(), body);
    }

}
