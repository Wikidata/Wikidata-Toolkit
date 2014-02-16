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
import org.wikidata.wdtk.datamodel.interfaces.EntityRecord;
import org.wikidata.wdtk.datamodel.interfaces.ItemId;
import org.wikidata.wdtk.datamodel.interfaces.ItemRecord;
import org.wikidata.wdtk.datamodel.interfaces.SiteLink;
import org.wikidata.wdtk.datamodel.interfaces.Statement;

// NOTE: the current implementation is extremely verbose regarding exceptions.
// It will not mask any exceptions occurring and always print a full stack trace

/**
 * 
 * @author fredo
 * 
 */
public class JsonParser {

	private DataObjectFactory factory = new DataObjectFactoryImpl();

	public JsonParser() {

	}

	/**
	 * Attempts to parse a given JSON object into an instance of ItemRecord. For
	 * the <i>baseIri</i> see also
	 * {@link org.wikidata.wdtk.datamodel.interfaces.ItemId}
	 * 
	 * @param toParse
	 *            the JSON object to parse. Must represent an entity record.
	 * @param baseIri
	 *            he first part of the IRI of the site this belongs to.
	 * @return the ItemRecord parsed from JSON. Might be <b>null</b>.
	 * @throws NullPointerException
	 *             if toParse was null.
	 */
	public ItemRecord parseToItemRecord(JSONObject toParse, String baseIri) {
		// initialize variables for the things we need to get
		ItemRecord result = null;
		ItemId itemId = null;
		Map<String, String> labels = null;
		Map<String, String> descriptions = null;
		Map<String, List<String>> aliases = null;
		List<Statement> statements = null;
		Map<String, SiteLink> siteLinks = null;

		// sanity check
		if (toParse == null) {
			throw new NullPointerException();
		}

		if (toParse.length() == 0) { // if the JSON object is empty
			return result;
			// TODO better throw exception? or return an empty ItemRecord?
		}

		// get the item Id
		itemId = getItemId(toParse, baseIri);

		// get the labels
		try {
			JSONObject jsonLabels = toParse.getJSONObject("label");
			labels = this.getLabels(jsonLabels);
		} catch (JSONException e) {
			e.printStackTrace();
		}

		// get the description
		try{
			JSONObject jsonDescriptions = toParse.getJSONObject("description");
			descriptions = this.getDescriptions(jsonDescriptions);
		} catch (JSONException e){
			e.printStackTrace();
		}
		// get the aliases
		try{
			JSONObject jsonAliases = toParse.getJSONObject("aliases");
			aliases = this.getAliases(jsonAliases);
		} catch (JSONException e){
			e.printStackTrace();
		}
		// TODO get the statements
		// TODO get the site links
		// links are string:{"name":string,"badges":[string] }

		// now put it all together
		result = factory.getItemRecord(itemId, labels, descriptions, aliases,
				statements, siteLinks);
		return result;
	}

	/**
	 * 
	 * @param aliases
	 * @return
	 */
	private Map<String, List<String>> getAliases(JSONObject aliases) {
		Map<String, List<String>> result = new HashMap<String,List<String>>();

		// aliases are of the form string:[string]
		
		Iterator<?> keyIterator = aliases.keys();
		
		while (keyIterator.hasNext()) {
			String key = (String) keyIterator.next();
			try {
				List<String> value = new LinkedList<String>();
				JSONArray aliasEntries = aliases.getJSONArray(key);
				// get all aliases for a certain language
				for(int i = 0; i < aliasEntries.length(); i++){
					value.add(aliasEntries.getString(i));
				}
				
				result.put(key, value);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		
		return result;
	}


	/**
	 * 
	 * @param descriptions
	 * @return
	 */
	private Map<String, String> getDescriptions(JSONObject descriptions) {
		Map<String,String> result = new HashMap<String,String>();
		
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
	 * @param toParse
	 * @param baseIri
	 * @return
	 */
	private ItemId getItemId(JSONObject toParse, String baseIri) {
		ItemId itemId;
		String id = null;
		try {
			JSONArray entity = toParse.getJSONArray("entity");
			for (int i = 0; i < entity.length(); i++) {
				if(entity.getString(i).equals("item")){ 
					// the next thing after "item" should be the item id number 
					id = "Q" + entity.getInt(i+1);
				}

			}
		} catch (JSONException e) {
			e.printStackTrace();
		}

		itemId = factory.getItemId(id, baseIri);
		return itemId;
	}

	/**
	 * 
	 * @param labels
	 *            a JSON object containing the labels
	 * @return
	 */
	private Map<String, String> getLabels(JSONObject labels) {
		Map<String, String> result = new HashMap<String, String>();
		assert labels != null : "Label JSON was null";

		Iterator<?> keyIterator = labels.keys();

		while (keyIterator.hasNext()) {
			String key = (String) keyIterator.next();
			try {
				String value = labels.getString(key);
				result.put(key, value);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}

		return result;
	}

}
