package com.hulton.hotels.image;

import java.io.InputStream;
import java.util.Optional;

import io.minio.BucketExistsArgs;
import io.minio.GetObjectArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.StatObjectArgs;
import io.minio.StatObjectResponse;
import io.minio.errors.ErrorResponseException;
import io.minio.errors.MinioException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Photos in the image bucket, addressed by object key such as {@code rooms/suite.jpg}.
 * The bucket is private; the app serves the photos (see {@link ImageController}).
 */
@Service
@RequiredArgsConstructor
public class ImageStore {

	private final MinioClient minio;

	private final ImageProperties properties;

	/** The photo's size, type and ETag, or empty if there is no such photo. */
	public Optional<StatObjectResponse> stat(String key) throws MinioException {
		try {
			return Optional.of(this.minio.statObject(
					StatObjectArgs.builder().bucket(this.properties.bucket()).object(key).build()));
		}
		catch (ErrorResponseException ex) {
			if ("NoSuchKey".equals(ex.errorResponse().code())) {
				return Optional.empty();
			}
			throw ex;
		}
	}

	/** The photo's content. The caller must close the stream. */
	public InputStream open(String key) throws MinioException {
		return this.minio.getObject(GetObjectArgs.builder().bucket(this.properties.bucket()).object(key).build());
	}

	public void put(String key, InputStream content, long size, String contentType) throws MinioException {
		this.minio.putObject(PutObjectArgs.builder()
			.bucket(this.properties.bucket())
			.object(key)
			.stream(content, size, null)
			.contentType(contentType)
			.build());
	}

	public void createBucketIfMissing() throws MinioException {
		String bucket = this.properties.bucket();
		if (!this.minio.bucketExists(BucketExistsArgs.builder().bucket(bucket).build())) {
			this.minio.makeBucket(MakeBucketArgs.builder().bucket(bucket).build());
		}
	}

}
