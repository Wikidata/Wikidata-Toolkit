package org.wikidata.wdtk.datamodel.json.jackson.documents.ids;

import org.wikidata.wdtk.datamodel.interfaces.EntityIdValue;
import org.wikidata.wdtk.datamodel.interfaces.ItemIdValue;

/**
 * Note that you have to differentiate between <b>ItemIdImpl</b> (this) and
 * <b>ItemIdValueImpl</b>. This class identifies an items id on the level of an
 * item document. The other one identifies an items id on the level of snak
 * values. They are different due to the resulting JSON being syntactically
 * different for these cases. Since the document ID's are only Strings in the
 * JSON, instances of this calss will never appear in the exported JSON.
 * 
 * @author Fredo Erxleben
 *
 */
public class ItemIdImpl 
extends EntityIdImpl 
implements ItemIdValue {

	public ItemIdImpl() {}
	public ItemIdImpl(String id) {
		this.id = id;
	}

	@Override
	public String getEntityType() {
		return EntityIdValue.ET_ITEM;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}

		if (!(o instanceof ItemIdImpl)) {
			return false;
		}

		return ((ItemIdImpl) o).getId().equalsIgnoreCase(this.id);
	}

}
