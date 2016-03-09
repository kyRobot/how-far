package com.kyrobot.howfar.reporting;

/**
 *	Checked or otherwise caught Exception Wrapper
 *
 *	Services should wrap and rethrow checked exceptions when a user
 *   could benefit from knowing the cause of a server error.
 *   
 *  Note: Unwrapped or Unchecked Runtime Exceptions will always
 *   report as HTTP 500 
 */
public class ServerRuntimeException extends RuntimeException {

	private static final long serialVersionUID = 4751684024686114205L;
	private final String userMessage;
	private final int httpStatus;
	private final Exception cause;
	
	public ServerRuntimeException(Exception cause, int httpStatus, String userMessage) {
		super(cause.getMessage(), cause);
		this.cause = cause;
		this.httpStatus = httpStatus;
		this.userMessage = userMessage;
	}
	
	@Override
	public synchronized Throwable getCause() {
		return this.cause;
	}
	
	public int getHttpStatus() {
		return httpStatus;
	}
	
	public String getUserMessage() {
		return userMessage;
	}
	
	@Override
	public String getMessage() {
		return cause.getMessage();
	}

}
