package org.wikidata.wdtk.datamodel.json.jackson;

import org.wikidata.wdtk.datamodel.interfaces.EntityIdValue;
import org.wikidata.wdtk.datamodel.interfaces.ItemIdValue;
import org.wikidata.wdtk.datamodel.interfaces.ValueVisitor;

/**
 * Note that you have to differentiate between <b>ItemIdImpl</b> (this) and <b>ItemIdValueImpl</b>.
 * This class identifies an items id on the level of an item document.
 * The other one identifies an items id on the level of snak values.
 * They are different due to the resulting JSON being syntactically different for these cases.
 * @author Fredo Erxleben
 *
 */
public class ItemIdImpl
extends EntityIdImpl
implements ItemIdValue {
	
	ItemIdImpl(){}
	ItemIdImpl(String id){
		this.id = id;
	}

	@Override
	public String getIri() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <T> T accept(ValueVisitor<T> valueVisitor) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getEntityType() {
		// XXX this collides with the interface description!
		return EntityIdValue.ET_ITEM;
	}
	
	@Override
	public boolean equals(Object o){
		if(this == o){
			return true;
		}
		
		if(!(o instanceof ItemIdImpl)){
			return false;
		}
		
		return ((ItemIdImpl)o).getId().equalsIgnoreCase(this.id);
	}

}
