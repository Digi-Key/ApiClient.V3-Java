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
package main.java.com.client.api.usage.examples;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import main.java.com.client.api.APIClientService;
import main.java.com.client.api.models.ApiClientSettings;
import main.java.com.client.api.oauth.AuthorizationException;


/**
 * This example will call the KeywordSearch operation in the PartSearch API. 
 * It will refresh a token if it is expired and a valid refresh token is available. 
 * The clientId must be subscribed to a plan containing PartSearch on the API portal.
 */
public class RefreshAndCall {
	private static Logger _log = Logger.getLogger("RefreshAndCall");
	
	public static void main(String[] args) {
		ApiClientSettings settings = new ApiClientSettings();
		try {
			APIClientService client = new APIClientService(settings);
			String response = client.KeywordSearch("P5555-ND", true);
			_log.log(Level.INFO, response);
		} catch (AuthorizationException e) {
			_log.log(Level.SEVERE,"Unable to refresh token. "+ e.getMessage());
		} catch (IOException e) {
			_log.log(Level.SEVERE,"API request failed. "+ e.getMessage());
		}
	}
}