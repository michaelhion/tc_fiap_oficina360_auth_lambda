package com.techchallenger.oficina360;

import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class JwtServiceTest {

	private User user;

	@BeforeEach
	void setup(){
		user = new User("12345678901",true,"teste");
	}

	@Test
	void shouldGenerateaccessToken() {

		JwtService jwtService = new JwtService("my-secret", "oficina360", 3600L);

		AuthResponse response = jwtService.generateToken(user);

		assertNotNull(response);
		assertNotNull(response.accessToken());
		assertFalse(response.accessToken().isBlank());
	}

	@Test
	void shouldReturnBearerTokenType() {

		JwtService jwtService = new JwtService("my-secret", "oficina360", 3600L);

		AuthResponse response = jwtService.generateToken(user);

		assertEquals("Bearer", response.tokenType());
	}

	@Test
	void shouldReturnConfiguredExpiration() {

		long expiration = 3600L;

		JwtService jwtService = new JwtService("my-secret", "oficina360", expiration);

		AuthResponse response = jwtService.generateToken(user);

		assertEquals(expiration, response.expiresIn());
	}

	@Test
	void shouldGenerateJwtWithCorrectIssuer() {

		JwtService jwtService = new JwtService("my-secret", "oficina360", 3600L);

		AuthResponse response = jwtService.generateToken(user);

		DecodedJWT jwt = JWT.decode(response.accessToken());

		assertEquals("oficina360", jwt.getIssuer());
	}

	@Test
	void shouldGenerateJwtWithCorrectSubject() {

		JwtService jwtService = new JwtService("my-secret", "oficina360", 3600L);

		AuthResponse response = jwtService.generateToken(user);

		DecodedJWT jwt = JWT.decode(response.accessToken());

		assertEquals(user.document(), jwt.getSubject());
	}
}