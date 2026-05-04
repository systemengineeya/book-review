package jp.systemengineeya.bookreview.api.integration;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Integration Test（MockMvc）
 *
 * ReviewはBook配下のリソースとして扱う
 */
@SpringBootTest
@AutoConfigureMockMvc
class ReviewApiIT {

    @Autowired
    private MockMvc mockMvc;

    private static Long bookId;
    private static Long reviewId;

    @Test
    void reviewのCRUDライフサイクルが正常に動作する() throws Exception {
        bookを登録する();
        reviewを登録する();
        reviewを取得できる();
        reviewを更新できる();
        更新後取得できる();
        reviewを削除できる();
        削除後取得すると404になる();
    }

    void bookを登録する() throws Exception {
        @SuppressWarnings("null")
        MvcResult result = mockMvc.perform(post("/books")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                          "title":"Java入門",
                          "author":"山田太郎"
                        }
                        """))
                .andExpect(status().isCreated())
                .andReturn();

        String response = result.getResponse().getContentAsString();

        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode json = objectMapper.readTree(response);

        bookId = json.get("id").asLong();
    }

    void reviewを登録する() throws Exception {
        @SuppressWarnings("null")
        MvcResult result = mockMvc.perform(post("/books/{bookId}/reviews", bookId)
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                          "content":"とても良い本です",
                          "rating":5
                        }
                        """))
                .andExpect(status().isCreated())
                .andReturn();

        String response = result.getResponse().getContentAsString();

        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode json = objectMapper.readTree(response);

        reviewId = json.get("id").asLong();
    }

    void reviewを取得できる() throws Exception {
        mockMvc.perform(get("/books/{bookId}/reviews/{reviewId}", bookId, reviewId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(reviewId))
                .andExpect(jsonPath("$.content").value("とても良い本です"))
                .andExpect(jsonPath("$.rating").value(5));
    }

    @SuppressWarnings("null")
    void reviewを更新できる() throws Exception {
        mockMvc.perform(patch("/books/{bookId}/reviews/{reviewId}", bookId, reviewId)
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                          "id": %d,
                          "content":"かなり良い本です",
                          "rating":4
                        }
                        """.formatted(reviewId)))
                .andExpect(status().isOk());
    }

    void 更新後取得できる() throws Exception {
        mockMvc.perform(get("/books/{bookId}/reviews/{reviewId}", bookId, reviewId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").value("かなり良い本です"))
                .andExpect(jsonPath("$.rating").value(4));
    }

    void reviewを削除できる() throws Exception {
        mockMvc.perform(delete("/books/{bookId}/reviews/{reviewId}", bookId, reviewId))
                .andExpect(status().isNoContent());
    }

    void 削除後取得すると404になる() throws Exception {
        mockMvc.perform(get("/books/{bookId}/reviews/{reviewId}", bookId, reviewId))
                .andExpect(status().isNotFound());
    }
}