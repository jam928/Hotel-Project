package com.hulton.hotels.image;

import io.minio.MinioClient;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(ImageProperties.class)
public class ImageConfig {

	@Bean
	MinioClient minioClient(ImageProperties properties) {
		return MinioClient.builder()
			.endpoint(properties.endpoint())
			.credentials(properties.accessKey(), properties.secretKey())
			.build();
	}

}
