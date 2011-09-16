package net.wildsleep.imgdl.postparser;

import java.net.URL;


public class PostParserFactory {
	public ImageboardParser getParser(URL url) {
		for (Imageboard imageboard : Imageboard.values()) {
			if (url.getHost().contains(imageboard.hostname())) {
				if (imageboard.isValidPostURL(url)) {
					return imageboard.getPostParser(url);
				}
			}
		}
		return null;
	}
	
	public static boolean canHandle(URL url) {
		for (Imageboard imageboard : Imageboard.values()) {
			if (url.getHost().contains(imageboard.hostname())) {
				if (imageboard.isValidPostURL(url)) {
					return true;
				}
			}
		}
		return false;
	}
}
