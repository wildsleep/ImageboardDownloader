package net.wildsleep.imgdl.gui;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.swing.TransferHandler;

import net.wildsleep.imgdl.TaskManager;
import net.wildsleep.imgdl.task.Task;
import net.wildsleep.imgdl.taskfactory.FileTaskFactory;
import net.wildsleep.imgdl.taskfactory.UrlTaskFactory;

public class URLTaskTransferHandler extends TransferHandler {
	
	private static final long serialVersionUID = 1L;
	
	private TaskManager taskManager;
	private List<UrlTaskFactory> urlTaskFactories;
	private List<FileTaskFactory> fileTaskFactories;
	
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
		this.urlTaskFactories = new ArrayList<UrlTaskFactory>();
		this.fileTaskFactories = new ArrayList<FileTaskFactory>();
	}
	
	@Override
	public boolean canImport(TransferHandler.TransferSupport info) {
		if (info.isDataFlavorSupported(urlFlavor)) {
			return true;
		}
		if (info.isDataFlavorSupported(DataFlavor.javaFileListFlavor)) {
			return true;
		}
		return false;
	}
	
	@Override
	public boolean importData(TransferHandler.TransferSupport info) {
				
		if (!info.isDrop()) {
			return false;
		}
		
		Transferable t = info.getTransferable();
		for (DataFlavor flavor : info.getDataFlavors()) {
			if (flavor.equals(urlFlavor)) {
				try {
					URL url = (URL)t.getTransferData(urlFlavor);			
					for (UrlTaskFactory taskFactory : urlTaskFactories) {
						if (taskFactory.canHandle(url)) {
							Task task = taskFactory.create(url);
							if (task != null) {
								taskManager.addTask(task);
								return true;
							}
						}
					}
				} catch (UnsupportedFlavorException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
				return false;
				
			} else if (flavor.equals(DataFlavor.javaFileListFlavor)) {
				
				try {
					List<?> fileList = (List<?>)t.getTransferData(DataFlavor.javaFileListFlavor);
					for (Object obj : fileList) {
						if (!(obj instanceof File))
							continue;
						File file = (File)obj;
						for (FileTaskFactory taskFactory : fileTaskFactories) {
							if (taskFactory.canHandle(file)) {
								Task task = taskFactory.create(file);
								if (task != null) {
									taskManager.addTask(task);
									return true;
								}
							}
						}
					}
				} catch (UnsupportedFlavorException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
				return false;
			}
		}
		return false;
	}
	
	public void addTaskFactory(UrlTaskFactory taskFactory) {
		urlTaskFactories.add(taskFactory);
	}
	
	public void addTaskFactory(FileTaskFactory taskFactory) {
		fileTaskFactories.add(taskFactory);
	}
	
}
