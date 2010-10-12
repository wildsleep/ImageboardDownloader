package net.wildsleep.imgdl.task;

import net.wildsleep.imgdl.taskstate.TaskState;

public interface Task {
	void perform();
	TaskState getState();
}
