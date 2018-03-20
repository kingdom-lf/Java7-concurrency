package com.lifeng.jcip.example;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * 读写锁并发读不阻塞
 * <br/>
 * 并发写阻塞
 */
public class UnblockReadWriterLock {

    private static Logger logger = LoggerFactory.getLogger(UnblockReadWriterLock.class);

    public static void main(String[] args) throws InterruptedException {
        ReadWriteLock readWriteLock = new ReentrantReadWriteLock();
        final Lock readLock = readWriteLock.readLock();
        final CountDownLatch latch = new CountDownLatch(10);
        final CyclicBarrier barrier = new CyclicBarrier(10);

        for (int i = 0; i < 10; i++) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        barrier.await();
                    } catch (InterruptedException | BrokenBarrierException e) {
                        e.printStackTrace();
                    }

                    if (Thread.currentThread().getName().equals("Thread-1")) {
                        readLock.lock();
                    } else {
                        readLock.lock();
                    }

                    try {
                        logger.info(Thread.currentThread().getName() + " get read lock success");
                    } finally {
                        if (Thread.currentThread().getName().equals("Thread-0")) {
                            readLock.unlock();
                        } else {
                            readLock.unlock();
                        }
                    }

                    latch.countDown();

                }
            }, "Thread-" + i).start();
        }

        latch.await();
    }

}
