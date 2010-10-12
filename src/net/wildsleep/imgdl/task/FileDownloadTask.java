package net.wildsleep.imgdl.task;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import net.wildsleep.imgdl.ValueStrategy;
import net.wildsleep.imgdl.taskstate.TaskState;
import net.wildsleep.imgdl.taskstate.TaskStateImpl;

public class FileDownloadTask implements Task {

	private static final int MAX_BUFFER_SIZE = 1024;

	private ValueStrategy<URL> urlStrategy;
	private ValueStrategy<File> outputFileStrategy;
		
	private TaskStateImpl state;
	
	public FileDownloadTask(ValueStrategy<URL> getImageUrlStrategy, ValueStrategy<File> getDestinationStrategy) {
		this.urlStrategy = getImageUrlStrategy;
		this.outputFileStrategy = getDestinationStrategy;
		this.state = new TaskStateImpl(this);
	}

	@Override
	public void perform() {		
		InputStream inputStream = null;
		FileOutputStream outputStream = null;

		try {
			state.setMessage("Connecting to file download location...");

			URL url = urlStrategy.get();	
			HttpURLConnection connection = (HttpURLConnection)url.openConnection();
			connection.connect();
			if (connection.getResponseCode() / 100 != 2) {
				throw new IOException("Connection failed. HTTP Code " + connection.getResponseCode() + " " + connection.getResponseMessage());
			}

			int contentLength = connection.getContentLength();
			if (contentLength < 1) {
				throw new IOException("Invalid content length returned from connection.");
			}

			File outputFile = outputFileStrategy.get();
			File tempFile = new File(outputFile.getAbsolutePath() + ".tmp");
			inputStream = connection.getInputStream();
			outputStream = new FileOutputStream(tempFile);

			state.setMessage("Downloading...");

			while (true) {
				byte[] buffer = new byte[MAX_BUFFER_SIZE];
				int read = inputStream.read(buffer);
				if (read == -1)
					break;
				outputStream.write(buffer, 0, read);
			}
			outputStream.close();
			tempFile.renameTo(outputFile);

			state.setMessage("Download complete.");
			state.setFinished();


		} catch (IOException e) {
			e.printStackTrace();
			state.setError("Error in file download.");
		} finally {
			if (outputStream != null) {
				try { outputStream.close(); } catch (IOException ignore) { }
			}
			if (inputStream != null) {
				try { inputStream.close(); } catch (IOException ignore) { }
			}
		}
	}

	@Override
	public TaskState getState() {
		return state;
	}

}
