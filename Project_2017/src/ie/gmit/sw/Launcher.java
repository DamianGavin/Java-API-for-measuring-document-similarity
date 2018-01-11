//OOP Project 2017:- Damian Gavin
package ie.gmit.sw;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

public class Launcher {
	public static void launch(String f1, String f2, int shingleSize, int k, int poolSize)
			throws InterruptedException {

		ExecutorService es = Executors.newFixedThreadPool(poolSize);
		// LinkedBlockingQueue<>(size)
		BlockingQueue<Shingle> q = new LinkedBlockingQueue<>();

		System.out.println("Parsing first Document.");
		es.submit(new DocumentParser(q, f1, shingleSize, k, 0));
		System.out.println("Parsing second Document.");
		es.submit(new DocumentParser(q, f2, shingleSize, k, 1));

		es.submit(new Consumer(q, k, 2));
		System.out.println("Computing min hashes...");
		// consumerThread.join(); //wait till consumer process last Poison
		System.out.println("Finished computing min hashes.");

		/*
		 * 
		 * COMPUTE JAACARD INDEX HERE AND REPORT TO USER double index = ....
		 * System.out.println("Index is...")
		 * 
		 */
		es.shutdown();
	}
}
