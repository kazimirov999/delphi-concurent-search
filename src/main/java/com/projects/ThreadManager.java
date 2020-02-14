package com.projects;

import lombok.Getter;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static lombok.experimental.FieldNameConstants.Exclude;

@Getter
public class ThreadManager {
    @Exclude
    private static final int PRODUCE_CYCLE_TIME = 5;

    private final Producer producer;
    private final Performer performer;
    private final Listener listener;
    private final ScheduledExecutorService producerService;

    public ThreadManager(StorageManager storageManager, int searchNumber) {
        producer = new Producer(storageManager);
        performer = new Performer(storageManager, searchNumber);
        listener = new Listener(storageManager, performer);

        producerService = Executors.newSingleThreadScheduledExecutor();
    }

    public void start() {
        listener.start();
        producerService.scheduleAtFixedRate(producer, PRODUCE_CYCLE_TIME, PRODUCE_CYCLE_TIME, TimeUnit.SECONDS);
        performer.start();

        System.out.println("Threads started!!!");
    }
}
