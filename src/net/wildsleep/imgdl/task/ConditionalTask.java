package net.wildsleep.imgdl.task;

import net.wildsleep.imgdl.ValueStrategy;
import net.wildsleep.imgdl.taskstate.FinishedTaskState;
import net.wildsleep.imgdl.taskstate.TaskState;

public class ConditionalTask implements Task {

	private static final TaskState FINISHED_STATE = new FinishedTaskState();
	
	private Task subTask;
	private ValueStrategy<Boolean> booleanStrategy;
	
	public ConditionalTask(Task task, ValueStrategy<Boolean> booleanStrategy) {
		this.subTask = task;
		this.booleanStrategy = booleanStrategy;
	}
	
	@Override
	public void perform() {
		if (booleanStrategy.get())
			subTask.perform();
	}

	@Override
	public TaskState getState() {
		if (booleanStrategy.get())
			return subTask.getState();
		else
			return FINISHED_STATE;
	}

}
