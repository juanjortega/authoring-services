package org.ihtsdo.snowowl.authoring.single.api.service.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;

public class Timer {

	private final String timerName;
	private final long start;
	private long lastCheck;
	private Logger logger = LoggerFactory.getLogger(getClass());

	public Timer(String timerName) {
		this.timerName = timerName;
		this.start = new Date().getTime();
		lastCheck = start;
		logger.info("Timer {}: started", timerName);
	}

	public void checkpoint(String name) {
		final long now = new Date().getTime();
		float millisTaken = now - lastCheck;
		lastCheck = now;
		logger.info("Timer {}: {} took {} seconds", timerName, name, millisTaken / 1000f);
	}

	public void finish() {
		final long now = new Date().getTime();
		float millisTaken = now - start;
		logger.info("Timer {}: total took {} seconds", timerName, millisTaken / 1000f);
	}

	public static void main(String[] args) throws InterruptedException {
		final Timer timer = new Timer("A");
		Thread.sleep(100);
		timer.checkpoint("thing");
		Thread.sleep(1000);
		timer.checkpoint("other thing");
		timer.finish();
	}

}
