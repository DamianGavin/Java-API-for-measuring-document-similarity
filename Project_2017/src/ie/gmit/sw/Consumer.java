//OOP Project 2017:- Damian Gavin
package ie.gmit.sw;
//Who creates consumer class? - Launcher

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

//Everything is in a God class - has to be broken to different classes
public class Consumer implements Runnable {

	private BlockingQueue<Shingle> queue;
	private int k, numDocs;

	// the random stuff
	private int[] minHashes;
	private Map<Integer, List<Integer>> minListForDocs = new ConcurrentHashMap<>();

	public Consumer(BlockingQueue<Shingle> q, int k, int numDocs) {
		this.queue = q;
		this.k = k;
		this.numDocs = numDocs;
		// initialize the minhash array and fill it up with random stuff
		init();
	}

	private void init() {
		Random random = new Random();
		minHashes = new int[k];// k = 200 ~ 300
		for (int i = 0; i < minHashes.length; i++) {
			minHashes[i] = random.nextInt();// generates random int
		}
	}// init

	// will override the java.lang run()
	@Override
	public void run() {
		int docCount = numDocs;
		while (docCount > 0) {
			try {
				// blocking method (wait while other threads add at least one shingle to this
				// queue) - pick one shingle and process its hash
				Shingle s = queue.take();

				if (s.isPoison()) {
					docCount--;
				}

				List<Integer> list;
				// create the list if it doesn't exist yet
				minListForDocs.putIfAbsent(s.getDocID(), new ArrayList<>());
				list = minListForDocs.get(s.getDocID());
				for (int i = 0; i < k; i++) {
					list.add(Integer.MAX_VALUE);
				}

				final List<Integer> hashList = list;
				// loop over minHashes
				for (int i = 0; i < minHashes.length; i++) {
					int value = s.getHashCode() ^ minHashes[i]; // xor

					if (hashList.get(i) > 0) {
						hashList.set(i, value);
					}
				}
			} catch (InterruptedException e) {
				Logger.getLogger(Consumer.class.getName()).log(Level.SEVERE, null, e);
			}
		}

	}// run
}
