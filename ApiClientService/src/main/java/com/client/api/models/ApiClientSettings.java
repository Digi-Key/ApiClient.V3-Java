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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * ApiClientSettings contains all client configuration needed to call an API and use OAuth.
 */
public class ApiClientSettings {
	static File configFile = new File("src/main/resources/apiclient.properties");
	private static Logger _log = Logger.getLogger("ApiClientSettings");
	public String getClientId() {
		return ClientId;
	}

	public String getClientSecret() {
		return ClientSecret;
	}

	public String getRedirectUri() {
		return RedirectUri;
	}

	public String getAccessToken() {
		return AccessToken;
	}

	public String getRefreshToken() {
		return RefreshToken;
	}

	public long getExpirationDateTime() {
		return ExpirationDateTime;
	}

	private String ClientId;
	private String ClientSecret;
	private String RedirectUri;
	private String AccessToken;
	private String RefreshToken;
	/// Expiration Date is stored as milliseconds since the Unix epoch
	private long ExpirationDateTime;

	/**
	 * Create a new ApiClientSettings object and load current configuration from file.
	 */
	public ApiClientSettings() {
		try(FileReader reader = new FileReader(configFile)) {
			Properties props = new Properties();
			props.load(reader);
			ClientId = props.getProperty("ApiClient.ClientId");
			ClientSecret = props.getProperty("ApiClient.ClientSecret");
			RedirectUri = props.getProperty("ApiClient.RedirectUri");
			AccessToken = props.getProperty("ApiClient.AccessToken");
			RefreshToken = props.getProperty("ApiClient.RefreshToken");
			ExpirationDateTime = Long.parseLong(props.getProperty("ApiClient.ExpirationDateTime"));
			reader.close();
		} catch (FileNotFoundException ex) {
			_log.log(Level.SEVERE, "apiclient.properties file not found!");
			// file does not exist
		} catch (IOException ex) {
			_log.log(Level.SEVERE, "An exception was thrown while reading configuration." + ex.getMessage());
			// I/O error
		} catch (NumberFormatException ex)
		{
			_log.log(Level.SEVERE, "ExpirationDateTime is not a valid format.");
		}
	}
	/**
	 * Store current configuration to file.
	 */
	public void Save() {
		
		try (FileReader reader = new FileReader(configFile);){
			Properties props = new Properties();
			props.load(reader);
			reader.close();
			props.setProperty("ApiClient.ClientId", ClientId);
			props.setProperty("ApiClient.ClientSecret", ClientSecret);
			props.setProperty("ApiClient.RedirectUri", RedirectUri);
			props.setProperty("ApiClient.AccessToken", AccessToken);
			props.setProperty("ApiClient.RefreshToken", RefreshToken);
			props.setProperty("ApiClient.ExpirationDateTime",(Long.toString(ExpirationDateTime)));
			FileWriter writer = new FileWriter(configFile);
			props.store(writer, "API Client Configuration");
			writer.close();
		} catch (FileNotFoundException ex) {
			_log.log(Level.SEVERE, "apiclient.properties file not found!");
			// file does not exist
		} catch (IOException ex) {
			_log.log(Level.SEVERE, "An exception was thrown while updating configuration." + ex.getMessage());
			// I/O error
		}
	}

	/**
	 * Saves changes to file after a token refresh or new token
	 * @param oAuth2AccessToken Token to fetch up to date Tokens from.
	 */
	public void UpdateAndSave(OAuth2AccessToken oAuth2AccessToken) {
		AccessToken = oAuth2AccessToken.getAccessToken();
		RefreshToken = oAuth2AccessToken.getRefreshToken();
		// Response expiration time is in seconds, convert to milliseconds before saving.
		ExpirationDateTime = System.currentTimeMillis() + oAuth2AccessToken.getExpiresIn() * 1000;
		Save();
	}
	
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("   ------------ [ ApiClientSettings ] -------------");
		sb.append("     ClientId            : " + ClientId);
		sb.append("     ClientSecret        : " + ClientSecret);
		sb.append("     RedirectUri         : " + RedirectUri);
		sb.append("     AccessToken         : " + AccessToken);
		sb.append("     RefreshToken        : " + RefreshToken);
		sb.append("     ExpirationDateTime  : " + ExpirationDateTime);
		sb.append("   ---------------------------------------------");

		return sb.toString();
	}
}
