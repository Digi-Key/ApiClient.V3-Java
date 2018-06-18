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
package main.java.com.client.api;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
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
import main.java.com.client.api.models.KeywordSearchRequest;
import main.java.com.client.api.oauth.AuthorizationException;
import main.java.com.client.api.oauth.OAuth2Service;

/**
 * This class provides the functionality to call the Keyword Search API operation with an extremely slim request model. It will attempt to refresh the OAuth
 * token if it is expired.
 */
public class APIClientService {
	private static Logger _log = Logger.getLogger("ApiClient");
	private ApiClientSettings ClientSettings;

	public APIClientService(ApiClientSettings clientSettings) {
		ClientSettings = clientSettings;
	}

	/**
	 * This will compare the current token in settings with the current time. If it is expired it will refresh and save the settings.
	 */
	public void ResetExpiredAccessTokenIfNeeded() throws AuthorizationException {
		if (ClientSettings.getExpirationDateTime() < System.currentTimeMillis()) {
			OAuth2Service oAuth2Service = new OAuth2Service(ClientSettings);
			_log.log(Level.WARNING, "Token expiration has passed. Attempting token refresh.");
			oAuth2Service.Authorize("", true);
			// The OAuth2Service has updated the clientSettings in file. Grab the new one.
			ClientSettings = oAuth2Service.getClientSettings();
		}
	}

	/**
	 * Calls the Keyword Search operation in the PartSearch API. If retry is true, it will attempt a token refresh if the call fails. This is used here to retry
	 * one time.
	 * @param keyword to search for
	 * @param retry Whether the keyword search should refresh the token and try again if the token is invalid.
	 * @return result string from API
	 * @throws AuthorizationException unable to retrieve OAuth token
	 * @throws IOException web request to API failed
	 */
	public String KeywordSearch(String keyword, boolean retry) throws AuthorizationException, IOException {
		String resourcePath = "/services/partsearch/v2/keywordsearch";
		KeywordSearchRequest request = new KeywordSearchRequest(keyword, 25);
		ResetExpiredAccessTokenIfNeeded();
		URL postUrl = null;
		try {
			postUrl = new URL(DigiKeyUrlConstants.APIEndpoint, resourcePath);
		} catch (MalformedURLException e1) {
			_log.log(Level.SEVERE, "URL malformed. " + e1.getMessage());
		}
		URLConnection con = null;
		con = postUrl.openConnection();
		HttpURLConnection http = (HttpURLConnection) con;
		http.setConnectTimeout(60000);
		try {
			http.setRequestMethod("POST");
		} catch (ProtocolException e) {
			// This will only happen if the above method isn't POST.
			_log.log(Level.SEVERE, "Invalid HTTP verb. " + e.getMessage());
		}
		http.setDoOutput(true);
		http.setDoInput(true);
		http.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
		_log.log(Level.INFO, "current token: " + ClientSettings.getAccessToken());
		http.setRequestProperty("Authorization", ClientSettings.getAccessToken());
		_log.log(Level.INFO, "client: " + ClientSettings.getClientId());
		http.setRequestProperty("X-IBM-Client-ID", ClientSettings.getClientId());
		Gson gson = new Gson();
		String body = (gson.toJson(request));
		byte[] postData = body.getBytes(StandardCharsets.UTF_8);
		http.setRequestProperty("Content-Length", Integer.toString(postData.length));
		DataOutputStream stream = new DataOutputStream(http.getOutputStream());
		stream.write(postData);
		int code = 0;
		code = http.getResponseCode();
		if (code == 401 && retry) {
			_log.log(Level.WARNING, "Keyword Search failed. Refreshing token and retrying.");
			http.disconnect();
			OAuth2Service oAuth2Service = new OAuth2Service(ClientSettings);
			oAuth2Service.Authorize("", true);
			// The OAuth2Service has updated the clientSettings in file. Grab the new one.
			ClientSettings = oAuth2Service.getClientSettings();
			// Set not to retry again - if it fails again it is likely due to an invalid client Id + subscription so a retry would not help.
			return KeywordSearch(keyword, false);
		}
		BufferedReader in = new BufferedReader(new InputStreamReader(http.getInputStream()));
		String inputLine;
		StringBuffer content = new StringBuffer();
		while ((inputLine = in.readLine()) != null) {
			content.append(inputLine);
		}
		in.close();
		return (content.toString());
	}
}
