package org.wikidata.wdtk.client;

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

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.wikidata.wdtk.datamodel.helpers.Datamodel;
import org.wikidata.wdtk.datamodel.interfaces.ItemDocument;
import org.wikidata.wdtk.datamodel.interfaces.ItemIdValue;
import org.wikidata.wdtk.datamodel.interfaces.PropertyDocument;
import org.wikidata.wdtk.datamodel.interfaces.PropertyIdValue;
import org.wikidata.wdtk.datamodel.interfaces.SiteLink;
import org.wikidata.wdtk.datamodel.interfaces.Sites;
import org.wikidata.wdtk.datamodel.interfaces.Snak;
import org.wikidata.wdtk.datamodel.interfaces.SnakGroup;
import org.wikidata.wdtk.datamodel.interfaces.Statement;
import org.wikidata.wdtk.datamodel.interfaces.StatementDocument;
import org.wikidata.wdtk.datamodel.interfaces.StatementGroup;
import org.wikidata.wdtk.datamodel.interfaces.StatementRank;
import org.wikidata.wdtk.datamodel.interfaces.StringValue;
import org.wikidata.wdtk.datamodel.interfaces.TermedDocument;
import org.wikidata.wdtk.datamodel.interfaces.Value;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 *
 * @author Markus Kroetzsch
 */
public class SchemaUsageAnalyzer implements DumpProcessingAction {

	private static final ItemIdValue ItemGadgetAuthorityControl = Datamodel
			.makeWikidataItemIdValue("Q22348290");

	/**
	 * Object mapper that is used to serialize JSON.
	 */
	protected static final ObjectMapper mapper = new ObjectMapper();
	static {
		mapper.configure(JsonGenerator.Feature.AUTO_CLOSE_TARGET, false);
	}

	/**
	 * The place where result files will be stored.
	 */
	protected Path resultDirectory;

	/**
	 * Sites information used to extract site data.
	 */
	protected Sites sites;

	/**
	 * Name as an action, if any
	 */
	protected String name = null;

	protected String dateStamp;
	protected String project;

	/**
	 * Simple record class to keep track of some usage numbers for one type of
	 * entity.
	 *
	 * @author Markus Kroetzsch
	 *
	 */
	class EntityStatistics {
		@JsonProperty("c")
		long count = 0;
		@JsonProperty("cLabels")
		long countLabels = 0;
		@JsonProperty("cDesc")
		long countDescriptions = 0;
		@JsonProperty("cAliases")
		long countAliases = 0;
		@JsonProperty("cStmts")
		long countStatements = 0;
		// long countReferencedStatements = 0;

		// Maps to store property usage data for each language:
		// final HashMap<String, Integer> labelCounts = new HashMap<>();
		// final HashMap<String, Integer> descriptionCounts = new HashMap<>();
		// final HashMap<String, Integer> aliasCounts = new HashMap<>();
	}

	/**
	 * Class to record the use of some class item or property.
	 *
	 * @author Markus Kroetzsch
	 * @author Markus Damm
	 *
	 */
	private abstract class UsageRecord {
		/**
		 * Number of items using this entity. For properties, this is the number
		 * of items with such a property. For class items, this is the number of
		 * direct instances of this class.
		 */
		@JsonProperty("i")
		@JsonInclude(Include.NON_EMPTY)
		public int itemCount = 0;
		/**
		 * Map that records how many times certain properties are used on items
		 * that use this entity (where "use" has the meaning explained for
		 * {@link UsageRecord#itemCount}).
		 */
		@JsonIgnore
		public HashMap<Integer, Integer> propertyCoCounts = new HashMap<>();
		/**
		 * The label of this item. If there isn't any English label available,
		 * the label is set to null.
		 */
		@JsonProperty("l")
		@JsonInclude(Include.NON_EMPTY)
		public String label;

