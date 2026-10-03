package com.hulton.hotels.image;

import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.ByteArrayInputStream;
import java.util.Optional;

import io.minio.StatObjectResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(ImageController.class)
class ImageControllerTest {

	private static final byte[] PHOTO = { 1, 2, 3, 4 };

	@Autowired
	private MockMvc mvc;

	@MockitoBean
	private ImageStore images;

	@Test
	void servesThePhotoWithCachingHeaders() throws Exception {
		photoExists("rooms/suite.jpg");
		given(this.images.open("rooms/suite.jpg")).willReturn(new ByteArrayInputStream(PHOTO));

		this.mvc.perform(get("/images/rooms/suite.jpg"))
			.andExpect(status().isOk())
			.andExpect(content().contentType("image/jpeg"))
			.andExpect(content().bytes(PHOTO))
			.andExpect(header().string("ETag", "\"abc123\""))
			.andExpect(header().string("Cache-Control", "max-age=604800, public"));
	}

	@Test
	void answersNotModifiedWhenTheBrowserHasTheCurrentVersion() throws Exception {
		photoExists("rooms/suite.jpg");

		this.mvc.perform(get("/images/rooms/suite.jpg").header("If-None-Match", "\"abc123\""))
			.andExpect(status().isNotModified());
		then(this.images).should(never()).open("rooms/suite.jpg");
	}

	@Test
	void answersNotFoundForAMissingPhoto() throws Exception {
		given(this.images.stat("rooms/missing.jpg")).willReturn(Optional.empty());

		this.mvc.perform(get("/images/rooms/missing.jpg")).andExpect(status().isNotFound());
	}

	private void photoExists(String key) throws Exception {
		StatObjectResponse stat = mock(StatObjectResponse.class);
		given(stat.etag()).willReturn("abc123");
		given(stat.contentType()).willReturn("image/jpeg");
		given(stat.size()).willReturn((long) PHOTO.length);
		given(this.images.stat(key)).willReturn(Optional.of(stat));
	}

}
