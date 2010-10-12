package net.wildsleep.imgdl.gui;

import java.awt.BorderLayout;
import java.io.File;
import java.util.TooManyListenersException;

import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JPanel;

import net.wildsleep.imgdl.TaskManager;
import net.wildsleep.imgdl.ValueStrategy;
import net.wildsleep.imgdl.taskfactory.IqdbDownloadTaskFactory;
import net.wildsleep.imgdl.taskfactory.IqdbDownloadTaskFactory.PostPrioritization;
import net.wildsleep.imgdl.taskfactory.PostDownloadTaskFactory;

public class DownloadFrameFactory {
	
	private static final File DEFAULT_DIRECTORY = new File(".");
	private static final String FRAME_TITLE = "Drag & Drop Download/Tagger";
	private static final int IQDB_MINIMUM_SIMILARITY = 85;
	private static final PostPrioritization IQDB_PRIORITIZATION = PostPrioritization.RESOLUTION;
	
	public JFrame makeImageboardDownloadFrame() {
		final FileDownloadPanel downloadPanel = new FileDownloadPanel();
		downloadPanel.setDirectory(DEFAULT_DIRECTORY);
		
		JPanel progressPanel = new JPanel();
		progressPanel.setLayout(new BoxLayout(progressPanel, BoxLayout.Y_AXIS));
		progressPanel.setOpaque(false);
		downloadPanel.add(progressPanel, BorderLayout.CENTER);
		
		TaskManager taskManager = new TaskManager(progressPanel);
		URLTaskTransferHandler transferHandler = new URLTaskTransferHandler(taskManager);
		downloadPanel.setTransferHandler(transferHandler);
		try {
			downloadPanel.getDropTarget().addDropTargetListener(downloadPanel);
		} catch (TooManyListenersException e) {
			e.printStackTrace();
		}
		
		ValueStrategy<File> directoryStrategy = new ValueStrategy<File>() {
			@Override
			public File get() {
				return downloadPanel.getDirectory();
			}};
		
		transferHandler.addTaskFactory(new PostDownloadTaskFactory(directoryStrategy));
		transferHandler.addTaskFactory(new IqdbDownloadTaskFactory(directoryStrategy, IQDB_MINIMUM_SIMILARITY, IQDB_PRIORITIZATION));
		
		JFrame frame = new JFrame(FRAME_TITLE);
		frame.getContentPane().add(downloadPanel);
		return frame;
	}
}
