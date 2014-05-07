package org.wikidata.wdtk.rdf;

/*
 * #%L
 * Wikidata Toolkit RDF
 * %%
 * Copyright (C) 2014 Wikidata Toolkit Developers
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import org.wikidata.wdtk.datamodel.interfaces.PropertyIdValue;

/**
 * This class is in charge of mapping the values of some predefined string
 * properties to URIs of other RDF datasets that they refer to. This is useful
 * to convert string identifiers to proper RDF identifiers.
 * 
 * @author Markus Kroetzsch, Fredo Erxleben
 * 
 */
public class LinkedDataProperties {
	
	static final String HTTP = "http://";
	static final String MUSIC_BRAINZ_URL = "http://musicbrainz.org/";
	static final String ISNI_URL = "http://www.isni.org/search&q=";
	static final String VIAF_PERMALINK_URL = "http://viaf.org/viaf/";
	static final String CHEMSPIDER = "http://rdf.chemspider.com/";

	/**
	 * Returns the URI string that should be used to represent the given value
	 * of the given property, or null if it should just be used as it is.
	 * 
	 * @param property
	 *            the property that the value belongs to
	 * @param value
	 *            the string value
	 * @return
	 */
	public static String getUriForPropertyValue(PropertyIdValue property,
			String value) {
		
		// NOTE: none of these functions is bulletproof
		// there is no guarantee that the generated URIs will work in any case
		// so check the html return codes if requesting one
		
		switch (property.getId()) {
		
//		case "P213": // ISNI // no RDF-export available yet
//			return ISNI_URL + value;
		case "P214": // VIAF
			return VIAF_PERMALINK_URL + value + ".rdf";
		case "P227": // GND
			return getGndUri(value);
		case "P243": // OCLC
			return getOclcUri(value);
		case "P244": // LCNAF
			return getLcnafUri(value);
		case "P269": // SUDOC
			return getSudocUri(value);
		case "P349":
			return getNdlUri(value);

		// --- Chemical Identifiers as resolved by chemspider.com	
		case "P231": // CAS registry number
		case "P233": // SMILES
		case "P235": // InChI-Keys
		case "P661": // ChemSpider ID
			return CHEMSPIDER + value;	
			
		case "P662": // PubChem (CID)
			return getPubChemCidUri(value);
			
//		case "P234": // InChIs
//			return CHEMSPIDER + formatInChI(value);
			
		case "P686":
			return getGeneOnthologyUri(value);
		
		// --- MusicBrainz //NOTE: no useful RDF yet	
//		case "P434":
//			return getMusicBrainz(value, "artist");
//		case "P435":
//			return getMusicBrainz(value, "work");
//		case "P436":
//			return getMusicBrainz(value, "release-group");
//		case "P966":
//			return getMusicBrainz(value, "label");
//		case "P982":
//			return getMusicBrainz(value, "area");
//		case "P1004":
//			return getMusicBrainz(value, "place");
			
		// --- Freebase
		case "P646":
			return getFreebaseUri(value);
		default:
			return null;
		}
	}
	
	static String getGeneOnthologyUri(String value) {
		// NOTE: should work in theory,
		// but delivers 404s
		// see http://www.geneontology.org/GO.format.rdfxml.shtml
		return HTTP + "www.geneontology.org/go#GO:" + value;
	}

	static String getNdlUri(String value) {
		return HTTP + "id.ndl.go.jp/auth/ndlna/" + value + ".rdf";
	}

	/**
	 * Note that there is also an SID, which is not covered by this method.
	 * @param value
	 * @return
	 */
	static String getPubChemCidUri(String value) {
		return HTTP + "rdf.ncbi.nlm.nih.gov/pubchem/compound/CID" + value;
	}

	static String getOclcUri(String value) {
		return HTTP + "www.worldcat.org/oclc/" + value;
	}

	private static String getSudocUri(String value) {
		return HTTP + "www.idref.fr/" + value + ".rdf";
	}

	/**
	 * Gets the RDF URIs for the given item in the LCNAF.
	 * @param value
	 * @return
	 */
	static String getLcnafUri(String value) {
		return HTTP + "id.loc.gov/authorities/names/" + value + ".rdf";
	}


	/**
	 * Returns the Wikimedia Commons page URL for the given page name.
	 * 
	 * @param pageName
	 *            name of a page on Wikimedia Commons
	 * @return URL of the page
	 */
	public static String getCommonsUrl(String pageName) {
		return "http://commons.wikimedia.org/wiki/File:"
				+ pageName.replace(' ', '_');
	}
	
	/**
	 * Returns the Freebase URI for the given Freebase identifier.
	 * 
	 * @param value
	 *            Freebase identifier
	 * @return the Freebase URI
	 */
	static String getFreebaseUri(String value) {
		return "http://rdf.freebase.com/ns/"
				+ value.substring(1).replace('/', '.');
	}

	/**
	 * Creates the MusicBrainz URI for a given identifier and infix.
	 * The infix determines what the identifier refers to and is dependent on the property that the identifier belongs to.
	 * 
	 * An identifier "<i>abcd</i>" and the infix "<b>label</b>" will create the URI
	 * "http://musicbrainz.org/<b>label</b>/<i>abcd</i>"
	 * 
	 * @param identifier
	 * @param infix
	 * @return
	 */
	static String getMusicBrainz(String identifier, String infix){
		return MUSIC_BRAINZ_URL + infix + "/" + identifier;
	}
	
	/**
	 * Format InChI-identifiers to be used with the chemspider.com search
	 * Do not use for InChI-Keys!
	 * @param inChi
	 * if the "InChI="- prefix is missing, it will be added.
	 * @return
	 */
	static String formatInChI(String inChi){
		// make sure it is prefixed
		if(!inChi.startsWith("InChI=")){
			inChi = "InChI="+inChi;
		}
		// there are no "+"-signs allowed in the url, replace them properly
		inChi.replaceAll("+", "%2b");
		return inChi;
	}
	
	/**
	 * See also <b>http://www.dnb.de/SharedDocs/Downloads/DE/DNB/service/linkedDataZugriff.pdf?__blob=publicationFile</b>
	 * (Description in german)
	 * @param identifier
	 * @return
	 */
	static String getGndUri(String identifier){
		return HTTP + "d-nb.info/gnd" + identifier + "/about/rdf";
	}
	
}
