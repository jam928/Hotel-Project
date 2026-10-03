package com.hulton.hotels.image;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Where hotel and room photos are stored (MinIO, or any S3-compatible store).
 * @param seedSamples whether to upload the photos in {@code classpath:sample-images}
 * on startup, for the sample hotels and rooms
 */
@ConfigurationProperties("hulton.images")
public record ImageProperties(String endpoint, String accessKey, String secretKey, String bucket,
		boolean seedSamples) {
}
