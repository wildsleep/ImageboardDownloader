package net.wildsleep.imgdl;

import java.net.URL;
import java.util.Collection;

public class PostInfo {
	private final URL imageUrl;
	private final Collection<String> keywords;
	private final String id;
	
	public PostInfo(URL imageUrl, Collection<String> keywords, String id) {
		this.imageUrl = imageUrl;
		this.keywords = keywords;
		this.id = id;
	}
	
	public URL getImageUrl() {
		return imageUrl;
	}
	
	public Collection<String> getKeywords() {
		return keywords;
	}
	
	public String getId() {
		return id;
	}
	
	@Override
	public String toString() {
		String result = "PostInfo(ID: " + id + " URL: " + imageUrl + " TAGS: ";
		for (String tag : keywords)
			result = result + tag + " ";
		result = result + ")";
		return result;
	}
}