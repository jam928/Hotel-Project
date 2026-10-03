package com.hulton.hotels.web;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

class LoginRequiredInterceptorTest {

	@ParameterizedTest
	@ValueSource(strings = { "/booking/payment", "/hotels/1?checkin=2026-10-01", "/" })
	void pathsInTheAppAreSafeToRedirectTo(String next) {
		assertThat(LoginRequiredInterceptor.isSafeNext(next)).isTrue();
	}

	@ParameterizedTest
	@NullSource
	@ValueSource(strings = { "https://evil.example", "//evil.example", "/\\evil.example", "welcome", "" })
	void otherSitesAndRelativePathsAreNot(String next) {
		assertThat(LoginRequiredInterceptor.isSafeNext(next)).isFalse();
	}

}
