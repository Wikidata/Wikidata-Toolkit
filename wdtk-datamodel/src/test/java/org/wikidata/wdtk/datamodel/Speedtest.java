package org.wikidata.wdtk.datamodel;

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

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.wikidata.wdtk.datamodel.implementation.DataObjectFactoryImpl;
import org.wikidata.wdtk.datamodel.interfaces.DataObjectFactory;
import org.wikidata.wdtk.datamodel.interfaces.ItemDocument;
import org.wikidata.wdtk.datamodel.interfaces.ItemIdValue;
import org.wikidata.wdtk.datamodel.interfaces.MonolingualTextValue;
import org.wikidata.wdtk.datamodel.interfaces.SiteLink;
import org.wikidata.wdtk.datamodel.interfaces.StatementGroup;
import org.wikidata.wdtk.datamodel.json.JsonSerializer;
import org.wikidata.wdtk.datamodel.json.jackson.documents.ItemDocumentImpl;
import org.wikidata.wdtk.util.Timer;

import com.fasterxml.jackson.databind.ObjectMapper;

public class Speedtest {

	private DataObjectFactory factory = new DataObjectFactoryImpl();
	private String baseIri = "Speedtest";

	private class NullOutputStream extends OutputStream {

		@Override
		public void write(int arg0) throws IOException {
		}

	}

	@Test
	public void runSerializationTest() throws IOException {
		ItemDocument document = this.createItemDocument();
		
		int runs = 1000;

		// traditional
		Timer t = new Timer(baseIri, Timer.RECORD_ALL);
		JsonSerializer traditional = new JsonSerializer(new NullOutputStream());
		for (int i = 0; i < runs; i++) {
			t.start();
			traditional.processItemDocument(document);
			t.stop();
		}
		
		System.err.println("Traditional");
		System.err.println("Avg. CPU: \t" + t.getAvgCpuTime());
		System.err.println("Avg. Wall: \t" + t.getAvgWallTime());
		System.err.println("Tot. CPU: \t" + t.getTotalCpuTime());
		System.err.println("Tot. Wall: \t" + t.getTotalWallTime());
		
		// jackson (incl. converting)
		
		t = new Timer(baseIri, Timer.RECORD_ALL);
		ObjectMapper mapper = new ObjectMapper();
		for (int i = 0; i < runs; i++) {
			t.start();
			ItemDocumentImpl altDocument = new ItemDocumentImpl(document);
			mapper.writeValueAsString(altDocument);
			t.stop();
		}
		
		System.err.println("Jackson (incl. conv.)");
		System.err.println("Avg. CPU: \t" + t.getAvgCpuTime());
		System.err.println("Avg. Wall: \t" + t.getAvgWallTime());
		System.err.println("Tot. CPU: \t" + t.getTotalCpuTime());
		System.err.println("Tot. Wall: \t" + t.getTotalWallTime());
		
		// jackson (excl converting)
		
		t = new Timer(baseIri, Timer.RECORD_ALL);
		mapper = new ObjectMapper();
		ItemDocumentImpl altDocument = new ItemDocumentImpl(document);
		for (int i = 0; i < runs; i++) {
			t.start();
			mapper.writeValueAsString(altDocument);
			t.stop();
		}
		
		System.err.println("Jackson (excl. conv.)");
		System.err.println("Avg. CPU: \t" + t.getAvgCpuTime());
		System.err.println("Avg. Wall: \t" + t.getAvgWallTime());
		System.err.println("Tot. CPU: \t" + t.getTotalCpuTime());
		System.err.println("Tot. Wall: \t" + t.getTotalWallTime());
		
		// jackson (deserialization)
		
		String token = mapper.writeValueAsString(altDocument);
		
		t = new Timer(baseIri, Timer.RECORD_ALL);
		mapper = new ObjectMapper();
		for (int i = 0; i < runs; i++) {
			t.start();
			mapper.readValue(token, ItemDocumentImpl.class);
			t.stop();
		}
		
		System.err.println("Jackson (deserialization)");
		System.err.println("Avg. CPU: \t" + t.getAvgCpuTime());
		System.err.println("Avg. Wall: \t" + t.getAvgWallTime());
		System.err.println("Tot. CPU: \t" + t.getTotalCpuTime());
		System.err.println("Tot. Wall: \t" + t.getTotalWallTime());
		
	}

	private ItemDocument createItemDocument() {

		ItemIdValue itemIdValue = factory.getItemIdValue("Q1", baseIri);
		List<MonolingualTextValue> labels = this.createMltvList();
		List<MonolingualTextValue> descriptions = this.createMltvList();
		List<MonolingualTextValue> aliases = this.createMltvList();
		List<StatementGroup> statementGroups = this.createStatementGroups();
		Map<String, SiteLink> siteLinks = this.createSiteLinks();
		return factory.getItemDocument(itemIdValue, labels, descriptions,
				aliases, statementGroups, siteLinks);

	}

	/**
	 * Creates a Map with 1000 entries
	 * 
	 * @return
	 */
	private Map<String, SiteLink> createSiteLinks() {
		Map<String, SiteLink> returnMap = new HashMap<>();

		for (int i = 0; i < 1000; i++) {
			returnMap.put("key" + i,
					factory.getSiteLink("title" + i, "site" + i, new LinkedList<String>()));
		}

		return returnMap;
	}

	private List<StatementGroup> createStatementGroups() {
		List<StatementGroup> returnList = new LinkedList<>();
		return returnList;
	}

	/**
	 * Create a List with 1000 Mltvs
	 * 
	 * @return
	 */
	private List<MonolingualTextValue> createMltvList() {

		List<MonolingualTextValue> returnList = new ArrayList<>(1000);

		for (int i = 0; i < 1000; i++) {
			returnList.add(factory.getMonolingualTextValue("text" + i, "lang"
					+ i));
		}

		return returnList;
	}

}
