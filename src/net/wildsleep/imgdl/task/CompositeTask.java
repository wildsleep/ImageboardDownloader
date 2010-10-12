package net.wildsleep.imgdl.task;

import java.util.ArrayList;
import java.util.List;

import net.wildsleep.imgdl.taskstate.CompositeTaskState;
import net.wildsleep.imgdl.taskstate.TaskState;

public class CompositeTask implements Task {

	private List<Task> tasks;
	private int currentTaskIndex;
	
	private CompositeTaskState state;
	
	public CompositeTask() {
		tasks = new ArrayList<Task>();
		currentTaskIndex = 0;
		state = new CompositeTaskState(this);
	}
	
	public void addTask(Task task) {
		tasks.add(task);
	}
	
	@Override
	public void perform() {
		// Implementation note:
		// The task list may be altered by addTask() during a perform().
		// However, tasks may only be added onto the end of the list, so
		// accessing via index should be safe even with concurrent
		// modifications to the list.
		while (currentTaskIndex < tasks.size()) {
			Task subTask = tasks.get(currentTaskIndex);
			TaskState subState = subTask.getState();
			state.setSubState(subState);
			subTask.perform();
			if (subState.isError())
				return;
			else if (!subState.isFinished())
				return;
			currentTaskIndex++;
		}
		state.setFinished();
	}

	public TaskState getState() {
		return state;
	}

}
