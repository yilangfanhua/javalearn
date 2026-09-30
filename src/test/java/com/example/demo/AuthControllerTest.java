package com.example.demo;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Sql(
        statements = {
                "DELETE FROM app_users WHERE username = 'phase6_user'"
        },
        executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD
)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void registerAndLoginReturnsJwt() throws Exception {
        mockMvc.perform(post("/auth/register")
                        .contentType(APPLICATION_JSON)
                        .content(
                                "{\"username\":\"phase6_user\","
                                        + "\"password\":\"123456\"}"
                        ))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0));

        mockMvc.perform(post("/auth/login")
                        .contentType(APPLICATION_JSON)
                        .content(
                                "{\"username\":\"phase6_user\","
                                        + "\"password\":\"123456\"}"
                        ))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data.token").isNotEmpty())
                .andExpect(jsonPath("$.data.tokenType")
                        .value("Bearer"));
    }

    @Test
    void wrongPasswordReturnsUnauthorized() throws Exception {
        mockMvc.perform(post("/auth/login")
                        .contentType(APPLICATION_JSON)
                        .content(
                                "{\"username\":\"not_exist\","
                                        + "\"password\":\"wrong123\"}"
                        ))
                .andExpect(status().isUnauthorized());
    }
}