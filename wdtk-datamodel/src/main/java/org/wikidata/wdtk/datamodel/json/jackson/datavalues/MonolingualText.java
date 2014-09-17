package org.wikidata.wdtk.datamodel.json.jackson.datavalues;

import com.fasterxml.jackson.annotation.JsonProperty;

public class MonolingualText {

	String language;
	String text;

	public MonolingualText() {
	}

	public MonolingualText(String language, String text) {
		this.language = language;
		this.text = text;
	}

	public void setLanguage(String language) {
		this.language = language;
	}

	public String getText() {
		return this.text;
	}

	public void setText(String text) {
		this.text = text;
	}

	@JsonProperty("language")
	public String getLanguageCode() {
		return this.language;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (!(o instanceof MonolingualText)) {
			return false;
		}
		MonolingualText other = (MonolingualText) o;
		return this.text.equals(other.text)
				&& this.language.equals(other.language);
	}
}
