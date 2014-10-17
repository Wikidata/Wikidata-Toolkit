package org.wikidata.wdtk.clt;

/*
 * #%L
 * Wikidata Toolkit Command-line Tool
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

import org.openrdf.rio.RDFFormat;
import org.wikidata.wdtk.datamodel.interfaces.Sites;
import org.wikidata.wdtk.dumpfiles.DumpProcessingController;
import org.wikidata.wdtk.dumpfiles.MwRevision;
import org.wikidata.wdtk.rdf.RdfSerializer;

public class RdfConfiguration extends OutputConfiguration {

	/**
	 * Constructor. See {@link OutputConfiguration} for more details.
	 * 
	 * @param conversionProperties
	 */
	public RdfConfiguration(ConversionProperties conversionProperties) {
		super(conversionProperties);
	}

	final static String COMPRESS_BZ2 = ".bz2";
	final static String COMPRESS_GZIP = ".gz";
	final static String COMPRESS_NONE = "";

	RdfSerializer serializer;

	String rdfdump = "";

	@Override
	public String getOutputFormat() {
		return "rdf";
	}

	public String getRdfdump() {
		return rdfdump;
	}

	public void setRdfdump(String rdfdump) {
		this.rdfdump = rdfdump.toLowerCase();
	}

	@Override
	public void setupSerializer(
			DumpProcessingController dumpProcessingController, Sites sites)
			throws IOException {

		if (this.outputDestination.equals("")) {
			setDefaultDestination();
		}

		// Create serializers for several data parts and encodings depending on
		// the Rdfdump property:
		if (this.rdfdump.equals("all_exact_data")) {
			this.serializer = createRdfSerializer(dumpProcessingController,
					sites, RdfSerializer.TASK_PROPERTIES
							| RdfSerializer.TASK_ALL_EXACT_DATA);
		}
		if (this.rdfdump.equals("terms")) {
			this.serializer = createRdfSerializer(dumpProcessingController,
					sites, RdfSerializer.TASK_ITEMS | RdfSerializer.TASK_TERMS);
		}
		if (this.rdfdump.equals("statements")) {
			this.serializer = createRdfSerializer(dumpProcessingController,
					sites, RdfSerializer.TASK_ITEMS
							| RdfSerializer.TASK_STATEMENTS);
		}
		if (this.rdfdump.equals("simple_statements")) {
			this.serializer = createRdfSerializer(dumpProcessingController,
					sites, RdfSerializer.TASK_ITEMS
							| RdfSerializer.TASK_SIMPLE_STATEMENTS);
		}
		if (this.rdfdump.equals("taxonomy")) {
			this.serializer = createRdfSerializer(dumpProcessingController,
					sites, RdfSerializer.TASK_ITEMS
							| RdfSerializer.TASK_TAXONOMY);
		}
		if (this.rdfdump.equals("instance_of")) {
			this.serializer = createRdfSerializer(dumpProcessingController,
					sites, RdfSerializer.TASK_ITEMS
							| RdfSerializer.TASK_INSTANCE_OF);
		}
		if (this.rdfdump.equals("sitelinks")) {
			this.serializer = createRdfSerializer(dumpProcessingController,
					sites, RdfSerializer.TASK_ITEMS
							| RdfSerializer.TASK_SITELINKS);
		}

	}

	/**
	 * Creates a new RDF Serializer. Output is written to the file of the given
	 * name (if there is an copression defined the compression extension will be
	 * added). The tasks define what the serializer will be writing into this
	 * file. The new serializer is also registered in an internal list, so it
	 * can be started and closed more conveniently.
	 * 
	 * @param dumpProcessingController
	 *            needed to register the serializer in the processing pipeline
	 * @param sites
	 *            needed to link to wikipedia pages in rdf
	 * @param tasks
	 *            an integer that is a bitwise OR of flags like
	 *            {@link RdfSerializer#TASK_LABELS}.
	 * @return the newly created rdf serializer
	 * @throws IOException
	 */
	RdfSerializer createRdfSerializer(
			DumpProcessingController dumpProcessingController, Sites sites,
			int tasks) throws IOException {

		OutputStream exportOutputStream = getCompressorOutputStream();

		RdfSerializer serializer = new RdfSerializer(RDFFormat.NTRIPLES,
				exportOutputStream, sites);
		serializer.setTasks(tasks);

		dumpProcessingController.registerEntityDocumentProcessor(serializer,
				MwRevision.MODEL_WIKIBASE_ITEM, true);
		dumpProcessingController.registerEntityDocumentProcessor(serializer,
				MwRevision.MODEL_WIKIBASE_PROPERTY, true);

		return serializer;
	}

	/**
	 * Sets a default value to output destination depending on the other
	 * attributes.
	 */
	void setDefaultDestination() {
		if (!rdfdump.equals(""))
			this.outputDestination = rdfdump + ".nt";
	}

	@Override
	public void startSerializer() {
		serializer.start();
	}

	@Override
	public void closeSerializer() {
		serializer.close();
		System.out.println("*** Finished serialization of "
				+ serializer.getTripleCount() + " RDF triples in file "
				+ this.outputDestination);
	}

}
