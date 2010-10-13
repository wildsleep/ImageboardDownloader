package net.wildsleep.imgdl;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.swing.JLabel;
import javax.swing.JPanel;

import net.wildsleep.imgdl.task.Task;
import net.wildsleep.imgdl.taskstate.TaskState;

public class TaskManager implements TaskObserver {
	
	private JPanel panel;
	
	private Map<Task, JLabel> tasks;
	
	public TaskManager(JPanel panel) {
		this.panel = panel;
		this.tasks = new HashMap<Task, JLabel>();
	}
	
	public void addTask(Task task) {
		task.getState().register(this);
		JLabel taskLabel = new JLabel();
		panel.add(taskLabel);
		tasks.put(task, taskLabel);
		Thread thread = new Thread(new TaskRunner(task));
		thread.start();
	}
	
	public void clearFinishedTasks() {
		Set<Task> taskSet = new HashSet<Task>(tasks.keySet());
		for (Task task : taskSet) {
			final TaskState state = task.getState();
			if (state.isError())
				removeTask(task);
			else if (state.isFinished())
				removeTask(task);
		}
	}
	
	@Override
	public void updateTask(Task task) {
		JLabel taskLabel = tasks.get(task);
		TaskState state = task.getState();
		if (state.isFinished()) {
			removeTask(task);
		} else if (state.isError()) {
			taskLabel.setText(state.getErrorMessage());
		} else {
			taskLabel.setText(state.getStateMessage());
		}
	}
	
	private void removeTask(Task task) {
		JLabel taskLabel = tasks.get(task);
		panel.remove(taskLabel);
		tasks.remove(task);
		panel.revalidate();
		panel.repaint();
	}
	
	private class TaskRunner implements Runnable {
		private final Task task;
		public TaskRunner(Task task) {
			this.task = task;
		}
		@Override
		public void run() {
			task.perform();
		}
	}
}
