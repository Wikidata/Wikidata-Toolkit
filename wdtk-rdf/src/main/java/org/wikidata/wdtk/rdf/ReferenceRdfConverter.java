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

import java.util.*;

import org.eclipse.rdf4j.model.Resource;
import org.eclipse.rdf4j.rio.RDFHandlerException;
import org.wikidata.wdtk.datamodel.interfaces.Reference;
import org.wikidata.wdtk.datamodel.interfaces.Snak;
import org.wikidata.wdtk.datamodel.interfaces.SnakGroup;

/**
 * This class supports the conversion of references to RDF. It buffers
 * references to avoid duplicates and to allow reference triples to be
 * serialized separately (for more efficient encodings in syntaxes like Turtle
 * or RDF/XML).
 *
 * @author Markus Kroetzsch
 *
 */
public class ReferenceRdfConverter {

	final RdfWriter rdfWriter;
	final SnakRdfConverter snakRdfConverter;

	final List<Reference> referenceQueue = new ArrayList<>();
	final List<Resource> referenceSubjectQueue = new ArrayList<>();
	final Set<Resource> declaredReferences = new HashSet<>();
	final String siteUri;

	/**
	 * Constructor.
	 *
	 * @param rdfWriter
	 *            object to use for constructing URI objects
	 * @param snakRdfConverter
	 *            object to use for writing snaks
	 * @param siteUri
	 *            URI prefix that is used by the processed site
	 */
	public ReferenceRdfConverter(RdfWriter rdfWriter,
			SnakRdfConverter snakRdfConverter, String siteUri) {
		this.rdfWriter = rdfWriter;
		this.snakRdfConverter = snakRdfConverter;
		this.siteUri = siteUri;
	}

	/**
	 * Adds the given reference to the list of references that should still be
	 * serialized, and returns the RDF resource that will be used as a subject.
	 *
	 * @param reference
	 *            the reference to be serialized
	 * @return RDF resource that represents this reference
	 */
	public Resource addReference(Reference reference) {
		Resource resource = this.rdfWriter.getUri(Vocabulary.getReferenceUri(reference));

		this.referenceQueue.add(reference);
		this.referenceSubjectQueue.add(resource);

		return resource;
	}

	/**
	 * Writes references that have been added recently. Auxiliary triples that
	 * are generated for serializing snaks in references will be written right
	 * afterwards. This will also trigger any other auxiliary triples to be
	 * written that the snak converter object may have buffered.
	 *
	 * @throws RDFHandlerException
	 *             if there was a problem writing the restrictions
	 */
	public void writeReferences() throws RDFHandlerException {
		Iterator<Reference> referenceIterator = this.referenceQueue.iterator();
		for (Resource resource : this.referenceSubjectQueue) {
			final Reference reference = referenceIterator.next();
			if (this.declaredReferences.add(resource)) {
				writeReference(reference, resource);
			}
		}
		this.referenceSubjectQueue.clear();
		this.referenceQueue.clear();

		this.snakRdfConverter.writeAuxiliaryTriples();
	}

	void writeReference(Reference reference, Resource resource)
			throws RDFHandlerException {

		this.rdfWriter.writeTripleValueObject(resource, RdfWriter.RDF_TYPE,
				RdfWriter.WB_REFERENCE);
		for (SnakGroup snakGroup : reference.getSnakGroups()) {
			this.snakRdfConverter.setSnakContext(resource,
					PropertyContext.REFERENCE);
			for (Snak snak : snakGroup) {
				snak.accept(this.snakRdfConverter);
			}
		}
	}
}
