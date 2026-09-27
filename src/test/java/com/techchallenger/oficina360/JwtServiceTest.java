package com.techchallenger.oficina360;

import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class JwtServiceTest {

	@Test
	void shouldGenerateaccessToken() {

		JwtService jwtService = new JwtService("my-secret", "oficina360", 3600L);

		AuthResponse response = jwtService.generateToken("12345678901");

		assertNotNull(response);
		assertNotNull(response.accessToken());
		assertFalse(response.accessToken().isBlank());
	}

	@Test
	void shouldReturnBearerTokenType() {

		JwtService jwtService = new JwtService("my-secret", "oficina360", 3600L);

		AuthResponse response = jwtService.generateToken("12345678901");

		assertEquals("Bearer", response.tokenType());
	}

	@Test
	void shouldReturnConfiguredExpiration() {

		long expiration = 3600L;

		JwtService jwtService = new JwtService("my-secret", "oficina360", expiration);

		AuthResponse response = jwtService.generateToken("12345678901");

		assertEquals(expiration, response.expiresIn());
	}

	@Test
	void shouldGenerateJwtWithCorrectIssuer() {

		JwtService jwtService = new JwtService("my-secret", "oficina360", 3600L);

		AuthResponse response = jwtService.generateToken("12345678901");

		DecodedJWT jwt = JWT.decode(response.accessToken());

		assertEquals("oficina360", jwt.getIssuer());
	}

	@Test
	void shouldGenerateJwtWithCorrectSubject() {

		JwtService jwtService = new JwtService("my-secret", "oficina360", 3600L);

		AuthResponse response = jwtService.generateToken("12345678901");

		DecodedJWT jwt = JWT.decode(response.accessToken());

		assertEquals("12345678901", jwt.getSubject());
	}
}