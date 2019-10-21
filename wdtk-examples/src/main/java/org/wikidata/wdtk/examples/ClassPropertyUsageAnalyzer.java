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
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map.Entry;

import org.apache.commons.lang3.tuple.ImmutablePair;
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
import org.wikidata.wdtk.datamodel.interfaces.StringValue;
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

	/**
	 * Set of top-level classes (without a superclass) that should be considered
	 * during processing.
	 * <p>
	 * We use this list since our one-pass processing may fail to collect labels
	 * for some classes, if they are used as classes only after they occur in
	 * the dump. This can only occur for top-level classes (since a
	 * "subclass of" statement would already be a use as a class). This list
	 * tries to make sure that some more labels are collected for known
	 * top-level classes. It is not a problem if some of these classes are not
	 * really "top level" in the current dump.
	 */
	private static final HashSet<String> TOP_LEVEL_CLASSES = new HashSet<>();
	static {
		TOP_LEVEL_CLASSES.add("Q35120"); // Entity
		TOP_LEVEL_CLASSES.add("Q14897293"); // Fictional entity
		TOP_LEVEL_CLASSES.add("Q726"); // horse
		TOP_LEVEL_CLASSES.add("Q12567"); // Vikings
		TOP_LEVEL_CLASSES.add("Q32099");
		TOP_LEVEL_CLASSES.add("Q47883");
		TOP_LEVEL_CLASSES.add("Q188913");
		TOP_LEVEL_CLASSES.add("Q236209");
		TOP_LEVEL_CLASSES.add("Q459297");
		TOP_LEVEL_CLASSES.add("Q786014");
		TOP_LEVEL_CLASSES.add("Q861951");
		TOP_LEVEL_CLASSES.add("Q7045");
		TOP_LEVEL_CLASSES.add("Q31579");
		TOP_LEVEL_CLASSES.add("Q35054");
		TOP_LEVEL_CLASSES.add("Q39825");
		TOP_LEVEL_CLASSES.add("Q81513");
		TOP_LEVEL_CLASSES.add("Q102496");
		TOP_LEVEL_CLASSES.add("Q159661");
		TOP_LEVEL_CLASSES.add("Q1130491");
		TOP_LEVEL_CLASSES.add("Q2022036");
		TOP_LEVEL_CLASSES.add("Q2198291");
		TOP_LEVEL_CLASSES.add("Q3034652");
		TOP_LEVEL_CLASSES.add("Q3505845");
	}

	/**
	 * Class to record the use of some class item or property.
	 *
	 * @author Markus Kroetzsch
	 *
	 */
	private abstract static class UsageRecord {
		/**
		 * Number of items using this entity. For properties, this is the number
		 * of items with such a property. For class items, this is the number of
		 * instances of this class.
		 */
		public int itemCount = 0;
		/**
		 * Map that records how many times certain properties are used on items
		 * that use this entity (where "use" has the meaning explained for
		 * {@link UsageRecord#itemCount}).
		 */
		public HashMap<PropertyIdValue, Integer> propertyCoCounts = new HashMap<>();
	}

	/**
	 * Class to record the usage of a property in the data.
	 *
	 * @author Markus Kroetzsch
	 *
	 */
	private static class PropertyRecord extends UsageRecord {
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
		 * Number of uses of this property in references. Multiple uses in the
		 * same references will be counted.
		 */
		public int referenceCount = 0;
		/**
		 * {@link PropertyDocument} for this property.
		 */
		public PropertyDocument propertyDocument = null;
	}

	/**
	 * Class to record the usage of a class item in the data.
	 *
	 * @author Markus Kroetzsch
	 *
	 */
	private static class ClassRecord extends UsageRecord {
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
	 * Comparator to order class items by their number of instances and direct
	 * subclasses.
	 *
	 * @author Markus Kroetzsch
	 *
	 */
	private static class ClassUsageRecordComparator implements
			Comparator<Entry<? extends EntityIdValue, ? extends ClassRecord>> {
		@Override
		public int compare(
				Entry<? extends EntityIdValue, ? extends ClassRecord> o1,
				Entry<? extends EntityIdValue, ? extends ClassRecord> o2) {
			return o2.getValue().subclassCount + o2.getValue().itemCount
					- (o1.getValue().subclassCount + o1.getValue().itemCount);
		}
	}

	/**
	 * Comparator to order class items by their number of instances and direct
	 * subclasses.
	 *
	 * @author Markus Kroetzsch
	 *
	 */
	private static class UsageRecordComparator
			implements
			Comparator<Entry<? extends EntityIdValue, ? extends PropertyRecord>> {
		@Override
		public int compare(
				Entry<? extends EntityIdValue, ? extends PropertyRecord> o1,
				Entry<? extends EntityIdValue, ? extends PropertyRecord> o2) {
			return (o2.getValue().itemCount + o2.getValue().qualifierCount + o2
					.getValue().referenceCount)
					- (o1.getValue().itemCount + o1.getValue().qualifierCount + o1
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
	 * once. The Map assigns an item to each label. If another item wants to use
	 * a label that is already assigned, it will use a label with an added Q-ID
	 * for disambiguation.
	 */
	final HashMap<String, EntityIdValue> labels = new HashMap<>();

	/**
	 * Main method. Processes the whole dump using this processor. To change
	 * which dump file to use and whether to run in offline mode, modify the
	 * settings in {@link ExampleHelpers}.
	 *
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) {
		ExampleHelpers.configureLogging();
		ClassPropertyUsageAnalyzer.printDocumentation();

		ClassPropertyUsageAnalyzer processor = new ClassPropertyUsageAnalyzer();
		ExampleHelpers.processEntitiesFromWikidataDump(processor);
		processor.writeFinalReports();
	}

	@Override
	public void processItemDocument(ItemDocument itemDocument) {
		this.countItems++;
		if (itemDocument.getStatementGroups().size() > 0) {
			this.countPropertyItems++;
		}

		ClassRecord classRecord = null;
		if (TOP_LEVEL_CLASSES.contains(itemDocument.getEntityId().getId())
				|| this.classRecords.containsKey(itemDocument.getEntityId())) {
			classRecord = getClassRecord(itemDocument.getEntityId());
		}

		for (StatementGroup sg : itemDocument.getStatementGroups()) {
			PropertyRecord propertyRecord = getPropertyRecord(sg.getProperty());
			propertyRecord.itemCount++;
			propertyRecord.statementCount += sg.size();

			boolean isInstanceOf = "P31".equals(sg.getProperty().getId());
			boolean isSubclassOf = "P279".equals(sg.getProperty().getId());
			if (isSubclassOf && classRecord == null) {
				classRecord = getClassRecord(itemDocument.getEntityId());
			}

			for (Statement s : sg) {
				// Count uses of properties in qualifiers
				for (SnakGroup q : s.getQualifiers()) {
					countPropertyQualifier(q.getProperty(), q.size());
				}
				// Count statements with qualifiers
				if (s.getQualifiers().size() > 0) {
					propertyRecord.statementWithQualifierCount++;
				}
				// Count uses of properties in references
				for (Reference r : s.getReferences()) {
					for (SnakGroup snakGroup : r.getSnakGroups()) {
						countPropertyReference(snakGroup.getProperty(), snakGroup.size());
					}
				}

				// Process value of instance of/subclass of:
				if ((isInstanceOf || isSubclassOf)
						&& s.getMainSnak() instanceof ValueSnak) {
					Value value = s.getValue();
					if (value instanceof EntityIdValue) {
						ClassRecord otherClassRecord = getClassRecord((EntityIdValue) value);
						if (isInstanceOf) {
							otherClassRecord.itemCount++;
							countCooccurringProperties(itemDocument,
									otherClassRecord, null);
						} else {
							otherClassRecord.subclassCount++;
							classRecord.superClasses.add((EntityIdValue) value);
						}
					}
				}
			}

			countCooccurringProperties(itemDocument, propertyRecord,
					sg.getProperty());
		}

		if (classRecord != null) {
			this.countClasses++;
			classRecord.itemDocument = itemDocument;
		}

		// print a report once in a while:
		if (this.countItems % 100000 == 0) {
			printReport();
		}
		// if (this.countItems % 100000 == 0) {
		// writePropertyData();
		// writeClassData();
		// }
	}

	@Override
	public void processPropertyDocument(PropertyDocument propertyDocument) {
		this.countProperties++;

		PropertyRecord propertyRecord = getPropertyRecord(propertyDocument.getEntityId());
		propertyRecord.propertyDocument = propertyDocument;
	}

	/**
	 * Creates the final file output of the analysis.
	 */
	public void writeFinalReports() {
		writePropertyData();
		writeClassData();
	}

	/**
	 * Print some basic documentation about this program.
	 */
	public static void printDocumentation() {
		System.out
				.println("********************************************************************");
		System.out
				.println("*** Wikidata Toolkit: Class and Property Usage Analyzer");
		System.out.println("*** ");
		System.out
				.println("*** This program will download and process dumps from Wikidata.");
		System.out
				.println("*** It will create a CSV file with statistics about class and");
		System.out
				.println("*** property useage. These files can be used with the Miga data");
		System.out.println("*** viewer to create the browser seen at ");
		System.out
				.println("*** http://tools.wmflabs.org/wikidata-exports/miga/");
		System.out
				.println("********************************************************************");
	}

	/**
	 * Returns record where statistics about a class should be stored.
	 *
	 * @param entityIdValue
	 *            the class to initialize
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
	 *            the property to initialize
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
			UsageRecord usageRecord, PropertyIdValue thisPropertyIdValue) {
		for (StatementGroup sg : itemDocument.getStatementGroups()) {
			if (!sg.getProperty().equals(thisPropertyIdValue)) {
				if (!usageRecord.propertyCoCounts.containsKey(sg.getProperty())) {
					usageRecord.propertyCoCounts.put(sg.getProperty(), 1);
				} else {
					usageRecord.propertyCoCounts
							.put(sg.getProperty(), usageRecord.propertyCoCounts
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
	 *            the property to count
	 * @param count
	 *            the number of times to count the property
	 */
	private void countPropertyQualifier(PropertyIdValue property, int count) {
		PropertyRecord propertyRecord = getPropertyRecord(property);
		propertyRecord.qualifierCount = propertyRecord.qualifierCount + count;
	}

	/**
	 * Counts additional occurrences of a property as property in references.
	 *
	 * @param property
	 *            the property to count
	 * @param count
	 *            the number of times to count the property
	 */
	private void countPropertyReference(PropertyIdValue property, int count) {
		PropertyRecord propertyRecord = getPropertyRecord(property);
		propertyRecord.referenceCount = propertyRecord.referenceCount + count;
	}

	/**
	 * Prints a report about the statistics gathered so far.
	 */
	private void printReport() {
		System.out.println("Processed " + this.countItems + " items:");
		System.out.println(" * Properties encountered: "
				+ this.propertyRecords.size());
		System.out.println(" * Property documents: " + this.countProperties);
		System.out.println(" * Classes encountered: "
				+ this.classRecords.size());
		System.out.println(" * Class documents: " + this.countClasses);
	}

	/**
	 * Writes the data collected about properties to a file.
	 */
	private void writePropertyData() {
		try (PrintStream out = new PrintStream(
				ExampleHelpers.openExampleFileOuputStream("properties.csv"))) {

			out.println("Id" + ",Label" + ",Description" + ",URL" + ",Datatype"
					+ ",Uses in statements" + ",Items with such statements"
					+ ",Uses in statements with qualifiers"
					+ ",Uses in qualifiers" + ",Uses in references"
					+ ",Uses total" + ",Related properties");

			List<Entry<PropertyIdValue, PropertyRecord>> list = new ArrayList<>(
					this.propertyRecords.entrySet());
			list.sort(new UsageRecordComparator());
			for (Entry<PropertyIdValue, PropertyRecord> entry : list) {
				printPropertyRecord(out, entry.getValue(), entry.getKey());
			}

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Writes the data collected about classes to a file.
	 */
	private void writeClassData() {
		try (PrintStream out = new PrintStream(
				ExampleHelpers.openExampleFileOuputStream("classes.csv"))) {

			out.println("Id" + ",Label" + ",Description" + ",URL" + ",Image"
					+ ",Number of direct instances"
					+ ",Number of direct subclasses" + ",Direct superclasses"
					+ ",All superclasses" + ",Related properties");

			List<Entry<EntityIdValue, ClassRecord>> list = new ArrayList<>(
					this.classRecords.entrySet());
			list.sort(new ClassUsageRecordComparator());
			for (Entry<EntityIdValue, ClassRecord> entry : list) {
				if (entry.getValue().itemCount > 0
						|| entry.getValue().subclassCount > 0) {
					printClassRecord(out, entry.getValue(), entry.getKey());
				}
			}

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Prints the data for a single class to the given stream. This will be a
	 * single line in CSV.
	 *
	 * @param out
	 *            the output to write to
	 * @param classRecord
	 *            the class record to write
	 * @param entityIdValue
	 *            the item id that this class record belongs to
	 */
	private void printClassRecord(PrintStream out, ClassRecord classRecord,
			EntityIdValue entityIdValue) {
		printTerms(out, classRecord.itemDocument, entityIdValue, "\""
				+ getClassLabel(entityIdValue) + "\"");
		printImage(out, classRecord.itemDocument);

		out.print("," + classRecord.itemCount + "," + classRecord.subclassCount);

		printClassList(out, classRecord.superClasses);

		HashSet<EntityIdValue> superClasses = new HashSet<>();
		for (EntityIdValue superClass : classRecord.superClasses) {
			addSuperClasses(superClass, superClasses);
		}

		printClassList(out, superClasses);

		printRelatedProperties(out, classRecord);

		out.println();
	}

	/**
	 * Prints a list of classes to the given output. The list is encoded as a
	 * single CSV value, using "@" as a separator. Miga can decode this.
	 * Standard CSV processors do not support lists of entries as values,
	 * however.
	 *
	 * @param out
	 *            the output to write to
	 * @param classes
	 *            the list of class items
	 */
	private void printClassList(PrintStream out, Iterable<EntityIdValue> classes) {
		out.print(",\"");
		boolean first = true;
		for (EntityIdValue superClass : classes) {
			if (first) {
				first = false;
			} else {
				out.print("@");
			}
			// makeshift escaping for Miga:
			out.print(getClassLabel(superClass).replace("@", "＠"));
		}
		out.print("\"");
	}

	private void addSuperClasses(EntityIdValue itemIdValue,
			HashSet<EntityIdValue> superClasses) {
		if (superClasses.contains(itemIdValue)) {
			return;
		}
		superClasses.add(itemIdValue);
		ClassRecord classRecord = this.classRecords.get(itemIdValue);
		if (classRecord == null) {
			return;
		}

		for (EntityIdValue superClass : classRecord.superClasses) {
			addSuperClasses(superClass, superClasses);
		}
	}

	/**
	 * Prints the terms (label, etc.) of one entity to the given stream. This
	 * will lead to several values in the CSV file, which are the same for
	 * properties and class items.
	 *
	 * @param out
	 *            the output to write to
	 * @param termedDocument
	 *            the document that provides the terms to write
	 * @param entityIdValue
	 *            the entity that the data refers to.
	 * @param specialLabel
	 *            special label to use (rather than the label string in the
	 *            document) or null if not using; used by classes, which need to
	 *            support disambiguation in their labels
	 */
	private void printTerms(PrintStream out, TermedDocument termedDocument,
			EntityIdValue entityIdValue, String specialLabel) {
		String label = specialLabel;
		String description = "-";

		if (termedDocument != null) {
			if (label == null) {
				MonolingualTextValue labelValue = termedDocument.getLabels()
						.get("en");
				if (labelValue != null) {
					label = csvStringEscape(labelValue.getText());
				}
			}
			MonolingualTextValue descriptionValue = termedDocument
					.getDescriptions().get("en");
			if (descriptionValue != null) {
				description = csvStringEscape(descriptionValue.getText());
			}
		}

		if (label == null) {
			label = entityIdValue.getId();
		}

		out.print(entityIdValue.getId() + "," + label + "," + description + ","
				+ entityIdValue.getIri());
	}

	/**
	 * Prints the URL of a thumbnail for the given item document to the output,
	 * or a default image if no image is given for the item.
	 *
	 * @param out
	 *            the output to write to
	 * @param itemDocument
	 *            the document that may provide the image information
	 */
	private void printImage(PrintStream out, ItemDocument itemDocument) {
		String imageFile = null;

		if (itemDocument != null) {
			for (StatementGroup sg : itemDocument.getStatementGroups()) {
				boolean isImage = "P18".equals(sg.getProperty().getId());
				if (!isImage) {
					continue;
				}
				for (Statement s : sg) {
					if (s.getMainSnak() instanceof ValueSnak) {
						Value value = s.getMainSnak().getValue();
						if (value instanceof StringValue) {
							imageFile = ((StringValue) value).getString();
							break;
						}
					}
				}
				if (imageFile != null) {
					break;
				}
			}
		}

		if (imageFile == null) {
			out.print(",\"http://commons.wikimedia.org/w/thumb.php?f=MA_Route_blank.svg&w=50\"");
		} else {
			try {
				String imageFileEncoded;
				imageFileEncoded = URLEncoder.encode(
						imageFile.replace(" ", "_"), "utf-8");
				// Keep special title symbols unescaped:
				imageFileEncoded = imageFileEncoded.replace("%3A", ":")
						.replace("%2F", "/");
				out.print(","
						+ csvStringEscape("http://commons.wikimedia.org/w/thumb.php?f="
								+ imageFileEncoded) + "&w=50");
			} catch (UnsupportedEncodingException e) {
				throw new RuntimeException(
						"Your JRE does not support UTF-8 encoding. Srsly?!", e);
			}
		}
	}

	/**
	 * Prints the data of one property to the given output. This will be a
	 * single line in CSV.
	 *
	 * @param out
	 *            the output to write to
	 * @param propertyRecord
	 *            the data to write
	 * @param propertyIdValue
	 *            the property that the data refers to
	 */
	private void printPropertyRecord(PrintStream out,
			PropertyRecord propertyRecord, PropertyIdValue propertyIdValue) {

		printTerms(out, propertyRecord.propertyDocument, propertyIdValue, null);

		String datatype = "Unknown";
		if (propertyRecord.propertyDocument != null) {
			datatype = getDatatypeLabel(propertyRecord.propertyDocument
					.getDatatype());
		}

		out.print(","
				+ datatype
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

		out.println();
	}

	/**
	 * Returns an English label for a given datatype.
	 *
	 * @param datatype
	 *            the datatype to label
	 * @return the label
	 */
	private String getDatatypeLabel(DatatypeIdValue datatype) {
		if (datatype.getIri() == null) { // TODO should be redundant once the
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
		case DatatypeIdValue.DT_PROPERTY:
			return "Property";
		case DatatypeIdValue.DT_EXTERNAL_ID:
			return "External identifier";
		case DatatypeIdValue.DT_MATH:
			return "Math";
		case DatatypeIdValue.DT_MONOLINGUAL_TEXT:
			return "Monolingual Text";
		default:
			throw new RuntimeException("Unknown datatype " + datatype.getIri());
		}
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

		List<ImmutablePair<PropertyIdValue, Double>> list = new ArrayList<>(
				usageRecord.propertyCoCounts.size());
		for (Entry<PropertyIdValue, Integer> coCountEntry : usageRecord.propertyCoCounts
				.entrySet()) {
			double otherThisItemRate = (double) coCountEntry.getValue()
					/ usageRecord.itemCount;
			double otherGlobalItemRate = (double) this.propertyRecords
					.get(coCountEntry.getKey()).itemCount
					/ this.countPropertyItems;
			double otherThisItemRateStep = 1 / (1 + Math.exp(6 * (-2
					* otherThisItemRate + 0.5)));
			double otherInvGlobalItemRateStep = 1 / (1 + Math.exp(6 * (-2
					* (1 - otherGlobalItemRate) + 0.5)));

			list.add(new ImmutablePair<>(coCountEntry
					.getKey(), otherThisItemRateStep
					* otherInvGlobalItemRateStep * otherThisItemRate
					/ otherGlobalItemRate));
		}

		list.sort((o1, o2) -> o2.getValue().compareTo(o1.getValue()));

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
			out.print(getPropertyLabel(relatedProperty.left).replace("@", "＠"));
			count++;
		}
		out.print("\"");
	}

	/**
	 * Returns a string that should be used as a label for the given property.
	 *
	 * @param propertyIdValue
	 *            the property to label
	 * @return the label
	 */
	private String getPropertyLabel(PropertyIdValue propertyIdValue) {
		PropertyRecord propertyRecord = this.propertyRecords
				.get(propertyIdValue);
		if (propertyRecord == null || propertyRecord.propertyDocument == null) {
			return propertyIdValue.getId();
		} else {
			return getLabel(propertyIdValue, propertyRecord.propertyDocument);
		}
	}

	/**
	 * Returns a string that should be used as a label for the given item. The
	 * method also ensures that each label is used for only one class. Other
	 * classes with the same label will have their QID added for disambiguation.
	 *
	 * @param entityIdValue
	 *            the item to label
	 * @return the label
	 */
	private String getClassLabel(EntityIdValue entityIdValue) {
		ClassRecord classRecord = this.classRecords.get(entityIdValue);
		String label;
		if (classRecord == null || classRecord.itemDocument == null) {
			label = entityIdValue.getId();
		} else {
			label = getLabel(entityIdValue, classRecord.itemDocument);
		}

		EntityIdValue labelOwner = this.labels.get(label);
		if (labelOwner == null) {
			this.labels.put(label, entityIdValue);
			return label;
		} else if (labelOwner.equals(entityIdValue)) {
			return label;
		} else {
			return label + " (" + entityIdValue.getId() + ")";
		}
	}

	/**
	 * Returns the CSV-escaped label for the given entity based on the terms in
	 * the given document. The returned string will have its quotes escaped, but
	 * it will not be put in quotes (since this is not appropriate in all
	 * contexts where this method is used).
	 *
	 * @param entityIdValue
	 *            the entity to label
	 * @param termedDocument
	 *            the document to get labels from
	 * @return the label
	 */
	private String getLabel(EntityIdValue entityIdValue,
			TermedDocument termedDocument) {
		MonolingualTextValue labelValue = termedDocument.getLabels().get("en");
		if (labelValue != null) {
			return labelValue.getText().replace("\"", "\"\"");
		} else {
			return entityIdValue.getId();
		}
	}

	/**
	 * Escapes a string for use in CSV. In particular, the string is quoted and
	 * quotation marks are escaped.
	 *
	 * @param string
	 *            the string to escape
	 * @return the escaped string
	 */
	private String csvStringEscape(String string) {
		return "\"" + string.replace("\"", "\"\"") + "\"";
	}
}
