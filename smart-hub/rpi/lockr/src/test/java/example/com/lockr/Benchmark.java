package example.com.lockr;


import static org.mockito.Mockito.timeout;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import example.com.lockr.controller.TestController;


@RunWith(SpringRunner.class)
@SpringBootTest
public class Benchmark {

	@Autowired
	private TestController testController;
	
//	private final AtomicInteger throughputTotal = new AtomicInteger(0);
//	private final AtomicInteger latencyAvg = new AtomicInteger(0);
	Random random = new Random();
	private static final int TX_NUM = 2;
	private static final int NUM_THREADS = 500;
	private static final int FULL_BENCH = 1;
	
	
	@Test
	public void test1() {

		ArrayList<Thread> threads = new ArrayList<>();
		long timeInit = System.currentTimeMillis();
		
		for (int i = 0; i < NUM_THREADS; ++i) {
			Thread thread = new Thread(new Worker(i));
			thread.setName("TCW_" + i);
			thread.setDaemon(true);
			thread.start();
			threads.add(thread);
		}

		threads.forEach(t -> {
			try {
				t.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		});
		
		float totalTime = ((float) (System.currentTimeMillis() - timeInit));
		
		float lat = totalTime;
		float thr = (TX_NUM*NUM_THREADS*FULL_BENCH) / (totalTime / 1000);
//		float thr = throughputTotal.get() / 10000;
		System.out.println(">>> Throughput: "+thr +" tps");
		System.out.println(">>> Latency: "+lat+" s");
	}

	
	class Worker implements Runnable {

		private final int id;

		Worker(int id) {
			this.id = id;

		}

		@Override
		public void run() {

//			long timeInit, timeEnd = 0L;
//			timeInit = System.currentTimeMillis();
//			float throughput = 0f;
			int tx_number = TX_NUM;
			for (int i = 0; i < tx_number; ++i) {
				System.out.println("\\\\\\ I'm "+Thread.currentThread().getName());
				String sector = String.valueOf(random.nextInt(100000) + 1);
				String res = testController.initSectorPrice(sector, "1", "user1").errorMessage.toString();
				
				if(res.equals("Error"))
					tx_number++;
				
//				try {
//					Thread.sleep(500);
//				} catch (InterruptedException e) {
//					// TODO Auto-generated catch block
////					e.printStackTrace();
//				}
//				testController.readSectorPrice(sector, "user1");
//				
//				
//				System.out.println(">>> resp: "+res);
//				if(res.equals("Error"))
//					tx_number++;
//				
//				
//				res = testController.initRental("1", sector, "100", "user1").errorMessage.toString();
//				
//				System.out.println(">>> resp: "+res);
//				
//				if(res.equals("Error"))
//					tx_number++;
				
			}
			
			
//			timeEnd = System.currentTimeMillis() - timeInit;
			
			
//			throughput = TX_NUM / (float) (timeEnd / 1000);
			
//			throughputTotal.addAndGet((int) (throughput * 10000));
//			latencyAvg.addAndGet((int) timeEnd);
//			System.out.println(">>> I did: "+tx_number+" transactions");
//			System.out.println(">>> "+Thread.currentThread().getName()+" Took: "+timeEnd+" ms");
			//System.out.println(">>> "+Thread.currentThread().getName()+" Throughput: "+throughput+" ops/s");

		}
	}
}