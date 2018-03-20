package com.lifeng.jcip.example;

import junit.framework.TestCase;
import org.junit.Assert;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class FutureTaskUnfinishBlock extends TestCase {

    public static void main(String[] args) throws InterruptedException {
        ExecutorService executorService = Executors.newCachedThreadPool();
        Future<?> result = executorService.submit(new Runnable() {
            @Override
            public void run() {
                Assert.assertNotEquals(Thread.currentThread().getName(), "xxxxx");
                System.out.println("11111111");
            }
        });




        try {
            System.out.println(result.get());
            System.out.println("22222");
        } catch (ExecutionException e) {
            e.printStackTrace();
        } finally {
            executorService.shutdown();
        }
    }


}
