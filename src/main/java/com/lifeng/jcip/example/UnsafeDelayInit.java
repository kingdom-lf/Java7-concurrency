package com.lifeng.jcip.example;

import net.jcip.annotations.NotThreadSafe;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.CyclicBarrier;

/**
 * 不安全的延迟初始化
 */
@NotThreadSafe
public class UnsafeDelayInit {

    private static Resource resource;

    public static Resource getResource(int first, int second) throws InterruptedException {
        return resource == null ? new Resource(first, second) : resource;
    }

    public static void main(String[] args) throws BrokenBarrierException, InterruptedException {
        for (int i = 0; i < 2000; i++) {
            xxxx();
        }
    }

    private static void xxxx() {
        final CountDownLatch latch = new CountDownLatch(1);
        final CyclicBarrier barrier = new CyclicBarrier(3);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    latch.await();
                    resource = getResource(6, 9);
                    System.out.println(Thread.currentThread().getName() + " - " + resource);
                    barrier.await();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (BrokenBarrierException e) {
                    e.printStackTrace();
                }
            }
        }, "Thread-A").start();

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    latch.await();
                    resource = getResource(4, 7);
                    barrier.await();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (BrokenBarrierException e) {
                    e.printStackTrace();
                }

                System.out.println(Thread.currentThread().getName() + " - " + resource);
            }
        }, "Thread-B").start();

        try {
            latch.countDown();
            barrier.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (BrokenBarrierException e) {
            e.printStackTrace();
        }
        System.out.println(Thread.currentThread().getName() + " - " + resource + "\n");
        resource = null;
    }

}


class Resource {

    private int first;

    private int second;

    public Resource(int first, int second) throws InterruptedException {
        this.first = first;
        this.second = second;
    }

    @Override
    public String toString() {
        return super.toString() + " [ first = " + first + " , second = " + second + "]";
    }
}