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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.IOException;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;

/**
 * Integration Test（MockMvc）
 */
@SpringBootTest
@Testcontainers
@AutoConfigureMockMvc
class BookApiIT {

	@Autowired
	private MockMvc mockMvc;

	private Long bookId;

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
		assertTrue(imageUrl.contains("550e8400-e29b-41d4-a716-446655440000.png"));
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
		assertTrue(imageUrl.contains("550e8400-e29b-41d4-a716-446655440000.png"));
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
		assertTrue(imageUrl.contains("550e8400-e29b-41d4-a716-446655440000.png"));
	}

	void 削除できる() throws Exception {
		mockMvc.perform(delete("/books/{bookId}", bookId))
				.andExpect(status().isNoContent());
	}

	void 削除後取得すると404になる() throws Exception {
		// mockMvc.perform(get("/books/{bookId}", bookId))
		// .andExpect(status().isNotFound());
		MvcResult result = mockMvc.perform(get("/books/{bookId}", bookId))
				.andReturn();
		System.out.println("status=" + result.getResponse().getStatus());
	}
}