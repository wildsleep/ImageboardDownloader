package net.wildsleep.imgdl.postparser;

import java.net.MalformedURLException;
import java.net.URL;

public enum Imageboard {
	
	GELBOORU("gelbooru", "gelbooru.com") {
		@Override protected String getPostURLString(String postId) {
			return "http://gelbooru.com/index.php?page=post&s=view&id=" + postId;
		}
		
		@Override protected boolean isValidPostURL(URL url) {
			return url.getQuery().contains("s=view");
		}
		
		@Override public ImageboardParser getPostParser(URL url) {
			return new GelbooruPostParser(url);
		}
	},
	IMOUTO("imouto", "imouto.org") {
		@Override protected String getPostURLString(String postId) {
			return "http://oreno.imouto.org/post/show/" + postId;
		}
		
		@Override protected boolean isValidPostURL(URL url) {
			return url.getPath().contains("post/show/");
		}
		
		@Override public ImageboardParser getPostParser(URL url) {
			return new ImoutoPostParser(url);
		}
	},
	KONACHAN("konachan", "konachan.com") {
		@Override protected String getPostURLString(String postId) {
			return "http://konachan.com/post/show/" + postId;
		}
		
		@Override protected boolean isValidPostURL(URL url) {
			return url.getPath().contains("post/show/");
		}
		
		@Override public ImageboardParser getPostParser(URL url) {
			return new KonachanPostParser(url);
		}
	},
	SANKAKU("sankaku", "sankakucomplex.com") {
		@Override protected String getPostURLString(String postId) {
			return "http://chan.sankakucomplex.com/post/show/" + postId;
		}
		
		@Override protected boolean isValidPostURL(URL url) {
			return url.getPath().contains("post/show/");
		}
		
		@Override public ImageboardParser getPostParser(URL url) {
			return new SankakuPostParser(url);
		}
	};
		
	private String idPrefix;
	private String hostname;
	
	private Imageboard(String idPrefix, String hostname) {
		this.idPrefix = idPrefix;
		this.hostname = hostname;
	}
	
	public String idPrefix() {
		return idPrefix;
	}
	
	public String hostname() {
		return hostname;
	}
	
	public URL getPostURL(String postId) {
		try {
			return new URL(getPostURLString(postId));
		} catch (MalformedURLException e) {
			throw new RuntimeException(e);
		}
	}
	
	protected String getPostURLString(String postId) {
		throw new RuntimeException("This method should be overridden.");
	}
	
	protected boolean isValidPostURL(URL url) {
		throw new RuntimeException("This method should be overridden.");
	}
	
	public ImageboardParser getPostParser(URL url) {
		throw new RuntimeException("This method should be overridden.");
	}
}
