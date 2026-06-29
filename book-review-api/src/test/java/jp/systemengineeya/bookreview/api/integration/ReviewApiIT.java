package jp.systemengineeya.bookreview.api.integration;

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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.IOException;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;

/**
 * Integration Test（MockMvc）
 *
 * ReviewはBook配下のリソースとして扱う
 */
@SpringBootTest
@Testcontainers
@AutoConfigureMockMvc
class ReviewApiIT {

	@Autowired
	private MockMvc mockMvc;

	private static Long bookId;
	private static Long reviewId;

	@MockitoBean
	private S3Service s3Service;

	@Container
	static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16");

	@DynamicPropertySource
	static void configureProperties(DynamicPropertyRegistry registry) {
		registry.add("spring.datasource.url", postgres::getJdbcUrl);
		registry.add("spring.datasource.username", postgres::getUsername);
		registry.add("spring.datasource.password", postgres::getPassword);
	}

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
	}

	void reviewを登録する() throws Exception {
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
		mockMvc.perform(get("/books/{bookId}/reviews", bookId))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$[0].id").value(reviewId))
				.andExpect(jsonPath("$[0].content").value("かなり良い本です"))
				.andExpect(jsonPath("$[0].rating").value(4));
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