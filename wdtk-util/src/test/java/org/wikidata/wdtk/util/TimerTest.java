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

import static org.junit.Assert.*;

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;
import java.util.Random;

import org.junit.Test;

public class TimerTest {

	/**
	 * Base value for the time in microseconds that we allow between our
	 * measured times and what the timer returns. In theory, there is not really
	 * any such time but in practice a sufficiently high value should work.
	 */
	static final int TIME_TOLERANCE = 200000;

	/**
	 * Spend some time computing to be able to measure something.
	 */
	void doDummyComputation() {
		long dummyValue = 0;
		// We use a random number and a subsequent check to avoid smart
		// compilers
		Random rand = new Random();
		int seed = rand.nextInt(10) + 1;
		for (int i = 0; i < 10000000; i++) {
			dummyValue = 10 + ((31 * (dummyValue + seed)) % 1234567);
		}
		if (dummyValue < 10) {
			throw new RuntimeException(
					"This never happens, but let's pretend the value matters to avoid this being complied away.");
		}
		try {
			Thread.sleep(50);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Test
	public void basicTimerOperation() {
		Timer timer = new Timer("Test timer", Timer.RECORD_ALL);
		assertEquals(timer.getName(), "Test timer");
		long threadId = timer.getThreadId();

		assertEquals(timer.getAvgCpuTime(), 0);
		assertEquals(timer.getAvgWallTime(), 0);

		ThreadMXBean tmxb = ManagementFactory.getThreadMXBean();
		if (!tmxb.isThreadCpuTimeEnabled()) {
			tmxb.setThreadCpuTimeEnabled(true);
		}

		long cpuTime1 = tmxb.getThreadCpuTime(threadId);
		long wallTime1 = System.nanoTime();
		timer.start();
		doDummyComputation();
		assertTrue("Timer should be running", timer.isRunning());
		timer.stop();
		cpuTime1 = tmxb.getThreadCpuTime(threadId) - cpuTime1;
		wallTime1 = System.nanoTime() - wallTime1;
		assertTrue(
				"Unrealistic CPU time: " + timer.getTotalCpuTime()
						+ " should be closer to " + cpuTime1,
				(cpuTime1 - TimerTest.TIME_TOLERANCE) <= timer
						.getTotalCpuTime()
						&& timer.getTotalCpuTime() <= cpuTime1);
		assertTrue(
				"Unrealistic wall time: " + timer.getTotalWallTime()
						+ " should be closer to " + wallTime1,
				(wallTime1 - 2 * TimerTest.TIME_TOLERANCE) <= timer
						.getTotalWallTime()
						&& timer.getTotalWallTime() <= wallTime1);

		long cpuTime2 = tmxb.getThreadCpuTime(threadId);
		long wallTime2 = System.nanoTime();
		timer.start();
		doDummyComputation();
		timer.stop();
		cpuTime1 += tmxb.getThreadCpuTime(threadId) - cpuTime2;
		wallTime1 += System.nanoTime() - wallTime2;
		assertTrue(
				"Unrealistic total CPU time: " + timer.getTotalCpuTime()
						+ " should be closer to " + cpuTime1,
				(cpuTime1 - 2 * TimerTest.TIME_TOLERANCE) <= timer
						.getTotalCpuTime()
						&& timer.getTotalCpuTime() <= cpuTime1);
		assertTrue(
				"Unrealistic total wall time: " + timer.getTotalWallTime()
						+ " should be closer to " + wallTime1,
				(wallTime1 - 4 * TimerTest.TIME_TOLERANCE) <= timer
						.getTotalWallTime()
						&& timer.getTotalWallTime() <= wallTime1);

		assertEquals(timer.getTotalCpuTime() / 2, timer.getAvgCpuTime());
		assertEquals(timer.getTotalWallTime() / 2, timer.getAvgWallTime());

		timer.reset();
		assertEquals(timer.getTotalCpuTime(), 0);
		assertEquals(timer.getTotalWallTime(), 0);
		assertFalse("Timer should not be running", timer.isRunning());
	}

	@Test
	public void namedTimers() {
		Timer timerA1 = Timer.getNamedTimer("test timer");
		Timer timerA2 = Timer.getNamedTimer("test timer");
		Timer timerA3 = Timer.getNamedTimer("test timer", Timer.RECORD_ALL);
		Timer timerA4 = Timer.getNamedTimer("test timer", Timer.RECORD_ALL,
				timerA1.getThreadId());
		Timer timerCpu = Timer
				.getNamedTimer("test timer", Timer.RECORD_CPUTIME);
		Timer timerWall = Timer.getNamedTimer("test timer",
				Timer.RECORD_WALLTIME);
		Timer timerNoThread = Timer.getNamedTimer("test timer",
				Timer.RECORD_ALL, 0);
		Timer timerNone = Timer.getNamedTimer("test timer none",
				Timer.RECORD_NONE);
		Timer timerB = Timer.getNamedTimer("test timer 2");

		// Testing Timer equality:
		assertEquals(timerA1, timerA2);
		assertEquals(timerA1, timerA3);
		assertEquals(timerA1, timerA4);
		assertNotEquals(timerA1, timerCpu);
		assertNotEquals(timerA1, timerWall);
		assertNotEquals(timerA1, timerNoThread);
		assertNotEquals(timerA1, timerB);
		assertNotEquals(timerA1, this);

		// Testing start/stop operation:
		Timer.startNamedTimer("test timer");
		Timer.startNamedTimer("test timer", Timer.RECORD_CPUTIME);
		Timer.startNamedTimer("test timer", Timer.RECORD_WALLTIME);
		Timer.startNamedTimer("test timer", Timer.RECORD_ALL, 0);
		doDummyComputation();
		Timer.stopNamedTimer("test timer");
		Timer.stopNamedTimer("test timer", Timer.RECORD_CPUTIME);
		Timer.stopNamedTimer("test timer", Timer.RECORD_WALLTIME);
		Timer.stopNamedTimer("test timer", Timer.RECORD_ALL, 0);

		assertTrue("Named timer should have measured a non-zero CPU time.",
				timerA1.getTotalCpuTime() > 0);
		assertTrue("Named timer should have measured a non-zero wall time.",
				timerA1.getTotalWallTime() > 0);
		assertTrue(
				"Timer for CPU time should have measured a non-zero CPU time.",
				timerCpu.getTotalCpuTime() > 0);
		assertEquals("Timer for CPU time should not have measured a wall time.", 0, timerCpu.getTotalWallTime());
		assertEquals("Timer for wall time should not have measured a CPU time.", 0, timerWall.getTotalCpuTime());
		assertTrue(
				"Timer for wall time should have measured a non-zero wall time.",
				timerWall.getTotalWallTime() > 0);
		assertEquals("Timer without threadId should not have measured a CPU time.", 0, timerNoThread.getTotalCpuTime());
		assertTrue(
				"Timer without threadId should have measured a non-zero wall time.",
				timerNoThread.getTotalWallTime() > 0);

		// Testing total timer creation:
		Timer totalTimer1 = Timer.getNamedTotalTimer("test timer");
		// There should be four *distinct* timers of that name
		assertEquals(totalTimer1.getTotalCpuTime(), timerA1.getTotalCpuTime()
				+ timerCpu.getTotalCpuTime() + timerWall.getTotalCpuTime()
				+ timerNoThread.getTotalCpuTime());
		assertEquals(totalTimer1.getTotalWallTime(), timerA1.getTotalWallTime()
				+ timerCpu.getTotalWallTime() + timerWall.getTotalWallTime()
				+ timerNoThread.getTotalWallTime());

		Timer totalTimer2 = Timer.getNamedTotalTimer("test timer 2");
		// There should be just one timer of that name
		assertEquals(totalTimer2, timerB);

		// Testing toString operation
		assertTrue(timerA1.toString().startsWith(
				"Time for test timer (thread " + timerA1.getThreadId()
						+ ") for 1 run(s) CPU/Wall/CPU avg/Wall avg (ms):"));
		assertTrue(timerCpu.toString().startsWith(
				"Time for test timer (thread " + timerCpu.getThreadId()
						+ ") for 1 run(s) CPU/CPU avg (ms):"));
		assertTrue(timerWall.toString().startsWith(
				"Time for test timer (thread " + timerWall.getThreadId()
						+ ") for 1 run(s) Wall/Wall avg (ms):"));
		assertTrue(totalTimer1.toString().startsWith(
				"Time for test timer (over 4 threads)"));
		assertTrue(timerNoThread.toString().startsWith(
				"Time for test timer for 1 run(s)"));
		assertEquals(timerNone.toString(), "Timer test timer none (thread "
				+ timerNone.getThreadId()
				+ ") recorded 0 run(s); no times taken");
		timerA1.start();
		assertTrue(timerA1.toString().endsWith("[timer running!]"));

		// Testing reset operation:
		Timer.resetNamedTimer("test timer");
		Timer.resetNamedTimer("test timer", Timer.RECORD_CPUTIME);
		Timer.resetNamedTimer("test timer", Timer.RECORD_WALLTIME);
		Timer.resetNamedTimer("test timer", Timer.RECORD_ALL, 0);

		assertEquals("Named timer should have reset CPU time.", 0, timerA1.getTotalCpuTime());
		assertEquals("Named timer should have reset wall time.", 0, timerA1.getTotalWallTime());
		assertEquals("Timer for CPU time should have reset CPU time.", 0, timerCpu.getTotalCpuTime());
		assertEquals("Timer for CPU time should have reset wall time.", 0, timerCpu.getTotalWallTime());
		assertEquals("Timer for wall time should have reset CPU time.", 0, timerWall.getTotalCpuTime());
		assertEquals("Timer for wall time should have reset wall time.", 0, timerWall.getTotalWallTime());
		assertEquals("Timer without threadId should have reset CPU time.", 0, timerNoThread.getTotalCpuTime());
		assertEquals("Timer without threadId should have reset wall time.", 0, timerNoThread.getTotalWallTime());

		// Testing unregistered timer stop (does not create one):
		assertEquals(Timer.stopNamedTimer("unknown name"), -1);
	}

	@Test
	public void timerStopReturnValues() {
		Timer timer1 = new Timer("stop test timer", Timer.RECORD_ALL);
		Timer timer2 = new Timer("stop test timer wall", Timer.RECORD_WALLTIME);

		timer1.start();
		timer2.start();
		doDummyComputation();
		long cpuTime1 = timer1.stop();
		long cpuTime2 = timer2.stop();

		assertEquals(cpuTime1, timer1.getTotalCpuTime());
		assertEquals(cpuTime2, -1);

		long cpuTime3 = timer1.stop();
		assertEquals(cpuTime3, -1);
	}

	@Test
	public void enableCpuTimeTaking() {
		ThreadMXBean tmxb = ManagementFactory.getThreadMXBean();
		tmxb.setThreadCpuTimeEnabled(false);

		Timer timer = new Timer("Test timer", Timer.RECORD_ALL);
		timer.start();
		doDummyComputation();
		timer.stop();

		assertTrue("Timer should have measured a CPU time.",
				timer.getTotalCpuTime() > 0);
	}
}
