package org.wikidata.wdtk.util;

/*
 * #%L
 * Wikidata Toolkit utilities
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

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.lang3.builder.HashCodeBuilder;

/**
 * Class for keeping CPU and system times. Timers measure wall clock and/or CPU
 * times (for specific threads). They can be started and stopped. Times between
 * these two methods will be recorded and (when starting and stopping more than
 * once) added up to total times. The number of start-stop measurements is
 * recorded, and one can also query the average times. Finally, a timer can be
 * reset.
 *
 * There are two main ways of accessing timers: by creating a Timer object
 * directly or by using a global registry of timers. Registered timers are
 * identified by their string name and thread id. The global registry is useful
 * since it makes it much easier to re-integrate measurements taken in many
 * threads. They also free the caller of the burden of keeping a reference to
 * the Timer.
 *
 * The code in this file was adapted from the <a href=
 * "https://code.google.com/p/elk-reasoner/source/browse/elk-util-parent/elk-util-logging/src/main/java/org/semanticweb/elk/util/logging/ElkTimer.java"
 * >ElkTimer</a> class of the ELK reasoner, with contributions from Yevgeny
 * Kasakov and Pavel Klinov.
 *
 * @author Markus Kroetzsch
 *
 */
public class Timer {

	/** Flag for indicating that no times should be taken (just count runs). */
	public static final int RECORD_NONE = 0x00000000;
	/** Flag for indicating that CPU time should be taken. */
	public static final int RECORD_CPUTIME = 0x00000001;
	/** Flag for indicating that wall clock time should be taken. */
	public static final int RECORD_WALLTIME = 0x00000002;
	/** Flag for indicating that all supported times should be taken. */
	public static final int RECORD_ALL = RECORD_CPUTIME | RECORD_WALLTIME;

	/** Object to access CPU times for specific threads. */
	static final ThreadMXBean tmxb = ManagementFactory.getThreadMXBean();

	/** Registry of named timers. */
	static final ConcurrentHashMap<Timer, Timer> registeredTimers = new ConcurrentHashMap<>();

	final String name;
	final long threadId;
	final int todoFlags;

	long currentStartCpuTime = -1;
	long currentStartWallTime = -1;
	boolean isRunning = false;
	long totalCpuTime = 0;
	long totalWallTime = 0;
	int measurements = 0;
	int threadCount = 0;

	/**
	 * Constructor. Every timer is identified by three things: a string name, an
	 * integer for flagging its tasks (todos), and a thread id (long).
	 *
	 * Tasks can be flagged by a disjunction of constants like RECORD_CPUTIME
	 * and RECORD_WALLTIME. Only times for which an according flag is set will
	 * be recorded.
	 *
	 * The thread id can be the actual id of the thread that is measured, or 0
	 * (invalid id) to not assign the timer to any thread. In this case, no CPU
	 * time measurement is possible since Java does not allow us to measure the
	 * total CPU time across all threads.
	 *
	 * @param name
	 *            a string that identifies the timer
	 * @param todoFlags
	 *            flags to define what the timer will measure
	 * @param threadId
	 *            the id of the thread for measuring CPU time or 0 if not
	 *            measuring
	 */
	public Timer(String name, int todoFlags, long threadId) {
		this.name = name;
		this.todoFlags = todoFlags;
		this.threadId = threadId;

		if (!tmxb.isThreadCpuTimeEnabled()) {
			tmxb.setThreadCpuTimeEnabled(true);
		}
	}

	/**
	 * Constructor. Same as {@link #Timer(String, int, long)}, but using the
	 * current thread instead of a freely specified thread.
	 *
	 * @param name
	 *            a string that identifies the timer
	 * @param todoFlags
	 *            flags to define what the timer will measure
	 */
	public Timer(String name, int todoFlags) {
		this(name, todoFlags, Thread.currentThread().getId());
	}

	/**
	 * Get the string name of the timer.
	 *
	 * @return string name
	 */
	public String getName() {
		return name;
	}

	/**
	 * Get the ID of the thread for which this timer was created.
	 *
	 * @return thread ID
	 */
	public long getThreadId() {
		return threadId;
	}

	/**
	 * Return true if the timer is running.
	 *
	 * @return true if running
	 */
	public boolean isRunning() {
		return isRunning;
	}

