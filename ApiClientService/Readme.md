# Java Api Client Library with OAuth2

### Features

* Makes structured calls to the DigiKey API from Java projects
* Logs in users using the OAuth2 code flow
* This is an example and does not necessarily follow all Java standards. Do not use as is in Production

### Basic Usage

```java
ApiClientSettings settings = ApiClientSettings.CreateFromConfigFile();
try
{
	APIClientService client = new APIClientService(settings);
	String response = client.KeywordSearch("P5555-ND", true);
	_log.log(Level.INFO, response);
}
catch (Exception e)
{
	_log.log(Level.SEVERE, e);
	throw e;
}
```

### Project Contents

* **ApiClient** - Client Library that contains the code to manage a config file with OAuth2 settings and classes to do the OAuth2 call and  an example call to DigiKey's KeywordSearch Api. 
* **RefreshAndCall** - Console app to test out programatic refresh of access token when needed and also check if access token failed to work and then refresh and try again.
* **FirstTokenProgram** - Console app to create the initial access token and refresh token.

### Getting Started  

1. Download the zip file containing the source
2. You will need to Register an application in order to create your unique Client ID, Client Secret, and OAuth Redirection URL. Follow the steps (1 thru 4) from <https://api-portal.digikey.com/start>.
3. In the solution folder update  apiclient.properties with the ClientId, ClientSecret, and RedirectUri values from step 2.
```
ApiClient.ClientSecret=YOUR SECRET HERE
ApiClient.ClientId=YOUR CLIENT HERE
ApiClient.RedirectUri=YOUR REDIRECT URI HERE
ApiClient.RefreshToken=
ApiClient.AccessToken=
ApiClient.ExpirationDateTime=

```
4. Run FirstTokenProgram to set the access token, refresh token and expiration date.
5. Run RefreshAndCall to get results from keyword search.




