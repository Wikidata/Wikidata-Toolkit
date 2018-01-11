package org.wikidata.wdtk.dumpfiles;

/*
 * #%L
 * Wikidata Toolkit Dump File Handling
 * %%
 * Copyright (C) 2014 - 2016 Wikidata Toolkit Developers
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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wikidata.wdtk.datamodel.interfaces.EntityDocumentDumpProcessor;
import org.wikidata.wdtk.datamodel.interfaces.EntityDocumentProcessor;
import org.wikidata.wdtk.datamodel.interfaces.ItemDocument;
import org.wikidata.wdtk.datamodel.interfaces.PropertyDocument;
import org.wikidata.wdtk.util.Timer;

/**
 * Simple {@link EntityDocumentProcessor} for basic counting and time keeping.
 * It will print statistics on elapsed time and processed entities once in a
 * while. The class also supports a timeout mechanism: if a timeout time (in
 * seconds) is given, then a {@link EntityTimerProcessor.TimeoutException}
 * (unchecked) will be thrown soon after this many seconds have passed. This can
 * be used to abort processing in a relatively clean way by catching this
 * exception at a higher level.
 *
 * @author Markus Kroetzsch
 *
 */
public class EntityTimerProcessor implements EntityDocumentDumpProcessor {

	static final Logger logger = LoggerFactory
			.getLogger(EntityTimerProcessor.class);

	final Timer timer = Timer.getNamedTimer("EntityTimerProcessor");
	final int timeout;
	int entityCount = 0;
	int lastSeconds = 0;

	/**
	 * Number of seconds after which a progress report is printed. If a timeout
	 * is configured, it will only be checked at a report.
	 */
	int reportInterval = 10;

	/**
	 * Constructor.
	 *
	 * @param timeout
	 *            the timeout in seconds or 0 if no timeout should be used
	 */
	public EntityTimerProcessor(int timeout) {
		this.timeout = timeout;
	}

	/**
	 * Sets the interval after which the timer should report progress. By
	 * default, this is ten seconds. When using a timeout, the timeout condition
	 * will only be checked at this interval, too, so using a very large value
	 * would lead to increasing imprecision with the timeout. The timer does not
	 * use a separate thread, and reports will only be generated after an entity
	 * was fully processed. Thus, very long processing times would also affect
	 * the accuracy of the interval.
	 *
	 * @param seconds
	 *            time after which progress should be reported.
	 */
	public void setReportInterval(int seconds) {
		if (seconds <= 0) {
			throw new IllegalArgumentException(
					"The report interval must be a non-zero, positive number of seconds.");
		}
		this.reportInterval = seconds;
	}

	@Override
	public void processItemDocument(ItemDocument itemDocument) {
		countEntity();
	}

	@Override
	public void processPropertyDocument(PropertyDocument propertyDocument) {
		countEntity();
	}

	@Override
	public void open() {
		// Nothing to do. We only start the timer when the first entity is
		// really processed.
	}

	/**
	 * Stops the processing and prints the final time.
	 */
	@Override
	public void close() {
		logger.info("Finished processing.");
		this.timer.stop();
		this.lastSeconds = (int) (timer.getTotalWallTime() / 1000000000);
		printStatus();
	}

	/**
	 * Counts one entity. Every once in a while, the current time is checked so
	 * as to print an intermediate report roughly every ten seconds.
	 */
	private void countEntity() {
		if (!this.timer.isRunning()) {
			startTimer();
		}

		this.entityCount++;
		if (this.entityCount % 100 == 0) {
			timer.stop();
			int seconds = (int) (timer.getTotalWallTime() / 1000000000);
			if (seconds >= this.lastSeconds + this.reportInterval) {
				this.lastSeconds = seconds;
				printStatus();
				if (this.timeout > 0 && seconds > this.timeout) {
					logger.info("Timeout. Aborting processing.");
					throw new TimeoutException();
				}
			}
			timer.start();
		}
	}

	/**
	 * Prints the current status, time and entity count.
	 */
	private void printStatus() {
		logger.info("Processed "
				+ this.entityCount
				+ " entities in "
				+ this.lastSeconds
				+ " sec"
				+ (this.lastSeconds > 0 ? " ("
						+ (this.entityCount / this.lastSeconds)
						+ " per second)" : ""));
	}

	private void startTimer() {
		logger.info("Starting processing.");
		this.timer.start();
	}

	public class TimeoutException extends RuntimeException {
		private static final long serialVersionUID = -1083533602730765194L;
	}

}
