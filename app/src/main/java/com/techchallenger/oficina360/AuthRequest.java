package com.techchallenger.oficina360;

public record AuthRequest(
		String document
) {
	public String normalizedDocument(){
		if (document == null) {
			return null;
		}
		return document.replaceAll("[^0-9a-zA-Z]", "").toUpperCase();
	}
}