		/**
		 * Returns a list of related properties in a list ordered by a custom
		 * relatedness measure.
		 *
		 * @return
		 */
		@JsonProperty("r")
		@JsonInclude(Include.NON_EMPTY)
		public Map<String, Integer> getRelatedProperties() {
			List<ImmutablePair<Integer, Double>> list = new ArrayList<>(
					this.propertyCoCounts.size());
			for (Entry<Integer, Integer> coCountEntry : this.propertyCoCounts
					.entrySet()) {
				double otherThisItemRate = (double) coCountEntry.getValue()
						/ this.itemCount;
				double otherGlobalItemRate = (double) SchemaUsageAnalyzer.this.propertyRecords
						.get(coCountEntry.getKey()).itemCount
						/ SchemaUsageAnalyzer.this.countPropertyEntities;
				double otherThisItemRateStep = 1 / (1 + Math.exp(6 * (-2
						* otherThisItemRate + 0.5)));
				double otherInvGlobalItemRateStep = 1 / (1 + Math.exp(6 * (-2
						* (1 - otherGlobalItemRate) + 0.5)));

				list.add(new ImmutablePair<Integer, Double>(coCountEntry
						.getKey(), otherThisItemRateStep
						* otherInvGlobalItemRateStep * otherThisItemRate
						/ otherGlobalItemRate));
			}

			Collections.sort(list,
					new Comparator<ImmutablePair<Integer, Double>>() {
						@Override
						public int compare(ImmutablePair<Integer, Double> o1,
								ImmutablePair<Integer, Double> o2) {
							return o2.getValue().compareTo(o1.getValue());
						}
					});

			LinkedHashMap<String, Integer> result = new LinkedHashMap<>();
			for (ImmutablePair<Integer, Double> entry : list) {
				result.put(entry.left.toString(), (int) (10 * entry.right));
			}

			return result;
		}
	}

	/**
	 * Class to record the usage of a class item in the data.
	 *
	 * @author Markus Kroetzsch
	 */
	private class ClassRecord extends UsageRecord {
		/**
		 * Number of direct subclasses of this class item.
		 */
		@JsonProperty("s")
		@JsonInclude(Include.NON_EMPTY)
		public int subclassCount = 0;
		/**
		 * Number of all (direct and indirect) instances of this class item.
		 */
		@JsonProperty("ai")
		@JsonInclude(Include.NON_EMPTY)
		public int allInstanceCount = 0;
		/**
		 * Number of all (direct and indirect) subclasses of this class item.
		 */
		@JsonProperty("as")
		@JsonInclude(Include.NON_EMPTY)
		public int allSubclassCount = 0;
		/**
		 * List of direct super classes of this class.
		 */
		@JsonIgnore
		public ArrayList<Integer> directSuperClasses = new ArrayList<>();
		/**
		 * Set of all super classes of this class.
		 */
		@JsonIgnore
		public Set<Integer> superClasses = new HashSet<>();

		@JsonProperty("sc")
		@JsonInclude(Include.NON_EMPTY)
		public String[] getSuperClasses() {
			String[] result = new String[superClasses.size()];
			int i = 0;
			for (Integer id : superClasses) {
				result[i] = id.toString();
				i++;
			}
			return result;
		}

		/**
		 * List of direct subclasses of this class that are included in the
		 * export. This is only filled at the end of the processing.
		 */
		@JsonProperty("sb")
		@JsonInclude(Include.NON_EMPTY)
		public ArrayList<String> nonemptyDirectSubclasses = new ArrayList<>();
	}

	/**
	 * Class to record the usage of a property in the data.
	 *
	 * @author Markus Kroetzsch
	 */
	private class PropertyRecord extends UsageRecord {

		/**
		 * Set of all qualifiers used with this property.
		 */
		@JsonIgnore
		public Map<Integer, Integer> qualifiers = new HashMap<>();

		/**
		 * Main URL pattern to be used in links, if any.
		 *
		 * @return
		 */
		@JsonProperty("u")
		@JsonInclude(Include.NON_EMPTY)
		public String urlPattern = null;

		/**
		 * Classes that this property is a direct instance of.
		 */
		@JsonIgnore
		public List<Integer> classes = new ArrayList<>();

		@JsonProperty("pc")
		@JsonInclude(Include.NON_EMPTY)
		public List<String> getClasses() {
			List<String> result = new ArrayList<>();
			for (Integer i : classes) {
				result.add(i.toString());
			}
			return result;
		}

