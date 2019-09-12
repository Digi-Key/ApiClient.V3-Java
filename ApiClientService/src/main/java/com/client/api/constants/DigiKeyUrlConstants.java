//-----------------------------------------------------------------------
//
// THE SOFTWARE IS PROVIDED `"AS IS`" WITHOUT ANY WARRANTIES OF ANY KIND, EXPRESS, IMPLIED, STATUTORY, 
// OR OTHERWISE. EXPECT TO THE EXTENT PROHIBITED BY APPLICABLE LAW, DIGI-KEY DISCLAIMS ALL WARRANTIES, 
// INCLUDING, WITHOUT LIMITATION, ANY IMPLIED WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE, 
// SATISFACTORY QUALITY, TITLE, NON-INFRINGEMENT, QUIET ENJOYMENT, 
// AND WARRANTIES ARISING OUT OF ANY COURSE OF DEALING OR USAGE OF TRADE. 
// 
// DIGI-KEY DOES NOT WARRANT THAT THE SOFTWARE WILL FUNCTION AS DESCRIBED, 
// WILL BE UNINTERRUPTED OR ERROR-FREE, OR FREE OF HARMFUL COMPONENTS.
// 
//-----------------------------------------------------------------------
package main.java.com.client.api.constants;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * URL constants to talk to Digi-Key OAuth2 server implementation.
 */
public final class DigiKeyUrlConstants {
	public static URL BaseAddress;
	public static URL TokenEndpoint;
	public static URL AuthorizationEndpoint;
	public static URL APIEndpoint;
	static {
		try {
			BaseAddress = new URL("https://api.digikey.com");
			TokenEndpoint = new URL("https://api.digikey.com/v1/oauth2/token");
			AuthorizationEndpoint = new URL("https://api.digikey.com/v1/oauth2/authorize");
			APIEndpoint = new URL("https://api.digikey.com");
		} catch (MalformedURLException e) {
			//This will not happen unless the above URLs are modified incorrectly.
			throw new RuntimeException(e);
		}
	}

	private DigiKeyUrlConstants() {
	}
}