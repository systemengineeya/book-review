package jp.systemengineeya.bookreview.api;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@MapperScan("jp.systemengineeya.bookreview.api.mapper")
@SpringBootApplication
public class BookReviewApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(BookReviewApiApplication.class, args);
	}

}