		@JsonProperty("qs")
		@JsonInclude(Include.NON_EMPTY)
		public Map<String, Integer> getQualifiers() {
			List<Map.Entry<Integer, Integer>> list = new ArrayList<>(
					this.qualifiers.entrySet());

			Collections.sort(list,
					new Comparator<Map.Entry<Integer, Integer>>() {
						@Override
						public int compare(Map.Entry<Integer, Integer> o1,
								Map.Entry<Integer, Integer> o2) {
							return o2.getValue().compareTo(o1.getValue());
						}
					});

			LinkedHashMap<String, Integer> result = new LinkedHashMap<>();
			for (Map.Entry<Integer, Integer> entry : list) {
				result.put(entry.getKey().toString(), entry.getValue());
			}

			return result;
		}

	}

	private class SiteRecord {
		@JsonProperty("u")
		final String urlPattern;

		@JsonProperty("g")
		final String group;

		@JsonProperty("l")
		final String language;

		@JsonProperty("i")
		long itemCount = 0;

		public SiteRecord(String language, String urlPattern, String group) {
			this.urlPattern = urlPattern;
			this.group = group;
			this.language = language;
		}
	}

	/**
	 * Collection of all property records.
	 */
	final HashMap<Integer, PropertyRecord> propertyRecords = new HashMap<>();
	/**
	 * Collection of all item records of items used as classes.
	 */
	final HashMap<Integer, ClassRecord> classRecords = new HashMap<>();
	/**
	 * Collection of all site records of items used as classes.
	 */
	final HashMap<String, SiteRecord> siteRecords = new HashMap<>();

	/**
	 * Total number of items processed.
	 */
	private long countEntities = 0;
	/**
	 * Total number of items that have some statement.
	 */
	private long countPropertyEntities = 0;
	/**
	 * Total number of site links.
	 */
	private long countSiteLinks = 0;

	private final EntityStatistics itemStatistics = new EntityStatistics();
	private final EntityStatistics propertyStatistics = new EntityStatistics();

	/**
	 * Create a directory at the given path if it does not exist yet.
	 *
	 * @param path
	 *            the path to the directory
	 * @throws IOException
	 *             if it was not possible to create a directory at the given
	 *             path
	 */
	private static void createDirectory(Path path) throws IOException {
		try {
			Files.createDirectory(path);
		} catch (FileAlreadyExistsException e) {
			if (!Files.isDirectory(path)) {
				throw e;
			}
		}
	}

	/**
	 * Opens a new FileOutputStream for a file of the given name in the given
	 * result directory. Any file of this name that exists already will be
	 * replaced. The caller is responsible for eventually closing the stream.
	 *
	 * @param resultDirectory
	 *            the path to the result directory
	 * @param filename
	 *            the name of the file to write to
	 * @return FileOutputStream for the file
	 * @throws IOException
	 *             if the file or example output directory could not be created
	 */
	public static FileOutputStream openResultFileOuputStream(
			Path resultDirectory, String filename) throws IOException {
		Path filePath = resultDirectory.resolve(filename);
		return new FileOutputStream(filePath.toFile());
	}

	@Override
	public void processItemDocument(ItemDocument itemDocument) {
		// Record relevant labels:
		Integer itemId = getNumId(itemDocument.getEntityId().getId(), false);
		if (this.classRecords.containsKey(itemId)) {
			this.classRecords.get(itemId).label = itemDocument.findLabel("en");
		}

		countTerms(itemDocument, itemStatistics);
		processStatementDocument(itemDocument, itemStatistics);

		this.countSiteLinks += itemDocument.getSiteLinks().size();
		for (SiteLink siteLink : itemDocument.getSiteLinks().values()) {
			countSiteLink(siteLink);
		}
	}

	private void countSiteLink(SiteLink siteLink) {
		if (!this.siteRecords.containsKey(siteLink.getSiteKey())) {
			String key = siteLink.getSiteKey();
			String url = this.sites.getPageUrl(key, "$Placeholder12345");
			if (url == null) {
				System.err
						.println("Could not find site information for " + key);
			} else {
				url = url.replace("%24Placeholder12345", "$1");
			}
			this.siteRecords.put(key,
					new SiteRecord(this.sites.getLanguageCode(key), url,
							this.sites.getGroup(key)));
		}
		this.siteRecords.get(siteLink.getSiteKey()).itemCount++;
	}

