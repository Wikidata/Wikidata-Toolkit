package org.wikidata.wdtk.datamodel.json.jackson.snaks;

import java.util.ArrayList;
import java.util.List;

import org.wikidata.wdtk.datamodel.interfaces.PropertyIdValue;
import org.wikidata.wdtk.datamodel.interfaces.Snak;
import org.wikidata.wdtk.datamodel.interfaces.SnakGroup;
import org.wikidata.wdtk.datamodel.json.jackson.documents.ids.PropertyIdImpl;

/**
 * This class is not actually used in the JSON model, but needed to satisfy the
 * WDTK-datamodel interface. Since this is used only for conversion purposes
 * between different implementations of the WDTK-interface the class does not
 * provide setters apart from the constructor.
 * 
 * @author Fredo Erxleben
 *
 */
public class SnakGroupImpl implements SnakGroup {

	private PropertyIdImpl property;
	private List<SnakImpl> snaks;

	@Override
	public List<Snak> getSnaks() {
		
		// because of the typing provided by the interface one has to
		// re-create the list anew, simple casting is not possible
		List<Snak> returnList = new ArrayList<>(this.snaks.size());
		for(SnakImpl snak : this.snaks){
			returnList.add(snak);
		}
		return returnList;
	}

	@Override
	public PropertyIdValue getProperty() {
		return this.property;
	}

}
