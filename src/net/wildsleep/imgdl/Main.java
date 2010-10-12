package net.wildsleep.imgdl;

import javax.swing.JFrame;

import net.wildsleep.imgdl.gui.DownloadFrameFactory;

public class Main {
	public static void main(String[] args) {
		JFrame frame = new DownloadFrameFactory().makeImageboardDownloadFrame();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
		frame.pack();		
	}
}
