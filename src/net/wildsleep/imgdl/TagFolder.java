package net.wildsleep.imgdl;

import java.io.File;
import java.net.URL;
import java.util.Iterator;
import java.util.List;

import net.wildsleep.imgdl.task.CompositeTask;
import net.wildsleep.imgdl.task.ConditionalTask;
import net.wildsleep.imgdl.task.IqdbQueryTask;
import net.wildsleep.imgdl.task.Task;
import net.wildsleep.imgdl.task.UrlTask;
import net.wildsleep.imgdl.taskfactory.IqdbPostPrioritization;
import net.wildsleep.imgdl.taskfactory.PostUrlTaskFactory;

public class TagFolder {
	private static final String SOURCE_FOLDER = "I:/fap/h/tagme";
	private static final String FINISHED_FOLDER = "I:/fap/h/deleteme";
	private static final String DEST_FOLDER = "I:/fap/h/autotagged";
	
	private static final IqdbPostPrioritization postPrioritization = IqdbPostPrioritization.RESOLUTION;
	private static final int minimumSimilarity = 90;

	private static final File sourceDir = new File(SOURCE_FOLDER);
	private static final File finishedDir = new File(FINISHED_FOLDER);
	private static final File destDir = new File(DEST_FOLDER);
	private static final ValueStrategy<File> directoryStrategy = new ValueStrategy<File>() {
		@Override
		public File get() {
			return destDir;
		}};
	
	public static void main(String[] args) {
		System.setProperty( "sun.net.client.defaultReadTimeout", "5000" );
		
		for (final File file : sourceDir.listFiles())
		{
			Task task = createIqdbTask(file);
			task.getState().register(new TaskObserver() {
				@Override
				public void updateTask(Task task) {
					if (task.getState().isError())
						System.out.println(task.getState().getErrorMessage());
					else
						System.out.println(task.getState().getStateMessage());
				}});
			task.perform();

			if (task.getState().isFinished()) {
				File finishedFile = new File(finishedDir.getAbsolutePath() + File.separator + file.getName());
				file.renameTo(finishedFile);
			}
		}
	}
	
	private static Task createIqdbTask(final File file) {
		ValueStrategy<File> sourceStrategy = new ValueStrategy<File>() {
			@Override
			public File get() {
				return file;
			}};
		final IqdbQueryTask queryTask = IqdbQueryTask.makeFileQueryTask(sourceStrategy);
		
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
					if (postPrioritization.equals(IqdbPostPrioritization.SIMILARITY)) {
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
		
		final UrlTask postDownloadTask = new UrlTask(new PostUrlTaskFactory(directoryStrategy), getPostUrlStrategy);
		final ConditionalTask conditionalDownloadTask = new ConditionalTask(postDownloadTask, resultsFoundStrategy);
		
		CompositeTask task = new CompositeTask();
		task.addTask(queryTask);
		task.addTask(conditionalDownloadTask);
		return task;
	}
}
