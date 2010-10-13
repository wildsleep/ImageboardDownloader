package net.wildsleep.imgdl.gui;

import java.awt.BorderLayout;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.util.TooManyListenersException;

import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JPanel;

import net.wildsleep.imgdl.TaskManager;
import net.wildsleep.imgdl.ValueStrategy;
import net.wildsleep.imgdl.taskfactory.IqdbFileTaskFactory;
import net.wildsleep.imgdl.taskfactory.IqdbPostPrioritization;
import net.wildsleep.imgdl.taskfactory.IqdbUrlTaskFactory;
import net.wildsleep.imgdl.taskfactory.PostUrlTaskFactory;

public class DownloadFrameFactory {
	
	private static final File DEFAULT_DIRECTORY = new File(".");
	private static final String FRAME_TITLE = "Drag & Drop Download/Tagger";
	private static final int IQDB_MINIMUM_SIMILARITY = 85;
	private static final IqdbPostPrioritization IQDB_PRIORITIZATION = IqdbPostPrioritization.RESOLUTION;
	
	public JFrame makeImageboardDownloadFrame() {
		final FileDownloadPanel downloadPanel = new FileDownloadPanel();
		downloadPanel.setDirectory(DEFAULT_DIRECTORY);
		
		JPanel progressPanel = new JPanel();
		progressPanel.setLayout(new BoxLayout(progressPanel, BoxLayout.Y_AXIS));
		progressPanel.setOpaque(false);
		downloadPanel.add(progressPanel, BorderLayout.CENTER);
		
		final TaskManager taskManager = new TaskManager(progressPanel);
		URLTaskTransferHandler transferHandler = new URLTaskTransferHandler(taskManager);
		downloadPanel.setTransferHandler(transferHandler);
		try {
			downloadPanel.getDropTarget().addDropTargetListener(downloadPanel);
		} catch (TooManyListenersException e) {
			e.printStackTrace();
		}
		
		downloadPanel.addMouseListener(new MouseListener() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 2) {
					taskManager.clearFinishedTasks();
				}
			}
			@Override
			public void mouseEntered(MouseEvent arg0) { }
			@Override
			public void mouseExited(MouseEvent arg0) { }
			@Override
			public void mousePressed(MouseEvent arg0) { }
			@Override
			public void mouseReleased(MouseEvent arg0) { }
		});
		
		ValueStrategy<File> directoryStrategy = new ValueStrategy<File>() {
			@Override
			public File get() {
				return downloadPanel.getDirectory();
			}};
		
		transferHandler.addTaskFactory(new PostUrlTaskFactory(directoryStrategy));
		transferHandler.addTaskFactory(new IqdbUrlTaskFactory(directoryStrategy, IQDB_MINIMUM_SIMILARITY, IQDB_PRIORITIZATION));
		transferHandler.addTaskFactory(new IqdbFileTaskFactory(directoryStrategy, IQDB_MINIMUM_SIMILARITY, IQDB_PRIORITIZATION));
		
		JFrame frame = new JFrame(FRAME_TITLE);
		frame.getContentPane().add(downloadPanel);
		return frame;
	}
}
