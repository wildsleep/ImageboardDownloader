package net.wildsleep.imgdl.task;

import java.io.IOException;
import java.net.URL;

import net.wildsleep.imgdl.PostInfo;
import net.wildsleep.imgdl.postparser.ImageboardParser;
import net.wildsleep.imgdl.postparser.PostParserFactory;
import net.wildsleep.imgdl.taskstate.TaskState;
import net.wildsleep.imgdl.taskstate.TaskStateImpl;


public class PostParseTask implements Task {

	private URL url;
	private PostInfo postInfo;

	private TaskStateImpl state;
	
	public PostParseTask(URL url) {
		this.url = url;
		this.postInfo = null;
		this.state = new TaskStateImpl(this);
	}
		
	@Override
	public void perform() {
		state.setMessage("Parsing post...");
		ImageboardParser parser = new PostParserFactory().getParser(url);
		if (parser == null) {
			state.setError("Could not find valid parser");
			return;
		}
		try {
			postInfo = parser.getInfo();
		} catch (IOException e) {
			state.setError(e.getMessage());
			return;
		}
		state.setMessage("Finished.");
		state.setFinished();
		
	}
	
	public PostInfo getPostInfo() {
		return postInfo;
	}

	@Override
	public TaskState getState() {
		return state;
	}
	
}
