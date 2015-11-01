package org.wikidata.wdtk.examples;

/*
 * #%L
 * Wikidata Toolkit Examples
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

import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.wikidata.wdtk.datamodel.helpers.DatamodelConverter;
import org.wikidata.wdtk.datamodel.implementation.DataObjectFactoryImpl;
import org.wikidata.wdtk.datamodel.interfaces.DataObjectFactory;
import org.wikidata.wdtk.datamodel.interfaces.DatatypeIdValue;
import org.wikidata.wdtk.datamodel.interfaces.EntityDocumentProcessor;
import org.wikidata.wdtk.datamodel.interfaces.EntityIdValue;
import org.wikidata.wdtk.datamodel.interfaces.ItemDocument;
import org.wikidata.wdtk.datamodel.interfaces.MonolingualTextValue;
import org.wikidata.wdtk.datamodel.interfaces.PropertyDocument;
import org.wikidata.wdtk.datamodel.interfaces.PropertyIdValue;
import org.wikidata.wdtk.datamodel.interfaces.Reference;
import org.wikidata.wdtk.datamodel.interfaces.SnakGroup;
import org.wikidata.wdtk.datamodel.interfaces.Statement;
import org.wikidata.wdtk.datamodel.interfaces.StatementGroup;
import org.wikidata.wdtk.datamodel.interfaces.TermedDocument;
import org.wikidata.wdtk.datamodel.interfaces.Value;
import org.wikidata.wdtk.datamodel.interfaces.ValueSnak;

/**
 * This advanced example analyses the use of properties and classes in a dump
 * file, and stores the results in two CSV files. These files can be used with
 * the Miga data viewer to create the <a
 * href="http://tools.wmflabs.org/wikidata-exports/miga/#">Wikidata Class and
 * Properties browser</a>. You can view the settings for configuring Miga in the
 * <a href="http://tools.wmflabs.org/wikidata-exports/miga/apps/classes/">Miga
 * directory for this app</a>.
 * <p>
 * However, you can also view the files in any other tool that processes CSV.
 * The only peculiarity is that some fields in CSV contain lists of items as
 * values, with items separated by "@". This is not supported by most
 * applications since it does not fit into the tabular data model of CSV.
 * <p>
 * The code is somewhat complex and not always clean. It should be considered as
 * an advanced example, not as a first introduction.
 *
 * @author Markus Kroetzsch
 *
 */
public class ClassPropertyUsageAnalyzer implements EntityDocumentProcessor {

	DataObjectFactory factory = new DataObjectFactoryImpl();
	DatamodelConverter converter = new DatamodelConverter(factory);

	/**
	 * Class to record the use of some class item or property.
	 *
	 * @author Markus Kroetzsch
	 *
	 */
	private abstract class UsageRecord {
		/**
		 * Number of items using this entity. For properties, this is
		 * the number of items with such a property. For class items,
		 * this is the number of instances of this class.
		 */
		public int itemCount = 0;
		/**
		 * Map that records how many times certain properties are used
		 * on items that use this entity (where "use" has the meaning
		 * explained for {@link UsageRecord#itemCount}).
		 */
		public HashMap<PropertyIdValue, Integer> propertyCoCounts = new HashMap<PropertyIdValue, Integer>();
		public String label;
		public String description;
		public String url;
	}

	/**
	 * Class to record the usage of a property in the data.
	 *
	 * @author Markus Kroetzsch
	 *
	 */
	private class PropertyRecord extends UsageRecord {
		/**
		 * Number of statements with this property.
		 */
		public int statementCount = 0;
		/**
		 * Number of qualified statements that use this property.
		 */
		public int statementWithQualifierCount = 0;
		/**
		 * Number of statement qualifiers that use this property.
		 */
		public int qualifierCount = 0;
		/**
		 * Number of uses of this property in references. Multiple uses
		 * in the same references will be counted.
		 */
		public int referenceCount = 0;
		/**
		 * {@link PropertyDocument} for this property.
		 */
		// public PropertyDocument propertyDocument = null;
		/**
		 * datatype of this property
		 */
		public String datatype;
	}

	/**
	 * Class to record the usage of a class item in the data.
	 *
	 * @author Markus Kroetzsch
	 *
	 */
	private class ClassRecord extends UsageRecord {
		/**
		 * Number of subclasses of this class item.
		 */
		public int subclassCount = 0;
		/**
		 * {@link ItemDocument} of this class.
		 */
		public ItemDocument itemDocument = null;
		/**
		 * List of all super classes of this class.
		 */
		public ArrayList<EntityIdValue> superClasses = new ArrayList<>();
	}

