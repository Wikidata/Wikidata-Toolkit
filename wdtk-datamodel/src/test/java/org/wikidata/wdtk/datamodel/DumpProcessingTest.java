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

import java.io.File;
import java.io.IOException;

import javax.swing.JFileChooser;

import org.wikidata.wdtk.datamodel.json.jackson.documents.EntityDocumentImpl;
import org.wikidata.wdtk.datamodel.json.jackson.documents.ItemDocumentImpl;
import org.wikidata.wdtk.datamodel.json.jackson.documents.PropertyDocumentImpl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;

/**
 * This test is intended to be fed an external JSON dump. It will then try to
 * process it and count JSON objects where the processing failed for some
 * reason.
 * 
 * @author Fredo Erxleben
 *
 */
public class DumpProcessingTest {
	
	public static void main(String[] args){
		
		ObjectMapper mapper = new ObjectMapper();
		ObjectReader reader = mapper.reader(EntityDocumentImpl.class);
		//ObjectReader propReader = mapper.reader(PropertyDocumentImpl.class);
		
		File dumpFile;
		
		JFileChooser fileChooser = new JFileChooser();
		
		if(!(fileChooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION)){
			return;
		}
		// file selected
		dumpFile = fileChooser.getSelectedFile();
		
		System.out.println("Start reading...");
		
		int processed = 0;
		int docs = 0;
		int props = 0;
		int lastReport = 0;
		 try {
			MappingIterator<EntityDocumentImpl> documentIter = reader.readValues(dumpFile);
			
			while(documentIter.hasNextValue()){
				EntityDocumentImpl document = documentIter.nextValue();
				if(document != null){ // TODO do more useful and thorough check here
					processed++;
					if(document instanceof ItemDocumentImpl){
						docs++;
					} else if (document instanceof PropertyDocumentImpl){
						props++;
					}
				}
				if(processed > lastReport + 10000){
					System.out.println(processed/1000 + "K: " + docs/1000 + "K docs, " + props + " props");
					lastReport = processed;
				}
			}
			
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
