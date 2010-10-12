package net.wildsleep.imgdl;

import java.util.HashMap;
import java.util.Map;

import javax.swing.JLabel;
import javax.swing.JPanel;

import net.wildsleep.imgdl.task.Task;
import net.wildsleep.imgdl.taskstate.TaskState;

public class TaskManager implements TaskObserver {
	
	private JPanel panel;
	
	private Map<Task, JLabel> downloads;
	
	public TaskManager(JPanel panel) {
		this.panel = panel;
		this.downloads = new HashMap<Task, JLabel>();
	}
	
	public void addTask(Task task) {
		task.getState().register(this);
		JLabel taskLabel = new JLabel();
		panel.add(taskLabel);
		downloads.put(task, taskLabel);
		Thread thread = new Thread(new TaskRunner(task));
		thread.start();
	}
	
	@Override
	public void updateTask(Task task) {
		JLabel taskLabel = downloads.get(task);
		TaskState state = task.getState();
		if (state.isFinished()) {
			panel.remove(taskLabel);
			downloads.remove(task);
			panel.revalidate();
			panel.repaint();
		}
		else if (state.isError()) {
			taskLabel.setText(state.getErrorMessage());
		} else {
			taskLabel.setText(state.getStateMessage());
		}
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
