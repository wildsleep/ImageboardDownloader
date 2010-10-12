package net.wildsleep.imgdl.postparser;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import net.htmlparser.jericho.Element;
import net.htmlparser.jericho.Source;
import net.htmlparser.jericho.TextExtractor;
import net.wildsleep.imgdl.PostInfo;

public class SankakuPostParser implements ImageboardParser {
	
	private URL url;
	
	private static final String ID_PREFIX = "sankaku";
	private static final Pattern ID_REGEX = Pattern.compile("/post/similar\\?id=[0-9]*");
	private static final Pattern RATING_REGEX = Pattern.compile("Rating: (Explicit|Questionable|Safe)");
	private static final Map<String, String> TAGTYPE_PREFIX_MAP = new HashMap<String, String>();
	static {
		TAGTYPE_PREFIX_MAP.put("tag-type-general", "");
		TAGTYPE_PREFIX_MAP.put("tag-type-artist", "artist: ");
		TAGTYPE_PREFIX_MAP.put("tag-type-character", "char: ");
		TAGTYPE_PREFIX_MAP.put("tag-type-copyright", "copy: ");
	}
	
	public SankakuPostParser(URL url) {
		this.url = url;
	}
	
	public PostInfo getInfo() throws IOException {
		
		PostInfo info = null;
		String imageUrl = null;
		List<String> keywords = new ArrayList<String>();
		String id = "";
		
		Source source = new Source(url);
		TextExtractor extractor;
		
		// Tag list
		Element ulTagList = source.getElementById("tag-sidebar");
		if (!"ul".equals(ulTagList.getName()))
			throw new IOException("Post parser error");
		for (Element liTag : ulTagList.getChildElements()) {
			if (!"li".equals(liTag.getName()))
				continue;
			String tagType = liTag.getAttributeValue("class");
			String tagTypePrefix = TAGTYPE_PREFIX_MAP.get(tagType);
			
			List<Element> subElements = liTag.getChildElements();
			if (subElements.size() != 3)
				throw new IOException("Post parser error");
			if (!"a".equals(subElements.get(0).getName()))
				throw new IOException("Post parser error");
			if (!"a".equals(subElements.get(1).getName()))
				throw new IOException("Post parser error");
			if (!"span".equals(subElements.get(2).getName()))
				throw new IOException("Post parser error");
			Element aTagName = subElements.get(0);
			
			extractor = aTagName.getContent().getTextExtractor();
			extractor.setConvertNonBreakingSpaces(true);
			String tagName = extractor.toString();
			
			keywords.add(tagTypePrefix + tagName);
		}
		
		Element divStats = source.getElementById("stats");
		Element ulStats = divStats.getContent().getFirstElement("ul");
		List<Element> liStats = ulStats.getChildElements();
		
		// Rating
		for (Element liElement : liStats) {
			extractor = liElement.getContent().getTextExtractor();
			extractor.setConvertNonBreakingSpaces(true);
			String str = extractor.toString();
			if (RATING_REGEX.matcher(str).matches()) {
				keywords.add(str.toLowerCase());
				break;
			}
		}
		
		// Post ID
		Element aSimilarPost = source.getNextStartTag(0, "href", ID_REGEX).getElement();
		id = aSimilarPost.getAttributeValue("href").replaceFirst("/post/similar\\?id=", ID_PREFIX);
		keywords.add("id: " + id);
		
		// Image URL
		Element aImageUrl = source.getElementById("highres");
		imageUrl = aImageUrl.getAttributeValue("href");
		
		info = new PostInfo(new URL(imageUrl), keywords, id);
		
		return info;
	}
}
