package org.wikidata.wdtk.examples;

/*
 * #%L
 * Wikidata Toolkit Examples
 * %%
 * Copyright (C) 2014 - 2015 Wikidata Toolkit Developers
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

import java.awt.image.BufferedImage;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.imageio.ImageIO;

import org.wikidata.wdtk.datamodel.interfaces.EntityDocumentProcessor;
import org.wikidata.wdtk.datamodel.interfaces.GlobeCoordinatesValue;
import org.wikidata.wdtk.datamodel.interfaces.ItemDocument;
import org.wikidata.wdtk.datamodel.interfaces.Statement;
import org.wikidata.wdtk.datamodel.interfaces.StatementGroup;
import org.wikidata.wdtk.datamodel.interfaces.Value;

/**
 * This example class processes EntityDocuments to create a map image that shows
 * the distribution of items with geographic coordinates on Earth. Several maps
 * are generated, for Wikidata as a whole and for several big Wikipedias.
 * <p>
 * The size of the images, the projects that are included, and the brightness of
 * the maps can be modified in the main method.
 *
 * @author Markus Kroetzsch
 *
 */
public class WorldMapProcessor implements EntityDocumentProcessor {

	/**
	 * The property id that encodes coordinates.
	 */
	static final String COORD_PROPERTY = "P625";

	/**
	 * Colors to use on the color scale, each specificed as {r,g,b}.
	 */
	static int[][] colors = { { 0, 0, 150 }, { 24, 99, 9 }, { 227, 70, 0 },
			{ 255, 214, 30 }, { 255, 255, 255 } };

	/**
	 * The width in pixels of the map image that is created.
	 */
	final int width;
	/**
	 * The height in pixels of the map image that is created.
	 */
	final int height;
	/**
	 * The total number of coordinates encountered so far.
	 */
	int count = 0;

	/**
	 * Value at which the brightest color will be reached.
	 */
	final int topValue;

	/**
	 * All maps for which data is recorded.
	 */
	Set<ValueMap> valueMaps = new HashSet<>();

	/**
	 * Number of articles with coordinates per site.
	 */
	final Map<String, Integer> siteCounts = new HashMap<>();

	/**
	 * Identifier of the globe for which coordinates are gathered.
	 */
	String globe = GlobeCoordinatesValue.GLOBE_EARTH;

	/**
	 * Main method. Processes the whole dump using this processor and writes the
	 * results to a file. To change which dump file to use and whether to run in
	 * offline mode, modify the settings in {@link ExampleHelpers}.
	 *
	 * @param args
	 */
	public static void main(String[] args) {
		ExampleHelpers.configureLogging();
		WorldMapProcessor.printDocumentation();

		int imageWidth = 8 * 360;
		double brightness = 1.0;
		WorldMapProcessor worldMapProcessor = new WorldMapProcessor(imageWidth,
				brightness);
		// worldMapProcessor.setGlobe(GlobeCoordinatesValue.GLOBE_MOON);
		// using the Moon or anything else but Earth might need some brightness
		// adjustment above, and possibly more frequent reporting below

		worldMapProcessor.addSite(null); // all data, no filter
		// Some other sites, ranked by the number of geolocated items they had
		// as of June 2015:
		worldMapProcessor.addSite("enwiki");
		worldMapProcessor.addSite("dewiki");
		worldMapProcessor.addSite("frwiki");
		worldMapProcessor.addSite("plwiki");
		worldMapProcessor.addSite("nlwiki");
		worldMapProcessor.addSite("ruwiki");
		worldMapProcessor.addSite("eswiki");
		worldMapProcessor.addSite("itwiki");
		worldMapProcessor.addSite("zhwiki");
		worldMapProcessor.addSite("ptwiki");
		// worldMapProcessor.addSite("ukwiki");
		// worldMapProcessor.addSite("svwiki");
		// worldMapProcessor.addSite("viwiki");
		// worldMapProcessor.addSite("srwiki");
		// worldMapProcessor.addSite("cawiki");
		// worldMapProcessor.addSite("shwiki");
		// worldMapProcessor.addSite("mswiki");
		// worldMapProcessor.addSite("rowiki");
		// worldMapProcessor.addSite("fawiki");
		// worldMapProcessor.addSite("jawiki");
		// worldMapProcessor.addSite("vowiki");
		// worldMapProcessor.addSite("warwiki");
		// worldMapProcessor.addSite("commonswiki");
		// worldMapProcessor.addSite("arwiki");

		ExampleHelpers.processEntitiesFromWikidataDump(worldMapProcessor);

		worldMapProcessor.writeFinalData();

	}

