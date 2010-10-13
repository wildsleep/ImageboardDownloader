package net.wildsleep.imgdl.taskfactory;

import java.io.File;
import java.net.URL;
import java.util.Collection;

import net.wildsleep.imgdl.PostInfo;
import net.wildsleep.imgdl.ValueStrategy;
import net.wildsleep.imgdl.postparser.PostParserFactory;
import net.wildsleep.imgdl.task.CompositeTask;
import net.wildsleep.imgdl.task.ConditionalTask;
import net.wildsleep.imgdl.task.FileDownloadTask;
import net.wildsleep.imgdl.task.JpegConvertTask;
import net.wildsleep.imgdl.task.JpegTagTask;
import net.wildsleep.imgdl.task.PostParseTask;
import net.wildsleep.imgdl.task.Task;

public class PostUrlTaskFactory implements UrlTaskFactory {

	private final ValueStrategy<File> directoryStrategy;
	
	public PostUrlTaskFactory(ValueStrategy<File> directoryStrategy) {
		this.directoryStrategy = directoryStrategy;
	}
	
	@Override
	public Task create(URL url) {
		final PostParseTask parseTask = new PostParseTask(url);

		ValueStrategy<URL> getImageUrlStrategy = new ValueStrategy<URL>() {
			@Override
			public URL get() {
				return parseTask.getPostInfo().getImageUrl();
			}};
		ValueStrategy<Collection<String>> getKeywordsStrategy = new ValueStrategy<Collection<String>>() {
			@Override
			public Collection<String> get() {
				return parseTask.getPostInfo().getKeywords();
			}};
		ValueStrategy<File> getDestinationStrategy = new ValueStrategy<File>() {
			@Override
			public File get() {
				File directory = directoryStrategy.get();
				PostInfo postInfo = parseTask.getPostInfo();
				String filename = directory.getPath() + File.separator + postInfo.getId();
				String extension = postInfo.getImageUrl().getPath();
				extension = extension.substring(extension.lastIndexOf("."));
				return new File(filename + extension);
			}};
			
		final FileDownloadTask imageDownloadTask = new FileDownloadTask(getImageUrlStrategy, getDestinationStrategy);
		final JpegConvertTask convertTask = new JpegConvertTask(getDestinationStrategy);
		
		ValueStrategy<Boolean> getConvertedStrategy = new ValueStrategy<Boolean>() {
			@Override
			public Boolean get() {
				return convertTask.isConverted();
			}};
		ValueStrategy<File> getConvertedDestinationStrategy = new ValueStrategy<File>() {
			@Override
			public File get() {
				return convertTask.getResultFile();
			}};
		
		final JpegTagTask tagTask = new JpegTagTask(getConvertedDestinationStrategy, getKeywordsStrategy);
		final ConditionalTask conditionalTagTask = new ConditionalTask(tagTask, getConvertedStrategy);
		
		final CompositeTask task = new CompositeTask();
		task.addTask(parseTask);
		task.addTask(imageDownloadTask);
		task.addTask(convertTask);
		task.addTask(conditionalTagTask);
		return task;
	}
	
	@Override
	public boolean canHandle(URL url) {
		return PostParserFactory.canHandle(url);
	}

}
