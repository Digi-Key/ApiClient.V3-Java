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

import java.awt.Desktop;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.KeyStore;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;

import main.java.com.client.api.constants.DigiKeyUrlConstants;
import main.java.com.client.api.models.ApiClientSettings;
import main.java.com.client.api.oauth.AuthorizationException;
import main.java.com.client.api.oauth.OAuth2Service;

/**
 * This example will get a completely new token given a valid client id and secret. A browser window will be opened where you will need to log in with a
 * Digikey.com account. You may need the browser to be open already depending on your default browser.
 */
public class FirstTokenProgram {
	private static Logger _log = Logger.getLogger("FirstTokenProgram");

	public static void main(String[] args) {
		ApiClientSettings settings = new ApiClientSettings();
		String code = "";
		// try {
		String urlString = DigiKeyUrlConstants.AuthorizationEndpoint.toString();
		URI authUrl = null;
		try {
			authUrl = new URI(urlString + "?response_type=code&client_id=" + settings.getClientId() + "&redirect_uri=" + settings.getRedirectUri());
		} catch (URISyntaxException e1) {
			_log.log(Level.SEVERE, "Authorization URI is malformed. " + e1.getMessage());
		}
		if (Desktop.isDesktopSupported()) {
			try {
				Desktop.getDesktop().browse(authUrl);
			} catch (IOException e) {
				_log.log(Level.SEVERE, "Unable to open browser for authentication. " + e.getMessage());
			}
		}
		try {
			// Listen for the code on the port of the redirect URI. Extract port from URI if specified. This should be specified in the App's redirectURI.
			int port;
			try {
				port = Integer.parseInt(settings.getRedirectUri().replaceAll("[^0-9]", ""));
			} catch (NumberFormatException e) // There is no port on the redirect so default to HTTPS default 443.
			{
				port = 443;
			}
			_log.log(Level.INFO, "Listening for OAuth redirect on port " + port + ".");
			// In Java a certificate to listen for HTTPS traffic is required.
			// The client browser will redirect to the service on that URL.
			// If using a self signed certificate for testing purposes the client will need to have accepted it prior to testing.
			// An example self-signed certificate is included but should not be used for production. Change this and store securely.

			String keystore = "src/main/resources/keystore.jks";
			String passphrase = "changeit";

			KeyStore ks = KeyStore.getInstance("PKCS12");
			ks.load(new FileInputStream(keystore), passphrase.toCharArray());
			KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
			kmf.init(ks, passphrase.toCharArray());

			SSLContext sc = SSLContext.getInstance("TLS");
			sc.init(kmf.getKeyManagers(), null, null);

			SSLServerSocketFactory ssf = sc.getServerSocketFactory();
			SSLServerSocket serverSocket = (SSLServerSocket) ssf.createServerSocket(port);

			serverSocket.setEnabledProtocols(new String[] { "TLSv1.2" });
			// NOTE: You must configure Java to support AES 256 encryption.
			// The Oracle Java Cryptography Extension (JCE) Unlimited Strength Jurisdiction Policy Files 6 must be installed.
			// Link is subject to change
			// http://www.oracle.com/technetwork/java/javase/downloads/jce8-download-2133166.html
			Socket socket = serverSocket.accept();
			BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			while (true) {
				String readString = "";
				try {
					readString = in.readLine();
					_log.log(Level.INFO, readString);
					// Grab code from request
					if (readString.contains("code=")) {
						code = readString.substring(readString.indexOf("code=") + 5, readString.indexOf(" HTTP"));
						_log.log(Level.INFO, "Access code: " + code);
						break;
					}
					in.close();
				} catch (IOException e) {
					// An error here indicates the client is not configured to correctly receive secured TLS connections.
					_log.log(Level.SEVERE, e.getMessage());
					System.exit(1);
				} finally {
					socket.close();
					serverSocket.close();
				}
			}
		} catch (IOException e) {
			// If using Eclipse, and running this multiple times, ensure "Terminate and Relaunch while launching" option is enabled so that the socket is freed.
			// If a previous instance is still running/debugging, the port will not be available.
			_log.log(Level.SEVERE, "Connection error:" + e.getMessage());
		} catch (Exception e) {
			// There are half a dozen different exceptions related to the TLS connection and keystore.
			_log.log(Level.SEVERE, "TLS/Keystore error: " + e.getMessage());
		}
		OAuth2Service oauth = new OAuth2Service(settings);
		try {
			oauth.Authorize(code, false);
		} catch (AuthorizationException e) {
			_log.log(Level.SEVERE, "Failed to get OAuth token. " + e.getMessage());
		}
	}
}
