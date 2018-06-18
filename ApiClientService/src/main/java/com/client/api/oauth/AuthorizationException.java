package main.java.com.client.api.oauth;

/**
 * Exception used to indicate that OAuth authorization has failed and no token has been retrieved.
 */
public class AuthorizationException extends Exception {
	public AuthorizationException(){}
	public AuthorizationException (String message)
	{
		super(message);
	}
}
