package com.example.ai.config;

import io.temporal.worker.WorkerFactory;
import org.springframework.context.SmartLifecycle;

public class TemporalWorkerLifecycle implements SmartLifecycle {

	private final WorkerFactory workerFactory;
	private final boolean enabled;
	private volatile boolean running;

	public TemporalWorkerLifecycle(WorkerFactory workerFactory, boolean enabled) {
		this.workerFactory = workerFactory;
		this.enabled = enabled;
	}

	@Override
	public void start() {
		if (enabled && !running) {
			workerFactory.start();
			running = true;
		}
	}

	@Override
	public void stop() {
		if (running) {
			workerFactory.shutdown();
			running = false;
		}
	}

	@Override
	public boolean isRunning() {
		return running;
	}

	@Override
	public boolean isAutoStartup() {
		return enabled;
	}
}