	/**
	 * Comparator to order class items by their number of instances and
	 * direct subclasses.
	 *
	 * @author Markus Kroetzsch
	 *
	 */
	private class ClassUsageRecordComparator
			implements
			Comparator<Entry<? extends EntityIdValue, ? extends ClassRecord>> {
		@Override
		public int compare(
				Entry<? extends EntityIdValue, ? extends ClassRecord> o1,
				Entry<? extends EntityIdValue, ? extends ClassRecord> o2) {
			return o2.getValue().subclassCount
					+ o2.getValue().itemCount
					- (o1.getValue().subclassCount + o1
							.getValue().itemCount);
		}
	}

	/**
	 * Comparator to order class items by their number of instances and
	 * direct subclasses.
	 *
	 * @author Markus Kroetzsch
	 *
	 */
	private class UsageRecordComparator
			implements
			Comparator<Entry<? extends EntityIdValue, ? extends PropertyRecord>> {
		@Override
		public int compare(
				Entry<? extends EntityIdValue, ? extends PropertyRecord> o1,
				Entry<? extends EntityIdValue, ? extends PropertyRecord> o2) {
			return (o2.getValue().itemCount
					+ o2.getValue().qualifierCount + o2
						.getValue().referenceCount)
					- (o1.getValue().itemCount
							+ o1.getValue().qualifierCount + o1
								.getValue().referenceCount);
		}
	}

	/**
	 * Total number of items processed.
	 */
	long countItems = 0;
	/**
	 * Total number of items that have some statement.
	 */
	long countPropertyItems = 0;
	/**
	 * Total number of properties processed.
	 */
	long countProperties = 0;
	/**
	 * Total number of items that are used as classes.
	 */
	long countClasses = 0;

	/**
	 * Collection of all property records.
	 */
	final HashMap<PropertyIdValue, PropertyRecord> propertyRecords = new HashMap<>();
	/**
	 * Collection of all item records of items used as classes.
	 */
	final HashMap<EntityIdValue, ClassRecord> classRecords = new HashMap<>();

	/**
	 * Map used during serialization to ensure that every label is used only
	 * once. The Map assigns an item to each label. If another item wants to
	 * use a label that is already assigned, it will use a label with an
	 * added Q-ID for disambiguation.
	 */
	final HashMap<String, EntityIdValue> labels = new HashMap<>();
	
	final Set<EntityIdValue> unCalculatedSuperClasses = new HashSet<>();

	/**
	 * Main method. Processes the whole dump using this processor. To change
	 * which dump file to use and whether to run in offline mode, modify the
	 * settings in {@link ExampleHelpers}.
	 *
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {
		ExampleHelpers.configureLogging();
		ClassPropertyUsageAnalyzer.printDocumentation();

		ClassPropertyUsageAnalyzer processor = new ClassPropertyUsageAnalyzer();
		// ExampleHelpers.processEntitiesFromWikidataDump(processor);
		processor.writeFinalReports();
	}

	@Override
	public void processItemDocument(ItemDocument itemDocument) {
		this.countItems++;
		if (itemDocument.getStatementGroups().size() > 0) {
			this.countPropertyItems++;
		}
		
		ClassRecord classRecord = null;
		if (this.classRecords.containsKey(itemDocument.getItemId())
				|| unCalculatedSuperClasses
						.contains(itemDocument
								.getItemId())) {
			classRecord = getClassRecord(itemDocument.getItemId());
			unCalculatedSuperClasses.remove(itemDocument
					.getItemId());
		}

		for (StatementGroup sg : itemDocument.getStatementGroups()) {
			PropertyRecord propertyRecord = getPropertyRecord(sg
					.getProperty());
			propertyRecord.itemCount++;
			propertyRecord.statementCount = propertyRecord.statementCount
					+ sg.getStatements().size();

			boolean isInstanceOf = "P31".equals(sg.getProperty()
					.getId());
			boolean isSubclassOf = "P279".equals(sg.getProperty()
					.getId());

			if (isSubclassOf && classRecord == null) {
				classRecord = getClassRecord(itemDocument
						.getItemId());
			}

			for (Statement s : sg.getStatements()) {
				// Count uses of properties in qualifiers
				for (SnakGroup q : s.getClaim().getQualifiers()) {
					countPropertyQualifier(q.getProperty(),
							q.getSnaks().size());
				}
				// Count statements with qualifiers
				if (s.getClaim().getQualifiers().size() > 0) {
					propertyRecord.statementWithQualifierCount++;
				}
				// Count uses of properties in references
				for (Reference r : s.getReferences()) {
					for (SnakGroup snakGroup : r
							.getSnakGroups()) {
						countPropertyReference(
								snakGroup.getProperty(),
								snakGroup.getSnaks()
										.size());
					}
				}
				if ((isInstanceOf || isSubclassOf)
						&& s.getClaim().getMainSnak() instanceof ValueSnak) {
					Value value = ((ValueSnak) s.getClaim()
							.getMainSnak())
							.getValue();
					if (value instanceof EntityIdValue) {
						if (!classRecords.containsKey((EntityIdValue) value)) {
							unCalculatedSuperClasses
									.add((EntityIdValue) value);
						}
						ClassRecord otherClassRecord = getClassRecord((EntityIdValue) value);
						if (isInstanceOf) {
							otherClassRecord.itemCount++;
							countCooccurringProperties(
									itemDocument,
									otherClassRecord,
									null);
						}
					}
				}
			}
			countCooccurringProperties(itemDocument,
					propertyRecord, sg.getProperty());

		}

		if (classRecord != null) {
			this.countClasses++;
			classRecord.itemDocument = converter.copy(itemDocument);
		}

		// print a report once in a while:
		if (this.countItems % 100000 == 0) {
			printReport();
		}

	}

	@Override
	public void processPropertyDocument(PropertyDocument propertyDocument) {
		this.countProperties++;
		PropertyRecord propertyRecord = getPropertyRecord(propertyDocument
				.getPropertyId());
		propertyRecord.datatype = getDatatypeLabel(propertyDocument
				.getDatatype());
		setTermsToUsageRecord(propertyDocument, propertyRecord, null);
		// TODO: putPropertyDocument
	}


	/**
	 * Creates the final file output of the analysis.
	 */
	public void writeFinalReports() {
		// TODO
		writePropertyData();
		// writeClassData();
	}

