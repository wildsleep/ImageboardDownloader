package net.wildsleep.imgdl.jpeg;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Iterator;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.FileImageOutputStream;
import javax.imageio.stream.ImageInputStream;

public class JpegConverter {
	
	// If the source file has a transparent background, it will be replaced with this color
	private static Color TRANSPARENT_BACKGROUND_COLOR = Color.white;
	
	// Indicates the level of JPEG compression to use.  0 = maximum compression, 1 = maximum quality
	private static float JPEG_QUALITY_LEVEL = 1;
	
	/*
	 * Converts the specified file to the JPEG format.
	 * Returns a boolean representing whether the conversion was successful.
	 * Conversion will automatically fail on animated source images.
	 */
	public static boolean convert(File original, File output) {
		boolean result = false;
		if (isJpeg(original)) {
			InputStream inputStream = null;
			OutputStream outputStream = null;
			try {
				inputStream = new FileInputStream(original);
				outputStream = new FileOutputStream(output);
				byte[] buf = new byte[1024];
				int len;
				while ((len = inputStream.read(buf)) > 0){
					outputStream.write(buf, 0, len);
				}
				result = true;
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				if (inputStream != null)
					try { inputStream.close(); } catch (IOException ignore) { }
				if (outputStream != null) {
					try { outputStream.close(); } catch (IOException ignore) { }
				}
			}
		}
		else {
			ImageWriter writer = null;
			FileImageOutputStream outputStream = null;
			try {
				BufferedImage originalImage = ImageIO.read(original);
				BufferedImage rgbImage = new BufferedImage(originalImage.getWidth(), originalImage.getHeight(), BufferedImage.TYPE_INT_RGB);
				Graphics g = rgbImage.createGraphics();
				g.drawImage(originalImage, 0, 0, rgbImage.getWidth(), rgbImage.getHeight(), TRANSPARENT_BACKGROUND_COLOR, null);
				
				Iterator<ImageWriter> iter = ImageIO.getImageWritersByFormatName("jpeg");
				writer = (ImageWriter)iter.next();
				
				ImageWriteParam param = writer.getDefaultWriteParam();
				param.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
				param.setCompressionQuality(JPEG_QUALITY_LEVEL);

				outputStream = new FileImageOutputStream(output);
				writer.setOutput(outputStream);
				IIOImage image = new IIOImage(rgbImage, null, null);
				writer.write(null, image, param);
				result = true;
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				if (writer != null)
					writer.dispose();
				if (outputStream != null)
					try { outputStream.close(); } catch (IOException ignore) { }
			}
		}
		return result;
	}

	public static boolean isJpeg(File file) {
		boolean result = false;
		ImageInputStream in = null;
		try {
			in = ImageIO.createImageInputStream(file);
			Iterator<ImageReader> readers = ImageIO.getImageReaders(in);

			if(!readers.hasNext()) {
				throw new RuntimeException("Could not read image data for JPEG conversion.");
			} else {
				ImageReader reader = readers.next();
				if("jpeg".equalsIgnoreCase(reader.getFormatName())) {
					result = true;
				}
			}
		} catch (IOException e) {
			System.err.println("Could not read file for JPEG conversion.");
			e.printStackTrace();
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

		return result;
	}
	
	public static boolean isAnimated(File file) {
		boolean result = false;
		ImageInputStream in = null;
		try {
			in = ImageIO.createImageInputStream(file);
			Iterator<ImageReader> readers = ImageIO.getImageReaders(in);
			ImageReader reader = readers.next();
			reader.setInput(in);
			int numImages = reader.getNumImages(true);
			result = (numImages != 1);
		} catch (IOException e) {
			System.err.println("Could not read file for JPEG conversion.");
			e.printStackTrace();
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return result;
	}
}