package net.wildsleep.imgdl.jpeg;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.sanselan.ImageReadException;
import org.apache.sanselan.ImageWriteException;
import org.apache.sanselan.formats.jpeg.iptc.IPTCConstants;
import org.apache.sanselan.formats.jpeg.iptc.IPTCRecord;
import org.apache.sanselan.formats.jpeg.iptc.JpegIptcRewriter;
import org.apache.sanselan.formats.jpeg.iptc.PhotoshopApp13Data;


public class JpegTagger {
	public static void tag(File original, File output, Collection<String> tags) {
		InputStream is = null;
		OutputStream os = null;
		try {
			List<Object> records = new ArrayList<Object>();
			List<Object> blocks = new ArrayList<Object>();
			
			for (String tag : tags) {
				records.add(new IPTCRecord(IPTCConstants.IPTC_TYPE_KEYWORDS, tag));
		    }
			
		    PhotoshopApp13Data data = new PhotoshopApp13Data(records, blocks);
		    
		    is = new FileInputStream(original);
		    os = new FileOutputStream(output);
		    JpegIptcRewriter rewriter = new JpegIptcRewriter();
			rewriter.writeIPTC(is, os, data);
		} catch (ImageReadException e) {
			System.err.println("Could not add IPTC tags.");
			e.printStackTrace();
		} catch (ImageWriteException e) {
			System.err.println("Could not add IPTC tags.");
			e.printStackTrace();
		} catch (IOException e) {
			System.err.println("Could not add IPTC tags.");
			e.printStackTrace();
		} finally {
			if (is != null) {
				try { is.close(); } catch (IOException ignore) { }
			}
			if (os != null) {
				try { os.close(); } catch (IOException ignore) { }
			}
		}
	}
}