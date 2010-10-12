package net.wildsleep.imgdl.task;

import java.net.URL;

import net.wildsleep.imgdl.ValueStrategy;
import net.wildsleep.imgdl.taskfactory.TaskFactory;
import net.wildsleep.imgdl.taskstate.CompositeTaskState;
import net.wildsleep.imgdl.taskstate.TaskState;

public class TaskFactoryTask implements Task {

	private TaskFactory taskFactory;
	private ValueStrategy<URL> urlStrategy;
	private CompositeTaskState state;
	
	public TaskFactoryTask(TaskFactory taskFactory, ValueStrategy<URL> urlStrategy) {
		this.taskFactory = taskFactory;
		this.urlStrategy = urlStrategy;
		this.state = new CompositeTaskState(this);
	}
	
	@Override
	public void perform() {
		Task subTask = taskFactory.create(urlStrategy.get());
		TaskState subState = subTask.getState();
		state.setSubState(subState);
		subTask.perform();
		if (subState.isFinished())
			state.setFinished();
	}

	@Override
	public TaskState getState() {
		return state;
	}

}
