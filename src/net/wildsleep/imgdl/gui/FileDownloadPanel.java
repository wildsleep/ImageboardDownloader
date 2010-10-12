package net.wildsleep.imgdl.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class FileDownloadPanel extends JPanel implements DropTargetListener {
	private static final long serialVersionUID = 1L;
	
	private static final Dimension PANEL_SIZE     = new Dimension(260, 180);
	private static final Dimension DIR_FIELD_SIZE = new Dimension(0, 25);
	
	private static final String DEFAULT_PATH = System.getProperty("user.home");
	
	private static final Color COLOR_DEFAULT = Color.LIGHT_GRAY;
	private static final Color COLOR_DRAG    = Color.DARK_GRAY;
	
	private JTextField directoryField;
	private File directory;
	
	public FileDownloadPanel() {
		setBackground(COLOR_DEFAULT);
		setPreferredSize(PANEL_SIZE);
		
		this.setLayout(new BorderLayout(5,5));
		
		directory = new File(DEFAULT_PATH);
		
		directoryField = new JTextField(DEFAULT_PATH);
		directoryField.setDragEnabled(false);
		directoryField.setEnabled(false);
		directoryField.setMaximumSize(DIR_FIELD_SIZE);
		directoryField.addMouseListener(new MouseListener() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 2) {
					JFileChooser chooser = new JFileChooser();
					chooser.setCurrentDirectory(directory);
					chooser.setDialogTitle("Select a folder to download into.");
					chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
					chooser.setAcceptAllFileFilterUsed(false);
					if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
						setDirectory(chooser.getSelectedFile());
					}
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
		
		this.add(directoryField, BorderLayout.NORTH);
	}
	
	@Override
	public void dragEnter(DropTargetDragEvent arg0) {
		setBackground(COLOR_DRAG);	
	}

	@Override
	public void dragExit(DropTargetEvent arg0) {
		setBackground(COLOR_DEFAULT);
	}

	@Override
	public void dragOver(DropTargetDragEvent arg0) { }

	@Override
	public void drop(DropTargetDropEvent arg0) {
		setBackground(COLOR_DEFAULT);
	}

	@Override
	public void dropActionChanged(DropTargetDragEvent arg0) { }
	
	public File getDirectory() {
		return directory;
	}

	public void setDirectory(File directory) {
		this.directory = directory;
		directoryField.setText(directory.getAbsolutePath());
	}
}