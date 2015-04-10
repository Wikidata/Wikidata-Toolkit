package org.wikidata.wdtk.rdf.extensions;

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

import org.wikidata.wdtk.datamodel.interfaces.StringValue;

/**
 * Export extension for identifiers that are formed by simply putting a URI
 * prefix and postfix around the string value stored in Wikibase.
 * 
 * @author Markus Kroetzsch
 * 
 */
public class SimpleIdExportExtension extends StringIdExportExtension {

	static final String MUSIC_BRAINZ_URL = "http://musicbrainz.org/";

	final String uriPrefix;
	final String uriPostfix;
	final String propertyPostfix;

	public SimpleIdExportExtension(String propertyPostfix, String uriPrefix,
			String uriPostfix) {
		this.uriPrefix = uriPrefix;
		this.uriPostfix = uriPostfix;
		this.propertyPostfix = propertyPostfix;
	}

	@Override
	public String getPropertyPostfix() {
		return this.propertyPostfix;
	}

	@Override
	public String getValueUri(StringValue value) {
		return this.uriPrefix + value.getString() + this.uriPostfix;
	}

	public static SimpleIdExportExtension newGndExportExtension() {
		return new SimpleIdExportExtension("gnd", "http://d-nb.info/gnd/",
				"/about/rdf");
	}

	public static SimpleIdExportExtension newViafExportExtension() {
		return new SimpleIdExportExtension("viaf", "http://viaf.org/viaf/", "");
	}

	public static SimpleIdExportExtension newOclcExportExtension() {
		return new SimpleIdExportExtension("oclc",
				"http://www.worldcat.org/oclc/", "");
	}

	public static SimpleIdExportExtension newLcnafExportExtension() {
		return new SimpleIdExportExtension("lcnaf",
				"http://id.loc.gov/authorities/names/", "");
	}

	public static SimpleIdExportExtension newNdlExportExtension() {
		return new SimpleIdExportExtension("ndl",
				"http://id.ndl.go.jp/auth/entity/", "");
	}

	public static SimpleIdExportExtension newChemSpiderExportExtension() {
		return new SimpleIdExportExtension("chemspider",
				"http://rdf.chemspider.com/", "");
	}

	public static SimpleIdExportExtension newPubChemCidExportExtension() {
		return new SimpleIdExportExtension("pubchemcid",
				"http://rdf.ncbi.nlm.nih.gov/pubchem/compound/CID", "");
	}

	public static SimpleIdExportExtension newChEMBLExportExtension() {
		return new SimpleIdExportExtension("chembl",
				"http://rdf.ebi.ac.uk/resource/chembl/molecule/", "");
	}

	public static SimpleIdExportExtension newKEGGExportExtension() {
		return new SimpleIdExportExtension("kegg",
				"http://www.kegg.jp/entry/", "");
	}

	public static SimpleIdExportExtension newMusicBrainzArtistExportExtension() {
		return new SimpleIdExportExtension("mbartist", MUSIC_BRAINZ_URL,
				"/artist");
	}

	public static SimpleIdExportExtension newMusicBrainzWorkExportExtension() {
		return new SimpleIdExportExtension("mbwork", MUSIC_BRAINZ_URL, "/work");
	}

	public static SimpleIdExportExtension newMusicBrainzLabelExportExtension() {
		return new SimpleIdExportExtension("mblabel", MUSIC_BRAINZ_URL,
				"/label");
	}

	public static SimpleIdExportExtension newMusicBrainzPlaceExportExtension() {
		return new SimpleIdExportExtension("mbplace", MUSIC_BRAINZ_URL,
				"/place");
	}

	public static SimpleIdExportExtension newMusicBrainzAreaExportExtension() {
		return new SimpleIdExportExtension("mbarea", MUSIC_BRAINZ_URL, "/area");
	}

	public static SimpleIdExportExtension newMusicBrainzReleaseGroupExportExtension() {
		return new SimpleIdExportExtension("mbrelgroup", MUSIC_BRAINZ_URL,
				"/release-group");
	}

	public static SimpleIdExportExtension newSudocExportExtension() {
		return new SimpleIdExportExtension("sudoc", "http://www.idref.fr/",
				"/id");
	}

	public static SimpleIdExportExtension newIso639_3ExportExtension() {
		return new SimpleIdExportExtension("iso639-3",
				"http://www.lexvo.org/data/iso639-3/", "");
	}

	public static SimpleIdExportExtension newGeneOntologyExportExtension() {
		return new SimpleIdExportExtension("go", "www.geneontology.org/go#GO:",
				"");
	}
}