	@Override
	public void processPropertyDocument(PropertyDocument propertyDocument) {
		// Record relevant labels:
		PropertyRecord pr = getPropertyRecord(propertyDocument.getEntityId());
		pr.label = propertyDocument.findLabel("en");

		// Find best URL pattern:
		StatementGroup urlPatterns = propertyDocument
				.findStatementGroup("P1630");
		if (urlPatterns != null) {
			for (Statement s : urlPatterns) {
				Value v = s.getValue();
				if (v == null) {
					continue;
				}
				String urlPattern = ((StringValue) v).getString();
				boolean foundGacUrl = false;
				if (pr.urlPattern == null) {
					pr.urlPattern = urlPattern;
				} else if (!foundGacUrl
						&& s.getRank() == StatementRank.PREFERRED) {
					pr.urlPattern = urlPattern;
				} else if (!foundGacUrl) {
					Iterator<Snak> snaks = s.getAllQualifiers();
					while (snaks.hasNext()) {
						Snak snak = snaks.next();
						if ("P1535".equals(snak.getPropertyId().getId())
								&& ItemGadgetAuthorityControl.equals(snak
										.getValue())) {
							pr.urlPattern = urlPattern;
							foundGacUrl = true;
							break;
						}
					}
				}
			}
		}

		// Collect classes that this property is in:
		StatementGroup instanceClasses = propertyDocument
				.findStatementGroup("P31");
		if (instanceClasses != null) {
			for (Statement s : instanceClasses) {
				Value v = s.getValue();
				if (v != null) {
					pr.classes.add(Integer.parseInt(((ItemIdValue) v).getId()
							.substring(1)));
				}
			}
		}

		countTerms(propertyDocument, propertyStatistics);
		processStatementDocument(propertyDocument, propertyStatistics);
	}

	private void processStatementDocument(StatementDocument statementDocument,
			EntityStatistics entityStatistics) {
		this.countEntities++;
		entityStatistics.count++;

		if (statementDocument.getStatementGroups().size() > 0) {
			this.countPropertyEntities++;
		}

		Set<Integer> superClasses = new HashSet<>();
		StatementGroup instanceOfStatements = statementDocument
				.findStatementGroup("P31");
		if (instanceOfStatements != null) {
			// Compute all superclasses and count direct instances:
			for (Statement s : instanceOfStatements) {
				Value v = s.getValue();
				if (v instanceof ItemIdValue) {
					Integer vId = getNumId(((ItemIdValue) v).getId(), false);
					superClasses.add(vId);
					ClassRecord classRecord = getClassRecord(vId);
					classRecord.itemCount++;
					superClasses.addAll(classRecord.superClasses);
				}
			}

			// Count item in all superclasses and count cooccuring properties
			// for all superclasses:
			for (Integer classId : superClasses) {
				ClassRecord classRecord = getClassRecord(classId);
				classRecord.allInstanceCount++;
				countCooccurringProperties(statementDocument, classRecord, null);
			}
		}

		// Count statements:
		for (StatementGroup sg : statementDocument.getStatementGroups()) {
			entityStatistics.countStatements += sg.size();
			PropertyRecord propertyRecord = getPropertyRecord(sg.getProperty());
			propertyRecord.itemCount++;
			countCooccurringProperties(statementDocument, propertyRecord,
					sg.getProperty());

			for (Statement s : sg) {
				for (SnakGroup snakGroup : s.getQualifiers()) {
					Integer qualifierId = getNumId(snakGroup.getProperty()
							.getId(), false);
					if (propertyRecord.qualifiers.containsKey(qualifierId)) {
						propertyRecord.qualifiers.put(qualifierId,
								propertyRecord.qualifiers.get(qualifierId) + 1);
					} else {
						propertyRecord.qualifiers.put(qualifierId, 1);
					}
				}
			}
		}

		// print a report once in a while:
		if (this.countEntities % 100000 == 0) {
			printReport();
			// writeFinalReports(); // DEBUG
		}
	}