	/**
	 * Get the total recorded CPU time in nanoseconds.
	 *
	 * @return recorded CPU time in nanoseconds
	 */
	public long getTotalCpuTime() {
		return totalCpuTime;
	}

	/**
	 * Return the average CPU time across all measurements.
	 *
	 * @return the average CPU time across all measurements
	 */
	public long getAvgCpuTime() {
		if (measurements > 0) {
			return totalCpuTime / measurements;
		} else {
			return 0;
		}
	}

	/**
	 * Get the total recorded wall clock time in nanoseconds.
	 *
	 * @return recorded wall time in nanoseconds
	 */
	public long getTotalWallTime() {
		return totalWallTime;
	}

	/**
	 * Return the average wall clock time across all measurements.
	 *
	 * @return the average wall clock time across all measurements
	 */
	public long getAvgWallTime() {
		if (measurements > 0) {
			return totalWallTime / measurements;
		} else {
			return 0;
		}
	}

	/**
	 * Start the timer.
	 */
	public synchronized void start() {
		if ((todoFlags & RECORD_CPUTIME) != 0) {
			currentStartCpuTime = getThreadCpuTime(threadId);
		} else {
			currentStartCpuTime = -1;
		}
		if ((todoFlags & RECORD_WALLTIME) != 0) {
			currentStartWallTime = System.nanoTime();
		} else {
			currentStartWallTime = -1;
		}
		isRunning = true;
	}

	/**
	 * Stop the timer (if running) and reset all recorded values.
	 */
	public synchronized void reset() {
		currentStartCpuTime = -1;
		currentStartWallTime = -1;
		totalCpuTime = 0;
		totalWallTime = 0;
		measurements = 0;
		isRunning = false;
		threadCount = 0;
	}

	/**
	 * Stop the timer and record the times that have passed since its start. The
	 * times that have passed are added to the internal state and can be
	 * retrieved with {@link #getTotalCpuTime()} etc.
	 *
	 * If CPU times are recorded, then the method returns the CPU time that has
	 * passed since the timer was last started; otherwise -1 is returned.
	 *
	 * @return CPU time that the timer was running, or -1 if timer not running
	 *         or CPU time unavailable for other reasons
	 */
	public synchronized long stop() {
		long totalTime = -1;

		if ((todoFlags & RECORD_CPUTIME) != 0 && (currentStartCpuTime != -1)) {
			long cpuTime = getThreadCpuTime(threadId);
			if (cpuTime != -1) { // may fail if thread already dead
				totalTime = cpuTime - currentStartCpuTime;
				totalCpuTime += totalTime;
			}
		}

		if ((todoFlags & RECORD_WALLTIME) != 0 && (currentStartWallTime != -1)) {
			long wallTime = System.nanoTime();
			totalWallTime += wallTime - currentStartWallTime;
		}

		if (isRunning) {
			measurements += 1;
			isRunning = false;
		}

		currentStartWallTime = -1;
		currentStartCpuTime = -1;

		return totalTime;
	}

