/*
 * Copyright (c) 2017. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package com.lifeng.task.cancel;

import java.math.BigInteger;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * Created by Administrator on 2017/12/1.
 */
public class PrimeProducer extends Thread {

    private final BlockingQueue<BigInteger> queue;

    public PrimeProducer(BlockingQueue<BigInteger> queue) {
        this.queue = queue;
    }

    @Override
    public void run() {
        try {
            BigInteger p = BigInteger.ONE;
            while (true) {
                p = p.nextProbablePrime();
                System.out.println("new prime = " + p + " , now = " + System.currentTimeMillis());
                queue.put(p);
                System.out.println("new prime = " + p + " , put finish");
            }
        } catch (InterruptedException e) {
            System.out.println("now = " + System.currentTimeMillis());
            e.printStackTrace();
        }
    }

    public void cancel() {
        interrupt();
    }

    public static void main(String[] args) throws InterruptedException {
        PrimeProducer producer = new PrimeProducer(new ArrayBlockingQueue<BigInteger>(10));
        producer.start();
        TimeUnit.SECONDS.sleep(3);
        producer.cancel();
    }

}
