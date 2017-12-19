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

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;

import org.wikidata.wdtk.datamodel.interfaces.EntityDocumentProcessor;
import org.wikidata.wdtk.datamodel.interfaces.ItemDocument;
import org.wikidata.wdtk.datamodel.interfaces.PropertyDocument;
import org.wikidata.wdtk.datamodel.interfaces.TimeValue;

/**
 * This is a simple template for an {@link EntityDocumentProcessor} that can be
 * modified to try your own code.
 * <p>
 * Exercise 1: Just run the code as it is and have a look at the output. It will
 * print a lot of data about item documents to the console. You can see roughly
 * what the data looks like. Find the data for one item and look up the item on
 * wikidata.org. Find the data that you can see on the Web page in the print out
 * (note that some details might have changed since you local data is based on a
 * dump).
 * <p>
 * Exercise 2: The code below already counts how many items and properties it
 * processes. Add additional counters to count: (1) the number of labels, (2)
 * the number of aliases, (3) the number of statements, (4) the number of site
 * links. Print this data at the end or write it to the file if you like.
 * <p>
 * Exercise 3: Extend your code from Exercise 2 to count how many items have a
 * link to English Wikipedia (or another Wikipedia of your choice). The site
 * identifier used in the data for English Wikipedia is "enwiki".
 * <p>
 * Exercise 4: Building on the code of Exercise 3, count the number of site
 * links for all sites that are linked. Use, for example, a hashmap to store
 * integer counters for each site id you encounter. Print the results to a CSV
 * file and load the file into a spreadsheet application (this can also be an
 * online application such as Google Drive). You can order the data by count and
 * create a diagram. The number of site links should be close to the number of
 * articles in the project.
 * <p>
 * Exercise 5: Compute the average life expectancy of people on Wikidata. To do
 * this, consider items with a birth date (P569) and death date (P570). Whenever
 * both dates are found, compute the difference of years between the dates.
 * Store the sum of these lifespans (in years) and the number of people for
 * which you recorded a lifespace to compute the average. Some hints:
 * <ul>
 * <li>There can be more than one statement for any property, even for date of
 * birth/death (if there are different opinions). For simplicity, just use the
 * first.</li>
 * <li>Dates can be uncertain. This is expressed by their precision,
 * {@link TimeValue#getPrecision()}. You should only consider values with
 * precision greater or equal to {@link TimeValue#PREC_DAY}.</li>
 * </ul>
 * <p>
 * Exercise 6: Compute the average life span as in Exercise 5, but now grouped
 * by year of birth. This will show you how life expectancy changed over time
 * (at least for people with Wikipedia articles). For this, create arrays or
 * maps to store the sum of the lifespan and number of people for each year of
 * birth. Finally, compute all the averages and store them to a CSV file that
 * gives the average life expectancy for each year of birth. Load this file into
 * a spreadsheet too to create a diagram. What do you notice? Some hints:
 * <ul>
 * <li>An easy way to store the numbers you need for each year of birth is to
 * use an array where the year is the index. This is possible here since you
 * know that years should be in a certain range. You could also use a Hashmap,
 * of course, but sorting by key is more work in this case.</li>
 * <li>The data can contain errors. If you see strange effects in the results,
 * maybe you need to filter some unlikely cases.</li>
 * <li>To get a smooth trend for life expectancy, you need to have at least a
 * few people for every year of birth. It might be a good idea to consider only
 * people born after the year 1200 to make sure that you have enough precise
 * data.</li>
 * </ul>
 *
 * @author Markus Kroetzsch
 *
 */
public class TutorialDocumentProcessor implements EntityDocumentProcessor {
	private long countItems = 0;
	private long countProperties = 0;

	/**
	 * Processes one item document. This is often the main workhorse that
	 * gathers the data you are interested in. You can modify this code as you
	 * wish.
	 */
	@Override
	public void processItemDocument(ItemDocument itemDocument) {
		this.countItems++;

		// Do some printing for demonstration/debugging.
		// Only print at most 50 items (or it would get too slow).
		if (this.countItems < 10) {
			System.out.println(itemDocument);
		} else if (this.countItems == 10) {
			System.out.println("*** I won't print any further items.\n"
					+ "*** We will never finish if we print all the items.\n"
					+ "*** Maybe remove this debug output altogether.");
		}
	}

	/**
	 * Processes one property document. Property documents mainly tell you the
	 * name and datatype of properties. It can be useful to process all
	 * properties first to store data about them that is useful when processing
	 * items. There are not very many properties (about 1100 as of August 2014),
	 * so it is safe to store all their data for later use.
	 */
	@Override
	public void processPropertyDocument(PropertyDocument propertyDocument) {
		this.countProperties++;

		// For testing; disable when no longer needed:
		if (this.countProperties < 10) {
			System.out.println(propertyDocument);
		} else if (this.countItems == 10) {
			System.out
					.println("*** I won't print any further properties.\n"
							+ "*** Otherwise you would see only properties and no items.\n"
							+ "*** Maybe remove this debug output altogether.");
		}
	}

	/**
	 * Stores the processing results in a file. CSV (comma separated values) is
	 * a simple format that makes sense for such tasks. It can be imported
	 * easily into spreadsheet tools to generate diagrams from the data.
	 */
	public void storeResults() {
		System.out.println("Processed " + countItems + " items and "
				+ countProperties + " properties in total.");
		System.out.println("Storing data ...");

		try (PrintStream out = new PrintStream(new FileOutputStream(
				"tutorial-results.csv"))) {
			// Two simple entries for demonstration purposes.
			// Use your own code when you have more interesting data.
			out.println("count of items," + countItems);
			out.println("count of properties," + countProperties);
		} catch (IOException e) {
			System.out.println("Oops, I could not write the file: "
					+ e.toString());
		}

		System.out.println("... data stored.");

	}

}
