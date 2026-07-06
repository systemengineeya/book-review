package jp.systemengineeya.bookreview.api.integration;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.IOException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import jp.systemengineeya.bookreview.api.service.infrastructure.S3Service;
import jp.systemengineeya.bookreview.api.testutil.ImageUrlAssert;

@SpringBootTest
@Testcontainers
@AutoConfigureMockMvc
class BookImageApiIT {

    @Autowired
    private MockMvc mockMvc;

    private static Long bookId;
    private static Long imageId;

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @MockitoBean
    private S3Service s3Service;

    @BeforeEach
    void setup() throws IOException {
        when(s3Service.upload(any()))
                .thenReturn("550e8400-e29b-41d4-a716-446655440000.png");
        when(s3Service.generatePresignedUrl(any()))
                .thenReturn(
                        "https://book-review-bucket.s3.ap-northeast-1.amazonaws.com/"
                                + "550e8400-e29b-41d4-a716-446655440000.png"
                                + "?X-Amz-Algorithm=AWS4-HMAC-SHA256"
                                + "&X-Amz-Credential=test");
    }

    @Test
    void bookImageのCRUDライフサイクルが正常に動作する() throws Exception {
        本を登録する();
        画像を削除できる();
        画像を追加できる();
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
        imageId = json.get("images")
                .get(0)
                .get("id")
                .asLong();
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

        String imageUrl = json.get("imageUrl").asText();
        ImageUrlAssert.assertImageUrlIsUuidPng(imageUrl);
    }

    void 画像を削除できる() throws Exception {

        mockMvc.perform(
                delete("/books/{bookId}/images/{imageId}",
                        bookId,
                        imageId))
                .andExpect(status().isNoContent());
    }
}