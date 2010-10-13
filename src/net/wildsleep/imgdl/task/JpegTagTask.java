package net.wildsleep.imgdl.task;

import java.io.File;
import java.util.Collection;

import net.wildsleep.imgdl.ValueStrategy;
import net.wildsleep.imgdl.jpeg.JpegTagger;
import net.wildsleep.imgdl.taskstate.TaskState;
import net.wildsleep.imgdl.taskstate.TaskStateImpl;

public class JpegTagTask implements Task {
	
	private ValueStrategy<File> fileStrategy;
	private ValueStrategy<Collection<String>> keywordStrategy;
	
	private TaskStateImpl state;
	
	public JpegTagTask(ValueStrategy<File> fileStrategy, ValueStrategy<Collection<String>> keywordStrategy) {
		this.fileStrategy = fileStrategy;
		this.keywordStrategy = keywordStrategy;
		this.state = new TaskStateImpl(this);
	}
	
	@Override
	public void perform() {
		File file = fileStrategy.get();
		File temp = new File(file.getAbsolutePath() + ".tmp");
		Collection<String> keywords = keywordStrategy.get();
		
		state.setMessage("Tagging file...");
		if (file.renameTo(temp)) {
			JpegTagger.tag(temp, file, keywords);
			temp.delete();
			
			state.setMessage("Finished.");
			state.setFinished();
		}
	}

	@Override
	public TaskState getState() {
		return state;
	}
}
