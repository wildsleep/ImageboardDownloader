package net.wildsleep.imgdl.taskfactory;

import java.io.File;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.wildsleep.imgdl.ValueStrategy;
import net.wildsleep.imgdl.postparser.Imageboard;
import net.wildsleep.imgdl.task.Task;
import net.wildsleep.imgdl.task.UrlTask;

public class RedownloadFileTaskFactory implements FileTaskFactory {

	private final ValueStrategy<File> directoryStrategy;
		
	public RedownloadFileTaskFactory(ValueStrategy<File> directoryStrategy) {
		this.directoryStrategy = directoryStrategy;
	}
	
	@Override
	public Task create(final File file) {
		ValueStrategy<URL> getPostUrlStrategy = new ValueStrategy<URL>() {
			@Override
			public URL get() {
				for (Imageboard imageboard : Imageboard.values()) {
					String filename = file.getName();
					String regex = imageboard.idPrefix() + "([0-9]*)\\.[0-9a-z]*\\.tmp";
					Matcher matcher = Pattern.compile(regex).matcher(filename);
					if (matcher.matches()) {
						return imageboard.getPostURL(matcher.group(1));
					}
				}
				return null;
			}};
		
		final UrlTask postDownloadTask = new UrlTask(new PostUrlTaskFactory(directoryStrategy), getPostUrlStrategy);
		return postDownloadTask;
	}

	@Override
	public boolean canHandle(File file) {
		String extension = file.getName();
		extension = extension.substring(extension.lastIndexOf(".") + 1);
		if (!extension.equalsIgnoreCase("tmp"))
			return false;
		return true;
	}

}
