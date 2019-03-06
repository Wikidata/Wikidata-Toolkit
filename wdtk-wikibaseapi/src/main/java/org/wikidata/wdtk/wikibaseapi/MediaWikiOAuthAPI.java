package org.wikidata.wdtk.wikibaseapi;

import com.github.scribejava.core.builder.api.DefaultApi10a;

/**
 * Sadly, Scribe Java does not make it possible (as far as
 * I can tell) to sign HTTP requests independently of an 
 * "API" contexts which also contains the OAuth endpoints
 * used to go through the OAuth login process, even if those
 * endpoints are not actually needed to do the signing once
 * the tokens have been acquired.
 * 
 * This class is a dummy service which leaves these URIs unspecified.
 * 
 * @author Antonin Delpeuch
 *
 */
public class MediaWikiOAuthAPI extends DefaultApi10a {

	@Override
	public String getRequestTokenEndpoint() {
		return null;
	}

	@Override
	public String getAccessTokenEndpoint() {
		return null;
	}

	@Override
	protected String getAuthorizationBaseUrl() {
		return null;
	}

}
