package com.kyrobot.howfar.responses;

public final class ErrorResponse {
	private final String message;

	public ErrorResponse(String message) {
		this.message = message;
	}

	public String getMessage() {
		return this.message;
	}

}
