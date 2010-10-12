package net.wildsleep.imgdl.taskstate;

import java.util.ArrayList;
import java.util.List;

import net.wildsleep.imgdl.TaskObserver;
import net.wildsleep.imgdl.task.Task;

public class CompositeTaskState implements TaskState, TaskObserver {

	private static final String INITIAL_MESSAGE = "Waiting...";
	
	private Task task;
	private TaskState subState;
	private boolean finished;
	private List<TaskObserver> observers;
	
	public CompositeTaskState(Task task) {
		this.task = task;
		this.subState = null;
		this.finished = false;
		this.observers = new ArrayList<TaskObserver>();
	}
	
	@Override
	public boolean isFinished() {
		return finished;
	}
	
	@Override
	public String getStateMessage() {
		if (subState == null)
			return INITIAL_MESSAGE;
		return subState.getStateMessage();
	}
	
	@Override
	public boolean isError() {
		if (subState == null)
			return false;
		return subState.isError();
	}
	
	@Override
	public String getErrorMessage() {
		if (subState == null)
			return null;
		return subState.getErrorMessage();
	}
	
	public void setSubState(TaskState state) {
		if (subState != null)
			subState.unregister(this);
		subState = state;
		subState.register(this);
		notifyObservers();
	}
	
	public void setFinished() {
		finished = true;
		notifyObservers();
	}
	
	@Override
	public void register(TaskObserver observer) {
		observers.add(observer);
	}

	@Override
	public void unregister(TaskObserver observer) {
		observers.remove(observer);
	}
	
	@Override
	public void notifyObservers() {
		for (TaskObserver observer : observers) {
			observer.updateTask(task);
		}
	}

	@Override
	public void updateTask(Task task) {
		notifyObservers();
	}

}
