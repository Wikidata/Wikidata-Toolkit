package org.wikidata.wdtk.dumpfiles;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import org.wikidata.wdtk.datamodel.implementation.DataObjectFactoryImpl;
import org.wikidata.wdtk.datamodel.interfaces.DataObjectFactory;
import org.wikidata.wdtk.datamodel.interfaces.ItemId;
import org.wikidata.wdtk.datamodel.interfaces.ItemRecord;
import org.wikidata.wdtk.datamodel.interfaces.SiteLink;
import org.wikidata.wdtk.datamodel.interfaces.Statement;

// TODO add @link to documentation where needed
/**
 * This class provides methods to convert dump-file JSON objects into
 * representations according to the WDTK data model. Since the converted JSON
 * normally belongs to the same domain, the site IRI is represented as an
 * attribute.
 * 
 * @author Fredo Erxleben
 * 
 */
public class JsonConverter {

	private DataObjectFactory factory = new DataObjectFactoryImpl();
	private String baseIri = "";

	/**
	 * For the <i>baseIri</i> see also
	 * {@link org.wikidata.wdtk.datamodel.interfaces.ItemId}
	 * 
	 * @param baseIri
	 *            the initial IRI to be used for the processed JSON.
	 */
	public JsonConverter(String baseIri) {
		this.setBaseIri(baseIri);
		;
	}

	/**
	 * Attempts to parse a given JSON object into an instance of ItemRecord.
	 * 
	 * @param toConvert
	 *            the JSON object to convert. Must represent an item record.
	 * @param baseIri
	 *            he first part of the IRI of the site this belongs to.
	 * @return the ItemRecord parsed from JSON. Might be <b>null</b>.
	 * @throws NullPointerException
	 *             if toParse was null.
	 * @throws JSONException
	 *             if the JSON object did not contain a key it should have had.
	 */
	public ItemRecord convertToItemRecord(JSONObject toConvert)
			throws JSONException, NullPointerException {

		// initialize variables for the things we need to get
		// TODO check if it would not be better to initialize…
		// …with empty maps/lists
		ItemRecord result = null;
		ItemId itemId = null;
		Map<String, String> labels = null;
		Map<String, String> descriptions = null;
		Map<String, List<String>> aliases = null;
		List<Statement> statements = null;
		Map<String, SiteLink> siteLinks = null;

		// sanity check
		if (toConvert == null) {
			throw new NullPointerException();
		}

		if (toConvert.length() == 0) { // if the JSON object is empty
			throw new JSONException("The JSON to convert was empty");
		}
		// get the item Id
		JSONArray jsonEntity = toConvert.getJSONArray("entity");
		itemId = getItemId(jsonEntity);

		// get the labels
		JSONObject jsonLabels = toConvert.getJSONObject("label");
		labels = this.getLabels(jsonLabels);

		// get the description
		JSONObject jsonDescriptions = toConvert.getJSONObject("description");
		descriptions = this.getDescriptions(jsonDescriptions);

		// get the aliases
		JSONObject jsonAliases = toConvert.getJSONObject("aliases");
		aliases = this.getAliases(jsonAliases);

		// get the statements
		JSONArray jsonStatements = toConvert.getJSONArray("claims");
		statements = this.getStatements(jsonStatements);

		// get the site links
		JSONObject jsonLinks = toConvert.getJSONObject("links");
		siteLinks = this.getSiteLinks(jsonLinks);

		// now put it all together
		result = factory.getItemRecord(itemId, labels, descriptions, aliases,
				statements, siteLinks);
		return result;
	}

	/**
	 * 
	 * @param jsonStatements
	 * @return
	 */
	private List<Statement> getStatements(JSONArray jsonStatements) {
		// TODO complete

		List<Statement> result = new LinkedList<Statement>();

		return result;
	}

