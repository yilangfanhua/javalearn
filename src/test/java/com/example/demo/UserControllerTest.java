package com.example.demo;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.springframework.security.test.context.support.WithMockUser;
@SpringBootTest
@AutoConfigureMockMvc
@WithMockUser(
        username = "admin",
        roles = "ADMIN"
)
@Sql(
        statements = {
                "DELETE FROM users WHERE id IN (9001, 9002, 9003)"
        },
        executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD
)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    

    @Test
    void getExistingUserReturnsUser() throws Exception {
        mockMvc.perform(get("/users/1"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data.id").value(1))
                .andExpect(jsonPath("$.data.name").value("Alice"))
                .andExpect(jsonPath("$.data.age").value(20));
    }

    @Test
    void getMissingUserReturnsNotFound() throws Exception {
        mockMvc.perform(get("/users/99999"))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value(404));
    }

    @Test
    void getUsersReturnsFirstPage() throws Exception {
        mockMvc.perform(get("/users")
                        .param("page", "1")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data.page").value(1))
                .andExpect(jsonPath("$.data.size").value(10))
                .andExpect(jsonPath("$.data.total").isNumber())
                .andExpect(jsonPath("$.data.totalPages").isNumber())
                .andExpect(jsonPath("$.data.items").isArray());
    }

    @Test
    void searchUsersByNameReturnsMatchingUsers() throws Exception {
        mockMvc.perform(get("/users/search")
                        .param("name", "Ali")
                        .param("page", "1")
                        .param("size", "10"))
                        .andDo(print())   
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data.page").value(1))
                .andExpect(jsonPath("$.data.size").value(10))
                .andExpect(jsonPath("$.data.items[0].name")
                        .value("Alice"));
    }

    @Test
    void createUserReturnsCreatedUser() throws Exception {
        mockMvc.perform(post("/users")
                        .contentType(APPLICATION_JSON)
                        .content(
                                "{\"id\":9001,\"name\":\"Bob\",\"age\":22}"
                        ))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data.id").value(9001))
                .andExpect(jsonPath("$.data.name").value("Bob"))
                .andExpect(jsonPath("$.data.age").value(22));
    }

    @Test
    void updateExistingUserReturnsUpdatedUser() throws Exception {
        mockMvc.perform(post("/users")
                        .contentType(APPLICATION_JSON)
                        .content(
                                "{\"id\":9002,\"name\":\"Bob\",\"age\":22}"
                        )).
                andDo(print())
                .andExpect(status().isOk());

        mockMvc.perform(put("/users/9002")
                        .contentType(APPLICATION_JSON)
                        .content(
                                "{\"name\":\"Bobby\",\"age\":23}"
                        ))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data.id").value(9002))
                .andExpect(jsonPath("$.data.name").value("Bobby"))
                .andExpect(jsonPath("$.data.age").value(23));
    }

    @Test
    void updateMissingUserReturnsNotFound() throws Exception {
        mockMvc.perform(put("/users/99999")
                        .contentType(APPLICATION_JSON)
                        .content(
                                "{\"name\":\"Nobody\",\"age\":99}"
                        ))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value(404));
    }

    @Test
    void deleteExistingUserReturnsNoContent() throws Exception {
        mockMvc.perform(post("/users")
                        .contentType(APPLICATION_JSON)
                        .content(
                                "{\"id\":9003,\"name\":\"Charlie\",\"age\":30}"
                        ))
                .andDo(print())
                .andExpect(status().isOk());

        mockMvc.perform(delete("/users/9003"))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/users/9003"))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteMissingUserReturnsNotFound() throws Exception {
        mockMvc.perform(delete("/users/99999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value(404));
    }

    @Test
    void invalidPageReturnsBadRequest() throws Exception {
        mockMvc.perform(get("/users")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(400));
    }

    @Test
    void invalidSizeReturnsBadRequest() throws Exception {
        mockMvc.perform(get("/users")
                        .param("page", "1")
                        .param("size", "101"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(400));
    }

    @Test
    void blankSearchNameReturnsBadRequest() throws Exception {
        mockMvc.perform(get("/users/search")
                        .param("name", "")
                        .param("page", "1")
                        .param("size", "10"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(400));
    }
}