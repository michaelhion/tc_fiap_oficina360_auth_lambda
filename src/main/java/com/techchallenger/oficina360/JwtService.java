package com.techchallenger.oficina360;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;

import java.time.Instant;

public class JwtService {

	private final Algorithm algorithm;
	private final long expirationSeconds;
	private final String issuer;

	public JwtService(String secret, String issuer,long expirationSeconds) {
		this.algorithm = Algorithm.HMAC256(secret);
		this.issuer = issuer;
		this.expirationSeconds = expirationSeconds;
	}

	public AuthResponse generateToken(String document) {

		Instant now = Instant.now();
		Instant expiration = now.plusSeconds(expirationSeconds);
		String token = JWT.create()
				.withIssuer(issuer)
				.withSubject(document)
				.withExpiresAt(expiration)
				.sign(algorithm);
		return new AuthResponse(
			token,
			"Bearer",
			expirationSeconds
		);
	}
}
