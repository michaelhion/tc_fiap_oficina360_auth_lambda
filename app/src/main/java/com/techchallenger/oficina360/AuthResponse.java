package com.techchallenger.oficina360;

public record AuthResponse(
		String accessToken,
		String tokenType,
		long expiresIn
) {
}