package net.wildsleep.imgdl.taskstate;

import net.wildsleep.imgdl.TaskObserver;

public class FinishedTaskState implements TaskState {

	@Override
	public boolean isFinished() {
		return true;
	}

	@Override
	public String getStateMessage() {
		return "Finished.";
	}

	@Override
	public boolean isError() {
		return false;
	}

	@Override
	public String getErrorMessage() {
		return null;
	}

	@Override
	public void register(TaskObserver observer) { }

	@Override
	public void unregister(TaskObserver observer) { }

	@Override
	public void notifyObservers() { }
}
