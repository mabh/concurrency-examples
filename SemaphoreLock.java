/**
 * @author MABH
 * An implementation of Lock (not exactly java.util.concurrent.locks.Lock interface) using single permit Semaphore
 */
package mt.ex;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Semaphore;

final class CustomLock {
	private Semaphore sem = new Semaphore(1, true);
	private Set<Thread> set = new HashSet<>();
	
	public void lock() {
		try {
			this.sem.acquire();
			synchronized(this) {set.add(Thread.currentThread());}
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
		}
	}

	public void unlock() {
		if(set.contains(Thread.currentThread())) {
			synchronized(this) {
				this.sem.release();
				set.clear();
			}
		}
	}
	
	public Set<Thread> lockSet() {
		return this.set;
	}

	public boolean tryLock() {
		if(this.set.isEmpty()) {
			this.lock();
			return true;
		}
		return false;
	}
}


public class SemaphoreLock {
	public static void main(String[] args) {
		CustomLock lock = new CustomLock();
		
		Thread t1 = new Thread(() -> {
			System.out.println("trying lock from t1");
			lock.lock();
			System.out.println("got lock in t1. releasing lock in t1");
			lock.unlock();
			System.out.println("released lock in t1");
		});t1.setName("T1");

		Thread t2 = new Thread(() -> {
			System.out.println("trying lock from t2");
			lock.lock();
			System.out.println("got lock in t2. releasing lock in t2");
			lock.unlock();
			System.out.println("released lock in t2");
		});t2.setName("T2");
		
		t1.start();
		t2.start();

		try {
			t1.join();t2.join();
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
		}
		
		System.out.println("done");
	}
}