	/**
	 * The implementation of toString() generates a summary of the times
	 * recorded so far. If the timer is still running, then it will not be
	 * stopped to add the currently measured time to the output but a warning
	 * will added.
	 *
	 * @return string description of the timer results and state
	 */
	@Override
	public String toString() {
		String runningWarning;
		if (isRunning) {
			runningWarning = " [timer running!]";
		} else {
			runningWarning = "";
		}

		String timerLabel;
		if (threadId != 0) {
			timerLabel = name + " (thread " + threadId + ")";
		} else if (threadCount > 1) {
			timerLabel = name + " (over " + threadCount + " threads)";
		} else {
			timerLabel = name;
		}

		if (todoFlags == RECORD_NONE) {
			return "Timer " + timerLabel + " recorded " + measurements
					+ " run(s); no times taken" + runningWarning;
		}

		StringBuilder labels = new StringBuilder();
		StringBuilder values = new StringBuilder();

		String separator;
		if ((todoFlags & RECORD_CPUTIME) != 0 && threadId != 0) {
			labels.append("CPU");
			values.append(totalCpuTime / 1000000);
			separator = "/";
		} else {
			separator = "";
		}
		if ((todoFlags & RECORD_WALLTIME) != 0) {
			labels.append(separator).append("Wall");
			values.append(separator).append(totalWallTime / 1000000);
		}

		if ((todoFlags & RECORD_CPUTIME) != 0 && threadId != 0) {
			labels.append("/CPU avg");
			values.append("/").append(
					(float) (totalCpuTime) / measurements / 1000000);
		}
		if ((todoFlags & RECORD_WALLTIME) != 0) {
			labels.append("/Wall avg");
			values.append("/").append(
					(float) (totalWallTime) / measurements / 1000000);
		}

		if (threadCount > 1) {
			if ((todoFlags & RECORD_CPUTIME) != 0 && threadId != 0) {
				labels.append("/CPU per thread");
				values.append("/").append(
						(float) (totalCpuTime) / threadCount / 1000000);
			}
			if ((todoFlags & RECORD_WALLTIME) != 0) {
				labels.append("/Wall per thread");
				values.append("/").append(
						(float) (totalWallTime) / threadCount / 1000000);
			}
		}

		return "Time for " + timerLabel + " for " + measurements + " run(s) "
				+ labels + " (ms): " + values + runningWarning;

	}

	/**
	 * Start a timer of the given string name for all todos and the current
	 * thread. If no such timer exists yet, then it will be newly created.
	 *
	 * @param timerName
	 *            the name of the timer
	 */
	public static void startNamedTimer(String timerName) {
		getNamedTimer(timerName).start();
	}

	/**
	 * Start a timer of the given string name for the current thread. If no such
	 * timer exists yet, then it will be newly created.
	 *
	 * @param timerName
	 *            the name of the timer
	 * @param todoFlags
	 */
	public static void startNamedTimer(String timerName, int todoFlags) {
		getNamedTimer(timerName, todoFlags).start();
	}

	/**
	 * Start a timer of the given string name for the current thread. If no such
	 * timer exists yet, then it will be newly created.
	 *
	 * @param timerName
	 *            the name of the timer
	 * @param todoFlags
	 * @param threadId
	 *            of the thread to track, or 0 if only system clock should be
	 *            tracked
	 */
	public static void startNamedTimer(String timerName, int todoFlags,
			long threadId) {
		getNamedTimer(timerName, todoFlags, threadId).start();
	}

	/**
	 * Stop a timer of the given string name for all todos and the current
	 * thread. If no such timer exists, -1 will be returned. Otherwise the
	 * return value is the CPU time that was measured.
	 *
	 * @param timerName
	 *            the name of the timer
	 * @return CPU time if timer existed and was running, and -1 otherwise
	 */
	public static long stopNamedTimer(String timerName) {
		return stopNamedTimer(timerName, RECORD_ALL, Thread.currentThread()
				.getId());
	}

	/**
	 * Stop a timer of the given string name for the current thread. If no such
	 * timer exists, -1 will be returned. Otherwise the return value is the CPU
	 * time that was measured.
	 *
	 * @param timerName
	 *            the name of the timer
	 * @param todoFlags
	 * @return CPU time if timer existed and was running, and -1 otherwise
	 */
	public static long stopNamedTimer(String timerName, int todoFlags) {
		return stopNamedTimer(timerName, todoFlags, Thread.currentThread()
				.getId());
	}

	/**
	 * Stop a timer of the given string name for the given thread. If no such
	 * timer exists, -1 will be returned. Otherwise the return value is the CPU
	 * time that was measured.
	 *
	 * @param timerName
	 *            the name of the timer
	 * @param todoFlags
	 * @param threadId
	 *            of the thread to track, or 0 if only system clock should be
	 *            tracked
	 * @return CPU time if timer existed and was running, and -1 otherwise
	 */
	public static long stopNamedTimer(String timerName, int todoFlags,
			long threadId) {
		Timer key = new Timer(timerName, todoFlags, threadId);
		if (registeredTimers.containsKey(key)) {
			return registeredTimers.get(key).stop();
		} else {
			return -1;
		}
	}

	/**
	 * Reset a timer of the given string name for all todos and the current
	 * thread. If no such timer exists yet, then it will be newly created.
	 *
	 * @param timerName
	 *            the name of the timer
	 */
	public static void resetNamedTimer(String timerName) {
		getNamedTimer(timerName).reset();
	}

