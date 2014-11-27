package org.wikidata.wdtk.storage.dbtowdtk;

import org.wikidata.wdtk.datamodel.helpers.Equality;
import org.wikidata.wdtk.datamodel.helpers.Hash;
import org.wikidata.wdtk.datamodel.helpers.ToString;
import org.wikidata.wdtk.datamodel.interfaces.MonolingualTextValue;
import org.wikidata.wdtk.datamodel.interfaces.ValueVisitor;
import org.wikidata.wdtk.storage.datamodel.ObjectValue;
import org.wikidata.wdtk.storage.datamodel.PropertyValuePair;
import org.wikidata.wdtk.storage.datamodel.StringValue;
import org.wikidata.wdtk.storage.wdtkbindings.WdtkSorts;

public class MonolingualTextValueFromObjectValue implements
		MonolingualTextValue {

	final ObjectValue value;

	StringValue text = null;
	StringValue languageCode = null;

	public MonolingualTextValueFromObjectValue(ObjectValue value) {
		this.value = value;
	}

	@Override
	public <T> T accept(ValueVisitor<T> valueVisitor) {
		return valueVisitor.visit(this);
	}

	private void initialize() {
		if (this.text == null) {
			for (PropertyValuePair pvp : this.value) {
				switch (pvp.getProperty()) {
				case WdtkSorts.PROP_MTV_TEXT:
					this.text = ((StringValue) pvp.getValue());
					break;
				case WdtkSorts.PROP_MTV_LANG:
					this.languageCode = ((StringValue) pvp.getValue());
					break;
				default:
					throw new RuntimeException("Unexpected property "
							+ pvp.getProperty()
							+ " in monolingual text value record.");
				}
			}
		}
	}

	@Override
	public String getText() {
		initialize();
		return this.text.getString();
	}

	@Override
	public String getLanguageCode() {
		initialize();
		return this.languageCode.getString();
	}

	@Override
	public int hashCode() {
		return Hash.hashCode(this);
	}

	@Override
	public boolean equals(Object obj) {
		return Equality.equalsMonolingualTextValue(this, obj);
	}

	@Override
	public String toString() {
		return ToString.toString(this);
	}

}
