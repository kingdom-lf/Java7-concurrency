package com.lifeng.task.cancel;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 可取消的任务
 */
public class PrimeGenerator implements Runnable {

    private final List<BigInteger> primes = new ArrayList<>();

    private volatile boolean cancelled;

    @Override
    public void run() {
        BigInteger p = BigInteger.ONE;
        while (!cancelled) {
            p = p.nextProbablePrime();
            System.out.println("prime = " + p);

            synchronized (this) {
                primes.add(p);
            }
        }
        if (cancelled) {
            System.out.println("task cancelled ....");
        }
    }

    public void cancel() {
        cancelled = true;
    }

    public synchronized List<BigInteger> get() {
        return new ArrayList<>(primes);
    }

    public static void main(String[] args) throws InterruptedException {
        PrimeGenerator generator = new PrimeGenerator();
        new Thread(generator).start();

        try {
            TimeUnit.SECONDS.sleep(3);
        } finally {
            generator.cancel();
        }

        System.out.println("primes[] size = " + generator.get().size());
    }


}