	/**
	 * Creates a new processor for building world maps.
	 *
	 * @param width
	 *            horizontal size of the world map; the map's height is half of
	 *            this (plus some pixels for printing the scale)
	 * @param brightness
	 *            parameter for scaling up the brightness of colors; the default
	 *            is 1.0; higher values make smaller numbers appear more
	 *            brightly; smaller numbers darken smaller numbers and thus help
	 *            to highlight the biggest concentrations of items
	 */
	public WorldMapProcessor(int width, double brightness) {
		this.width = width;
		this.height = width / 2;
		this.topValue = (int) ((1600 * 360 * 180) / (brightness * this.width * this.height));
	}

	/**
	 * Registers a new site for specific data collection. If null is used as a
	 * site key, then all data is collected.
	 *
	 * @param siteKey
	 *            the site to collect geo data for
	 */
	public void addSite(String siteKey) {
		ValueMap gv = new ValueMap(siteKey);
		this.valueMaps.add(gv);
	}

	/**
	 * Sets the globe on which coordinates should be gathered. This should be an
	 * entity URI, e.g., {@link GlobeCoordinatesValue#GLOBE_EARTH}.
	 *
	 * @param globe
	 */
	public void setGlobe(String globe) {
		this.globe = globe;
	}

	@Override
	public void processItemDocument(ItemDocument itemDocument) {

		for (StatementGroup sg : itemDocument.getStatementGroups()) {
			if (COORD_PROPERTY.equals(sg.getProperty().getId())) {
				for (Statement s : sg) {
					countCoordinateStatement(s, itemDocument);
				}
			}
		}

	}

	/**
	 * Counts the coordinates stored in a single statement for the relevant
	 * property, if they are actually given and valid.
	 *
	 * @param statement
	 * @param itemDocument
	 */
	private void countCoordinateStatement(Statement statement,
			ItemDocument itemDocument) {
		Value value = statement.getValue();
		if (!(value instanceof GlobeCoordinatesValue)) {
			return;
		}

		GlobeCoordinatesValue coordsValue = (GlobeCoordinatesValue) value;
		if (!this.globe.equals((coordsValue.getGlobe()))) {
			return;
		}

		int xCoord = (int) (((coordsValue.getLongitude() + 180.0) / 360.0) * this.width)
				% this.width;
		int yCoord = (int) (((coordsValue.getLatitude() + 90.0) / 180.0) * this.height)
				% this.height;

		if (xCoord < 0 || yCoord < 0 || xCoord >= this.width
				|| yCoord >= this.height) {
			System.out.println("Dropping out-of-range coordinate: "
					+ coordsValue);
			return;
		}

		countCoordinates(xCoord, yCoord, itemDocument);
		this.count += 1;

		if (this.count % 100000 == 0) {
			reportProgress();
			writeImages();
		}
	}

	/**
	 * Counts a single pair of coordinates in all datasets.
	 *
	 * @param xCoord
	 * @param yCoord
	 * @param itemDocument
	 */
	private void countCoordinates(int xCoord, int yCoord,
			ItemDocument itemDocument) {

		for (String siteKey : itemDocument.getSiteLinks().keySet()) {
			this.siteCounts.merge(siteKey, 1, Integer::sum);
		}

		for (ValueMap vm : this.valueMaps) {
			vm.countCoordinates(xCoord, yCoord, itemDocument);
		}
	}

	/**
	 * Writes all collected data to files after processing is finished.
	 */
	public void writeFinalData() {
		reportProgress();
		writeImages();
	}

