package org.wikidata.wdtk.datamodel.helpers;

/*
 * #%L
 * Wikidata Toolkit Data Model
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

import org.wikidata.wdtk.datamodel.interfaces.*;

import java.text.DecimalFormat;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * Static class for computing a toString of arbitrary data objects using only
 * their Stringerfaces. This can be used to implement the toString() method of
 * arbitrary Stringerface implementations. More efficient solutions might exist
 * if the object that implements an Stringerface is of a specific known type,
 * but the methods here could always be used as a fallback or default.
 * <p>
 * The methods here are only meant for human consumption. Generic methods for
 * serializing certain data values in well-defined string formats are found in
 * {@link DataFormatter} instead.
 *
 * @author Markus Kroetzsch
 *
 */
public class ToString {

	/**
	 * Returns a human-readable string representation of the given object.
	 *
	 * @see java.lang.Object#toString()
	 * @param o
	 *            the object to represent as string
	 * @return a string representation of the object
	 */
	public static String toString(ItemIdValue o) {
		return o.getIri() + " (item)";
	}

	/**
	 * Returns a human-readable string representation of the given object.
	 *
	 * @see java.lang.Object#toString()
	 * @param o
	 *            the object to represent as string
	 * @return a string representation of the object
	 */
	public static String toString(PropertyIdValue o) {
		return o.getIri() + " (property)";
	}

	/**
	 * Returns a human-readable string representation of the given object.
	 *
	 * @see java.lang.Object#toString()
	 * @param o
	 *            the object to represent as string
	 * @return a string representation of the object
	 */
	public static String toString(LexemeIdValue o) {
		return o.getIri() + " (lexeme)";
	}

	/**
	 * Returns a human-readable string representation of the given object.
	 *
	 * @see java.lang.Object#toString()
	 * @param o
	 *            the object to represent as string
	 * @return a string representation of the object
	 */
	public static String toString(FormIdValue o) {
		return o.getIri() + " (form)";
	}

	/**
	 * Returns a human-readable string representation of the given object.
	 *
	 * @see java.lang.Object#toString()
	 * @param o
	 *            the object to represent as string
	 * @return a string representation of the object
	 */
	public static String toString(SenseIdValue o) {
		return o.getIri() + " (sense)";
	}
	/**
	 * Returns a human-readable string representation of the given object.
	 *
	 * @see java.lang.Object#toString()
	 * @param o
	 *            the object to represent as string
	 * @return a string representation of the object
	 */
	public static String toString(MediaInfoIdValue o) {
		return o.getIri() + " (media-info)";
	}

	/**
	 * Returns a human-readable string representation of the given object.
	 *
	 * @see java.lang.Object#toString()
	 * @param o
	 *            the object to represent as string
	 * @return a string representation of the object
	 */
	public static String toString(DatatypeIdValue o) {
		return o.getIri();
	}

	/**
	 * Returns a human-readable string representation of the given object.
	 *
	 * @see java.lang.Object#toString()
	 * @param o
	 *            the object to represent as string
	 * @return a string representation of the object
	 */
	public static String toString(TimeValue o) {
		DecimalFormat timeForm = new DecimalFormat("00");

		return o.getYear()
				+ "-"
				+ timeForm.format(o.getMonth())
				+ "-"
				+ timeForm.format(o.getDay())
				+ (o.getPrecision() > TimeValue.PREC_DAY
						|| o.getHour() + o.getMinute() + o.getSecond() > 0 ? ("T"
						+ timeForm.format(o.getHour())
						+ ":"
						+ timeForm.format(o.getMinute()) + ":" + timeForm
						.format(o.getSecond())) : "")
				+ " (Prec.: "
				+ getTimePrecisionString(o.getPrecision())
				+ " [-"
				+ o.getBeforeTolerance()
				+ " .. +"
				+ o.getAfterTolerance()
				+ "]"
				+ ", PCal: "
				+ getCalendarString(o.getPreferredCalendarModel())
				+ (o.getTimezoneOffset() != 0 ? (", Timezone: "
						+ o.getTimezoneOffset() + "min ") : "") + ")";
	}