	/**
	 * Count the terms (labels, descriptions, aliases) of an item or property
	 * document.
	 *
	 * @param termedDocument
	 *            document to count the terms of
	 * @param entityStatistics
	 *            record where statistics are counted
	 */
	protected void countTerms(TermedDocument termedDocument,
			EntityStatistics entityStatistics) {
		entityStatistics.countLabels += termedDocument.getLabels().size();
		// for (MonolingualTextValue mtv : termedDocument.getLabels().values())
		// {
		// countKey(usageStatistics.labelCounts, mtv.getLanguageCode(), 1);
		// }

		entityStatistics.countDescriptions += termedDocument.getDescriptions()
				.size();
		// for (MonolingualTextValue mtv : termedDocument.getDescriptions()
		// .values()) {
		// countKey(usageStatistics.descriptionCounts, mtv.getLanguageCode(),
		// 1);
		// }

		for (String languageKey : termedDocument.getAliases().keySet()) {
			int count = termedDocument.getAliases().get(languageKey).size();
			entityStatistics.countAliases += count;
			// countKey(usageStatistics.aliasCounts, languageKey, count);
		}
	}

	/**
	 * Fetches all subclass relationships (P279) using the SPARQL endpoint, and
	 * uses them to compute direct and indirect superclasses of each class.
	 *
	 * @throws IOException
	 */
	private void fetchSubclassHierarchy() throws IOException {
		System.out.println("Fetching subclass relationships from SPARQL ...");
		try (InputStream response = runSparqlQuery("PREFIX ps: <http://www.wikidata.org/prop/statement/>\n"
				+ "PREFIX p: <http://www.wikidata.org/prop/>\n"
				+ "SELECT ?subC ?supC WHERE { ?subC p:P279/ps:P279 ?supC }")) {
			System.out.println("Processing subclass relationships ...");

			// DEBUG
			// BufferedReader br = new BufferedReader(new InputStreamReader(
			// response));
			// String read;
			// while ((read = br.readLine()) != null) {
			// System.out.println(read);
			// }
			// System.out.println("*** done ***");

			ObjectMapper mapper = new ObjectMapper();
			JsonNode root = mapper.readTree(response);
			JsonNode bindings = root.path("results").path("bindings");
			int count = 0;
			for (JsonNode binding : bindings) {
				count++;
				Integer subId = getNumId(binding.path("subC").path("value")
						.asText(), true);
				Integer supId = getNumId(binding.path("supC").path("value")
						.asText(), true);
				if (supId == 0 || subId == 0) {
					System.out.println("Ignoring "
							+ binding.path("subC").path("value").asText()
							+ " subClassOf "
							+ binding.path("supC").path("value").asText());
					continue;
				}
				getClassRecord(subId).directSuperClasses.add(supId);
				ClassRecord superClass = getClassRecord(supId);
				superClass.subclassCount++;
				if (count % 10000 == 0) {
					System.out.print("*");
				}
			}
			System.out.println("Found " + count
					+ " subclass relationships among "
					+ this.classRecords.size() + " Wikidata items.");

			System.out.println("Computing indirect subclass relationships ...");
			for (ClassRecord classRecord : this.classRecords.values()) {
				for (Integer superClass : classRecord.directSuperClasses) {
					addSuperClasses(superClass, classRecord);
				}
			}

			System.out.println("Computing total subclass counts ...");
			for (ClassRecord classRecord : this.classRecords.values()) {
				for (Integer superClass : classRecord.superClasses) {
					getClassRecord(superClass).allSubclassCount++;
				}
			}

			System.out.println("Preprocessing of class hierarchy complete.");
		}
	}

	/**
	 * Recursively add indirect subclasses to a class record.
	 *
	 * @param directSuperClass
	 *            the superclass to add (together with its own superclasses)
	 * @param subClassRecord
	 *            the subclass to add to
	 */
	private void addSuperClasses(Integer directSuperClass,
			ClassRecord subClassRecord) {
		if (subClassRecord.superClasses.contains(directSuperClass)) {
			return;
		}
		subClassRecord.superClasses.add(directSuperClass);
		ClassRecord superClassRecord = getClassRecord(directSuperClass);
		if (superClassRecord == null) {
			return;
		}

		for (Integer superClass : superClassRecord.directSuperClasses) {
			addSuperClasses(superClass, subClassRecord);
		}
	}

