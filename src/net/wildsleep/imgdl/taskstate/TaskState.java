package net.wildsleep.imgdl.taskstate;

import net.wildsleep.imgdl.TaskObserver;

public interface TaskState {

	public abstract boolean isFinished();
	public abstract String getStateMessage();
	public abstract boolean isError();
	public abstract String getErrorMessage();
	
	void register(TaskObserver observer);
	void unregister(TaskObserver observer);
	void notifyObservers();

}