//OOP Project 2017:- Damian Gavin
package ie.gmit.sw;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Deque;
import java.util.LinkedList;
import java.util.concurrent.BlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DocumentParser implements Runnable {

	private BlockingQueue<Shingle> queue;
	private String file;
	private int shingleSize;
	private int k;
	private Deque<String> buffer = new LinkedList<>();
	private int docID;// start at 0

	public DocumentParser(BlockingQueue<Shingle> queue, String file, int shingleSize, int k, int docID) {
		this.queue = queue;
		this.file = file;
		this.shingleSize = shingleSize;
		this.k = k;
		this.docID = docID;
	}
	// getters and setters

	public void run() {// implements Java Runnable

		BufferedReader br = null;
		try {
			br = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
			String line = null;
			while ((line = br.readLine()) != null) {
				String uLine = line.toUpperCase();
				// can take regex but I just split the words by a space
				String[] words = uLine.split(" ");

				addWordsToBuffer(words);
			} // while

			while (buffer.size() >= shingleSize) {
				queue.put(getNextShingle());
			} // while

			queue.put(getPoison());

			br.close();
		} // run
		catch (FileNotFoundException e) {
			Logger.getLogger(DocumentParser.class.getName()).log(Level.SEVERE, null, e);
		} catch (IOException e) {
			Logger.getLogger(DocumentParser.class.getName()).log(Level.SEVERE, null, e);
		} catch (InterruptedException e) {
			Logger.getLogger(DocumentParser.class.getName()).log(Level.SEVERE, null, e);
		} finally {
			try {
				br.close();
			} catch (IOException e) {
				Logger.getLogger(DocumentParser.class.getName()).log(Level.SEVERE, null, e);
			}
		}

	}

	private void addWordsToBuffer(String[] words) {
		for (String s : words) {
			buffer.addLast(s);
		}
	}

	private Shingle getNextShingle() {
		StringBuilder sb = new StringBuilder();
		int counter = 0;
		while (counter < shingleSize && buffer.peek() != null) {
			sb.append(buffer.poll());
			counter++;
		} // while
		if (counter > 0) {
			return new Shingle(docID, sb.toString().hashCode());// convert to string, then convert to number;
		} else {
			return null;
		}
	}// getNextShingle

	private Poison getPoison() {
		StringBuilder sb = new StringBuilder();
		while (buffer.peek() != null) {
			sb.append(buffer.poll());
		} // while
		
		// convert to string, then convert to number
		return new Poison(docID, sb.toString().hashCode());
	}// getPoison

}// DocumentParser
