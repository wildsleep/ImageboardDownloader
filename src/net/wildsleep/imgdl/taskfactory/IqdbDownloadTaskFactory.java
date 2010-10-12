package net.wildsleep.imgdl.taskfactory;

import java.io.File;
import java.net.URL;
import java.util.List;

import net.wildsleep.imgdl.IqdbResult;
import net.wildsleep.imgdl.ValueStrategy;
import net.wildsleep.imgdl.task.CompositeTask;
import net.wildsleep.imgdl.task.IqdbQueryTask;
import net.wildsleep.imgdl.task.Task;
import net.wildsleep.imgdl.task.TaskFactoryTask;

public class IqdbDownloadTaskFactory implements TaskFactory {

	private final ValueStrategy<File> directoryStrategy;
	private int minimumSimilarity;
	private PostPrioritization postPrioritization;
	
	public enum PostPrioritization {
		SIMILARITY,
		RESOLUTION;
	}
	
	public IqdbDownloadTaskFactory(ValueStrategy<File> directoryStrategy, int minimumSimilarity, PostPrioritization postPrioritization) {
		this.directoryStrategy = directoryStrategy;
		this.minimumSimilarity = minimumSimilarity;
		this.postPrioritization = postPrioritization;
	}
	
	@Override
	public Task create(final URL url) {
		ValueStrategy<URL> getSourceUrlStrategy = new ValueStrategy<URL>() {
			@Override
			public URL get() {
				return url;
			}};
		final IqdbQueryTask queryTask = IqdbQueryTask.makeUrlQueryTask(getSourceUrlStrategy);
		
		ValueStrategy<URL> getPostUrlStrategy = new ValueStrategy<URL>() {
			@Override
			public URL get() {
				List<IqdbResult> results = queryTask.getResults();
				if (results.size() == 0)
					return null;
				IqdbResult best = results.get(0);
				
				for (int index = 1; index < results.size(); index++) {
					IqdbResult current = results.get(index);
					if (current.getSimilarity() < minimumSimilarity)
						continue;
					if (postPrioritization.equals(PostPrioritization.SIMILARITY)) {
						if (current.getSimilarity() > best.getSimilarity())
							best = current;
					} else {
						if (current.getHeight() * current.getWidth() > best.getHeight() * best.getWidth())
							best = current;
					}
				}
				
				return best.getUrl();
			}};
		
		final TaskFactoryTask postDownloadTask = new TaskFactoryTask(new PostDownloadTaskFactory(directoryStrategy), getPostUrlStrategy);
		
		CompositeTask task = new CompositeTask();
		task.addTask(queryTask);
		task.addTask(postDownloadTask);
		return task;
	}

	@Override
	public boolean canHandle(URL url) {
		String extension = url.getPath();
		extension = extension.substring(extension.lastIndexOf(".") + 1);
		if (extension.equalsIgnoreCase("jpg"))
			return true;
		if (extension.equalsIgnoreCase("jpeg"))
			return true;
		if (extension.equalsIgnoreCase("png"))
			return true;
		if (extension.equalsIgnoreCase("gif"))
			return true;
		return false;
	}

}
