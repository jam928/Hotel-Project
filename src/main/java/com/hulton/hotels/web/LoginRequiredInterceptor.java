package com.hulton.hotels.web;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.util.UriComponentsBuilder;

/**
 * Sends visitors without a logged-in customer to the login page. For a page they
 * opened (a GET), the login page sends them back to it afterwards.
 */
public class LoginRequiredInterceptor implements HandlerInterceptor {

	/** Session attribute holding the logged-in customer's ID. */
	public static final String CID = "CID";

	/** Session attribute holding the logged-in customer's name. */
	public static final String NAME = "name";

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
		HttpSession session = request.getSession(false);
		if (session != null && session.getAttribute(CID) != null) {
			return true;
		}
		UriComponentsBuilder login = UriComponentsBuilder.fromPath(request.getContextPath() + "/login");
		if ("GET".equals(request.getMethod())) {
			String path = request.getRequestURI().substring(request.getContextPath().length());
			String query = request.getQueryString();
			login.queryParam("next", (query != null) ? path + "?" + query : path);
		}
		response.sendRedirect(login.encode().build().toUriString());
		return false;
	}

	/**
	 * Whether {@code next} is a path in this app, so redirecting to it after login
	 * cannot send the visitor to another site.
	 */
	public static boolean isSafeNext(String next) {
		return next != null && next.startsWith("/") && !next.startsWith("//") && !next.contains("\\");
	}

}
