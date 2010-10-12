package net.wildsleep.imgdl.postparser;

import java.net.URL;


public class PostParserFactory {
	public ImageboardParser getParser(URL url) {		
		// http://gelbooru.com/index.php?page=post&s=view&id=######
		if (url.getHost().contains("gelbooru.com")) {
			if (url.getQuery().contains("s=view")) {
				return new GelbooruPostParser(url);
			}
		}
		
		// http://chan.sankakucomplex.com/post/show/######
		if (url.getHost().contains("sankakucomplex.com")) {
			if (url.getPath().contains("post/show/")) {
				return new SankakuPostParser(url);
			}
		}
		
		// http://konachan.com/post/show/######
		if (url.getHost().contains("konachan.com")) {
			if (url.getPath().contains("post/show/")) {
				return new KonachanPostParser(url);
			}
		}
		
		// http://moe.imouto.org/post/show/######
		if (url.getHost().contains("imouto.org")) {
			if (url.getPath().contains("post/show/")) {
				return new ImoutoPostParser(url);
			}
		}
		
		return null;
	}
	
	public static boolean canHandle(URL url) {
		// http://gelbooru.com/index.php?page=post&s=view&id=######
		if (url.getHost().contains("gelbooru.com")) {
			if (url.getQuery().contains("s=view")) {
				return true;
			}
		}
		
		// http://chan.sankakucomplex.com/post/show/######
		if (url.getHost().contains("sankakucomplex.com")) {
			if (url.getPath().contains("post/show/")) {
				return true;
			}
		}
		
		// http://konachan.com/post/show/######
		if (url.getHost().contains("konachan.com")) {
			if (url.getPath().contains("post/show/")) {
				return true;
			}
		}
		
		// http://moe.imouto.org/post/show/######
		if (url.getHost().contains("imouto.org")) {
			if (url.getPath().contains("post/show/")) {
				return true;
			}
		}
		
		return false;
	}
}
