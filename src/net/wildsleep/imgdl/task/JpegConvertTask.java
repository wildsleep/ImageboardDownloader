package net.wildsleep.imgdl.task;

import java.io.File;

import net.wildsleep.imgdl.ValueStrategy;
import net.wildsleep.imgdl.jpeg.JpegConverter;
import net.wildsleep.imgdl.taskstate.TaskState;
import net.wildsleep.imgdl.taskstate.TaskStateImpl;

public class JpegConvertTask implements Task {
	
	private ValueStrategy<File> fileStrategy;
	
	private boolean converted;
	private File resultFile;
	
	private TaskStateImpl state;
	
	public JpegConvertTask(ValueStrategy<File> fileStrategy) {
		this.fileStrategy = fileStrategy;
		this.converted = false;
		this.resultFile = null;
		this.state = new TaskStateImpl(this);
	}
	
	@Override
	public void perform() {
		File source = fileStrategy.get();
		String filename = source.getAbsolutePath();
		File temp = new File(filename + ".tmp");
		File destination = new File(filename.substring(0, filename.lastIndexOf(".")) + ".jpg");	
		
		if (JpegConverter.isAnimated(source)) {
			resultFile = source;
			state.setMessage("Finished (no conversion done).");
			state.setFinished();
		} else {
			state.setMessage("Converting file to JPEG...");
			if (source.renameTo(temp)) {
				JpegConverter.convert(temp, destination);
				temp.delete();
				resultFile = destination;
				converted = true;
				state.setMessage("Finished conversion.");
				state.setFinished();
			}
		}
	}

	@Override
	public TaskState getState() {
		return state;
	}
	
	public boolean isConverted() {
		return converted;
	}
	
	public File getResultFile() {
		return resultFile;
	}

}