	/**
	 * Extracts a numeric id from a string, which can be either a Wikidata
	 * entity URI or a short entity or property id.
	 *
	 * @param idString
	 * @param isUri
	 * @return numeric id, or 0 if there was an error
	 */
	private Integer getNumId(String idString, boolean isUri) {
		String numString;
		if (isUri) {
			if (!idString.startsWith("http://www.wikidata.org/entity/")) {
				return 0;
			}
			numString = idString.substring("http://www.wikidata.org/entity/Q"
					.length());
		} else {
			numString = idString.substring(1);
		}
		return Integer.parseInt(numString);
	}

	/**
	 * Returns record where statistics about a class should be stored.
	 *
	 * @param classId
	 *            the numeric id of the class to initialize
	 * @return the class record
	 */
	private ClassRecord getClassRecord(Integer classId) {
		if (!this.classRecords.containsKey(classId)) {
			ClassRecord classRecord = new ClassRecord();
			this.classRecords.put(classId, classRecord);
			return classRecord;
		} else {
			return this.classRecords.get(classId);
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
		Integer id = getNumId(property.getId(), false);
		if (!this.propertyRecords.containsKey(id)) {
			PropertyRecord propertyRecord = new PropertyRecord();
			this.propertyRecords.put(id, propertyRecord);
			return propertyRecord;
		} else {
			return this.propertyRecords.get(id);
		}
	}

	/**
	 * Counts each property for which there is a statement in the given item
	 * document, ignoring the property thisPropertyIdValue to avoid properties
	 * counting themselves.
	 *
	 * @param statementDocument
	 * @param usageRecord
	 * @param thisPropertyIdValue
	 */
	private void countCooccurringProperties(
			StatementDocument statementDocument, UsageRecord usageRecord,
			PropertyIdValue thisPropertyIdValue) {
		for (StatementGroup sg : statementDocument.getStatementGroups()) {
			if (!sg.getProperty().equals(thisPropertyIdValue)) {
				Integer propertyId = getNumId(sg.getProperty().getId(), false);
				if (!usageRecord.propertyCoCounts.containsKey(propertyId)) {
					usageRecord.propertyCoCounts.put(propertyId, 1);
				} else {
					usageRecord.propertyCoCounts.put(propertyId,
							usageRecord.propertyCoCounts.get(propertyId) + 1);
				}
			}
		}
	}

	/**
	 * Executes a given SPARQL query and returns a stream with the result in
	 * JSON format.
	 *
	 * @param query
	 * @return
	 * @throws IOException
	 */
	private InputStream runSparqlQuery(String query) throws IOException {
		try {
			String queryString = "query=" + URLEncoder.encode(query, "UTF-8")
					+ "&format=json";
			URL url = new URL("https://query.wikidata.org/sparql?"
					+ queryString);
			HttpURLConnection connection = (HttpURLConnection) url
					.openConnection();
			connection.setRequestMethod("GET");

			return connection.getInputStream();
		} catch (UnsupportedEncodingException | MalformedURLException e) {
			throw new RuntimeException(e.getMessage(), e);
		}
	}

	/**
	 * Creates the final file output of the analysis.
	 */
	private void writeFinalReports() {
		System.out.println("Printing data to output files ...");
		writePropertyData();
		writeClassData();
		System.out.println("Finished printing data.");
	}

	/**
	 * Writes all data that was collected about properties to a json file.
	 */
	private void writePropertyData() {
		try (PrintStream out = new PrintStream(openResultFileOuputStream(
				resultDirectory, "properties.json"))) {
			out.println("{");

			int count = 0;
			for (Entry<Integer, PropertyRecord> propertyEntry : this.propertyRecords
					.entrySet()) {
				if (count > 0) {
					out.println(",");
				}
				out.print("\"" + propertyEntry.getKey() + "\":");
				mapper.writeValue(out, propertyEntry.getValue());
				count++;
			}
			out.println("\n}");

			System.out.println(" Serialized information for " + count
					+ " properties.");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void writeStatisticsData(PrintStream out)
			throws JsonGenerationException, JsonMappingException, IOException {
		out.println(" \"entityCount\": \"" + this.countEntities + "\",");
		out.println(" \"siteLinkCount\": \"" + this.countSiteLinks + "\",");

		out.print(" \"propertyStatistics\": ");
		mapper.writeValue(out, this.propertyStatistics);
		out.println(",");
		out.print(" \"itemStatistics\": ");
		mapper.writeValue(out, this.itemStatistics);
		out.println(",");

		out.print(" \"sites\": ");
		mapper.writeValue(out, this.siteRecords);
		out.println(",");

	}

	/**
	 * Writes all data that was collected about classes to a json file.
	 */
	private void writeClassData() {
		try (PrintStream out = new PrintStream(openResultFileOuputStream(
				resultDirectory, "classes.json"))) {
			out.println("{");

			// Add direct subclass information:
			for (Entry<Integer, ClassRecord> classEntry : this.classRecords
					.entrySet()) {
				if (classEntry.getValue().subclassCount == 0
						&& classEntry.getValue().itemCount == 0) {
					continue;
				}
				for (Integer superClass : classEntry.getValue().directSuperClasses) {
					this.classRecords.get(superClass).nonemptyDirectSubclasses
							.add(classEntry.getKey().toString());
				}
			}

			int count = 0;
			int countNoLabel = 0;
			for (Entry<Integer, ClassRecord> classEntry : this.classRecords
					.entrySet()) {
				if (classEntry.getValue().subclassCount == 0
						&& classEntry.getValue().itemCount == 0) {
					continue;
				}

				if (classEntry.getValue().label == null) {
					countNoLabel++;
				}

				if (count > 0) {
					out.println(",");
				}
				out.print("\"" + classEntry.getKey() + "\":");
				mapper.writeValue(out, classEntry.getValue());
				count++;
			}
			out.println("\n}");

			System.out.println(" Serialized information for " + count
					+ " class items.");
			System.out.println(" -- class items with missing label: "
					+ countNoLabel);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Prints a report about the statistics gathered so far.
	 */
	private void printReport() {
		System.out.println(getReport());
	}

	@Override
	public boolean needsSites() {
		return true;
	}

	@Override
	public boolean isReady() {
		return true;
	}

	@Override
	public String getReport() {
		return "Processed " + this.countEntities + " entities:\n"
				+ " * Property documents: " + this.propertyRecords.size()
				+ "\n" + " * Class documents: " + this.classRecords.size();
	}

	@Override
	public void open() {
		this.resultDirectory = Paths.get("results");
		try {
			// Make output directories for results
			createDirectory(resultDirectory);
			resultDirectory = resultDirectory.resolve("wikidatawiki-"
					+ dateStamp);
			createDirectory(resultDirectory);

			// Initialise processor object
			fetchSubclassHierarchy();
		} catch (IOException e) {
			throw new RuntimeException(e.toString(), e);
		}
	}

	@Override
	public void close() {
		writeFinalReports();
		try (PrintStream out = new PrintStream(openResultFileOuputStream(
				resultDirectory, "statistics.json"))) {
			out.println("{ ");
			writeStatisticsData(out);
			out.println(" \"dumpDate\": \"" + dateStamp + "\"");
			out.println("}");
		} catch (IOException e) {
			throw new RuntimeException(e.toString(), e);
		}
	}

	@Override
	public void setSites(Sites sites) {
		this.sites = sites;
	}

	@Override
	public boolean setOption(String option, String value) {
		// no options
		return false;
	}

	@Override
	public boolean useStdOut() {
		return true;
	}

	@Override
	public void setDumpInformation(String project, String dateStamp) {
		this.project = project;
		this.dateStamp = dateStamp;
	}

	@Override
	public void setActionName(String name) {
		this.name = name;
	}

	@Override
	public String getActionName() {
		if (this.name != null) {
			return this.name;
		} else {
			return getDefaultActionName();
		}
	}

	@Override
	public String getDefaultActionName() {
		return "SchemaAnalyzerAction";
	}

}
