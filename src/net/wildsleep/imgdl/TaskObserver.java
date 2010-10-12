package net.wildsleep.imgdl;

import net.wildsleep.imgdl.task.Task;


public interface TaskObserver {
	public void updateTask(Task task);
}