	/**
	 * Sets the terms (label, etc.) of one entity to the given usageRecord.
	 * This will lead to several values, which are the same for properties
	 * and class items and will be printed later to the CSV-file.
	 *
	 * @param termedDocument
	 *                the document that provides the terms
	 * @param usageRecord
	 *                the UsageRecord in which the data should be stored
	 * @param specialLabel
	 *                special label to use (rather than the label string in
	 *                the document) or null if not using; used by classes,
	 *                which need to support disambiguation in their labels
	 */
	private void setTermsToUsageRecord(TermedDocument termedDocument,
			UsageRecord usageRecord, String specialLabel) {
		usageRecord.label = specialLabel;
		usageRecord.description = "-";

		if (termedDocument != null) {
			if (usageRecord.label == null) {
				MonolingualTextValue labelValue = termedDocument
						.getLabels().get("en");
				if (labelValue != null) {
					usageRecord.label = csvStringEscape(labelValue
							.getText());
				}
			}
			MonolingualTextValue descriptionValue = termedDocument
					.getDescriptions().get("en");
			if (descriptionValue != null) {
				usageRecord.description = csvStringEscape(descriptionValue
						.getText());
			}
		}
	}

	/**
	 * Writes the data collected about properties to a file.
	 */
	private void writePropertyData() {
		try (PrintStream out = new PrintStream(
				ExampleHelpers.openExampleFileOuputStream("properties.csv"))) {

			out.println("Id" + ",Label" + ",Description" + ",URL"
					+ ",Datatype" + ",Uses in statements"
					+ ",Items with such statements"
					+ ",Uses in statements with qualifiers"
					+ ",Uses in qualifiers"
					+ ",Uses in references" + ",Uses total"
					+ ",Related properties");

			List<Entry<PropertyIdValue, PropertyRecord>> list = new ArrayList<Entry<PropertyIdValue, PropertyRecord>>(
					this.propertyRecords.entrySet());
			Collections.sort(list, new UsageRecordComparator());
			for (Entry<PropertyIdValue, PropertyRecord> entry : list) {
				printPropertyRecord(out, entry.getValue(),
						entry.getKey());
			}

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Prints the data of one property to the given output. This will be a
	 * single line in CSV.
	 *
	 * @param out
	 *                the output to write to
	 * @param propertyRecord
	 *                the data to write
	 * @param propertyIdValue
	 *                the property that the data refers to
	 */
	private void printPropertyRecord(PrintStream out,
			PropertyRecord propertyRecord, PropertyIdValue propertyIdValue) {

		printTerms(out, propertyRecord, propertyIdValue);

		if (propertyRecord.datatype == null) {
			propertyRecord.datatype = "Unknown";
		}

		out.print(","
				+ propertyRecord.datatype
				+ ","
				+ propertyRecord.statementCount
				+ ","
				+ propertyRecord.itemCount
				+ ","
				+ propertyRecord.statementWithQualifierCount
				+ ","
				+ propertyRecord.qualifierCount
				+ ","
				+ propertyRecord.referenceCount
				+ ","
				+ (propertyRecord.statementCount
						+ propertyRecord.qualifierCount + propertyRecord.referenceCount));

		printRelatedProperties(out, propertyRecord);

		out.println("");
	}
	
	/**
	 * Prints the terms (label, etc.) of one entity to the given stream.
	 * This will lead to several values in the CSV file, which are the same
	 * for properties and class items.
	 *
	 * @param out
	 *                the output to write to
	 * @param termedDocument
	 *                the document that provides the terms to write
	 * @param entityIdValue
	 *                the entity that the data refers to.
	 * @param specialLabel
	 *                special label to use (rather than the label string in
	 *                the document) or null if not using; used by classes,
	 *                which need to support disambiguation in their labels
	 */
	private void printTerms(PrintStream out, UsageRecord usageRecord,
			EntityIdValue entityIdValue) {

		if (usageRecord.description == null) {
			usageRecord.description = "-";
		}

		if (usageRecord.label == null) {
			usageRecord.label = entityIdValue.getId();
		}

		out.print(entityIdValue.getId() + "," + usageRecord.label + ","
				+ usageRecord.description + ","
				+ entityIdValue.getIri());
	}
	
	/**
	 * Prints a list of related properties to the output. The list is encoded as
	 * a single CSV value, using "@" as a separator. Miga can decode this.
	 * Standard CSV processors do not support lists of entries as values,
	 * however.
	 *
	 * @param out
	 *            the output to write to
	 * @param usageRecord
	 *            the data to write
	 */
	private void printRelatedProperties(PrintStream out, UsageRecord usageRecord) {
		List<ImmutablePair<PropertyIdValue, Double>> list = new ArrayList<ImmutablePair<PropertyIdValue, Double>>(
				usageRecord.propertyCoCounts.size());
		for (Entry<PropertyIdValue, Integer> coCountEntry : usageRecord.propertyCoCounts
				.entrySet()) {
			double otherThisItemRate = (double) coCountEntry
					.getValue() / usageRecord.itemCount;
			double otherGlobalItemRate = (double) this.propertyRecords
					.get(coCountEntry.getKey()).itemCount
					/ this.countPropertyItems;
			double otherThisItemRateStep = 1 / (1 + Math
					.exp(6 * (-2 * otherThisItemRate + 0.5)));
			double otherInvGlobalItemRateStep = 1 / (1 + Math
					.exp(6 * (-2
							* (1 - otherGlobalItemRate) + 0.5)));

			list.add(new ImmutablePair<PropertyIdValue, Double>(
					coCountEntry.getKey(),
					otherThisItemRateStep
							* otherInvGlobalItemRateStep
							* otherThisItemRate
							/ otherGlobalItemRate));
		}

		Collections.sort(
				list,
				new Comparator<ImmutablePair<PropertyIdValue, Double>>() {
					@Override
					public int compare(
							ImmutablePair<PropertyIdValue, Double> o1,
							ImmutablePair<PropertyIdValue, Double> o2) {
						return o2.getValue().compareTo(
								o1.getValue());
					}
				});

		out.print(",\"");
		int count = 0;
		for (ImmutablePair<PropertyIdValue, Double> relatedProperty : list) {
			if (relatedProperty.right < 1.5) {
				break;
			}
			if (count > 0) {
				out.print("@");
			}
			// makeshift escaping for Miga:
			out.print(getPropertyLabel(relatedProperty.left)
					.replace("@", "＠"));
			count++;
		}
		out.print("\"");
	}

	/**
	 * Returns a string that should be used as a label for the given
	 * property.
	 *
	 * @param propertyIdValue
	 *                the property to label
	 * @return the label
	 */
	private String getPropertyLabel(PropertyIdValue propertyIdValue) {
		PropertyRecord propertyRecord = this.propertyRecords
				.get(propertyIdValue);
		if (propertyRecord == null) {
			return propertyIdValue.getId();
		} else {
			return propertyRecord.label;
		}
	}

	/**
	 * Print some basic documentation about this program.
	 */
	public static void printDocumentation() {
		System.out.println("********************************************************************");
		System.out.println("*** Wikidata Toolkit: Class and Property Usage Analyzer");
		System.out.println("*** ");
		System.out.println("*** This program will download and process dumps from Wikidata.");
		System.out.println("*** It will create a CSV file with statistics about class and");
		System.out.println("*** property useage. These files can be used with the Miga data");
		System.out.println("*** viewer to create the browser seen at ");
		System.out.println("*** http://tools.wmflabs.org/wikidata-exports/miga/");
		System.out.println("********************************************************************");
	}

	/**
	 * Returns record where statistics about a class should be stored.
	 *
	 * @param entityIdValue
	 *                the class to initialize
	 * @return the class record
	 */
	private ClassRecord getClassRecord(EntityIdValue entityIdValue) {
		if (!this.classRecords.containsKey(entityIdValue)) {
			ClassRecord classRecord = new ClassRecord();
			this.classRecords.put(entityIdValue, classRecord);
			return classRecord;
		} else {
			return this.classRecords.get(entityIdValue);
		}
	}

	/**
	 * Returns record where statistics about a property should be stored.
	 *
	 * @param property
	 *                the property to initialize
	 * @return the property record
	 */
	private PropertyRecord getPropertyRecord(PropertyIdValue property) {
		if (!this.propertyRecords.containsKey(property)) {
			PropertyRecord propertyRecord = new PropertyRecord();
			this.propertyRecords.put(property, propertyRecord);
			return propertyRecord;
		} else {
			return this.propertyRecords.get(property);
		}
	}

	private void countCooccurringProperties(ItemDocument itemDocument,
			UsageRecord usageRecord,
			PropertyIdValue thisPropertyIdValue) {
		for (StatementGroup sg : itemDocument.getStatementGroups()) {
			if (!sg.getProperty().equals(thisPropertyIdValue)) {
				if (!usageRecord.propertyCoCounts
						.containsKey(sg.getProperty())) {
					usageRecord.propertyCoCounts.put(
							sg.getProperty(), 1);
				} else {
					usageRecord.propertyCoCounts
							.put(sg.getProperty(),
									usageRecord.propertyCoCounts
											.get(sg.getProperty()) + 1);
				}
			}
		}
	}

	/**
	 * Counts additional occurrences of a property as qualifier property of
	 * statements.
	 *
	 * @param property
	 *                the property to count
	 * @param count
	 *                the number of times to count the property
	 */
	private void countPropertyQualifier(PropertyIdValue property, int count) {
		PropertyRecord propertyRecord = getPropertyRecord(property);
		propertyRecord.qualifierCount = propertyRecord.qualifierCount
				+ count;
	}

	/**
	 * Counts additional occurrences of a property as property in
	 * references.
	 *
	 * @param property
	 *                the property to count
	 * @param count
	 *                the number of times to count the property
	 */
	private void countPropertyReference(PropertyIdValue property, int count) {
		PropertyRecord propertyRecord = getPropertyRecord(property);
		propertyRecord.referenceCount = propertyRecord.referenceCount
				+ count;
	}

	/**
	 * Prints a report about the statistics gathered so far.
	 */
	private void printReport() {
		System.out.println("Processed " + this.countItems + " items:");
		System.out.println(" * Properties encountered: "
				+ this.propertyRecords.size());
		System.out.println(" * Property documents: "
				+ this.countProperties);
		System.out.println(" * Classes encountered: "
				+ this.classRecords.size());
		System.out.println(" * Class documents: " + this.countClasses);
	}

	/**
	 * Returns an English label for a given datatype.
	 *
	 * @param datatype
	 *                the datatype to label
	 * @return the label
	 */
	private String getDatatypeLabel(DatatypeIdValue datatype) {
		if (datatype.getIri() == null) { // TODO should be redundant
						 // once the
						 // JSON parsing works
			return "Unknown";
		}

		switch (datatype.getIri()) {
		case DatatypeIdValue.DT_COMMONS_MEDIA:
			return "Commons media";
		case DatatypeIdValue.DT_GLOBE_COORDINATES:
			return "Globe coordinates";
		case DatatypeIdValue.DT_ITEM:
			return "Item";
		case DatatypeIdValue.DT_QUANTITY:
			return "Quantity";
		case DatatypeIdValue.DT_STRING:
			return "String";
		case DatatypeIdValue.DT_TIME:
			return "Time";
		case DatatypeIdValue.DT_URL:
			return "URL";
		default:
			throw new RuntimeException("Unknown datatype "
					+ datatype.getIri());
		}
	}

	/**
	 * Escapes a string for use in CSV. In particular, the string is quoted
	 * and quotation marks are escaped.
	 *
	 * @param string
	 *                the string to escape
	 * @return the escaped string
	 */
	private String csvStringEscape(String string) {
		return "\"" + string.replace("\"", "\"\"") + "\"";
	}
}
