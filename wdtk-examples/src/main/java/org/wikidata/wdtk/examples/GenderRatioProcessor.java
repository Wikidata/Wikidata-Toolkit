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
import java.util.List;

import org.wikidata.wdtk.datamodel.helpers.Datamodel;
import org.wikidata.wdtk.datamodel.interfaces.EntityDocumentProcessor;
import org.wikidata.wdtk.datamodel.interfaces.EntityIdValue;
import org.wikidata.wdtk.datamodel.interfaces.ItemDocument;
import org.wikidata.wdtk.datamodel.interfaces.ItemIdValue;
import org.wikidata.wdtk.datamodel.interfaces.MonolingualTextValue;
import org.wikidata.wdtk.datamodel.interfaces.PropertyDocument;
import org.wikidata.wdtk.datamodel.interfaces.SiteLink;
import org.wikidata.wdtk.datamodel.interfaces.Statement;
import org.wikidata.wdtk.datamodel.interfaces.StatementGroup;
import org.wikidata.wdtk.datamodel.interfaces.Value;

/**
 * This document processor calculates the gender ratios of people featured on
 * Wikimedia projects. It is inspired by the <a
 * href="http://notconfusing.com/sex-ratios-in-wikidata-part-iii/"
 * >investigations of Max Klein</a>.
 * <p>
 * For each Wikidata item we consider all the Wikimedia projects (Wikipedia
 * etc.) that have an article on this subject. We find out if the Wikidata item
 * is about a human and which sex/gender values it has (if any). We then count
 * the pages, humans, humans with gender, and humans with each particular gender
 * for each site. The script genderates intermediate status reports for the
 * biggest sites, and eventually writes a CSV file with all the data for all the
 * sites.
 * <p>
 * There are certainly more than two genders, but in fact we cannot even assume
 * a previously known list of genders. So we collect the data in a way that
 * allows arbitrary items as values for gender. We make an effort to find an
 * English label for all of them, but we don't go as far as looking through the
 * dump twice (if we encounter a gender value after the item for that gender was
 * already processed, we cannot go back to fetch the value). It is possible to
 * preconfigure some labels so as to have them set from the very start.
 * <p>
 * The program could also be used to compare the amount of other articles by
 * language. For this, the value of {@link GenderRatioProcessor#filterClass} can
 * be changed.
 *
 * @author Markus Kroetzsch
 *
 */
public class GenderRatioProcessor implements EntityDocumentProcessor {
	int itemCount = 0;
	int genderItemCount = 0;
	boolean printedStatus = true;

	/**
	 * Class to store basic information for each site in a simple format.
	 *
	 * @author Markus Kroetzsch
	 *
	 */
	public class SiteRecord {
		public int pageCount = 0;
		public int humanGenderPageCount = 0;
		public int humanPageCount = 0;
		public final HashMap<EntityIdValue, Integer> genderCounts = new HashMap<>();
		public final String siteKey;

		public SiteRecord(String siteKey) {
			this.siteKey = siteKey;
		}
	}

	/**
	 * Class to order site records human page count.
	 *
	 * @author Markus Kroetzsch
	 *
	 */
	public class SiteRecordComparator implements Comparator<SiteRecord> {
		@Override
		public int compare(SiteRecord o1, SiteRecord o2) {
			return o2.humanPageCount - o1.humanPageCount;
		}
	}

	final HashMap<String, SiteRecord> siteRecords = new HashMap<>();
	final HashMap<EntityIdValue, String> genderNames = new HashMap<>();
	final List<EntityIdValue> genderNamesList = new ArrayList<>();

	/**
	 * Class to use for filtering items. This can be changed to analyse a more
	 * specific set of items. Gender information will always be collected, but
	 * it would not be a problem if there was none. For example, you could use
	 * the same code to compare the number of articles about lighthouses
	 * (Q39715) by site; the gender counts would (hopefully) be zero in this
	 * case.
	 */
	static final ItemIdValue filterClass = Datamodel
			.makeWikidataItemIdValue("Q5");

