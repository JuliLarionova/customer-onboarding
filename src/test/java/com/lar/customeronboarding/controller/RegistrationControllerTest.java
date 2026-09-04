package com.lar.customeronboarding.controller;

import com.lar.customeronboarding.dto.CustomerDto;
import com.lar.customeronboarding.exception.custom.CountryNotAllowedException;
import com.lar.customeronboarding.exception.custom.UsernameAlreadyExistsException;
import com.lar.customeronboarding.mapper.CustomerMapperImpl;
import com.lar.customeronboarding.security.JwtProperties;
import com.lar.customeronboarding.security.SecurityConfig;
import com.lar.customeronboarding.service.RegistrationService;
import com.lar.customeronboarding.support.testdata.CustomerTestDataProvider;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;

import static com.lar.customeronboarding.support.testdata.CustomerTestDataProvider.*;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(RegistrationController.class)
@Import({CustomerMapperImpl.class, SecurityConfig.class})
@EnableConfigurationProperties(JwtProperties.class)
@TestPropertySource(properties = {
        "app.security.jwt.secret=" + CustomerTestDataProvider.JWT_TEST_SECRET,
        "app.security.jwt.time-to-live=15m",
        "app.rate-limit.enabled=false",
        "app.rate-limit.capacity=1000",
        "app.rate-limit.refill-tokens=1000",
        "app.rate-limit.refill-period=1s"
})
class RegistrationControllerTest {

    private static final String REGISTER_URL = "/api/v1/register";
    private static final String INVALID_REQUEST = "Invalid request";

    @Autowired
    MockMvc mockMvc;

    @MockitoBean
    RegistrationService registrationService;

    @Test
    void register_returns201AndCredentials_whenValid() throws Exception {
        when(registrationService.register(any(CustomerDto.class)))
                .thenReturn(registeredCustomerDtoBuilder().build());

        mockMvc.perform(post(REGISTER_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validRegistrationJson()))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.customerId", is(CUSTOMER_ID.toString())))
                .andExpect(jsonPath("$.username", is(USERNAME)))
                .andExpect(jsonPath("$.password", is(PASSWORD)));
    }

    @Test
    void register_mapsRequestToDto_whenValid() throws Exception {
        var captor = ArgumentCaptor.forClass(CustomerDto.class);
        when(registrationService.register(any(CustomerDto.class)))
                .thenReturn(registeredCustomerDtoBuilder().build());

        mockMvc.perform(post(REGISTER_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validRegistrationJson()))
                .andExpect(status().isCreated());

        verify(registrationService).register(captor.capture());
        var dto = captor.getValue();
        assertAll(
                () -> assertEquals(USERNAME, dto.username()),
                () -> assertEquals(COUNTRY_CODE, dto.address().countryCode())
        );
    }

    @Test
    void register_returns400WithFieldErrors_whenUnderage() throws Exception {
        var underageDob = LocalDate.now().minusYears(17).toString();

        mockMvc.perform(post(REGISTER_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validRegistrationJson().replace("1990-01-01", underageDob)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error", is(INVALID_REQUEST)))
                .andExpect(jsonPath("$.errors.dateOfBirth").exists())
                .andExpect(jsonPath("$.traceId", notNullValue()));

        verifyNoInteractions(registrationService);
    }

    @Test
    void register_returns400WithFieldErrors_whenRequiredFieldsMissing() throws Exception {
        mockMvc.perform(post(REGISTER_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"JSmith\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.firstName").exists())
                .andExpect(jsonPath("$.errors.address").exists());
    }

    @Test
    void register_returns400_whenCountryCodeLowercase() throws Exception {
        mockMvc.perform(post(REGISTER_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validRegistrationJson().replace("\"NL\"", "\"nl\"")))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors['address.countryCode']").exists());
    }

    @Test
    void register_returns400_whenBodyMalformed() throws Exception {
        mockMvc.perform(post(REGISTER_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{not json"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error", is(INVALID_REQUEST)))
                .andExpect(jsonPath("$.traceId", notNullValue()));

        verifyNoInteractions(registrationService);
    }

    @Test
    void register_returns409_whenUsernameTaken() throws Exception {
        when(registrationService.register(any(CustomerDto.class)))
                .thenThrow(new UsernameAlreadyExistsException(USERNAME));

        mockMvc.perform(post(REGISTER_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validRegistrationJson()))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.error", containsString(USERNAME)));
    }

    @Test
    void register_returns422_whenCountryNotAllowed() throws Exception {
        when(registrationService.register(any(CustomerDto.class)))
                .thenThrow(new CountryNotAllowedException("DE"));

        mockMvc.perform(post(REGISTER_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validRegistrationJson()))
                .andExpect(status().isUnprocessableContent())
                .andExpect(jsonPath("$.error", containsString("DE")));
    }

    @Test
    void register_returns500WithGenericMessage_whenServiceFailsUnexpectedly() throws Exception {
        when(registrationService.register(any(CustomerDto.class)))
                .thenThrow(new IllegalStateException("connection pool exhausted"));

        mockMvc.perform(post(REGISTER_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validRegistrationJson()))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.error", is("Internal server error")))
                .andExpect(jsonPath("$.error", not(containsString("connection pool"))))
                .andExpect(jsonPath("$.traceId", notNullValue()));
    }

    private static String validRegistrationJson() {
        return """
                {
                  "username": "JSmith",
                  "firstName": "Jane",
                  "lastName": "Smith",
                  "dateOfBirth": "1990-01-01",
                  "address": {
                    "street": "Appelstraat",
                    "houseNumber": "159A",
                    "postalCode": "1131AB",
                    "city": "Amsterdam",
                    "countryCode": "NL"
                  }
                }
                """;
    }

}