	/**
	 * Returns a human-readable string representation of the given object.
	 *
	 * @see java.lang.Object#toString()
	 * @param o
	 *            the object to represent as string
	 * @return a string representation of the object
	 */
	public static String toString(GlobeCoordinatesValue o) {
		return (o.getLatitude() / GlobeCoordinatesValue.PREC_DEGREE) + ":"
				+ (o.getLongitude() / GlobeCoordinatesValue.PREC_DEGREE) + " ("
				+ getGlobeString(o.getGlobe()) + ")";
	}

	/**
	 * Returns a human-readable string representation of the given object.
	 *
	 * @see java.lang.Object#toString()
	 * @param o
	 *            the object to represent as string
	 * @return a string representation of the object
	 */
	public static String toString(StringValue o) {
		return "\"" + o.getString().replace("\"", "\\\"") + "\"";
	}

	/**
	 * Returns a human-readable string representation of the given object.
	 *
	 * @see java.lang.Object#toString()
	 * @param o
	 *            the object to represent as string
	 * @return a string representation of the object
	 */
	public static String toString(MonolingualTextValue o) {
		return "\"" + o.getText().replace("\"", "\\\"") + "\" ("
				+ o.getLanguageCode() + ")";
	}

	/**
	 * Returns a human-readable string representation of the given object.
	 *
	 * @see java.lang.Object#toString()
	 * @param o
	 *            the object to represent as string
	 * @return a string representation of the object
	 */
	public static String toString(QuantityValue o) {
		String str = o.getNumericValue().toString();
		if(o.getLowerBound() != null && o.getUpperBound() != null) {
			str += " [" + o.getLowerBound().toString() + " .. " + o.getUpperBound().toString() + "]";
		}
		if(!"1".equals(o.getUnit())) {
			str += " " + o.getUnit();
		}
		return str;
	}
	
	/**
	 * Returns a human-readable string representation of the given object.
	 *
	 * @see java.lang.Object#toString()
	 * @param o
	 *            the object to represent as string
	 * @return a string representation of the object
	 */
	public static String toString(UnsupportedValue o) {
		return "unsupported value of type "+o.getTypeJsonString();
	}
	
	/**
	 * Returns a human-readable string representation of the given object.
	 *
	 * @see java.lang.Object#toString()
	 * @param o
	 *            the object to represent as string
	 * @return a string representation of the object
	 */
	public static String toString(UnsupportedEntityIdValue o) {
		return o.getIri() + " (unsupported)";
	}

	/**
	 * Returns a human-readable string representation of the given object.
	 *
	 * @see java.lang.Object#toString()
	 * @param o
	 *            the object to represent as string
	 * @return a string representation of the object
	 */
	public static String toString(ValueSnak o) {
		return o.getPropertyId().getIri() + " :: " + o.getValue().toString();
	}

	/**
	 * Returns a human-readable string representation of the given object.
	 *
	 * @see java.lang.Object#toString()
	 * @param o
	 *            the object to represent as string
	 * @return a string representation of the object
	 */
	public static String toString(SomeValueSnak o) {
		return o.getPropertyId().getIri() + " has some value";
	}

	/**
	 * Returns a human-readable string representation of the given object.
	 *
	 * @see java.lang.Object#toString()
	 * @param o
	 *            the object to represent as string
	 * @return a string representation of the object
	 */
	public static String toString(NoValueSnak o) {
		return o.getPropertyId().getIri() + " has no value";
	}

	/**
	 * Returns a human-readable string representation of the given object.
	 *
	 * @see java.lang.Object#toString()
	 * @param o
	 *            the object to represent as string
	 * @return a string representation of the object
	 */
	public static String toString(SnakGroup o) {
		final String indent = "      ";

		StringBuilder result = new StringBuilder();
		for (Snak s : o.getSnaks()) {
			result.append(indent)
					.append(s.toString())
					.append("\n");
		}
		return result.toString();
	}

	/**
	 * Returns a human-readable string representation of the given object.
	 *
	 * @see java.lang.Object#toString()
	 * @param o
	 *            the object to represent as string
	 * @return a string representation of the object
	 */
	public static String toString(Claim o) {
		StringBuilder sb = new StringBuilder();

		sb.append(o.getSubject()).append(": ");
		sb.append(o.getMainSnak().toString()).append("\n");
		for (SnakGroup s : o.getQualifiers()) {
			sb.append(toString(s));
		}

		return sb.toString();
	}