	/**
	 * Main method. Processes the whole dump using this processor and writes the
	 * results to a file. To change which dump file to use and whether to run in
	 * offline mode, modify the settings in {@link ExampleHelpers}.
	 *
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {
		ExampleHelpers.configureLogging();
		GenderRatioProcessor.printDocumentation();

		GenderRatioProcessor processor = new GenderRatioProcessor();
		ExampleHelpers.processEntitiesFromWikidataDump(processor);
		processor.writeFinalResults();
	}

	/**
	 * Constructor.
	 */
	public GenderRatioProcessor() {
		// Pre-configure some common genders to get more readable status outputs
		// (we also extract labels from the dump, but usually only quite late)
		addNewGenderName(
				Datamodel.makeItemIdValue("Q6581072", Datamodel.SITE_WIKIDATA),
				"female");
		addNewGenderName(
				Datamodel.makeItemIdValue("Q6581097", Datamodel.SITE_WIKIDATA),
				"male");
		addNewGenderName(
				Datamodel.makeItemIdValue("Q48270", Datamodel.SITE_WIKIDATA),
				"genderqueer");
		addNewGenderName(
				Datamodel.makeItemIdValue("Q1052281", Datamodel.SITE_WIKIDATA),
				"transgender female");
		addNewGenderName(
				Datamodel.makeItemIdValue("Q2449503", Datamodel.SITE_WIKIDATA),
				"transgender male");

		addNewGenderName(
				Datamodel.makeItemIdValue("Q1097630", Datamodel.SITE_WIKIDATA),
				"intersex");
		addNewGenderName(
				Datamodel.makeItemIdValue("Q746411", Datamodel.SITE_WIKIDATA),
				"kathoey");
		addNewGenderName(
				Datamodel.makeItemIdValue("Q1399232", Datamodel.SITE_WIKIDATA),
				"fa'afafine");

		// Should not needed since we restrict to humans, but still happens:
		addNewGenderName(
				Datamodel.makeItemIdValue("Q43445", Datamodel.SITE_WIKIDATA),
				"female animal");
		addNewGenderName(
				Datamodel.makeItemIdValue("Q44148", Datamodel.SITE_WIKIDATA),
				"male animal");
	}

	@Override
	public void processItemDocument(ItemDocument itemDocument) {
		this.itemCount++;

		List<EntityIdValue> genderValues = Collections.emptyList();
		boolean isHumanWithGender = false;
		boolean isHuman = false;

		for (StatementGroup statementGroup : itemDocument.getStatementGroups()) {
			switch (statementGroup.getProperty().getId()) {
			case "P21": // P21 is "sex or gender"
				genderValues = getItemIdValueList(statementGroup);
				break;
			case "P31": // P31 is "instance of"
				isHuman = containsValue(statementGroup, filterClass);
				break;
			}
		}

		if (isHuman && genderValues.size() > 0) {
			isHumanWithGender = true;
			this.genderItemCount++;
			this.printedStatus = false;

			for (EntityIdValue gender : genderValues) {
				if (!this.genderNames.containsKey(gender)) {
					addNewGenderName(gender, gender.getId());
				}
			}
		}

		// Record site data
		for (SiteLink siteLink : itemDocument.getSiteLinks().values()) {
			SiteRecord siteRecord = getSiteRecord(siteLink.getSiteKey());
			siteRecord.pageCount++;
			if (isHumanWithGender) {
				siteRecord.humanGenderPageCount++;
			}
			if (isHuman) {
				siteRecord.humanPageCount++;
			}
			for (EntityIdValue gender : genderValues) {
				countGender(gender, siteRecord);
			}
		}

		// Also collect labels of items used as genders.
		// Only works if the gender is used before the item is processed, but
		// better than nothing.
		if (this.genderNames.containsKey(itemDocument.getEntityId())) {
			MonolingualTextValue label = itemDocument.getLabels().get("en");
			if (label != null) {
				this.genderNames.put(itemDocument.getEntityId(),
						label.getText());
			}
		}

		// Print status once in a while
		if (!this.printedStatus && this.genderItemCount % 100000 == 0) {
			printStatus();
			this.printedStatus = true;
		}

	}

