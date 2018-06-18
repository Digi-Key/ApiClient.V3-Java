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
package main.java.com.client.api.oauth;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import main.java.com.client.api.constants.DigiKeyUrlConstants;
import main.java.com.client.api.models.ApiClientSettings;
import main.java.com.client.api.models.OAuth2AccessToken;

///Class responsible for getting and refreshing OAuth tokens.
public class OAuth2Service {
	private static Logger _log = Logger.getLogger("OAuth2Service");

	public ApiClientSettings getClientSettings() {
		return ClientSettings;
	}

	private ApiClientSettings ClientSettings;

	public OAuth2Service(ApiClientSettings clientSettings) {
		ClientSettings = clientSettings;
	}

	/**
	 * Finishes authorization by passing the authorization code to the Token or utilizes a refresh token.
	 * 
	 * @param code
	 *            Code value returned by the RedirectUri. Required if not refreshing.
	 * @param refresh
	 *            Boolean if refreshing or not.
	 * @return OAuth2AccessToken with up to date tokens.
	 * @throws AuthorizationException
	 *             Indicates that OAuth authorization has failed and no token has been retrieved.
	 */
	public OAuth2AccessToken Authorize(String code, boolean refresh) throws AuthorizationException {
		String body = "";
		if (refresh) {
			body = "client_id=" + ClientSettings.getClientId() + "&client_secret=" + ClientSettings.getClientSecret()
					+ "&grant_type=refresh_token&refresh_token=" + ClientSettings.getRefreshToken() + "&redirect_uri=" + ClientSettings.getRedirectUri();
		} else {
			body = "code=" + code + "&client_id=" + ClientSettings.getClientId() + "&client_secret=" + ClientSettings.getClientSecret()
					+ "&grant_type=authorization_code&redirect_uri=" + ClientSettings.getRedirectUri();
		}
		_log.log(Level.INFO, "Authorization body: " + body);
		URL postUrl = DigiKeyUrlConstants.TokenEndpoint;
		URLConnection con = null;
		try {
			con = postUrl.openConnection();
		} catch (IOException e) {
			_log.log(Level.SEVERE, "Unable to open connection with OAuth server. " + e.getMessage());
			throw new AuthorizationException("Unable to retrieve OAuth token. Error with connection to OAuth server. " + e.getMessage());
		}
		HttpURLConnection http = (HttpURLConnection) con;
		http.setConnectTimeout(30000);
		try {
			http.setRequestMethod("POST");
		} catch (ProtocolException e) {
			// This will only happen if you type something that isn't POST above.
			_log.log(Level.SEVERE, "Invalid HTTP verb. " + e.getMessage());
			throw new AuthorizationException("Unable to retrieve OAuth token. HTTP Verb was not POST.");
		}
		http.setDoOutput(true);
		http.setDoInput(true);
		http.setRequestProperty("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
		byte[] postData = body.getBytes(StandardCharsets.UTF_8);
		http.setRequestProperty("Content-Length", Integer.toString(postData.length));
		StringBuffer content = new StringBuffer();
		try (DataOutputStream stream = new DataOutputStream(http.getOutputStream())) {
			stream.write(postData);

			if (http.getResponseCode() == 401 || http.getResponseCode() == 400)
				_log.log(Level.SEVERE,
						"Client Id + Secret + Redirect combo or code was invalid when attempting to get token. Or if refreshing, refresh token may be expired.");
			BufferedReader in = new BufferedReader(new InputStreamReader(http.getInputStream()));
			String inputLine;
			while ((inputLine = in.readLine()) != null) {
				content.append(inputLine);
			}
			in.close();
		} catch (IOException e) {
			_log.log(Level.SEVERE, "Unable to send OAuth request. " + e.getMessage());
			throw new AuthorizationException("Unable to retrieve OAuth token. Connection Error. " + e.getMessage());
		}
		try {
			OAuth2AccessToken oAuth2AccessTokenResponse = ParseOAuth2AccessTokenResponse(content.toString());
			ClientSettings.UpdateAndSave(oAuth2AccessTokenResponse);
			return oAuth2AccessTokenResponse;
		} catch (JsonSyntaxException e) {
			_log.log(Level.SEVERE, "Unable to parse OAuth2 access token response " + e.getMessage());
			throw new AuthorizationException("Unable to retrieve OAuth token. OAuth response was not well formed.");
		} finally {
			http.disconnect();
		}
	}

	/**
	 * Converts the OAuth token JSON response into a OAuth2AccessToken object
	 * 
	 * @param response
	 *            unparsed data from token request.
	 * @return OAuth2AccessToken with up to date tokens.
	 * @throws JsonSyntaxException
	 *             thrown when the token response is not well formed.
	 */
	protected static OAuth2AccessToken ParseOAuth2AccessTokenResponse(String response) throws JsonSyntaxException {
		_log.log(Level.INFO, "Raw OAuth token response: " + response);
		Gson gson = new Gson();
		OAuth2AccessToken oAuth2AccessTokenResponse = gson.fromJson(response, OAuth2AccessToken.class);
		_log.log(Level.INFO, "RefreshToken: " + oAuth2AccessTokenResponse.toString());
		return oAuth2AccessTokenResponse;
	}
}