	/**
	 * Returns a human-readable string representation of the given object.
	 *
	 * @see java.lang.Object#toString()
	 * @param o
	 *            the object to represent as string
	 * @return a string representation of the object
	 */
	public static String toString(Reference o) {
		StringBuilder sb = new StringBuilder();

		sb.append("  Reference:\n");
		for (SnakGroup s : o.getSnakGroups()) {
			sb.append(toString(s));
		}

		return sb.toString();
	}

	/**
	 * Returns a human-readable string representation of the given object.
	 *
	 * @see java.lang.Object#toString()
	 * @param o
	 *            the object to represent as string
	 * @return a string representation of the object
	 */
	public static String toString(Statement o) {
		StringBuilder sb = new StringBuilder();

		sb.append("[ID ").append(o.getStatementId()).append("] ");
		sb.append(o.getSubject()).append(": ");
		sb.append(o.getMainSnak().toString()).append("\n");
		for (SnakGroup s : o.getQualifiers()) {
			sb.append(toString(s));
		}
		if (o.getRank() != StatementRank.NORMAL) {
			sb.append("  Rank: ").append(o.getRank()).append("\n");
		}
		for (Reference r : o.getReferences()) {
			sb.append(toString(r));
		}

		return sb.toString();
	}

	/**
	 * Returns a human-readable string representation of the given object.
	 *
	 * @see java.lang.Object#toString()
	 * @param o
	 *            the object to represent as string
	 * @return a string representation of the object
	 */
	public static String toString(StatementGroup o) {
		StringBuilder sb = new StringBuilder();

		for (Statement s : o) {
			sb.append(toString(s));
		}

		return sb.toString();
	}

	/**
	 * Returns a human-readable string representation of the given object.
	 *
	 * @see java.lang.Object#toString()
	 * @param o
	 *            the object to represent as string
	 * @return a string representation of the object
	 */
	public static String toString(SiteLink o) {
		if (o.getBadges().isEmpty()) {
			return o.getSiteKey() + "/" + o.getPageTitle();
		} else {
			return o.getSiteKey() + "/" + o.getPageTitle() + " "
					+ o.getBadges();
		}
	}

	/**
	 * Returns a human-readable string representation of the given object.
	 *
	 * @see java.lang.Object#toString()
	 * @param o
	 *            the object to represent as string
	 * @return a string representation of the object
	 */
	public static String toString(PropertyDocument o) {
		return "==PropertyDocument " + o.getEntityId().getIri() + " (r"
				+ o.getRevisionId() + ") ==\n" + "* Datatype: "
				+ o.getDatatype() + toStringForTermedDocument(o)
				+ toStringForStatementDocument(o);
	}

	/**
	 * Returns a human-readable string representation of the given object.
	 *
	 * @see java.lang.Object#toString()
	 * @param o
	 *            the object to represent as string
	 * @return a string representation of the object
	 */
	public static String toString(ItemDocument o) {
		StringBuilder sb = new StringBuilder();
		sb.append("==ItemDocument ").append(o.getEntityId().getIri());
		sb.append(" (r").append(o.getRevisionId()).append(") ");
		sb.append("==").append(toStringForTermedDocument(o));
		sb.append(toStringForStatementDocument(o));

		sb.append("* Site links: ");
		boolean first = true;
		SortedSet<String> siteKeys = new TreeSet<>(o.getSiteLinks()
				.keySet());
		for (String key : siteKeys) {
			if (first) {
				first = false;
			} else {
				sb.append("; ");
			}
			sb.append(toString(o.getSiteLinks().get(key)));
		}

		return sb.toString();
	}

