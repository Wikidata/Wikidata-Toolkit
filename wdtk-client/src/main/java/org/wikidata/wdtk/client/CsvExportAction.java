package org.wikidata.wdtk.client;

/*
 * #%L
 * Wikidata Toolkit Command-line Tool
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

import java.io.IOException;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wikidata.wdtk.datamodel.interfaces.DatatypeIdValue;
import org.wikidata.wdtk.datamodel.interfaces.EntityDocument;
import org.wikidata.wdtk.datamodel.interfaces.EntityIdValue;
import org.wikidata.wdtk.datamodel.interfaces.GlobeCoordinatesValue;
import org.wikidata.wdtk.datamodel.interfaces.ItemDocument;
import org.wikidata.wdtk.datamodel.interfaces.ItemIdValue;
import org.wikidata.wdtk.datamodel.interfaces.MonolingualTextValue;
import org.wikidata.wdtk.datamodel.interfaces.NoValueSnak;
import org.wikidata.wdtk.datamodel.interfaces.PropertyDocument;
import org.wikidata.wdtk.datamodel.interfaces.PropertyIdValue;
import org.wikidata.wdtk.datamodel.interfaces.QuantityValue;
import org.wikidata.wdtk.datamodel.interfaces.SiteLink;
import org.wikidata.wdtk.datamodel.interfaces.Statement;
import org.wikidata.wdtk.datamodel.interfaces.StatementDocument;
import org.wikidata.wdtk.datamodel.interfaces.StatementGroup;
import org.wikidata.wdtk.datamodel.interfaces.StringValue;
import org.wikidata.wdtk.datamodel.interfaces.TermedDocument;
import org.wikidata.wdtk.datamodel.interfaces.TimeValue;
import org.wikidata.wdtk.datamodel.interfaces.Value;
import org.wikidata.wdtk.datamodel.interfaces.ValueSnak;
import org.wikidata.wdtk.wikibaseapi.WikibaseDataFetcher;

public class CsvExportAction extends DumpProcessingOutputAction {

	static final Logger logger = LoggerFactory.getLogger(CsvExportAction.class);

	public static final String OPTION_CSV_COLUMNS = "csvcolumns";
	public static final String OPTION_CSV_QUERY = "csvquery";
	public static final String OPTION_CSV_LANGUAGE = "csvlang";
	public static final String OPTION_CSV_MIGA = "csvmiga";

	class CsvColumn {
		final static int TYPE_PROPERTY = 0;
		final static int TYPE_LABEL = 1;
		final static int TYPE_DESCRIPTION = 2;
		final static int TYPE_ALIAS = 3;
		final static int TYPE_SITE = 4;

		public CsvColumn(int type, String key) {
			this.type = type;
			this.key = key;
		}

		final int type;
		final String key;
	}

	String selectionPropertyId = null;
	String selectionItemId = null;
	String mainLanguage = "en";
	final List<CsvColumn> columns = new ArrayList<>();
	final List<String> propertyIds = new ArrayList<>();
	boolean needsSites = false;
	boolean migaSupport = false;

	static Map<String, String> entityLabels = new HashMap<>();

	final Map<String, PropertyDocument> propertyDocuments = new HashMap<>();

	final WikibaseDataFetcher wikibaseDataFetcher = new WikibaseDataFetcher();

	int entityCount;

	PrintStream out;

	@Override
	public boolean setOption(String option, String value) {
		if (super.setOption(option, value)) {
			return true;
		}

		switch (option) {
		case OPTION_CSV_COLUMNS:
			setColumnOption(value);
			return true;
		case OPTION_CSV_QUERY:
			setQueryOption(value);
			return true;
		case OPTION_CSV_LANGUAGE:
			this.mainLanguage = value;
			return true;
		case OPTION_CSV_MIGA:
			if ("true".equals(value)) {
				this.migaSupport = true;
			}
			return true;
		default:
			return false;
		}
	}

	@Override
	public boolean needsSites() {
		return this.needsSites;
	}

	@Override
	public boolean isReady() {
		return true;
	}

	@Override
	public void open() {
		this.entityCount = 0;

		fetchPropertyDocuments();

		try {
			openOutputStream();
		} catch (IOException e) {
			// TODO better add proper exceptions to open() declaration
			throw new RuntimeException(e.getMessage(), e);
		}

		writeHeader();
		if (this.migaSupport) {
			writeMigaSchema();
		}

		this.wikibaseDataFetcher.getFilter().setLanguageFilter(
				Collections.<String> singleton(this.mainLanguage));
		this.wikibaseDataFetcher.getFilter().setPropertyFilter(
				Collections.<PropertyIdValue> emptySet());
		this.wikibaseDataFetcher.getFilter().setSiteLinkFilter(
				Collections.<String> emptySet());
	}

	private void writeMigaSchema() {
		try (PrintStream schemaOut = new PrintStream(getOutputStream(false,
				insertDumpInformation(getFinalOutputDestination() + ".ini"),
				DumpProcessingOutputAction.COMPRESS_NONE))) {

			StringBuilder migaDeclarations = new StringBuilder();
			boolean hasNameProperty = false;
			for (CsvColumn column : this.columns) {
				switch (column.type) {
				case CsvColumn.TYPE_PROPERTY:
					PropertyDocument pd = this.propertyDocuments
							.get(column.key);
					migaDeclarations
							.append(pd.getLabels().get(this.mainLanguage)
									.getText()
									+ " = "
									+ getMigaDatatype(pd.getDatatype().getIri())
									+ "\n");
					break;
				case CsvColumn.TYPE_LABEL:
					if (hasNameProperty) {
						migaDeclarations.append("Label " + column.key
								+ " = Text\n");
					} else {
						hasNameProperty = true;
						migaDeclarations.append("Label " + column.key
								+ " = Name\n");
					}
					break;
				case CsvColumn.TYPE_ALIAS:
					migaDeclarations
							.append("Alias " + column.key + " = Text\n");
					break;
				case CsvColumn.TYPE_DESCRIPTION:
					migaDeclarations.append("Description " + column.key
							+ " = Text\n");
					break;
				case CsvColumn.TYPE_SITE:
					migaDeclarations.append("Site " + column.key + " = URL\n");
					break;
				}
			}

			schemaOut.println("[Miga project]");
			if (hasNameProperty) {
				schemaOut.println("Id = Text");
			} else {
				schemaOut.println("Id = Name");
			}
			schemaOut.print(migaDeclarations.toString());
			schemaOut.println();
		} catch (IOException e) {
			logger.error("Failed to write Miga schema file: " + e.toString());
		}
	}

	private String getMigaDatatype(String iri) {
		switch (iri) {
		case DatatypeIdValue.DT_COMMONS_MEDIA:
			return "Image URL";
		case DatatypeIdValue.DT_GLOBE_COORDINATES:
			return "Coordinates";
		case DatatypeIdValue.DT_QUANTITY:
			return "Number";
		case DatatypeIdValue.DT_TIME:
			return "Date";
		case DatatypeIdValue.DT_URL:
			return "URL";
		case DatatypeIdValue.DT_ITEM:
		case DatatypeIdValue.DT_PROPERTY:
		case DatatypeIdValue.DT_MONOLINGUAL_TEXT:
		case DatatypeIdValue.DT_STRING:
		default:
			return "Text";
		}

	}

	@Override
	public void close() {
		this.out.close();
		super.close();
		logger.info("Finished CSV export of " + this.entityCount + " entities.");
	}

	@Override
	public void processItemDocument(ItemDocument itemDocument) {
		if (matchesQuery(itemDocument)) {
			this.entityCount++;
			writeValuesForDocument(itemDocument);
		}
	}

	@Override
	public void processPropertyDocument(PropertyDocument propertyDocument) {
		// TODO Auto-generated method stub
	}

	private void fetchPropertyDocuments() {
		if (!this.propertyIds.isEmpty()) {
			Map<String, EntityDocument> entities = this.wikibaseDataFetcher
					.getEntityDocuments(this.propertyIds);
			for (EntityDocument entityDocument : entities.values()) {
				if (entityDocument instanceof PropertyDocument) {
					this.propertyDocuments.put(entityDocument.getEntityId()
							.getId(), (PropertyDocument) entityDocument);
				}
			}
		}

		for (String propertyId : this.propertyIds) {
			if (!this.propertyDocuments.containsKey(propertyId)) {
				throw new RuntimeException("Could not fine property \""
						+ propertyId + "\" online. Emergency stop.");
			}
			logger.info("Found property "
					+ propertyId
					+ ": "
					+ this.propertyDocuments.get(propertyId).getLabels()
							.get("en").getText());
		}
	}

	private void openOutputStream() throws IOException {
		this.out = new PrintStream(getOutputStream(this.useStdOut,
				insertDumpInformation(getFinalOutputDestination()),
				this.compressionType));
	}

	private String getFinalOutputDestination() {
		if (this.outputDestination != null) {
			return this.outputDestination;
		} else {
			return "{PROJECT}-{DATE}.csv";
		}
	}

	private boolean matchesQuery(StatementDocument document) {
		for (StatementGroup statementGroup : document.getStatementGroups()) {
			if (!this.selectionPropertyId.equals(statementGroup.getProperty()
					.getId())) {
				continue;
			}

			for (Statement statement : statementGroup.getStatements()) {
				if (statement.getClaim().getMainSnak() instanceof NoValueSnak) {
					continue;
				}
				if (this.selectionItemId == null) {
					return true;
				}
				if (statement.getClaim().getMainSnak() instanceof ValueSnak) {
					Value value = ((ValueSnak) statement.getClaim()
							.getMainSnak()).getValue();
					if (value instanceof ItemIdValue
							&& ((ItemIdValue) value).getId().equals(
									this.selectionItemId)) {
						return true;
					}
				}
			}
		}

		return false;
	}

	private void setQueryOption(String value) {
		String[] queryParts = value.split("::", 2);
		this.selectionPropertyId = queryParts[0];
		if (queryParts.length > 1) {
			this.selectionItemId = queryParts[1];
		}
	}

	private void setColumnOption(String value) {
		for (String columnSpec : value.split(",")) {
			CsvColumn column = makeCsvColumn(columnSpec);
			if (column != null) {
				this.columns.add(column);
			}
		}
	}

	private CsvColumn makeCsvColumn(String columnSpec) {
		if (columnSpec.matches("^P[1-9][0-9]*$")) {
			this.propertyIds.add(columnSpec);
			return new CsvColumn(CsvColumn.TYPE_PROPERTY, columnSpec);
		} else if (columnSpec.startsWith("label:")) {
			return new CsvColumn(CsvColumn.TYPE_LABEL, columnSpec.substring(6));
		} else if (columnSpec.startsWith("desc:")) {
			return new CsvColumn(CsvColumn.TYPE_DESCRIPTION,
					columnSpec.substring(5));
		} else if (columnSpec.startsWith("alias:")) {
			return new CsvColumn(CsvColumn.TYPE_ALIAS, columnSpec.substring(6));
		} else if (columnSpec.startsWith("site:")) {
			this.needsSites = true;
			return new CsvColumn(CsvColumn.TYPE_SITE, columnSpec.substring(5));
		} else {
			logger.error("CSV column specification \"" + columnSpec
					+ "\" was not understood.");
			return null;
		}
	}

	private static Value getPropertyValue(StatementDocument statementDocument,
			String propertyId) {
		for (StatementGroup sg : statementDocument.getStatementGroups()) {
			if (!propertyId.equals(sg.getProperty().getId())) {
				continue;
			}

			for (Statement s : sg.getStatements()) {
				if (s.getClaim().getMainSnak() instanceof ValueSnak) {
					return ((ValueSnak) s.getClaim().getMainSnak()).getValue();
				}
			}
		}

		return null;
	}

	private void writeHeader() {
		this.out.print("Id");
		for (CsvColumn column : this.columns) {
			this.out.print(',');

			switch (column.type) {
			case CsvColumn.TYPE_PROPERTY:
				out.print(this.propertyDocuments.get(column.key).getLabels()
						.get(this.mainLanguage).getText());
				break;
			case CsvColumn.TYPE_LABEL:
				out.print("Label " + column.key);
				break;
			case CsvColumn.TYPE_ALIAS:
				out.print("Alias " + column.key);
				break;
			case CsvColumn.TYPE_DESCRIPTION:
				out.print("Description " + column.key);
				break;
			case CsvColumn.TYPE_SITE:
				out.print("Site " + column.key);
				break;
			}
		}
		out.println();
	}

	private void writeValuesForDocument(ItemDocument document) {

		this.out.print(document.getEntityId().getId());
		for (CsvColumn column : this.columns) {
			this.out.print(',');

			switch (column.type) {
			case CsvColumn.TYPE_PROPERTY:
				writePropertyValue(document, column.key);
				break;
			case CsvColumn.TYPE_LABEL:
				writeMtvText(document.getLabels().get(column.key));
				break;
			case CsvColumn.TYPE_DESCRIPTION:
				writeMtvText(document.getDescriptions().get(column.key));
				break;
			case CsvColumn.TYPE_ALIAS:
				// TODO
				// writeMtvText(document.getAliases().get(column.key).get(0));
				break;
			case CsvColumn.TYPE_SITE:
				if (column.key.equals(this.project)) {
					this.out.print(csvStringEscape(this.sites.getPageUrl(
							this.project, document.getEntityId().getId())));
				} else {
					writeSiteLink(document.getSiteLinks().get(column.key));
				}
				break;
			}
		}
		this.out.println();
	}

	private void writePropertyValue(StatementDocument statementDocument,
			String propertyId) {
		Value value = getPropertyValue(statementDocument, propertyId);
		if (value == null) {
			return;
		}

		switch (this.propertyDocuments.get(propertyId).getDatatype().getIri()) {
		case DatatypeIdValue.DT_ITEM:
		case DatatypeIdValue.DT_PROPERTY:
			writeEntityIdValue((EntityIdValue) value);
			return;
		case DatatypeIdValue.DT_STRING:
		case DatatypeIdValue.DT_URL:
			this.out.print(csvStringEscape(((StringValue) value).getString()));
			return;
		case DatatypeIdValue.DT_COMMONS_MEDIA:
			writeImageValue((StringValue) value);
			return;
		case DatatypeIdValue.DT_GLOBE_COORDINATES:
			writeGlobeCoordinatesValue((GlobeCoordinatesValue) value);
			return;
		case DatatypeIdValue.DT_TIME:
			writeTimeValue((TimeValue) value);
			return;
		case DatatypeIdValue.DT_QUANTITY:
			this.out.print(((QuantityValue) value).getNumericValue().toString());
			return;
		case DatatypeIdValue.DT_MONOLINGUAL_TEXT:
			this.out.print(csvStringEscape(((MonolingualTextValue) value)
					.getText()));
			return;
		}
	}

	private void writeEntityIdValue(EntityIdValue value) {
		// this.out.println(value.getId());
		if (!entityLabels.containsKey(value.getId())) {
			logger.info("Fetching label for " + value.getId());
			EntityDocument d = this.wikibaseDataFetcher.getEntityDocument(value
					.getId());
			if (d instanceof TermedDocument
					&& ((TermedDocument) d).getLabels().containsKey(
							this.mainLanguage)) {
				entityLabels.put(value.getId(), ((TermedDocument) d)
						.getLabels().get(this.mainLanguage).getText());
			} else {
				entityLabels.put(value.getId(), value.getId());
			}
		}

		this.out.print(csvStringEscape(entityLabels.get(value.getId())));
	}

	private void writeSiteLink(SiteLink siteLink) {
		if (siteLink == null) {
			return;
		}
		this.out.print(csvStringEscape(this.sites.getSiteLinkUrl(siteLink)));
	}

	private void writeMtvText(MonolingualTextValue monolingualTextValue) {
		if (monolingualTextValue == null) {
			return;
		}
		this.out.print(csvStringEscape(monolingualTextValue.getText()));
	}

	private void writeImageValue(StringValue value) {
		if (value == null) {
			this.out.print("\"http://commons.wikimedia.org/w/thumb.php?f=MA_Route_blank.svg&w=50\"");
		} else {
			try {
				String imageFileEncoded;
				imageFileEncoded = URLEncoder.encode(
						value.getString().replace(" ", "_"), "utf-8");
				// Keep special title symbols unescaped:
				imageFileEncoded = imageFileEncoded.replace("%3A", ":")
						.replace("%2F", "/");
				this.out.print(csvStringEscape("http://commons.wikimedia.org/w/thumb.php?f="
						+ imageFileEncoded + "&w=50"));
			} catch (UnsupportedEncodingException e) {
				throw new RuntimeException(
						"Your JRE does not support UTF-8 encoding. Srsly?!", e);
			}
		}
	}

	private void writeGlobeCoordinatesValue(GlobeCoordinatesValue value) {
		this.out.print("\"");
		this.out.print(value.getLatitude() / GlobeCoordinatesValue.PREC_DEGREE);
		this.out.print(",");
		this.out.print(value.getLongitude() / GlobeCoordinatesValue.PREC_DEGREE);
		this.out.print("\"");
	}

	private void writeTimeValue(TimeValue value) {
		this.out.print("\"");
		this.out.print(value.getYear());
		this.out.print("-");
		this.out.print(value.getMonth());
		this.out.print("-");
		this.out.print(value.getDay());
		this.out.print("\"");
	}

	private static String csvStringEscape(String string) {
		if (string == null) {
			return "";
		} else {
			return "\"" + string.replace("\"", "\"\"") + "\"";
		}
	}

}
