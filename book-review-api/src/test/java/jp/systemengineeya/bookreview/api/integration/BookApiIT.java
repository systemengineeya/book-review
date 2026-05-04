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
 */
@SpringBootTest
@AutoConfigureMockMvc
class BookApiIT {

    @Autowired
    private MockMvc mockMvc;

    private static Long bookId;

    @Test
    void bookのCRUDライフサイクルが正常に動作する() throws Exception {
        登録できる();
        取得できる();
        更新できる();
        更新後取得できる();
        削除できる();
        削除後取得すると404になる();
    }

    void 登録できる() throws Exception {
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

    void 取得できる() throws Exception {
        mockMvc.perform(get("/books/{bookId}", bookId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(bookId))
                .andExpect(jsonPath("$.title").value("Java入門"))
                .andExpect(jsonPath("$.author").value("山田太郎"));
    }

    @SuppressWarnings("null")
    void 更新できる() throws Exception {
        mockMvc.perform(patch("/books/{bookId}", bookId)
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                          "id": %d,
                          "title":"Java入門 改訂版",
                          "author":"佐藤一郎"
                        }
                        """.formatted(bookId)))
                .andExpect(status().isOk());
    }

    void 更新後取得できる() throws Exception {
        mockMvc.perform(get("/books/{bookId}", bookId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Java入門 改訂版"))
                .andExpect(jsonPath("$.author").value("佐藤一郎"));
    }

    void 削除できる() throws Exception {
        mockMvc.perform(delete("/books/{bookId}", bookId))
                .andExpect(status().isNoContent());
    }

    void 削除後取得すると404になる() throws Exception {
        mockMvc.perform(get("/books/{bookId}", bookId))
                .andExpect(status().isNotFound());
    }
}