	/**
	 * Returns a human-readable string representation of the given object.
	 *
	 * @see java.lang.Object#toString()
	 * @param o
	 *            the object to represent as string
	 * @return a string representation of the object
	 */
	public static String toString(LexemeDocument o) {
		StringBuilder sb = new StringBuilder();
		sb.append("==LexemeDocument ").append(o.getEntityId().getIri());
		sb.append(" (r").append(o.getRevisionId()).append(") ");
		sb.append("==");
		sb.append("\n* Lexical category: ").append(o.getLexicalCategory().getIri());
		sb.append("\n* Language: ").append(o.getLanguage().getIri());
		boolean first;
		sb.append("\n* Lemmas: ");
		first = true;
		SortedSet<String> labelKeys = new TreeSet<>(o.getLemmas().keySet());
		for (String key : labelKeys) {
			if (first) {
				first = false;
			} else {
				sb.append("; ");
			}
			sb.append(toString(o.getLemmas().get(key)));
		}
		sb.append(toStringForStatementDocument(o));
		sb.append("\n* Forms: \n");
		for(FormDocument form : o.getForms()) {
			sb.append(form.toString()).append('\n');
		}
		sb.append("\n* Senses: \n");
		for(SenseDocument sense : o.getSenses()) {
			sb.append(sense.toString()).append('\n');
		}
		return sb.toString();
	}

	/**
	 * Returns a human-readable string representation of the given object.
	 *
	 * @see java.lang.Object#toString()
	 * @param o
	 *            the object to represent as string
	 * @return a string representation of the object
	 */
	public static String toString(FormDocument o) {
		StringBuilder sb = new StringBuilder();
		sb.append("==FormDocument ").append(o.getEntityId().getIri());
		sb.append(" (r").append(o.getRevisionId()).append(") ");
		sb.append("==");
		boolean first;
		sb.append("\n* Lemmas: ");
		first = true;
		SortedSet<String> labelKeys = new TreeSet<>(o.getRepresentations().keySet());
		for (String key : labelKeys) {
			if (first) {
				first = false;
			} else {
				sb.append("; ");
			}
			sb.append(toString(o.getRepresentations().get(key)));
		}
		sb.append("\n* Grammatical features: ");
		for(ItemIdValue feature : o.getGrammaticalFeatures()) {
			sb.append(feature.toString()).append(' ');
		}
		sb.append(toStringForStatementDocument(o));
		return sb.toString();
	}

	/**
	 * Returns a human-readable string representation of the given object.
	 *
	 * @see java.lang.Object#toString()
	 * @param o
	 *            the object to represent as string
	 * @return a string representation of the object
	 */
	public static String toString(SenseDocument o) {
		StringBuilder sb = new StringBuilder();
		sb.append("==SenseDocument ").append(o.getEntityId().getIri());
		sb.append(" (r").append(o.getRevisionId()).append(") ");
		sb.append("==");
		boolean first;
		sb.append("\n* Lemmas: ");
		first = true;
		SortedSet<String> labelKeys = new TreeSet<>(o.getGlosses().keySet());
		for (String key : labelKeys) {
			if (first) {
				first = false;
			} else {
				sb.append("; ");
			}
			sb.append(toString(o.getGlosses().get(key)));
		}
		sb.append(toStringForStatementDocument(o));
		return sb.toString();
	}

	/**
	 * Returns a human-readable string representation of the given object.
	 *
	 * @see java.lang.Object#toString()
	 * @param o
	 *            the object to represent as string
	 * @return a string representation of the object
	 */
	public static String toString(MediaInfoDocument o) {
		StringBuilder sb = new StringBuilder();
		sb.append("==MediaInfoDocument ").append(o.getEntityId().getIri());
		sb.append(" (r").append(o.getRevisionId()).append(") ");
		sb.append("==");
		sb.append(toStringForLabeledDocument(o));
		sb.append(toStringForStatementDocument(o));
		return sb.toString();
	}

	protected static String toStringForStatementDocument(StatementDocument o) {
		StringBuilder sb = new StringBuilder();

		sb.append("\n===Statements===\n");
		for (StatementGroup sg : o.getStatementGroups()) {
			sb.append(toString(sg));
		}
		sb.append("\n===End of statements===\n");

		return sb.toString();
	}

