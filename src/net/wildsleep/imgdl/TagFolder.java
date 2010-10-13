package net.wildsleep.imgdl;

import java.io.File;

import net.wildsleep.imgdl.task.Task;
import net.wildsleep.imgdl.taskfactory.IqdbFileTaskFactory;
import net.wildsleep.imgdl.taskfactory.IqdbPostPrioritization;

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
		
		IqdbFileTaskFactory factory = new IqdbFileTaskFactory(directoryStrategy, minimumSimilarity, postPrioritization);
		for (File file : sourceDir.listFiles())
		{
			Task task = factory.create(file);
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
}
