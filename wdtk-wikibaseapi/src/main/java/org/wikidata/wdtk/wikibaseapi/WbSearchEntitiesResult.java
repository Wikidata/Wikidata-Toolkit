package org.wikidata.wdtk.wikibaseapi;

import java.util.List;

import org.wikidata.wdtk.wikibaseapi.JacksonWbSearchEntitiesResult.JacksonMatch;

/**
 * Represents the result of a wbsearchentities action.
 *
 * @author Sören Brunk
 */
public interface WbSearchEntitiesResult {
	
	/**
	 * Represents information about how a document matched the query
	 */
	public  interface Match  {	
		/**
		 * Returns the type (field) of the matching term
		 * e.g "entityId", "label" or "alias".
		 * 
		 * @return type (field) of the match
		 */
		public String getType();
		
		/**
		 * Returns the language of the matching term field.
		 * 
		 * @return language of the match
		 */
		public String getLanguage();
		/**
		 * Returns the text of the matching term.
		 * 
		 * @return text of the match
		 */
		public String getText();
	}

	/**
	 * Returns the id of the entity that the document refers to.
	 * 
	 * @return the entity ID
	 */
	public abstract String getEntityId();

	/** 
	 * Returns the full concept URI (the site IRI with entity ID).
	 * 
	 * @return full concept URI
	 */
	public abstract String getConceptUri();

	/** 
	 * The URL of the wiki site that shows the concept.
	 * 
	 * @return wiki site URL
	 */
	public abstract String getUrl();

	/**
	 * Returns the title of the entity (currently the same as the entity ID).
	 */
	public abstract String getTitle();

	/**
	 * Returns the internal Mediawiki pageid of the entity.
	 * 
	 * @return internal pageid
	 */
	public abstract long getPageId();

	/**
	 * Returns the label of the entity.
	 * 
	 * The language of the returned label depends on the HTTP
	 * <a href="https://www.w3.org/Protocols/rfc2616/rfc2616-sec14.html#sec14.4">
	 * Accept-Language header or the uselang URL parameter.
	 * 
	 * @return the label of the entity
	 */
	public abstract String getLabel();

	/**
	 * Returns the description of the entity
	 * 
	 * The language of the returned description depends on the HTTP
	 * <a href="https://www.w3.org/Protocols/rfc2616/rfc2616-sec14.html#sec14.4">
	 * Accept-Language header or the uselang URL parameter.
	 * 
	 * @return the description
	 */
	public abstract String getDescription();

	/**
	 * Returns detailed information about how a document matched the query.
	 * 
	 * @return match information
	 */
	public abstract JacksonMatch getMatch();

	/**
	 * A list of alias labels (returned only when an alias matched the query).
	 * 
	 * @ return a list of aliases
	 */
	public abstract List<String> getAliases();

}