	/**
	 * Returns a human-readable string representation of the given
	 * {@link TermedDocument}.
	 *
	 * @see java.lang.Object#toString()
	 * @param o
	 *            the object to represent as string
	 * @return a string representation of the object
	 */
	protected static String toStringForTermedDocument(TermedDocument o) {
		StringBuilder sb = new StringBuilder();
		boolean first;

		sb.append(toStringForLabeledDocument(o));

		sb.append("\n* Descriptions: ");
		first = true;
		SortedSet<String> descriptionKeys = new TreeSet<>(o
				.getDescriptions().keySet());
		for (String key : descriptionKeys) {
			if (first) {
				first = false;
			} else {
				sb.append("; ");
			}
			sb.append(toString(o.getDescriptions().get(key)));
		}

		sb.append("\n* Aliases: ");
		first = true;
		SortedSet<String> aliasKeys = new TreeSet<>(o.getAliases()
				.keySet());
		for (String key : aliasKeys) {
			for (MonolingualTextValue mtv : o.getAliases().get(key)) {
				if (first) {
					first = false;
				} else {
					sb.append("; ");
				}
				sb.append(toString(mtv));
			}
		}

		return sb.toString();
	}

	/**
	 * Returns a human-readable string representation of the given
	 * {@link LabeledDocument}.
	 *
	 * @see java.lang.Object#toString()
	 * @param o
	 *            the object to represent as string
	 * @return a string representation of the object
	 */
	private static String toStringForLabeledDocument(LabeledDocument o) {
		StringBuilder sb = new StringBuilder();
		boolean first;
		sb.append("\n* Labels: ");
		first = true;
		SortedSet<String> labelKeys = new TreeSet<>(o.getLabels()
				.keySet());
		for (String key : labelKeys) {
			if (first) {
				first = false;
			} else {
				sb.append("; ");
			}
			sb.append(toString(o.getLabels().get(key)));
		}
		return sb.toString();
	}

	/**
	 * Returns a human-readable string representation of a reference to a globe
	 * on which coordinates may be present. Known globes (such as Earth) are
	 * replaced by their common English names.
	 *
	 * @param globeIri
	 *            the globe item's IRI
	 * @return a string representation of the globe
	 */
	protected static String getGlobeString(String globeIri) {
		switch (globeIri) {
		case GlobeCoordinatesValue.GLOBE_EARTH:
			return "Earth";
		default:
			return globeIri;
		}
	}

	/**
	 * Returns a human-readable string representation of a reference to a
	 * calendar model that is used for a time value. Known calendars (such as
	 * proleptic Gregorian calendar) are replaced by short English names. The
	 * term "proleptic" is omitted.
	 *
	 * @param calendarModel
	 *            the calendar model item's IRI
	 * @return a string representation of the calendar model
	 */
	protected static String getCalendarString(String calendarModel) {
		switch (calendarModel) {
		case TimeValue.CM_GREGORIAN_PRO:
			return "Gregorian";
		case TimeValue.CM_JULIAN_PRO:
			return "Julian";
		default:
			return calendarModel;
		}
	}

	/**
	 * Returns a human-readable string representation of a reference to a
	 * precision that is used for a time value.
	 *
	 * @param precision
	 *            the numeric precision
	 * @return a string representation of the precision
	 */
	protected static String getTimePrecisionString(byte precision) {
		switch (precision) {
		case TimeValue.PREC_SECOND:
			return "sec";
		case TimeValue.PREC_MINUTE:
			return "min";
		case TimeValue.PREC_HOUR:
			return "hour";
		case TimeValue.PREC_DAY:
			return "day";
		case TimeValue.PREC_MONTH:
			return "month";
		case TimeValue.PREC_YEAR:
			return "year";
		case TimeValue.PREC_DECADE:
			return "decade";
		case TimeValue.PREC_100Y:
			return "100 years";
		case TimeValue.PREC_1KY:
			return "1000 years";
		case TimeValue.PREC_10KY:
			return "10K years";
		case TimeValue.PREC_100KY:
			return "100K years";
		case TimeValue.PREC_1MY:
			return "1 million years";
		case TimeValue.PREC_10MY:
			return "10 million years";
		case TimeValue.PREC_100MY:
			return "100 million years";
		case TimeValue.PREC_1GY:
			return "1000 million years";
		default:
			return "Unsupported precision " + precision;
		}
	}

	/**
	 * Returns a human-readable string representation of the given object.
	 *
	 * @see java.lang.Object#toString()
	 * @param o
	 *            the object to represent as string
	 * @return a string representation of the object
	 */
	public static String toString(EntityRedirectDocument o) {
		return "==EntityRedirect " + o.getEntityId().getIri() +
				" (r" + o.getRevisionId() + ") " +
				"==\n" +
				"Target: " + o.getTargetId().getIri();
	}

}
