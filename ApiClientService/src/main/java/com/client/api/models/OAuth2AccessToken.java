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
package main.java.com.client.api.models;

import com.google.gson.annotations.Expose;

/**
 * Class used for JSON serialization of OAuth token response
 */
public class OAuth2AccessToken {

	public String getAccessToken() {
		return access_token;
	}

	public void setAccessToken(String accessToken) {
		access_token = accessToken;
	}

	public String getTokenType() {
		return token_type;
	}

	public void setTokenType(String tokenType) {
		token_type = tokenType;
	}

	public String getRefreshToken() {
		return refresh_token;
	}

	public void setRefreshToken(String refreshToken) {
		refresh_token = refreshToken;
	}

	public int getExpiresIn() {
		return expires_in;
	}

	public void setExpiresIn(int expiresIn) {
		expires_in = expiresIn;
	}

	@Expose
	private String access_token;
	@Expose
	private String token_type;
	@Expose
	private String refresh_token;
	@Expose
	private int expires_in;

	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("   ----------- [ OAuth2AccessToken ] ----------");
		sb.append("     AccessToken      : " + access_token);
		sb.append("     RefreshToken     : " + refresh_token);
		sb.append("     TokenType        : " + token_type);
		sb.append("     ExpiresIn        : " + expires_in);
		sb.append("   ---------------------------------------------");

		return sb.toString();
	}
}
