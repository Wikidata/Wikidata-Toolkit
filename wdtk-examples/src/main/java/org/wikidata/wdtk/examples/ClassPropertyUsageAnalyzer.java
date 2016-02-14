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
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.wikidata.wdtk.datamodel.interfaces.DatatypeIdValue;
import org.wikidata.wdtk.datamodel.interfaces.EntityDocument;
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
import org.wikidata.wdtk.datamodel.interfaces.Value;
import org.wikidata.wdtk.datamodel.interfaces.ValueSnak;
import org.wikidata.wdtk.wikibaseapi.WikibaseDataFetcher;
import org.wikidata.wdtk.wikibaseapi.apierrors.MediaWikiApiErrorException;

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
 * @author Markus Damm
 *
 */
public class ClassPropertyUsageAnalyzer implements EntityDocumentProcessor {

	/**
	 * Class to record the use of some class item or property.
	 *
	 * @author Markus Kroetzsch
	 * @author Markus Damm
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
		/**
		 * The label of this item. If a label is ambiguous, the EntityId
		 * of this item is added in brackets. If there isn't any English
		 * label available, the label is set to null.
		 */
		public String label;
		/**
		 * The description of this item. If there isn't any English
		 * description available, the description is set to a hyphen.
		 */
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
		 * Number of uses of this property in other properties.
		 */
		public int propertyCount = 0;
		/**
		 * Datatype of this property
		 */
		public String datatype = "Unknown";
	}

	/**
	 * Class to record the usage of a class item in the data.
	 *
	 * @author Markus Kroetzsch
	 * @author Markus Damm
	 *
	 */
	private class ClassRecord extends UsageRecord {
		/**
		 * Number of subclasses of this class item.
		 */
		public int subclassCount = 0;
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
			Comparator<Entry<? extends EntityIdValue, ? extends UsageRecord>> {
		@Override
		public int compare(
				Entry<? extends EntityIdValue, ? extends UsageRecord> o1,
				Entry<? extends EntityIdValue, ? extends UsageRecord> o2) {
			return (o2.getValue().itemCount - (o1.getValue().itemCount));
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
	 * Set used for storing of classes of which a subclass was calculated
	 * but not the superclass. After processing the dump file classes of
	 * this set represented by it EntityIdValue will be downloaded later.
	 */
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
		ExampleHelpers.processEntitiesFromWikidataDump(processor);
		processor.completeMissedClasses();
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
						if (!classRecords
								.containsKey((EntityIdValue) value)) {
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
						} else {
							otherClassRecord.subclassCount++;
						}
					}
				}
			}
			countCooccurringProperties(itemDocument,
					propertyRecord, sg.getProperty());

		}

		if (classRecord != null) {
			this.countClasses++;
			setLabelToClassRecord(itemDocument, classRecord);
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

		for (StatementGroup sg : propertyDocument.getStatementGroups()) {
			PropertyRecord otherPropertyRecord = getPropertyRecord(sg
					.getProperty());
			otherPropertyRecord.propertyCount++;
			for (Statement s : sg.getStatements()) {
				// Count uses of properties in qualifiers
				for (SnakGroup q : s.getClaim().getQualifiers()) {
					countPropertyQualifier(q.getProperty(),
							q.getSnaks().size());
				}
				// Count statements with qualifiers
				if (s.getClaim().getQualifiers().size() > 0) {
					otherPropertyRecord.statementWithQualifierCount++;
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
			}
		}

		MonolingualTextValue labelValue = propertyDocument.getLabels()
				.get("en");
		if (labelValue != null) {
			propertyRecord.label = labelValue.getText();
		}
		propertyRecord.datatype = getDatatypeLabel(propertyDocument
				.getDatatype());

	}

	/**
	 * Creates the final file output of the analysis.
	 */
	private void writeFinalReports() {
		System.out.println(" * Fetching data from Wikidata API finished");
		System.out.println(" * Printing data to CSV output file");
		writePropertyData();
		writeClassData();
		System.out.println(" * Finished printing data");
	}

	/**
	 * Completes the data in the classRecords of classes that where not
	 * processed. The elements are requested by the Wikidata API.
	 */
	private void completeMissedClasses() {
		System.out.println(" * Start to fetch data from Wikidata API to collect missed");
		System.out.println(" * labels, descriptions and images.");
		System.out.println(" * Number of missed classes: "
				+ this.unCalculatedSuperClasses.size());
		while (!this.unCalculatedSuperClasses.isEmpty()) {
			Set<EntityIdValue> entityIdValues = getSubSetOfUncalculatedSuperClasses();
			Map<String, EntityDocument> result = getItemDocuments(entityIdValues);
			for (EntityIdValue entityIdValue : entityIdValues) {
				EntityDocument entityDocument = result
						.get(entityIdValue.getId());
				if (entityDocument != null
						&& entityDocument instanceof ItemDocument) {
					ItemDocument itemDocument = (ItemDocument) entityDocument;

					ClassRecord classRecord = getClassRecord(entityIdValue);

					setLabelToClassRecord(itemDocument,
							classRecord);
				}
			}
		}
	}

	/**
	 * Collects 50 missed classes and deletes from the
	 * unCulculatedSuperClasses. If there are less than 50 missed classes
	 * remaining, all classes are returned. This step is necessary since the
	 * API processes at most 50 entities per request.
	 * 
	 * @return Set of 50 missed classes
	 */
	private Set<EntityIdValue> getSubSetOfUncalculatedSuperClasses() {
		Set<EntityIdValue> entityIdValues = new HashSet<>();
		if (this.unCalculatedSuperClasses.size() <= 50) {
			entityIdValues.addAll(this.unCalculatedSuperClasses);
			this.unCalculatedSuperClasses.clear();
		} else {
			int i = 0;
			for (EntityIdValue eiv : this.unCalculatedSuperClasses) {
				entityIdValues.add(eiv);
				i++;
				if (i >= 50) {
					this.unCalculatedSuperClasses
							.removeAll(entityIdValues);
					return entityIdValues;
				}
			}
		}
		return entityIdValues;
	}

	/**
	 * Takes a set of EntityIdValues and returns a list of id strings
	 * 
	 * @param entityIdValues
	 *                set of EntityIdValues
	 * @return list of id strings
	 */
	private List<String> entityIdValueSetToStringList(
			Set<EntityIdValue> entityIdValues) {
		List<String> strings = new ArrayList<>();
		for (EntityIdValue eid : entityIdValues) {
			strings.add(eid.getId());
		}
		return strings;
	}

	/**
	 * Fetches an ItemDocument for a class via the Wikidata API.
	 * 
	 * @param entityIdValue
	 *                EntityIdValue that represents the class
	 * @return ItemDocument of this class
	 */
	private Map<String, EntityDocument> getItemDocuments(
			Set<EntityIdValue> entityIdValues) {
		WikibaseDataFetcher wdf = WikibaseDataFetcher
				.getWikidataDataFetcher();
		try {
			return wdf.getEntityDocuments(entityIdValueSetToStringList(entityIdValues));
		} catch (MediaWikiApiErrorException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Writes the data collected about properties to a file.
	 */
	private void writePropertyData() {
		try (PrintStream out = new PrintStream(
				ExampleHelpers.openExampleFileOuputStream("properties.json"),
				true, "UTF-8")) {

			out.print("{");
			List<Entry<PropertyIdValue, PropertyRecord>> list = new ArrayList<Entry<PropertyIdValue, PropertyRecord>>(
					this.propertyRecords.entrySet());
			Collections.sort(list, new UsageRecordComparator());
			boolean first = true;
			for (Entry<PropertyIdValue, PropertyRecord> entry : list) {
				if (!first) {
					out.print(",");
				} else {
					first = false;
				}
				printPropertyRecord(out, entry.getValue(),
						entry.getKey());
			}

			out.print("}");

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Writes the data collected about classes to a file.
	 */
	private void writeClassData() {
		try {
			PrintStream out = new PrintStream(
					ExampleHelpers.openExampleFileOuputStream("classes.json"),
					true, "UTF-8");
			List<Entry<EntityIdValue, ClassRecord>> list = new ArrayList<>(
					this.classRecords.entrySet());
			Collections.sort(list, new UsageRecordComparator());
			out.print("{");
			boolean first = true;
			for (Entry<EntityIdValue, ClassRecord> entry : list) {
				if (entry.getValue().itemCount > 0
						|| entry.getValue().subclassCount > 0) {
					if (!first) {
						out.print(",");
					} else {
						first = false;
					}
					printClassRecord(out, entry.getValue(),
							entry.getKey());
				}

			}
			out.print("}");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}



	/**
	 * Prints the data for a single class to the given stream. This will be
	 * a single element in json.
	 *
	 * @param out
	 *                the output to write to
	 * @param classRecord
	 *                the class record to write
	 * @param entityIdValue
	 *                the item id that this class record belongs to
	 */
	private void printClassRecord(PrintStream out, ClassRecord classRecord,
			EntityIdValue entityIdValue) {
		StringBuilder builder = new StringBuilder();
		builder.append(jsonStringEscape(entityIdValue.getId())).append(
				":{");
		builder.append(getJsonStringForLabel(classRecord));
		builder.append(jsonStringEscape("i")).append(":")
				.append(classRecord.itemCount).append(",");
		builder.append(jsonStringEscape("s")).append(":")
				.append(classRecord.subclassCount).append(",");
		builder.append(buildStringForRelatedProperties(classRecord));
		builder.append("}");
		out.print(builder.toString());
	}

	/**
	 * Builds a string for json of the related properties to the output.
	 *
	 * @param usageRecord
	 *                the data to write
	 * @return String for json
	 */
	private String buildStringForRelatedProperties(UsageRecord usageRecord) {
		StringBuilder builder = new StringBuilder();
		builder.append(jsonStringEscape("r")).append(
				":{");

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

		int count = 0;
		for (ImmutablePair<PropertyIdValue, Double> relatedProperty : list) {
			if (relatedProperty.right < 1.5) {
				break;
			}
			if (count > 0) {
				builder.append(",");
			}
			builder.append(jsonStringEscape(relatedProperty.left
					.getId()));
			builder.append(":");
			builder.append(Math.round(relatedProperty.right * 100) / 100);
			count++;
		}
		builder.append("}");
		return builder.toString();
	}

	/**
	 * Returns Json-String for the label, if the label exists.
	 * 
	 * @param usageRecord
	 *                the record to get the label from
	 * @return Json-String for label
	 */
	private String getJsonStringForLabel(UsageRecord usageRecord) {

		StringBuilder builder = new StringBuilder();
		if (usageRecord.label != null) {
			builder.append(jsonStringEscape("l"))
					.append(":")
					.append(jsonStringEscape(usageRecord.label))
					.append(",");
		}
		return builder.toString();
	}

	/**
	 * Prints the data of one property to the given output. This will be a
	 * single object in JSON.
	 *
	 * @param out
	 *                the output to write to
	 * @param propertyRecord
	 *                the data to write
	 * @param propertyIdValue
	 *                the property that the data refers to
	 */
	private void printPropertyRecord(PrintStream out,
			PropertyRecord propertyRecord,
			PropertyIdValue propertyIdValue) {

		StringBuilder builder = new StringBuilder();
		builder.append(jsonStringEscape(propertyIdValue.getId()))
				.append(":{");
		builder.append(getJsonStringForLabel(propertyRecord));
		builder.append(jsonStringEscape("s"))
				.append(":")
				.append(propertyRecord.statementCount)
				.append(",");
		builder.append(jsonStringEscape("i"))
				.append(":").append(propertyRecord.itemCount)
				.append(",");
		builder.append(jsonStringEscape("w"))
				.append(":")
				.append(propertyRecord.statementWithQualifierCount)
				.append(",");
		builder.append(jsonStringEscape("q"))
				.append(":")
				.append(propertyRecord.qualifierCount)
				.append(",");
		builder.append(jsonStringEscape("e"))
				.append(":")
				.append(propertyRecord.referenceCount)
				.append(",");
		builder.append(jsonStringEscape("p"))
				.append(":")
				.append(propertyRecord.propertyCount)
				.append(",");
		if (propertyRecord.datatype != null) {
			builder.append(jsonStringEscape("d")).append(":")
					.append(jsonStringEscape(propertyRecord.datatype))
					.append(",");
		} else {
			builder.append(jsonStringEscape("d")).append(":")
					.append(jsonStringEscape("unknown"))
					.append(",");
		}

		builder.append(buildStringForRelatedProperties(propertyRecord));
		builder.append("}");
		out.print(builder.toString());
	}

	/**
	 * Sets the label for the given class record based on the terms in the
	 * given document. If there is an ambiguous label, the Q-ID of this
	 * class is added in braces.
	 *
	 * @param entityIdValue
	 *                the entity to label
	 * @param termedDocument
	 *                the document to get labels from
	 * @return the label
	 */
	private void setLabelToClassRecord(ItemDocument itemDocument,
			ClassRecord classRecord) {
		MonolingualTextValue labelValue = itemDocument.getLabels().get(
				"en");
		if (labelValue != null) {
			String label = labelValue.getText();
			classRecord.label = label;
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

	/**
	 * Counts properties that occurs together
	 * 
	 * @param itemDocument
	 *                document which is scanned for a cooccurring property
	 * @param usageRecord
	 *                usage record for storing cooccuring
	 * @param thisPropertyIdValue
	 *                the property to count
	 */
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
	 * Escapes a string for use in Json. In particular, the string is quoted
	 * and quotation marks and backslashes are escaped.
	 *
	 * @param string
	 *                the string to escape
	 * @return the escaped string
	 */
	private String jsonStringEscape(String string) {
		string = string.replace("\t", " ").replace("\n", " ")
				.replace("\b", " ").replace("\f", " ")
				.replace("\r", " ");
		string = string.replace("\\", "\\\\\\\\");
		return "\"" + string.replace("\"", "\\\\\\\"") + "\"";
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
		case DatatypeIdValue.DT_MONOLINGUAL_TEXT:
			return "Monolingual text";
		case DatatypeIdValue.DT_PROPERTY:
			return "Property";
		case DatatypeIdValue.DT_MATH:
			return "Math";
		case DatatypeIdValue.DT_EXTERNAL_ID:
			return "External ID";

		default:
			throw new RuntimeException("Unknown datatype "
					+ datatype.getIri());
		}
	}

}
