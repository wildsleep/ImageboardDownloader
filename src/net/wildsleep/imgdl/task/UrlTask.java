package net.wildsleep.imgdl.task;

import java.net.URL;

import net.wildsleep.imgdl.ValueStrategy;
import net.wildsleep.imgdl.taskfactory.UrlTaskFactory;
import net.wildsleep.imgdl.taskstate.CompositeTaskState;
import net.wildsleep.imgdl.taskstate.TaskState;

public class UrlTask implements Task {

	private UrlTaskFactory taskFactory;
	private ValueStrategy<URL> urlStrategy;
	private CompositeTaskState state;
	
	public UrlTask(UrlTaskFactory taskFactory, ValueStrategy<URL> urlStrategy) {
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