	/**
	 * Writes image files for all data that was collected and the statistics
	 * file for all sites.
	 */
	private void writeImages() {
		for (ValueMap gv : this.valueMaps) {
			gv.writeImage();
		}

		try (PrintStream out = new PrintStream(
				ExampleHelpers.openExampleFileOuputStream("map-site-count.csv"))) {
			out.println("Site key,Number of geo items");
			out.println("wikidata total," + this.count);
			for (Entry<String, Integer> entry : this.siteCounts.entrySet()) {
				out.println(entry.getKey() + "," + entry.getValue());
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
		System.out.println("*** Wikidata Toolkit: WorldMapProcessor");
		System.out.println("*** ");
		System.out
				.println("*** This program will download and process dumps from Wikidata.");
		System.out
				.println("*** It will collect geographic coordinates from the data to ");
		System.out.println("***create a map that is stored in an image file.");
		System.out.println("*** See source code for further details.");
		System.out
				.println("********************************************************************");
	}

	/**
	 * Prints the progress for all data collections.
	 */
	private void reportProgress() {
		for (ValueMap gv : this.valueMaps) {
			gv.reportProgress();
		}
	}

	/**
	 * Returns a color for a given absolute number that is to be shown on the
	 * map.
	 *
	 * @param value
	 * @return
	 */
	private int getColor(int value) {
		if (value == 0) {
			return 0;
		}

		double scale = Math.log10(value) / Math.log10(this.topValue);
		double lengthScale = Math.min(1.0, scale) * (colors.length - 1);
		int index = 1 + (int) lengthScale;
		if (index == colors.length) {
			index--;
		}
		double partScale = lengthScale - (index - 1);

		int r = (int) (colors[index - 1][0] + partScale
				* (colors[index][0] - colors[index - 1][0]));
		int g = (int) (colors[index - 1][1] + partScale
				* (colors[index][1] - colors[index - 1][1]));
		int b = (int) (colors[index - 1][2] + partScale
				* (colors[index][2] - colors[index - 1][2]));

		r = Math.min(255, r);
		b = Math.min(255, b);
		g = Math.min(255, g);
		return (r << 16) | (g << 8) | b;
	}

	/**
	 * Class to collect the data for one particular map, e.g., for coordinates
	 * of items with German Wikipedia articles. Objects of the class aggregate
	 * all relevant data and finally create the output file for the current
	 * settings.
	 *
	 * @author Markus Kroetzsch
	 *
	 */
	class ValueMap {

		final int[][] values;
		final String siteFilter;
		int count = 0;
		int maxValue = 1; // avoid drawing scale with 0 elements

		/**
		 * Constructor.
		 *
		 * @param siteFilter
		 */
		public ValueMap(String siteFilter) {
			this.values = new int[WorldMapProcessor.this.width][WorldMapProcessor.this.height];
			this.siteFilter = siteFilter;
		}

		/**
		 * Counts the given coordinates, unless the item document is filtered.
		 * It is assumed that the coordinates are in the admissible range.
		 *
		 * @param xCoord
		 * @param yCoord
		 * @param itemDocument
		 */
		public void countCoordinates(int xCoord, int yCoord,
				ItemDocument itemDocument) {
			if (this.siteFilter != null) {
				if (!itemDocument.getSiteLinks().containsKey(this.siteFilter)) {
					return;
				}
			}

			this.count++;
			this.values[xCoord][yCoord] += 1;
			if (this.maxValue < this.values[xCoord][yCoord]) {
				this.maxValue = this.values[xCoord][yCoord];
			}
		}

		/**
		 * Writes the image file for the collected data.
		 */
		public void writeImage() {
			int width = WorldMapProcessor.this.width;
			int height = WorldMapProcessor.this.height;

			BufferedImage image = new BufferedImage(width, height + 13,
					BufferedImage.TYPE_INT_RGB);

			for (int x = 0; x < width; x++) {
				for (int y = 0; y < height; y++) {
					image.setRGB(x, height - 1 - y, getColor(this.values[x][y]));
				}
			}

			int previousValue = 0;
			int scaleMarkStep = 1;
			for (int x = 0; x < width; x++) {
				int value = (int) Math.exp(Math.log(10)
						* Math.log10(Math.max(10, this.maxValue)) * x / width);
				int color = getColor(value);

				if (value / scaleMarkStep > previousValue / scaleMarkStep) {
					if (value / (10 * scaleMarkStep) > previousValue
							/ (10 * scaleMarkStep)) {
						scaleMarkStep = 10 * scaleMarkStep;
					}
					previousValue = value;
					continue;
				}

				for (int y = height + 12; y > height + 3; y--) {
					image.setRGB(x, y, color);
				}

				previousValue = value;
			}

			String fileName = "map-items";
			if (this.siteFilter != null) {
				fileName += "-" + this.siteFilter;
			}
			if (!GlobeCoordinatesValue.GLOBE_EARTH
					.equals(WorldMapProcessor.this.globe)) {
				fileName += "-"
						+ WorldMapProcessor.this.globe
								.substring(WorldMapProcessor.this.globe
										.lastIndexOf('Q'));
			}
			fileName += "-" + width + "x" + height + ".png";

			try (FileOutputStream out = ExampleHelpers
					.openExampleFileOuputStream(fileName)) {
				ImageIO.write(image, "PNG", out);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		/**
		 * Prints the progress of the data collection.
		 */
		public void reportProgress() {
			System.out.print("* Processed " + this.count + " coordinates");
			if (this.siteFilter != null) {
				System.out.print(" for site " + this.siteFilter);
			} else {
				System.out.print(" in total");
			}
			System.out.print(" (max. value: " + this.maxValue + ")");
			System.out.println();
		}

		@Override
		public int hashCode() {
			return ((siteFilter == null) ? 0 : siteFilter.hashCode());
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj) {
				return true;
			}
			if (obj == null) {
				return false;
			}
			if (!(obj instanceof ValueMap)) {
				return false;
			}
			return this.siteFilter.equals(((ValueMap) obj).siteFilter);
		}

	}
}
