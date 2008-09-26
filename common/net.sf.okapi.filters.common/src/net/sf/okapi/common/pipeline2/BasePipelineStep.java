package net.sf.okapi.common.pipeline2;

public abstract class BasePipelineStep implements IPipelineStep {
	private PipelineReturnValue result;
	private volatile boolean pause;

	public void pause() {
		pause = true;
	}

	public synchronized void resume() {
		pause = false;
		notify();
	}

	public PipelineReturnValue call() throws Exception {
		result = PipelineReturnValue.RUNNING;
		try {
			initialize();
			while (result == PipelineReturnValue.RUNNING) {

				result = process();

				if (pause) {
					synchronized (this) {
						while (pause)
							wait();
					}
				}

				// Interrupted exception only thrown in waiting mode
				if (Thread.currentThread().isInterrupted()) {
					return PipelineReturnValue.INTERRUPTED;
				}
			}
		} catch (InterruptedException e) {
			return PipelineReturnValue.INTERRUPTED;
		} finally {
			finish();
		}

		return result;
	}
}
