package net.wildsleep.imgdl;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import net.wildsleep.imgdl.postparser.ImageboardParser;
import net.wildsleep.imgdl.postparser.PostParserFactory;
import net.wildsleep.imgdl.task.IqdbQueryTask;
import net.wildsleep.imgdl.task.Task;

public class Debug {
	
	private static PostParserFactory factory = new PostParserFactory();
	
	public static void main(String[] args) {
		Debug debug = new Debug();
		debug.testIqdb();
	}
	
	public void testIqdb() {
		ValueStrategy<File> fileStrategy = new ValueStrategy<File>() {
			@Override
			public File get() {
				return new File("I:/fap/h/tagged/sankaku795170.jpg");
			}};
		IqdbQueryTask task = IqdbQueryTask.makeFileQueryTask(fileStrategy);
		task.getState().register(new TaskObserver() {
			@Override
			public void updateTask(Task task) {
				System.out.println(task.getState().getStateMessage());
			}});
		task.perform();
		for (IqdbResult result : task.getResults()) {
			System.out.println(result);
		}
	}
	
	public void testImageboardParsers() {
		testImageboardParser("http://gelbooru.com/index.php?page=post&s=view&id=935717");
		testImageboardParser("http://konachan.com/post/show/150");
		testImageboardParser("http://konachan.com/post/show/84374");
		testImageboardParser("http://chan.sankakucomplex.com/post/show/223655");
		testImageboardParser("http://oreno.imouto.org/post/show/42417");
		testImageboardParser("http://oreno.imouto.org/post/show/155718");
	}
	
	private void testImageboardParser(String urlString) {
		try {
			URL posturl = new URL(urlString);
			ImageboardParser parser = factory.getParser(posturl);
			PostInfo info = parser.getInfo();
			System.out.println(info);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
