package jp.systemengineeya.bookreview.api.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import software.amazon.awssdk.services.s3.S3Client;

@Configuration
@Profile("prod")
public class ProdS3Config {

    @Bean
    public S3Client s3Client() {
        return S3Client.builder()
                .build();
    }
}