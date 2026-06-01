package jp.systemengineeya.bookreview.api.integration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("local")
class BookImageApiIT {

    @Autowired
    private MockMvc mockMvc;

    private static Long bookId;
    private static Long imageId;

    @Test
    void bookImageのCRUDライフサイクルが正常に動作する() throws Exception {
        本を登録する();
        画像を追加できる();
        画像を削除できる();
    }

    void 本を登録する() throws Exception {
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

        JsonNode json = new ObjectMapper()
                .readTree(result.getResponse().getContentAsString());

        bookId = json.get("id").asLong();
    }

    void 画像を追加できる() throws Exception {

        MockMultipartFile image = new MockMultipartFile(
                "image",
                "additional.png",
                MediaType.IMAGE_PNG_VALUE,
                "additional image".getBytes());

        MvcResult result = mockMvc.perform(
                multipart("/books/{bookId}/images", bookId)
                        .file(image))
                .andExpect(status().isCreated())
                .andReturn();

        JsonNode json = new ObjectMapper()
                .readTree(result.getResponse().getContentAsString());

        imageId = json.get("id").asLong();

        assertNotNull(imageId);
    }

    void 画像を削除できる() throws Exception {

        mockMvc.perform(
                delete("/books/{bookId}/images/{imageId}",
                        bookId,
                        imageId))
                .andExpect(status().isNoContent());
    }
}