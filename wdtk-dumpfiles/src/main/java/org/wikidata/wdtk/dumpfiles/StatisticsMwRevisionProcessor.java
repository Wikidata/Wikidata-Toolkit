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

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wikidata.wdtk.util.Timer;

/**
 * A simple revision processor that counts some basic figures and logs the
 * result.
 * 
 * @author Markus Kroetzsch
 * 
 */
public class StatisticsMwRevisionProcessor implements MwRevisionProcessor {

	static final Logger logger = LoggerFactory
			.getLogger(StatisticsMwRevisionProcessor.class);

	final String name;
	final int logFrequency;
	long totalRevisionCount = 0;
	long currentRevisionCount = 0;
	final Timer totalTimer;
	final Timer currentTimer;

	/**
	 * Constructor.
	 * 
	 * @param name
	 *            a string name used in log messages to refer to this processor
	 * @param logFrequency
	 *            the number of revisions after which an intermediate status
	 *            report should be logged; or -1 if no such reports should be
	 *            logged
	 */
	public StatisticsMwRevisionProcessor(String name, int logFrequency) {
		this.name = name;
		this.logFrequency = logFrequency;
		this.totalTimer = Timer.getNamedTimer(name + "-totalTimer",
				Timer.RECORD_ALL);
		this.currentTimer = Timer.getNamedTimer(name + "-currentTimer",
				Timer.RECORD_ALL);
	}

	/**
	 * Returns the total number of revisions processed so far.
	 * 
	 * @return the number of revisions
	 */
	public long getTotalRevisionCount() {
		return this.totalRevisionCount;
	}

	/**
	 * Returns the number of revisions processed in the current run.
	 * 
	 * @return the number of revisions
	 */
	public long getCurrentRevisionCount() {
		return this.currentRevisionCount;
	}

	@Override
	public void startRevisionProcessing(String siteName, String baseUrl,
			Map<Integer, String> namespaces) {
		this.currentRevisionCount = 0;

		if (!this.totalTimer.isRunning()) {
			this.totalTimer.reset();
			this.totalTimer.start();
		}
		this.currentTimer.reset();
		this.currentTimer.start();

		StatisticsMwRevisionProcessor.logger.info("[" + this.name
				+ "] Starting processing run for \"" + siteName + "\" ("
				+ baseUrl + ").");
		StatisticsMwRevisionProcessor.logger.info("[" + this.name
				+ "] Namespaces: " + namespaces.toString());
	}

	@Override
	public void processRevision(MwRevision mwRevision) {
		this.currentRevisionCount++;
		this.totalRevisionCount++;

		if (this.logFrequency > 0
				&& this.totalRevisionCount % this.logFrequency == 0) {
			logProgress();
		}
	}

	@Override
	public void finishRevisionProcessing() {
		logProgress();
		StatisticsMwRevisionProcessor.logger.info("[" + this.name
				+ "] Finished processing run.");
	}

	void logProgress() {
		this.currentTimer.stop();
		this.totalTimer.stop();
		if (this.totalRevisionCount > 0) {
			StatisticsMwRevisionProcessor.logger.info("[" + this.name
					+ "] Processed " + this.totalRevisionCount
					+ " revisions (total) in "
					+ this.totalTimer.getTotalWallTime() / 1000000000
					+ "s (wall)/" + this.totalTimer.getTotalCpuTime()
					/ 1000000000 + "s (cpu). " + "Time per revision (mics): "
					+ this.totalTimer.getTotalWallTime()
					/ this.totalRevisionCount / 1000 + "/"
					+ this.totalTimer.getTotalCpuTime()
					/ this.totalRevisionCount / 1000);
		}
		if (this.currentRevisionCount > 0) {
			StatisticsMwRevisionProcessor.logger.info("[" + this.name
					+ "] Processed " + this.currentRevisionCount
					+ " revisions (current run) in "
					+ this.currentTimer.getTotalWallTime() / 1000000000
					+ "s (wall)/" + this.currentTimer.getTotalCpuTime()
					/ 1000000000 + "s (cpu)." + " Time per revision (mics): "
					+ this.currentTimer.getTotalWallTime()
					/ this.currentRevisionCount / 1000 + "/"
					+ this.currentTimer.getTotalCpuTime()
					/ this.currentRevisionCount / 1000);
		}
		this.currentTimer.start();
		this.totalTimer.start();
	}

}
