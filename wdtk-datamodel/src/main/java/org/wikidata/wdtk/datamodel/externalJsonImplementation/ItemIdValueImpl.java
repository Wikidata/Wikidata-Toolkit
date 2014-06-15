package org.wikidata.wdtk.datamodel.externalJsonImplementation;

import org.wikidata.wdtk.datamodel.interfaces.EntityIdValue;
import org.wikidata.wdtk.datamodel.interfaces.ItemIdValue;
import org.wikidata.wdtk.datamodel.interfaces.ValueVisitor;

public class ItemIdValueImpl
extends EntityIdValueImpl
implements ItemIdValue {
	
	ItemIdValueImpl(){}
	ItemIdValueImpl(String id){
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
		return EntityIdValue.ET_ITEM;
	}
	
	@Override
	public boolean equals(Object o){
		if(this == o){
			return true;
		}
		
		if(!(o instanceof ItemIdValueImpl)){
			return false;
		}
		
		return ((ItemIdValueImpl)o).getId().equalsIgnoreCase(this.id);
	}

}
