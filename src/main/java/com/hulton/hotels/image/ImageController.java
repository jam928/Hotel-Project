package com.hulton.hotels.image;

import java.time.Duration;
import java.util.Optional;

import io.minio.StatObjectResponse;
import io.minio.errors.MinioException;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.CacheControl;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.WebRequest;

/**
 * Serves photos from the image bucket at {@code /images/<key>}, so browsers never
 * talk to MinIO directly and the bucket can stay private.
 */
@RestController
@RequiredArgsConstructor
public class ImageController {

	private static final CacheControl CACHE = CacheControl.maxAge(Duration.ofDays(7)).cachePublic();

	private final ImageStore images;

	@GetMapping("/images/{*key}")
	public ResponseEntity<InputStreamResource> image(@PathVariable String key, WebRequest request)
			throws MinioException {
		String objectKey = key.substring(1);
		Optional<StatObjectResponse> stat = this.images.stat(objectKey);
		if (stat.isEmpty()) {
			return ResponseEntity.notFound().build();
		}
		String etag = "\"" + stat.get().etag() + "\"";
		if (request.checkNotModified(etag)) {
			return ResponseEntity.status(304).eTag(etag).cacheControl(CACHE).build();
		}
		return ResponseEntity.ok()
			.contentType(MediaType.parseMediaType(stat.get().contentType()))
			.contentLength(stat.get().size())
			.eTag(etag)
			.cacheControl(CACHE)
			.body(new InputStreamResource(this.images.open(objectKey)));
	}

}
