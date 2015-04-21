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

import org.wikidata.wdtk.datamodel.interfaces.EntityDocumentDumpProcessor;
import org.wikidata.wdtk.datamodel.interfaces.Sites;

/**
 * This interface represents an action that may be performed with Wikibase
 * dumps. The processing as such is achieved by implementing
 * {@link EntityDocumentDumpProcessor}. Additional methods provide a generic
 * interface for setting options and other auxiliary information that the action
 * might require.
 * 
 * @author Markus Kroetzsch
 * 
 */
public interface DumpProcessingAction extends EntityDocumentDumpProcessor {

	/**
	 * Returns true if processing requires a {@link Sites} object to be set.
	 * This is the case for some operations that process site links.
	 * 
	 * @return true if sites information is needed.
	 */
	boolean needsSites();

	/**
	 * Returns true if the action is ready to process a dump. An action that is
	 * insufficiently or wrongly configured can return false here to avoid being
	 * run.
	 * <p>
	 * If this method is called on an action that is not ready, the action
	 * should print helpful information on the missing configuration to stdout
	 * as a side effect.
	 * 
	 * @return true if ready to run
	 */
	boolean isReady();

	/**
	 * Sets the sites information to the given value. The method
	 * {@link #needsSites()} is used to find out if this is actually needed.
	 * 
	 * @param sites
	 *            the sites information for the data that will be processed
	 */
	void setSites(Sites sites);

	/**
	 * Sets the options of the specified name to the given value. Returns true
	 * if the option was known, and false otherwise. Implementation should
	 * overwrite this function to support additional options.
	 * 
	 * @param option
	 *            name of the option to be set
	 * @param value
	 *            the new value of the option
	 * @return true if the option was known (no matter if the given value could
	 *         actually be used or not)
	 */
	boolean setOption(String option, String value);

	/**
	 * Returns true if this action will write results (not log messages) to
	 * stdout. The default implementation returns true if no other output
	 * destination has been specified. Subclasses that do not write results to
	 * stdout in this case should overwrite this method.
	 * 
	 * @return true if the action is configured to write results to stdout
	 */
	boolean useStdOut();

	/**
	 * Provides the action with general information about the dump that is to be
	 * processed. This may be used, e.g., to define file names to use for the
	 * output.
	 * 
	 * @param project
	 *            the name of the project that the dump is from
	 * @param dateStamp
	 *            the datestamp (YYYYMMDD) for the dump
	 */
	void setDumpInformation(String project, String dateStamp);

	/**
	 * Returns a report message containing information about the files which
	 * were generated.
	 * 
	 * @return report message
	 */
	String getReport();

	/**
	 * Sets the name of the action. If this is not set, a default name will be
	 * used.
	 * 
	 * @param name
	 */
	void setActionName(String name);

	/**
	 * Returns the name of the action.
	 * 
	 * @return name
	 */
	String getActionName();

	/**
	 * Returns the default name for an action.
	 * 
	 * @return default name
	 */
	String getDefaultActionName();

}
