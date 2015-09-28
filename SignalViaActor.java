/**
  * @author MABH
  * A simple demo of signalling between two threads using actor model
  * These Actors are naive and much better implementation could be using a standard Actor library
  * like Akka or using Akka FSM
*/

package mt.ex;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

final class Actor implements Runnable {
	private BlockingQueue<Integer> queue = new ArrayBlockingQueue<Integer>(1);
	private Actor other;
	
	private Consumer<Integer> consumer = t -> {
		System.out.println(Thread.currentThread().getId() + " > " + t);
		other.send(t + 1);
	};
	
	public void setOther(Actor other) {
		this.other = other;
	}
	
	public boolean send(Integer t) {
		return this.queue.offer(t);
	}

	public void run() {
		do {
			try {
				Integer t = this.queue.poll(10, TimeUnit.SECONDS);
				if(null != t) {
					this.consumer.accept(t);
					Thread.sleep(500);
				}
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
			}
			
		} while(true);
	}
}

final public class SignalViaActor {
	public static void main(String[] args) {
		Actor a1 = new Actor();
		Actor a2 = new Actor();
		a1.setOther(a2);
		a2.setOther(a1);
		
		Thread t1 = new Thread(a1);
		Thread t2 = new Thread(a2);
		
		a1.send(1); //init signal - a2.send(1) will also work only the threads will switch
		
		t1.start();
		t2.start();
		
		try {
			t1.join();t2.join();
		} catch(InterruptedException ie) {
			Thread.currentThread().interrupt();
		}
	}
}