	/**
	 * Reset a timer of the given string name for the current thread. If no such
	 * timer exists yet, then it will be newly created.
	 *
	 * @param timerName
	 *            the name of the timer
	 * @param todoFlags
	 */
	public static void resetNamedTimer(String timerName, int todoFlags) {
		getNamedTimer(timerName, todoFlags).reset();
	}

	/**
	 * Reset a timer of the given string name for the given thread. If no such
	 * timer exists yet, then it will be newly created.
	 *
	 * @param timerName
	 *            the name of the timer
	 * @param todoFlags
	 * @param threadId
	 *            of the thread to track, or 0 if only system clock should be
	 *            tracked
	 */
	public static void resetNamedTimer(String timerName, int todoFlags,
			long threadId) {
		getNamedTimer(timerName, todoFlags, threadId).reset();
	}

	/**
	 * Get a timer of the given string name that takes all possible times
	 * (todos) for the current thread. If no such timer exists yet, then it will
	 * be newly created.
	 *
	 * @param timerName
	 *            the name of the timer
	 * @return timer
	 */
	public static Timer getNamedTimer(String timerName) {
		return getNamedTimer(timerName, RECORD_ALL, Thread.currentThread()
				.getId());
	}

	/**
	 * Get a timer of the given string name and todos for the current thread. If
	 * no such timer exists yet, then it will be newly created.
	 *
	 * @param timerName
	 *            the name of the timer
	 * @param todoFlags
	 * @return timer
	 */
	public static Timer getNamedTimer(String timerName, int todoFlags) {
		return getNamedTimer(timerName, todoFlags, Thread.currentThread()
				.getId());
	}

	/**
	 * Get a timer of the given string name for the given thread. If no such
	 * timer exists yet, then it will be newly created.
	 *
	 * @param timerName
	 *            the name of the timer
	 * @param todoFlags
	 * @param threadId
	 *            of the thread to track, or 0 if only system clock should be
	 *            tracked
	 * @return timer
	 */
	public static Timer getNamedTimer(String timerName, int todoFlags,
			long threadId) {
		Timer key = new Timer(timerName, todoFlags, threadId);
		registeredTimers.putIfAbsent(key, key);
		return registeredTimers.get(key);
	}

	/**
	 * Collect the total times measured by all known named timers of the given
	 * name. This is useful to add up times that were collected across separate
	 * threads.
	 *
	 * @param timerName
	 * @return timer
	 */
	public static Timer getNamedTotalTimer(String timerName) {
		long totalCpuTime = 0;
		long totalSystemTime = 0;
		int measurements = 0;
		int timerCount = 0;
		int todoFlags = RECORD_NONE;

		Timer previousTimer = null;
		for (Map.Entry<Timer, Timer> entry : registeredTimers.entrySet()) {
			if (entry.getValue().name.equals(timerName)) {
				previousTimer = entry.getValue();
				timerCount += 1;
				totalCpuTime += previousTimer.totalCpuTime;
				totalSystemTime += previousTimer.totalWallTime;
				measurements += previousTimer.measurements;
				todoFlags |= previousTimer.todoFlags;
			}
		}

		if (timerCount == 1) {
			return previousTimer;
		} else {
			Timer result = new Timer(timerName, todoFlags, 0);
			result.totalCpuTime = totalCpuTime;
			result.totalWallTime = totalSystemTime;
			result.measurements = measurements;
			result.threadCount = timerCount;
			return result;
		}
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder(997, 1013).append(name).append(threadId)
				.append(todoFlags).toHashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (!(obj instanceof Timer)) {
			return false;
		}
		Timer other = (Timer) obj;
		return (threadId == other.threadId && todoFlags == other.todoFlags && name
				.equals(other.name));
	}

	/**
	 * Get the current CPU time of the given thread.
	 *
	 * @param threadId
	 *            id of the thread to get CPU time for
	 * @return current CPU time in the given thread, or 0 if thread is 0
	 */
	static long getThreadCpuTime(long threadId) {
		if (threadId == 0) { // generally invalid as a thread id
			return 0;
		} else {
			return tmxb.getThreadCpuTime(threadId);
		}
	}

}
