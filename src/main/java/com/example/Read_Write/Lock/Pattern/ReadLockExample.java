package com.example.Read_Write.Lock.Pattern;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class ReadLockExample {
    private final ReadWriteLock readWriteLock = new ReentrantReadWriteLock();

    public void readData() {
        readWriteLock.readLock().lock();
        try {
            System.out.println(Thread.currentThread().getName() + " is reading data.");
            Thread.sleep(1000); // Simulate time taken to read data
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            readWriteLock.readLock().unlock();
        }
    }

    public static void main(String[] args) {
        ReadLockExample example = new ReadLockExample();
        ExecutorService executorService = Executors.newFixedThreadPool(5);

        // Create 5 threads
        for (int i = 0; i < 5; i++) {
            executorService.submit(() -> example.readData());
        }

        executorService.shutdown();
    }
}