	/**
	 * Converts a JSON object into a mapping from site keys to
	 * SiteLink-instances.
	 * 
	 * @param jsonLinks
	 *            a JSON object representing the site links.
	 * @return A mapping with a String representing a site key e.g. "enwiki" as
	 *         key and a SiteLink-object as value.
	 * @throws JSONException
	 */
	private Map<String, SiteLink> getSiteLinks(JSONObject jsonLinks)
			throws JSONException {
		assert jsonLinks != null : "Link JSON object was null";

		// links are siteKey:{"name":string,"badges":[string] }
		// the siteKey is the key for the returned map

		Map<String, SiteLink> result = new HashMap<String, SiteLink>();

		@SuppressWarnings("unchecked")
		Iterator<String> linkIterator = jsonLinks.keys();

		while (linkIterator.hasNext()) {

			String siteKey = linkIterator.next();
			JSONObject currentLink = jsonLinks.getJSONObject(siteKey);
			String title = currentLink.getString("name");
			JSONArray badgeArray = currentLink.getJSONArray("badges");

			// convert badges to List<String>
			List<String> badges = new LinkedList<String>();
			for (int i = 0; i < badgeArray.length(); i++) {
				badges.add(badgeArray.getString(i));
			}

			// create the SiteLink instance
			SiteLink siteLink = factory.getSiteLink(title, siteKey,
					this.baseIri, badges);
			result.put(siteKey, siteLink);
		}

		return result;
	}

	/**
	 * 
	 * @param aliases
	 * @return
	 * @throws JSONException
	 */
	private Map<String, List<String>> getAliases(JSONObject aliases)
			throws JSONException {
		// TODO assertions
		Map<String, List<String>> result = new HashMap<String, List<String>>();

		// aliases are of the form string:[string]

		@SuppressWarnings("unchecked")
		Iterator<String> keyIterator = aliases.keys();

		while (keyIterator.hasNext()) {
			String key = keyIterator.next();
			List<String> value = new LinkedList<String>();
			JSONArray aliasEntries = aliases.getJSONArray(key);

			// get all aliases for a certain language
			for (int i = 0; i < aliasEntries.length(); i++) {
				value.add(aliasEntries.getString(i));
			}
			result.put(key, value);
		}

		return result;
	}

	/**
	 * 
	 * @param descriptions
	 * @return
	 */
	private Map<String, String> getDescriptions(JSONObject descriptions) {
		assert descriptions != null : "Description JSON object was null";

		Map<String, String> result = new HashMap<String, String>();

		Iterator<?> keyIterator = descriptions.keys();

		while (keyIterator.hasNext()) {
			String key = (String) keyIterator.next();
			try {
				String value = descriptions.getString(key);
				result.put(key, value);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}

		return result;
	}

	/**
	 * Constructs the item id of a JSON object denoting an item.
	 * 
	 * @param entity
	 *            a JSON array containing information about the entity.
	 * @throws JSONException
	 *             if the entity does not contain an "item"-entry or the entry
	 *             is not followed by an integer denoting the item id.
	 */
	private ItemId getItemId(JSONArray entity) throws JSONException {
		assert entity != null : "Entity JSONArray was null";

		ItemId itemId;
		String id = null;

		for (int i = 0; i < entity.length(); i++) {
			if (entity.getString(i).equals("item")) {
				// the next thing after "item" should be the item id number
				id = "Q" + entity.getInt(i + 1);
			}
		}

		itemId = factory.getItemId(id, baseIri);
		return itemId;
	}

	/**
	 * Converts a JSON description of the labels into a mapping from language
	 * abbreviations to the labels in these languages.
	 * 
	 * @param labels
	 *            a JSON object containing the labels
	 * @return a mapping between language abbreviations to the label in the
	 *         referring language.
	 * @throws JSONException
	 *             if the iterator returned a non existing key. This could mean
	 *             the JSON object is broken (i.e. has no Strings as keys) or
	 *             something is wrong with the <i>org.json</i> JSON parser.
	 */
	private Map<String, String> getLabels(JSONObject labels)
			throws JSONException {
		assert labels != null : "Label JSON was null";

		Map<String, String> result = new HashMap<String, String>();

		@SuppressWarnings("unchecked")
		Iterator<String> keyIterator = labels.keys();

		while (keyIterator.hasNext()) {
			String key = keyIterator.next();
			String value = labels.getString(key);
			result.put(key, value);
		}

		return result;
	}

	public String getBaseIri() {
		return baseIri;
	}

	/**
	 * For the <i>baseIri</i> see also
	 * {@link org.wikidata.wdtk.datamodel.interfaces.ItemId}
	 * 
	 * @param baseIri
	 *            the new baseIRI to be set. If the given string is null,
	 *            nothing will be done.
	 */
	public void setBaseIri(String baseIri) {
		if (baseIri == null)
			return;
		this.baseIri = baseIri;
	}

}
