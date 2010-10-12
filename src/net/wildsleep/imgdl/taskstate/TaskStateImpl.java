package net.wildsleep.imgdl.taskstate;

import java.util.ArrayList;
import java.util.List;

import net.wildsleep.imgdl.TaskObserver;
import net.wildsleep.imgdl.task.Task;

public class TaskStateImpl implements TaskState {
	
	private static final String INITIAL_MESSAGE = "Waiting...";
	
	private Task task;
	
	private boolean finished;
	private String message;
	
	private boolean error;
	private String errorMessage;
	
	private List<TaskObserver> observers;
	
	public TaskStateImpl(Task task) {
		this.task = task;
		this.finished = false;
		this.message = INITIAL_MESSAGE;
		this.error = false;
		this.errorMessage = "";
		this.observers = new ArrayList<TaskObserver>();
	}
	
	@Override
	public boolean isFinished() {
		return finished;
	}
	
	@Override
	public String getStateMessage() {
		return message;
	}
	
	@Override
	public boolean isError() {
		return error;
	}
	
	@Override
	public String getErrorMessage() {
		return errorMessage;
	}
	
	public void setFinished() {
		finished = true;
		notifyObservers();
	}
	
	public void setMessage(String message) {
		this.message = message;
		notifyObservers();
	}
	
	public void setError(String errorMessage) {
		this.errorMessage = errorMessage;
		error = true;
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
}
