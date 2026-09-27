package com.techchallenger.oficina360;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;

import java.time.Instant;

public class JwtService {

	private final Algorithm algorithm;
	private final long expirationSeconds;

	public JwtService(String secret, long expirationSeconds) {
		this.algorithm = Algorithm.HMAC256(secret);
		this.expirationSeconds = expirationSeconds;
	}

	public AuthResponse generateToken(String document) {

		Instant now = Instant.now();
		Instant expiration = now.plusSeconds(expirationSeconds);
		String token = JWT.create()
				.withIssuer("customer-auth")
				.withSubject(maskDocument(document))
				.withExpiresAt(expiration)
				.sign(algorithm);
		return new AuthResponse(
			token,
			"Bearer",
			expirationSeconds
		);
	}

	private String maskDocument(String document) {
		if (document == null || document.length() < 4) {
			return "***";
		}

		return "***" + document.substring(document.length() - 4);
	}
}
