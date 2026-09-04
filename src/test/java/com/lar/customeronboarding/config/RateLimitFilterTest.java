package com.lar.customeronboarding.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RateLimitFilterTest {

    private static final String API_PATH = "/api/v1/register";
    private static final String SWAGGER_UI_PATH = "/swagger-ui.html";
    private static final String POST_REQUEST = "POST";
    private static final String GET_REQUEST = "GET";

    private RateLimitFilter filter;

    @BeforeEach
    void setUp() {
        var objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());
        filter = new RateLimitFilter(
                new RateLimitProperties(true, 2, 2, Duration.ofSeconds(1)), objectMapper);
    }

    @Test
    void allowsRequestsWithinCapacity() throws Exception {
        assertAll(
                () -> assertEquals(200, callFilter(API_PATH).getStatus()),
                () -> assertEquals(200, callFilter(API_PATH).getStatus())
        );
    }

    @Test
    void shedsRequestsBeyondCapacityWith429() throws Exception {
        callFilter(API_PATH);
        callFilter(API_PATH);

        var response = callFilter(API_PATH);

        assertAll(
                () -> assertEquals(429, response.getStatus()),
                () -> assertTrue(response.getContentAsString().contains("Too many requests")),
                () -> assertTrue(response.getContentAsString().contains("traceId")),
                () -> assertTrue(response.getContentAsString().contains("\"status\":429"))
        );
    }

    @Test
    void doesNotFilterNonApiPaths() {
        var request = new MockHttpServletRequest(GET_REQUEST, SWAGGER_UI_PATH);
        request.setRequestURI(SWAGGER_UI_PATH);

        assertTrue(filter.shouldNotFilter(request));
    }

    @Test
    void doesNotFilterWhenDisabled() {
        var disabled = new RateLimitFilter(
                new RateLimitProperties(false, 2, 2, Duration.ofSeconds(1)), new ObjectMapper());

        var request = new MockHttpServletRequest(POST_REQUEST, API_PATH);
        request.setRequestURI(API_PATH);

        assertTrue(disabled.shouldNotFilter(request));
    }

    private MockHttpServletResponse callFilter(String uri) throws Exception {
        var request = new MockHttpServletRequest(POST_REQUEST, uri);
        request.setRequestURI(uri);
        var response = new MockHttpServletResponse();
        filter.doFilter(request, response, new MockFilterChain());
        return response;
    }

}
