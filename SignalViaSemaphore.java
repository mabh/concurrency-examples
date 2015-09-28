/**
 * @author MABH
 * A simple demo of signalling between two threads using a fair semaphore
 * Fairness is crucuial for this example otherwise order cannot be guaranteed
 */
package mt.ex;

import java.util.concurrent.Semaphore;

final class Generator implements Runnable {
	private final Semaphore sem;
	private final int start;
	private final int increment;
	
	public Generator(Semaphore sem, int start, int increment) {
		this.sem = sem;
		this.start = start;
		this.increment = increment;
	}
	
	public void run() {
		int counter = 0;
		while(true) {
			try {
				this.sem.acquire();
				System.out.println((this.start % 2 == 0 ? "Even> " : "Odd> ") + (this.start + (counter++ * this.increment)));
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
			} finally {
				this.sem.release();
			}
		}
	}
}

public class SignalViaSemaphore {
	public static void main(String[] args) {
		Semaphore sem = new Semaphore(1, true);	//a fair Semaphore to allow correct toggling

		Thread t1 = new Thread(new Generator(sem, 1, 2));
		Thread t2 = new Thread(new Generator(sem, 2, 2));
		try {
			t1.start();
			Thread.sleep(10); //most of the times this will cause t1 to start first but it is not guaranteed
							  //for guarantee we need an external mechanism to initiate the two threads
			t2.start();
			
			t1.join();t2.join();
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
		}
	}
}
