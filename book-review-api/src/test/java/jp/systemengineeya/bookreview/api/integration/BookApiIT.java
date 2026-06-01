package jp.systemengineeya.bookreview.api.integration;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.net.URI;
import java.nio.file.Paths;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;

/**
 * Integration Test（MockMvc）
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("local")
class BookApiIT {

    @Autowired
    private MockMvc mockMvc;

    private static Long bookId;

    @Test
    void bookのCRUDライフサイクルが正常に動作する() throws Exception {
        登録できる();
        取得できる();
        更新できる();
        削除できる();
        削除後取得すると404になる();
    }

    void 登録できる() throws Exception {
        MockMultipartFile image = new MockMultipartFile(
                "images",
                "test.png",
                MediaType.IMAGE_PNG_VALUE,
                "dummy image".getBytes());

        MvcResult result = mockMvc.perform(multipart("/books")
                .file(image)
                .param("title", "Java入門")
                .param("author", "山田太郎"))
                .andExpect(status().isCreated())
                .andReturn();

        String response = result.getResponse().getContentAsString();

        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode json = objectMapper.readTree(response);
        bookId = json.get("id").asLong();
        assertEquals("Java入門", json.get("title").asText());
        assertEquals("山田太郎", json.get("author").asText());
        String imageUrl = json.get("images")
                .get(0)
                .get("imageUrl")
                .asText();
        assertImageUrlIsUuidPng(imageUrl);
    }

    private void assertImageUrlIsUuidPng(String imageUrl) {
        URI uri = URI.create(imageUrl);
        String path = uri.getPath();
        String fileName = Paths.get(path).getFileName().toString();
        assertTrue(fileName.endsWith(".png"));
        String uuidPart = fileName.replace(".png", "");
        assertDoesNotThrow(() -> UUID.fromString(uuidPart));
    }

    void 取得できる() throws Exception {
        MvcResult result = mockMvc.perform(get("/books/{bookId}", bookId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(bookId))
                .andExpect(jsonPath("$.title").value("Java入門"))
                .andExpect(jsonPath("$.author").value("山田太郎"))
                .andReturn();

        String response = result.getResponse().getContentAsString();
        JsonNode json = new ObjectMapper().readTree(response);
        String imageUrl = json.get("images")
                .get(0)
                .get("imageUrl")
                .asText();
        assertImageUrlIsUuidPng(imageUrl);
    }

    void 更新できる() throws Exception {
        MvcResult result = mockMvc.perform(patch("/books/{bookId}", bookId)
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                          "id": %d,
                          "title":"Java入門 改訂版",
                          "author":"佐藤一郎"
                        }
                        """.formatted(bookId)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(bookId))
                .andExpect(jsonPath("$.title").value("Java入門 改訂版"))
                .andExpect(jsonPath("$.author").value("佐藤一郎"))
                .andReturn();

        String response = result.getResponse().getContentAsString();
        JsonNode json = new ObjectMapper().readTree(response);
        String imageUrl = json.get("images")
                .get(0)
                .get("imageUrl")
                .asText();
        assertImageUrlIsUuidPng(imageUrl);
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