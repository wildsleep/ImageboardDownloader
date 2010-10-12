package net.wildsleep.imgdl;

import java.net.URL;

public class IqdbResult {
	private final URL url;
	private final int width;
	private final int height;
	private final int similarity;
	
	public IqdbResult(URL url, int width, int height, int similarity) {
		this.url = url;
		this.width = width;
		this.height = height;
		this.similarity = similarity;
	}
	
	public URL getUrl() {
		return url;
	}
	
	public int getWidth() {
		return width;
	}
	
	public int getHeight() {
		return height;
	}
	
	public int getSimilarity() {
		return similarity;
	}
	
	@Override
	public String toString() {
		return "IQDB<" + similarity + "% " + width + "x" + height + " " + url + ">";
	}
}
