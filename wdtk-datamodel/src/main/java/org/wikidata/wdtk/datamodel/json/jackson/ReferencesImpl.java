package org.wikidata.wdtk.datamodel.json.jackson;

import java.util.List;

import org.wikidata.wdtk.datamodel.interfaces.Reference;
import org.wikidata.wdtk.datamodel.interfaces.SnakGroup;

/**
 * This class is what the external Json describes as references.
 * It contains some meta-information and the mapping called "reference" in the wdtk.
 * @author fredo
 *
 */
public class ReferencesImpl implements Reference {

	@Override
	public List<SnakGroup> getSnakGroups() {
		// delegate down to reference level
		// TODO Auto-generated method stub
		return null;
	}

}
