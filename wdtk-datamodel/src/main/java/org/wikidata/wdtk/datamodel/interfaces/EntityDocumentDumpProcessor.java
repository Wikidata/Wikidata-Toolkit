package org.wikidata.wdtk.datamodel.interfaces;

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

/**
 * Interface for classes that process a list of {@link EntityDocument} objects.
 * The difference to {@link EntityDocumentProcessor} is that there are
 * additional methods to start and end processing. They can be used to do
 * initial processing steps (open files, write headers etc.) and final
 * processing steps (closing files etc.), respectively.
 * <p>
 * Implementations expect callers to invoke the methods in a strict order: first
 * {@link #open()}, followed by any number of calls to
 * {@link #processItemDocument(ItemDocument)} and
 * {@link #processPropertyDocument(PropertyDocument)}, and finally
 * {@link #close()}. Any other order of invocation may lead to undefined
 * results. In particular, implementations are not expected to guard against
 * such wrong use.
 *
 * @author Michael Günther
 *
 */
public interface EntityDocumentDumpProcessor extends EntityDocumentProcessor {

	/**
	 * Starts the processing by performing any initial steps to prepare
	 * processing.
	 */
	void open();

	/**
	 * Finishes the processing by performing any final steps, such as closing
	 * resources.
	 */
	void close();

}
