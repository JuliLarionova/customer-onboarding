package com.lar.customeronboarding.controller;

import com.lar.customeronboarding.dto.response.AccountSummary;
import com.lar.customeronboarding.dto.response.OverviewResponse;
import com.lar.customeronboarding.enums.AccountType;
import com.lar.customeronboarding.enums.Currency;
import com.lar.customeronboarding.mapper.CustomerMapperImpl;
import com.lar.customeronboarding.security.JwtProperties;
import com.lar.customeronboarding.security.SecurityConfig;
import com.lar.customeronboarding.service.OverviewService;
import com.lar.customeronboarding.support.testdata.CustomerTestDataProvider;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static com.lar.customeronboarding.support.testdata.AccountTestDataProvider.IBAN;
import static com.lar.customeronboarding.support.testdata.CustomerTestDataProvider.CUSTOMER_ID;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(OverviewController.class)
@Import({CustomerMapperImpl.class, SecurityConfig.class})
@EnableConfigurationProperties(JwtProperties.class)
@TestPropertySource(properties = {
        "app.security.jwt.secret=" + CustomerTestDataProvider.JWT_TEST_SECRET,
        "app.security.jwt.time-to-live=15m",
        "app.rate-limit.enabled=false",
        "app.rate-limit.capacity=1000",
        "app.rate-limit.refill-period=1s"
})
class OverviewControllerTest {

    private static final String OVERVIEW_URL = "/api/v1/overview";

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private OverviewService overviewService;

    @Test
    void overview_returns200WithAccounts_whenTokenValid() throws Exception {
        when(overviewService.overviewFor(CUSTOMER_ID)).thenReturn(new OverviewResponse(
                List.of(new AccountSummary(IBAN, AccountType.CURRENT, BigDecimal.ZERO, Currency.EUR))));

        mockMvc.perform(get(OVERVIEW_URL)
                        .with(jwt().jwt(j -> j.subject(CUSTOMER_ID.toString()))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accounts[0].iban", is(IBAN)))
                .andExpect(jsonPath("$.accounts[0].accountType", is("CURRENT")))
                .andExpect(jsonPath("$.accounts[0].currency", is("EUR")));
    }

    @Test
    void overview_passesTokenSubjectToService_neverARequestParameter() throws Exception {
        when(overviewService.overviewFor(any())).thenReturn(new OverviewResponse(List.of()));

        mockMvc.perform(get(OVERVIEW_URL + "?customerId=" + UUID.randomUUID())
                        .with(jwt().jwt(j -> j.subject(CUSTOMER_ID.toString()))))
                .andExpect(status().isOk());

        verify(overviewService).overviewFor(CUSTOMER_ID);
    }

    @Test
    void overview_returns401_whenNoToken() throws Exception {
        mockMvc.perform(get(OVERVIEW_URL))
                .andExpect(status().isUnauthorized());

        verifyNoInteractions(overviewService);
    }

}
