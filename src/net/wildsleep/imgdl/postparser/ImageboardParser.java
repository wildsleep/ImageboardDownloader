package net.wildsleep.imgdl.postparser;

import java.io.IOException;

import net.wildsleep.imgdl.PostInfo;


public interface ImageboardParser {
	
	PostInfo getInfo() throws IOException;
	
}
