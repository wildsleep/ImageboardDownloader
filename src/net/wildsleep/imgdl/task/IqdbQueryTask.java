package net.wildsleep.imgdl.task;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.htmlparser.jericho.Element;
import net.htmlparser.jericho.Source;
import net.wildsleep.imgdl.IqdbResult;
import net.wildsleep.imgdl.ValueStrategy;
import net.wildsleep.imgdl.taskstate.TaskState;
import net.wildsleep.imgdl.taskstate.TaskStateImpl;

import com.myjavatools.web.ClientHttpRequest;

public class IqdbQueryTask implements Task {
	
	private static final boolean[] SERVICES = {
		false,	// Haruhi doujins
		false,	// Danbooru
		true,	// Konachan
		true,	// Oreno.imouto.org
		true,	// Gelbooru
		true,	// Sankaku Channel
		false	// Shuushuu Image Board
	};
	
	private static boolean IGNORE_COLORS = false;
	
	private static final Pattern DIMENSION_REGEX   = Pattern.compile("([0-9]*)×([0-9]*) \\[[A-za-z]*\\]");
	private static final Pattern SIMILARITY_REGEX  = Pattern.compile("([0-9]*)% similarity");
	private static final Pattern SEARCH_FAIL_REGEX = Pattern.compile("No relevant matches");
	
	private ValueStrategy<File> fileStrategy;
	private ValueStrategy<URL> urlStrategy;
	private TaskStateImpl state;
	
	private List<IqdbResult> results;
	
	public static IqdbQueryTask makeFileQueryTask(ValueStrategy<File> fileStrategy) {
		return new IqdbQueryTask(fileStrategy, null);
	}
	
	public static IqdbQueryTask makeUrlQueryTask(ValueStrategy<URL> urlStrategy) {
		return new IqdbQueryTask(null, urlStrategy);
	}
	
	private IqdbQueryTask(ValueStrategy<File> fileStrategy, ValueStrategy<URL> urlStrategy) {
		this.fileStrategy = fileStrategy;
		this.urlStrategy = urlStrategy;
		this.state = new TaskStateImpl(this);
		this.results = new ArrayList<IqdbResult>();
	}
	
	@Override
	public void perform() {
		try {
			state.setMessage("Querying IQDB...");
			
			ClientHttpRequest request = new ClientHttpRequest("http://iqdb.org");
			request.setParameter("MAX_FILE_SIZE", "8388608");
			for (int index = 0; index < SERVICES.length; index++) {
				if (SERVICES[index])
					request.setParameter("service[]", String.format("%d", index));
			}
			if (fileStrategy != null) {
				request.setParameter("file", fileStrategy.get());
				request.setParameter("url", "http://");
			} else {
				request.setParameter("url", urlStrategy.get());
			}
			if (IGNORE_COLORS)
				request.setParameter("forcegray", "on");
			
			URLConnection response = request.post();
			state.setMessage("Parsing response...");
			
			Source source = new Source(response);
			
			Element divPages = source.getElementById("pages");
			if (divPages == null)
				throw new IOException("IQDB parser error");
			if (!"div".equals(divPages.getName()))
				throw new IOException("IQDB parser error");
			if (divPages.getChildElements().size() < 2)
				throw new IOException("IQDB parser error");
			Element divFirstMatch = divPages.getChildElements().get(1);
			if (!"div".equals(divFirstMatch.getName()))
				throw new IOException("IQDB parser error");
			if (!isFailedSearch(divFirstMatch)) {
				Iterator<Element> iterPages = divPages.getChildElements().iterator();
				iterPages.next(); // Ignore first child ("Your image")
				while (iterPages.hasNext()) {
					Element subElement = iterPages.next();
					if (!"div".equals(subElement.getName()))
						continue;
					results.add(parseResultDiv(subElement));
				}
			}
			state.setMessage("Finished.");
			state.setFinished();		
			
		} catch (IOException e) {
			e.printStackTrace();
			if (fileStrategy != null) {
				state.setError("IQDB lookup failed on file " + fileStrategy.get().getName() + ".");
			} else {
				state.setError("IQDB lookup failed on url " + urlStrategy.get().getFile() + ".");
			}
		}
	}
	
	private boolean isFailedSearch(Element div) throws IOException {
		if (div.getChildElements().size() != 1)
			throw new IOException("IQDB parser error");
		
		Element table = div.getChildElements().get(0);
		if (table.getChildElements().size() < 1)
			throw new IOException("IQDB parser error");
		
		Element trHeader = table.getChildElements().get(0);
		if (trHeader.getChildElements().size() != 1)
			throw new IOException("IQDB parser error");
		
		String headerString = trHeader.getContent().getTextExtractor().toString();
		Matcher headerMatcher = SEARCH_FAIL_REGEX.matcher(headerString);
		if (headerMatcher.matches())
			return true;
		
		return false;
	}
	
	private IqdbResult parseResultDiv(Element div) throws IOException {
		String urlString;
		int width = 0;
		int height = 0;
		int similarity = 0;
		
		if (div.getChildElements().size() != 1)
			throw new IOException("IQDB parser error");
		
		Element table = div.getChildElements().get(0);
		if (table.getChildElements().size() != 5)
			throw new IOException("IQDB parser error");
		
		{
			Element trImage = table.getChildElements().get(1);
			if (trImage.getChildElements().size() != 1)
				throw new IOException("IQDB parser error");
			Element tdImage = trImage.getChildElements().get(0);
			if (tdImage.getChildElements().size() != 1)
				throw new IOException("IQDB parser error");
			Element linkImage = tdImage.getChildElements().get(0);
			urlString = linkImage.getAttributeValue("href");
		}
		
		{
			Element trDimensions = table.getChildElements().get(3);
			if (trDimensions.getChildElements().size() != 1)
				throw new IOException("IQDB parser error");
			Element tdDimensions = trDimensions.getChildElements().get(0);
			String dimensionString = tdDimensions.getContent().getTextExtractor().toString();
			Matcher dimensionMatcher = DIMENSION_REGEX.matcher(dimensionString);
			if (!dimensionMatcher.matches()) 
				throw new IOException("IQDB parser error");
			width = new Integer(dimensionMatcher.group(1));
			height = new Integer(dimensionMatcher.group(2));
		}
		
		{
			Element trSimilarity = table.getChildElements().get(4);
			if (trSimilarity.getChildElements().size() != 1)
				throw new IOException("IQDB parser error");
			Element tdSimilarity = trSimilarity.getChildElements().get(0);
			String similarityString = tdSimilarity.getContent().getTextExtractor().toString();
			Matcher similarityMatcher = SIMILARITY_REGEX.matcher(similarityString);
			if (!similarityMatcher.matches())
				throw new IOException("IQDB parser error");
			similarity = new Integer(similarityMatcher.group(1));
		}
		
		return new IqdbResult(new URL(urlString), width, height, similarity);
	}

	@Override
	public TaskState getState() {
		return state;
	}

	public List<IqdbResult> getResults() {
		return results;
	}
}
