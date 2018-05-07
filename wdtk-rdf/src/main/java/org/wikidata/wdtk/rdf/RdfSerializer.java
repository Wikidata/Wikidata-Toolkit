package org.wikidata.wdtk.rdf;

/*
 * #%L
 * Wikidata Toolkit RDF
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

import org.eclipse.rdf4j.rio.RDFFormat;
import org.eclipse.rdf4j.rio.RDFHandlerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wikidata.wdtk.datamodel.interfaces.EntityDocument;
import org.wikidata.wdtk.datamodel.interfaces.EntityDocumentDumpProcessor;
import org.wikidata.wdtk.datamodel.interfaces.ItemDocument;
import org.wikidata.wdtk.datamodel.interfaces.PropertyDocument;
import org.wikidata.wdtk.datamodel.interfaces.Sites;

/**
 * This class implements {@link EntityDocumentDumpProcessor} to provide a RDF
 * serializer to render RDF graphs of {@link EntityDocument} objects.
 *
 * @author Michael Günther
 *
 */
public class RdfSerializer implements EntityDocumentDumpProcessor {

	static final Logger logger = LoggerFactory.getLogger(RdfSerializer.class);

	public static final int TASK_STATEMENTS = 0x00000001;
	public static final int TASK_SITELINKS = 0x00000002;
	public static final int TASK_DATATYPES = 0x00000004;
	public static final int TASK_PROPERTY_LINKS = 0x00000080;
	public static final int TASK_LABELS = 0x00000010;
	public static final int TASK_DESCRIPTIONS = 0x00000020;
	public static final int TASK_ALIASES = 0x00000040;
	public static final int TASK_TERMS = TASK_LABELS | TASK_DESCRIPTIONS
			| TASK_ALIASES;
	public static final int TASK_ALL_EXACT_DATA = TASK_TERMS | TASK_STATEMENTS
			| TASK_SITELINKS | TASK_DATATYPES | TASK_PROPERTY_LINKS;

	public static final int TASK_SIMPLE_STATEMENTS = 0x00040000;

	public static final int TASK_ITEMS = 0x00000100;
	public static final int TASK_PROPERTIES = 0x00000200;
	public static final int TASK_ALL_ENTITIES = TASK_ITEMS | TASK_PROPERTIES;

	final OutputStream output;
	final RdfConverter rdfConverter;
	final RdfWriter rdfWriter;

	/**
	 * Creates a new RDF serializer for the specified format and output stream.
	 *
	 * @param format
	 *            RDF format, such as RDFFormat.TURTLE
	 * @param output
	 *            the output stream to write to
	 * @param sites
	 *            information about site links
	 */
	public RdfSerializer(RDFFormat format, OutputStream output, Sites sites,
			PropertyRegister propertyRegister) {
		this.output = output;
		this.rdfWriter = new RdfWriter(format, output);
		this.rdfConverter = new RdfConverter(this.rdfWriter, sites,
				propertyRegister);
	}

	/**
	 * Sets the tasks that should be performed during export. The value should
	 * be a combination of flags such as {@link RdfSerializer#TASK_STATEMENTS}.
	 *
	 * @param tasks
	 *            the tasks to be performed
	 */
	public void setTasks(int tasks) {
		this.rdfConverter.setTasks(tasks);
	}

	/**
	 * Returns the tasks that should be performed during export. The value
	 * should be a combination of flags such as
	 * {@link RdfSerializer#TASK_STATEMENTS}.
	 *
	 * @return tasks to be performed
	 */
	public int getTasks() {
		return this.rdfConverter.getTasks();
	}

	/**
	 * Returns the number of triples that have been written so far.
	 *
	 * @return number of triples
	 */
	public long getTripleCount() {
		return this.rdfWriter.getTripleCount();
	}

	@Override
	public void open() {
		try {
			this.rdfWriter.start();
			this.rdfConverter.writeNamespaceDeclarations();
			this.rdfConverter.writeBasicDeclarations();
		} catch (RDFHandlerException e) { // we cannot recover here
			throw new RuntimeException(e.toString(), e);
		}
	}

	@Override
	public void processItemDocument(ItemDocument itemDocument) {
		try {
			this.rdfConverter.writeItemDocument(itemDocument);
		} catch (RDFHandlerException e) { // we cannot recover here
			throw new RuntimeException(e.toString(), e);
		}
	}

	@Override
	public void processPropertyDocument(PropertyDocument propertyDocument) {
		try {
			this.rdfConverter.writePropertyDocument(propertyDocument);
		} catch (RDFHandlerException e) { // we cannot recover here
			throw new RuntimeException(e.toString(), e);
		}
	}

	@Override
	public void close() {
		try {
			this.rdfWriter.finish();
		} catch (RDFHandlerException e) { // we cannot recover here
			throw new RuntimeException(e.toString(), e);
		}
		try {
			this.output.close();
		} catch (IOException e) {
			throw new RuntimeException(e.toString(), e);
		}
	}

}
