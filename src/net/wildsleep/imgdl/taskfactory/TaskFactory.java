package net.wildsleep.imgdl.taskfactory;

import java.net.URL;

import net.wildsleep.imgdl.task.Task;


public interface TaskFactory {
	Task create(URL url);
	boolean canHandle(URL url);
}
