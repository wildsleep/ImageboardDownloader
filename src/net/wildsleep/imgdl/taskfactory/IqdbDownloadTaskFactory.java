package net.wildsleep.imgdl.taskfactory;

import java.io.File;
import java.net.URL;
import java.util.Iterator;
import java.util.List;

import net.wildsleep.imgdl.IqdbResult;
import net.wildsleep.imgdl.ValueStrategy;
import net.wildsleep.imgdl.task.CompositeTask;
import net.wildsleep.imgdl.task.ConditionalTask;
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
				
				Iterator<IqdbResult> iter = results.iterator();
				IqdbResult best = iter.next();
				
				while (iter.hasNext()) {
					IqdbResult current = iter.next();
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
		
			ValueStrategy<Boolean> resultsFoundStrategy = new ValueStrategy<Boolean>() {
				@Override
				public Boolean get() {
					List<IqdbResult> results = queryTask.getResults();
					if (results.size() == 0)
						return false;				
					return true;
				}};
		
		final TaskFactoryTask postDownloadTask = new TaskFactoryTask(new PostDownloadTaskFactory(directoryStrategy), getPostUrlStrategy);
		final ConditionalTask conditionalDownloadTask = new ConditionalTask(postDownloadTask, resultsFoundStrategy);
		
		CompositeTask task = new CompositeTask();
		task.addTask(queryTask);
		task.addTask(conditionalDownloadTask);
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
