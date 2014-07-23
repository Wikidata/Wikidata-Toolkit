package org.wikidata.wdtk.datamodel.json.jackson;

import org.wikidata.wdtk.datamodel.interfaces.Value;
import org.wikidata.wdtk.datamodel.interfaces.ValueVisitor;

public abstract class ValueImpl implements Value {
	
	private String type;

	@Override
	public <T> T accept(ValueVisitor<T> valueVisitor) {
		// TODO Auto-generated method stub
		return null;
	}

}
