package com.lar.customeronboarding.controller;

import com.lar.customeronboarding.exception.custom.InvalidCredentialsException;
import com.lar.customeronboarding.security.AuthService;
import com.lar.customeronboarding.security.JwtProperties;
import com.lar.customeronboarding.security.SecurityConfig;
import com.lar.customeronboarding.support.testdata.CustomerTestDataProvider;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static com.lar.customeronboarding.support.testdata.CustomerTestDataProvider.PASSWORD;
import static com.lar.customeronboarding.support.testdata.CustomerTestDataProvider.USERNAME;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames.TOKEN;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthController.class)
@Import(SecurityConfig.class)
@EnableConfigurationProperties(JwtProperties.class)
@TestPropertySource(properties = {
        "app.security.jwt.secret=" + CustomerTestDataProvider.JWT_TEST_SECRET,
        "app.security.jwt.time-to-live=15m"
})
class AuthControllerTest {

    private static final String LOGIN_URL = "/api/v1/login";
    private static final String INVALID_CREDENTIALS_MESSAGE = "Invalid username or password";

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AuthService authService;

    @Test
    void login_returns200WithToken_whenCredentialsValid() throws Exception {
        when(authService.login(USERNAME, PASSWORD)).thenReturn(TOKEN);

        mockMvc.perform(post(LOGIN_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validLoginJson()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token", is(TOKEN)));
    }

    @Test
    void login_returns401WithGenericMessage_whenCredentialsInvalid() throws Exception {
        when(authService.login(any(), any())).thenThrow(new InvalidCredentialsException());

        mockMvc.perform(post(LOGIN_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validLoginJson()))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error", is(INVALID_CREDENTIALS_MESSAGE)))
                .andExpect(jsonPath("$.traceId", notNullValue()));
    }

    @Test
    void login_returns400WithFieldErrors_whenFieldsMissing() throws Exception {
        mockMvc.perform(post(LOGIN_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"JSmith\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.password").exists());

        verifyNoInteractions(authService);
    }

    @Test
    void login_returns400_whenBodyMalformed() throws Exception {
        mockMvc.perform(post(LOGIN_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{not json"))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(authService);
    }

    private String validLoginJson() {
        return """
                {
                  "username": "%s",
                  "password": "%s"
                }
                """.formatted(USERNAME, PASSWORD);
    }

}