	/**
	 * Writes the results of the processing to a CSV file.
	 */
	public void writeFinalResults() {
		printStatus();

		try (PrintStream out = new PrintStream(
				ExampleHelpers.openExampleFileOuputStream("gender-ratios.csv"))) {

			out.print("Site key,pages total,pages on humans,pages on humans with gender");
			for (EntityIdValue gender : this.genderNamesList) {
				out.print("," + this.genderNames.get(gender) + " ("
						+ gender.getId() + ")");
			}
			out.println();

			List<SiteRecord> siteRecords = new ArrayList<>(
					this.siteRecords.values());
			Collections.sort(siteRecords, new SiteRecordComparator());
			for (SiteRecord siteRecord : siteRecords) {
				out.print(siteRecord.siteKey + "," + siteRecord.pageCount + ","
						+ siteRecord.humanPageCount + ","
						+ siteRecord.humanGenderPageCount);

				for (EntityIdValue gender : this.genderNamesList) {
					if (siteRecord.genderCounts.containsKey(gender)) {
						out.print("," + siteRecord.genderCounts.get(gender));
					} else {
						out.print(",0");
					}
				}
				out.println();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Prints some basic documentation about this program.
	 */
	public static void printDocumentation() {
		System.out
				.println("********************************************************************");
		System.out.println("*** Wikidata Toolkit: GenderRatioProcessor");
		System.out.println("*** ");
		System.out
				.println("*** This program will download and process dumps from Wikidata.");
		System.out
				.println("*** It will compute the numbers of articles about humans across");
		System.out
				.println("*** Wikimedia projects, and in particular it will count the articles");
		System.out
				.println("*** for each sex/gender. Results will be stored in a CSV file.");
		System.out.println("*** See source code for further details.");
		System.out
				.println("********************************************************************");
	}

	/**
	 * Prints the current status to the system output.
	 */
	private void printStatus() {
		System.out.println("*** Found " + genderItemCount
				+ " items with gender within " + itemCount + " items.");

		System.out
				.println("*** Showing top ten sites with most items with gender data: ");
		int siteCount = 0;
		List<SiteRecord> siteRecords = new ArrayList<>(
				this.siteRecords.values());
		Collections.sort(siteRecords, new SiteRecordComparator());
		for (SiteRecord siteRecord : siteRecords) {
			if (siteCount >= 10) {
				break;
			}
			siteCount++;

			System.out.print(String.format("%1$8s", siteRecord.siteKey) + ": ");

			int genderCount = 0;
			for (EntityIdValue gender : this.genderNamesList) {
				System.out.print(this.genderNames.get(gender) + " ");

				int count;
				float ratio;
				if (siteRecord.genderCounts.containsKey(gender)) {
					count = siteRecord.genderCounts.get(gender);
					ratio = (float) count / siteRecord.humanGenderPageCount
							* 100;
				} else {
					count = 0;
					ratio = 0;
				}

				if (genderCount < 2) {
					System.out.printf("%7d (%5.3f%%) ", count, ratio);
				} else {
					System.out.printf("%3d (%5.4f%%) ", count, ratio);
				}

				genderCount++;
			}

			System.out
					.println(" -- gender pages: "
							+ siteRecord.humanGenderPageCount
							+ ", human pages: "
							+ siteRecord.humanPageCount
							+ ", total pages: "
							+ siteRecord.pageCount
							+ ", ghp/hp: "
							+ ((float) siteRecord.humanGenderPageCount
									/ siteRecord.humanPageCount * 100)
							+ "%, hp/p: "
							+ ((float) siteRecord.humanPageCount
									/ siteRecord.pageCount * 100) + "%");
		}
	}

	/**
	 * Helper method that extracts the list of all {@link ItemIdValue} objects
	 * that are used as values in the given statement group.
	 *
	 * @param statementGroup
	 *            the {@link StatementGroup} to extract the data from
	 * @return the list of values
	 */
	private List<EntityIdValue> getItemIdValueList(StatementGroup statementGroup) {
		List<EntityIdValue> result = new ArrayList<>(statementGroup.size());

		for (Statement s : statementGroup) {
			Value v = s.getValue();
			if (v instanceof EntityIdValue) {
				result.add((EntityIdValue) v);
			}
		}

		return result;
	}

	/**
	 * Checks if the given group of statements contains the given value as the
	 * value of a main snak of some statement.
	 *
	 * @param statementGroup
	 *            the statement group to scan
	 * @param value
	 *            the value to scan for
	 * @return true if value was found
	 */
	private boolean containsValue(StatementGroup statementGroup, Value value) {
		for (Statement s : statementGroup) {
			if (value.equals(s.getValue())) {
				return true;
			}
		}

		return false;
	}

	/**
	 * Adds a new gender item and an initial name.
	 *
	 * @param entityIdValue
	 *            the item representing the gender
	 * @param name
	 *            the label to use for representing the gender
	 */
	private void addNewGenderName(EntityIdValue entityIdValue, String name) {
		this.genderNames.put(entityIdValue, name);
		this.genderNamesList.add(entityIdValue);
	}

	/**
	 * Returns a site record for the site of the given name, creating a new one
	 * if it does not exist yet.
	 *
	 * @param siteKey
	 *            the key of the site
	 * @return the suitable site record
	 */
	private SiteRecord getSiteRecord(String siteKey) {
		SiteRecord siteRecord = this.siteRecords.get(siteKey);
		if (siteRecord == null) {
			siteRecord = new SiteRecord(siteKey);
			this.siteRecords.put(siteKey, siteRecord);
		}
		return siteRecord;
	}

	/**
	 * Counts a single page of the specified gender. If this is the first page
	 * of that gender on this site, a suitable key is added to the list of the
	 * site's genders.
	 *
	 * @param gender
	 *            the gender to count
	 * @param siteRecord
	 *            the site record to count it for
	 */
	private void countGender(EntityIdValue gender, SiteRecord siteRecord) {
		Integer curValue = siteRecord.genderCounts.get(gender);
		if (curValue == null) {
			siteRecord.genderCounts.put(gender, 1);
		} else {
			siteRecord.genderCounts.put(gender, curValue + 1);
		}
	}

}
