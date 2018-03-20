package com.lifeng.jcip.example;

import junit.framework.TestCase;

import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by Administrator on 2017/12/26.
 */
public class PutTakeTest extends TestCase {

    protected static final ExecutorService pool = Executors.newCachedThreadPool();

    protected CyclicBarrier barrier;

    protected final SemaphoreBoundedBuffer<Integer> bb;

    protected final int nTrials, nPairs;

    protected final AtomicInteger putSum = new AtomicInteger(0);

    protected final AtomicInteger takeSum = new AtomicInteger(0);


    public static void main(String[] args) throws Exception {
        new PutTakeTest(10, 10, 100000).test(); // sample parameters
        pool.shutdown();
    }

    public PutTakeTest(int capacity, int npairs, int ntrials) {
        this.bb = new SemaphoreBoundedBuffer<Integer>(capacity);
        this.nTrials = ntrials;
        this.nPairs = npairs;
        this.barrier = new CyclicBarrier(npairs * 2 + 1);
    }

    void test() {
        try {
            for (int i = 0; i < nPairs; i++) {
                pool.execute(new Producer());
                pool.execute(new Consumer());
            }
            System.out.println(Thread.currentThread().getName()
                    + " Main 1st await ...."
                    + " parties = " + barrier.getParties()
                    + " waiting number = " + barrier.getNumberWaiting());
            barrier.await(); // wait for all threads to be ready

            System.out.println(Thread.currentThread().getName()
                    + " Main 2nd await ...."
                    + " parties = " + barrier.getParties()
                    + " waiting number = " + barrier.getNumberWaiting());
            barrier.await(); // wait for all threads to finish

            assertEquals(putSum.get(), takeSum.get());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    static int xorShift(int y) {
        y ^= (y << 6);
        y ^= (y >>> 21);
        y ^= (y << 7);
        return y;
    }

    class Producer implements Runnable {

        public void run() {
            try {
                int seed = (this.hashCode() ^ (int) System.nanoTime());
                int sum = 0;

                System.out.println(Thread.currentThread().getName()
                        + " P 1st await ...."
                        + " parties = " + barrier.getParties()
                        + " waiting number = " + barrier.getNumberWaiting());
                barrier.await();

                for (int i = nTrials; i > 0; --i) {
                    bb.put(seed);
                    sum += seed;
                    seed = xorShift(seed);
                }
                putSum.getAndAdd(sum);

                System.out.println(Thread.currentThread().getName()
                        + " P 2nd await ...."
                        + " parties = " + barrier.getParties()
                        + " waiting number = " + barrier.getNumberWaiting());
                barrier.await();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    class Consumer implements Runnable {

        public void run() {
            try {
                System.out.println(Thread.currentThread().getName()
                        + " C 1st await ...."
                        + " parties = " + barrier.getParties()
                        + " waiting number = " + barrier.getNumberWaiting());
                barrier.await();

                int sum = 0;
                for (int i = nTrials; i > 0; --i) {
                    sum += bb.take();
                }
                takeSum.getAndAdd(sum);
                System.out.println(Thread.currentThread().getName()
                        + " C 2nd await ...."
                        + " parties = " + barrier.getParties()
                        + " waiting number = " + barrier.getNumberWaiting());
                barrier.await();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

}
