package net.wildsleep.imgdl.gui;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.swing.TransferHandler;

import net.wildsleep.imgdl.TaskManager;
import net.wildsleep.imgdl.task.Task;
import net.wildsleep.imgdl.taskfactory.TaskFactory;

public class URLTaskTransferHandler extends TransferHandler {
	
	private static final long serialVersionUID = 1L;
	
	private TaskManager taskManager;
	private List<TaskFactory> taskFactories;
	
	private static DataFlavor urlFlavor;
	static {
		try { 
			urlFlavor = new DataFlavor("application/x-java-url; class=java.net.URL"); 
		} catch (ClassNotFoundException cnfe) {
			cnfe.printStackTrace(); 
		}
	}
	
	public URLTaskTransferHandler(TaskManager taskManager) {
		this.taskManager = taskManager;
		this.taskFactories = new ArrayList<TaskFactory>();
	}
	
	@Override
	public boolean canImport(TransferHandler.TransferSupport info) {
		if (!info.isDataFlavorSupported(urlFlavor)) {
			return false;
		}
		return true;
	}
	
	@Override
	public boolean importData(TransferHandler.TransferSupport info) {
				
		if (!info.isDrop()) {
			return false;
		}
		
		Transferable t = info.getTransferable();
		URL url;

		try {
			url = (URL)t.getTransferData(urlFlavor);			
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		
		for (TaskFactory taskFactory : taskFactories) {
			if (taskFactory.canHandle(url)) {
				Task task = taskFactory.create(url);
				if (task != null)
					taskManager.addTask(task);
				return true;
			}
		}
		
		return false;
	}
	
	public void addTaskFactory(TaskFactory taskFactory) {
		taskFactories.add(taskFactory);
	}
	
}
