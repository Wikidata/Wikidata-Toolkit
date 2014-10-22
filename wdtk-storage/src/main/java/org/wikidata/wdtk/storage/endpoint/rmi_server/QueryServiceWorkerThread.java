package org.wikidata.wdtk.storage.endpoint.rmi_server;

class QueryServiceWorkerThread implements Runnable {

	// TODO make waitTimeout configurable
	// TODO logging

	/**
	 * The time, the thread waits until it runs its next cycle in milliseconds.
	 */
	private int waitTimeout = 1000;

	/**
	 * A flag that tells, if the thread should still be running.
	 */
	private Boolean running = true;
	// this needs to be a non-primitive type, otherwise it can not be
	// synchronized.

	private DefaultQueryService monitoredService;

	QueryServiceWorkerThread(DefaultQueryService monitoredService) {
		this.monitoredService = monitoredService;
	}

	@Override
	public void run() {

		while (this.running) {
			// update all query information
			for (QueryInformation qInformation : monitoredService
					.getCurrentQueries().values()) {
				qInformation.update();
			}

			// TODO clear unused queries

			try {
				this.wait(this.waitTimeout);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	void terminate() {
		synchronized (this.running) {
			this.running = false;
		}
	}
}
