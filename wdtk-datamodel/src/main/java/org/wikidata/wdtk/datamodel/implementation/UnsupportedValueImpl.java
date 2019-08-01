package org.wikidata.wdtk.datamodel.implementation;

import java.util.HashMap;
import java.util.Map;

import org.wikidata.wdtk.datamodel.helpers.ToString;
import org.wikidata.wdtk.datamodel.interfaces.UnsupportedValue;
import org.wikidata.wdtk.datamodel.interfaces.ValueVisitor;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonDeserializer.None;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

/**
 * Represents a value with an unsupported datatype.
 * We can still "deserialize" it by just storing its
 * JSON representation, so that it can be serialized
 * back to its original representation.
 * This avoids parsing failures on documents containing
 * these values.
 * 
 * @author Antonin Delpeuch
 *
 */
@JsonDeserialize(using = None.class)
public class UnsupportedValueImpl extends ValueImpl implements UnsupportedValue {
	
	private final String typeString;
	private final Map<String, JsonNode> contents; 

	@JsonCreator
	private UnsupportedValueImpl(
			@JsonProperty("type")
			String typeString) {
		super(typeString);
		this.typeString = typeString;
		this.contents = new HashMap<>();
	}

	@Override
	public <T> T accept(ValueVisitor<T> valueVisitor) {
		return valueVisitor.visit(this);
	}

	@Override
	@JsonProperty("type")
	public String getTypeString() {
		return typeString;
	}
	
	@JsonAnyGetter
	protected Map<String, JsonNode> getContents() {
		return contents;
	}
	
	@JsonAnySetter
	protected void loadContents(String key, JsonNode value) {
		this.contents.put(key, value);
	}
	
	@Override
	public String toString() {
		return ToString.toString(this);
	}
	
	/**
	 * We do not use the Hash helper as in other datamodel
	 * classes because this would require exposing the contents
	 * of the value publicly, which goes against the desired
	 * opacity of the representation.
	 */
	@Override
	public int hashCode() {
		return typeString.hashCode() + 31*contents.hashCode();
	}
	
	/**
	 * We do not use the Equality helper as in other datamodel
	 * classes because this would require exposing the contents
	 * of the value publicly, which goes against the desired
	 * opacity of the representation.
	 */
	@Override
	public boolean equals(Object other) {
		if (other == null || !(other instanceof UnsupportedValueImpl)) {
			return false;
		}
		UnsupportedValueImpl otherValue = (UnsupportedValueImpl) other;
		return typeString.equals(otherValue.getTypeString()) &&
				contents.equals(otherValue.getContents());
	}
}
