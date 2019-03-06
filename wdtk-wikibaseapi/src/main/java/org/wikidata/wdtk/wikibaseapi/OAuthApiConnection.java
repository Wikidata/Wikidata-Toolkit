package org.wikidata.wdtk.wikibaseapi;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import com.github.scribejava.core.model.OAuth1AccessToken;
import com.github.scribejava.core.model.OAuthRequest;
import com.github.scribejava.core.model.Response;
import com.github.scribejava.core.model.Verb;
import com.github.scribejava.core.oauth.OAuth10aService;
import com.github.scribejava.core.builder.ServiceBuilder;

/**
 * A connection to the MediaWiki/Wikibase API which uses OAuth
 * for authentication.
 * 
 * @author Antonin Delpeuch
 *
 */
public class OAuthApiConnection extends ApiConnection {
	
	/**
	 * The OAuth access token obtained by the user through
	 * the OAuth login process.
	 */
	protected OAuth1AccessToken accessToken = null;
	
	/**
	 * The OAuth service, holding the client token and secret.
	 */
	protected OAuth10aService service = null;

	/**
	 * Constructs a connection to the given MediaWiki
	 * API endpoint.
	 * 
	 * @param apiBaseUrl
	 *     the MediaWiki API endpoint, such as "https://www.wikidat.org/w/api.php"
	 */
	public OAuthApiConnection(String apiBaseUrl, String clientToken, String clientSecret) {
		super(apiBaseUrl);
		service = new ServiceBuilder(clientToken)
                .apiSecret(clientSecret)
                .build(new MediaWikiOAuthAPI());
	}
	
	/**
	 * Once an access token has been obtained via the OAuth login process,
	 * this registers the connection as logged in with this token.
	 * This method does not do any HTTP request by itself - the access token
	 * is only stored for future use by API calls.
	 * 
	 * @param username
	 * 		the name of the user logged in
	 * @param accessToken
	 *      the access token obtained after the OAuth process
	 * @param accessSecret
	 * 		the secret key obtained after the OAuth process,
	 */
	public void login(String username, String accessToken, String accessSecret) {
		this.accessToken = new OAuth1AccessToken(accessToken, accessSecret);
		loggedIn = true;
	}
	
	@Override
	public InputStream sendRequest(String requestMethod,
			Map<String, String> parameters) throws IOException {
		OAuthRequest request = new OAuthRequest(Verb.POST, apiBaseUrl);
		service.signRequest(accessToken, request);

		try {
			Response response = service.execute(request);
			int rc = response.getCode();
			if (rc != 200) {
				logger.warn("Error: API request returned response code " + rc);
			}
			return response.getStream();
		} catch (ExecutionException | InterruptedException e) {
			throw new IOException(e);
		}
	}

	@Override
	public void logout() throws IOException {
	    accessToken = null;
		loggedIn = false;
	}

}
