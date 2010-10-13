package net.wildsleep.imgdl.taskfactory;

import java.io.File;

import net.wildsleep.imgdl.task.Task;


public interface FileTaskFactory {
	Task create(File file);
	boolean canHandle(File file);
}
