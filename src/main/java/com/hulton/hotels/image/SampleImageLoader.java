package com.hulton.hotels.image;

import java.io.InputStream;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBooleanProperty;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.http.MediaType;
import org.springframework.http.MediaTypeFactory;
import org.springframework.stereotype.Component;

/**
 * Uploads the sample photos (image files only) in {@code classpath:sample-images} to the image bucket on
 * startup, keyed by their path (e.g. {@code hotels/toronto.jpg}), skipping any already
 * there. The sample data migration points the sample hotels and rooms at these keys.
 * If the store can't be reached the app still starts, just without photos.
 */
@Slf4j
@Component
@RequiredArgsConstructor
@ConditionalOnBooleanProperty("hulton.images.seed-samples")
class SampleImageLoader implements ApplicationRunner {

	private static final String LOCATION = "sample-images/";

	private final ImageStore images;

	@Override
	public void run(ApplicationArguments args) {
		try {
			this.images.createBucketIfMissing();
			int uploaded = 0;
			for (Resource photo : new PathMatchingResourcePatternResolver().getResources("classpath:" + LOCATION + "**/*.*")) {
				MediaType type = MediaTypeFactory.getMediaType(photo).orElse(MediaType.APPLICATION_OCTET_STREAM);
				if (!"image".equals(type.getType())) {
					continue;
				}
				String uri = photo.getURI().toString();
				String key = uri.substring(uri.lastIndexOf(LOCATION) + LOCATION.length());
				if (this.images.stat(key).isEmpty()) {
					try (InputStream content = photo.getInputStream()) {
						this.images.put(key, content, photo.contentLength(), type.toString());
					}
					uploaded++;
				}
			}
			log.info("Sample photos: uploaded {} to the image store", uploaded);
		}
		catch (Exception ex) {
			log.warn("Could not upload the sample photos to the image store: {}", ex.toString());
		}
	}

}
