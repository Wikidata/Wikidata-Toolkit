package org.wikidata.wdtk.dumpfiles;

/*
 * #%L
 * Wikidata Toolkit Dump File Handling
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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wikidata.wdtk.datamodel.implementation.SitesImpl;
import org.wikidata.wdtk.datamodel.interfaces.Sites;

/**
 * This class processes dump files that contain the SQL dump of the MediaWiki <a
 * href="https://www.mediawiki.org/wiki/Manual:Sites_table">sites table</a>.
 * <p>
 * The class expects all URLs in the dump to be protocol-relative (i.e.,
 * starting with "//" rather than with "http://" or "https://") and it will
 * prepend "http:".
 *
 * @author Markus Kroetzsch
 *
 */
public class MwSitesDumpFileProcessor implements MwDumpFileProcessor {

	static final Logger logger = LoggerFactory
			.getLogger(MwSitesDumpFileProcessor.class);

	final SitesImpl sites = new SitesImpl();

	/**
	 * Returns the information about sites that has been extracted from the dump
	 * file(s) processed earlier.
	 *
	 * @return the sites information
	 */
	public Sites getSites() {
		return this.sites;
	}

	@Override
	public void processDumpFileContents(InputStream inputStream,
			MwDumpFile dumpFile) {

		logger.info("Processing sites dump file " + dumpFile.toString());

		BufferedReader bufferedReader = new BufferedReader(
				new InputStreamReader(inputStream));

		try {
			String line;
			while ((line = bufferedReader.readLine()) != null) {
				if (line.startsWith("INSERT INTO `sites` VALUES")) {
					Matcher matcher = Pattern.compile("[(][^)]*[)]").matcher(
							line.substring(27, line.length() - 1));
					while (matcher.find()) {
						processSiteRow(matcher.group());
					}
					break; // stop after finding rows
				}
			}
		} catch (IOException e) {
			MwSitesDumpFileProcessor.logger
					.error("IO Error when processing dump of sites table: "
							+ e.toString());
		}
	}

	/**
	 * Processes a row of the sites table and stores the site information found
	 * therein.
	 *
	 * @param siteRow
	 *            string serialisation of a sites table row as found in the SQL
	 *            dump
	 */
	void processSiteRow(String siteRow) {
		String[] row = getSiteRowFields(siteRow);

		String filePath = "";
		String pagePath = "";

		String dataArray = row[8].substring(row[8].indexOf('{'),
				row[8].length() - 2);

		// Explanation for the regular expression below:
		// "'{' or ';'" followed by either
		// "NOT: ';', '{', or  '}'" repeated one or more times; or
		// "a single '}'"
		// The first case matches ";s:5:\"paths\""
		// but also ";a:2:" in "{s:5:\"paths\";a:2:{s:9:\ ...".
		// The second case matches ";}" which terminates (sub)arrays.
		Matcher matcher = Pattern.compile("[{;](([^;}{][^;}{]*)|[}])").matcher(
				dataArray);
		String prevString = "";
		String curString = "";
		String path = "";
		boolean valuePosition = false;

		while (matcher.find()) {
			String match = matcher.group().substring(1);
			if (match.length() == 0) {
				valuePosition = false;
				continue;
			}
			if (match.charAt(0) == 's') {
				valuePosition = !valuePosition && !"".equals(prevString);
				curString = match.substring(match.indexOf('"') + 1,
						match.length() - 2);
			} else if (match.charAt(0) == 'a') {
				valuePosition = false;
				path = path + "/" + prevString;
			} else if ("}".equals(match)) {
				valuePosition = false;
				path = path.substring(0, path.lastIndexOf('/'));
			}

			if (valuePosition && "file_path".equals(prevString)
					&& "/paths".equals(path)) {
				filePath = curString;
			} else if (valuePosition && "page_path".equals(prevString)
					&& "/paths".equals(path)) {
				pagePath = curString;
			}

			prevString = curString;
			curString = "";
		}

		MwSitesDumpFileProcessor.logger.debug("Found site data \"" + row[1]
				+ "\" (group \"" + row[3] + "\", language \"" + row[5]
				+ "\", type \"" + row[2] + "\")");
		this.sites.setSiteInformation(row[1], row[3], row[5], row[2], filePath,
				pagePath);
	}

	/**
	 * Extract the individual fields for one row in the sites table. The entries
	 * are encoded by position, with the following meaning: 0: site_id, 1:
	 * site_global_key, 2: site_type, 3: site_group, 4: site_source 5:
	 * site_language, 6: site_protocol, 7: site_domain, 8: site_data, 9:
	 * site_forward, 10: site_config. The method assumes that this is the layout
	 * of the table, which is the case in MediaWiki 1.21 and above.
	 *
	 * @param siteRow
	 *            the string representation of a row in the sites table, with
	 *            the surrounding parentheses
	 * @return an array with the individual entries
	 */
	String[] getSiteRowFields(String siteRow) {
		String[] siteRowFields = new String[11];

		Matcher matcher = Pattern.compile("[(,](['][^']*[']|[^'][^),]*)")
				.matcher(siteRow);
		int columnIndex = 0;
		while (matcher.find()) {
			String field = matcher.group().substring(1);
			if (field.charAt(0) == '\'') {
				field = field.substring(1, field.length() - 1);
			}

			siteRowFields[columnIndex] = field;
			// ... will throw an exception if there are more fields than
			// expected; this is fine.
			columnIndex++;
		}
		return siteRowFields;
	}
}
