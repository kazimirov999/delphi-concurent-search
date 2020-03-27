package com.projects;

import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public class Producer implements Runnable {
    private static final int CAPACITY_TO_INCREASE = 100;

    private final StorageManager storageManager;

    public Producer(StorageManager storageManager) {
        this.storageManager = storageManager;
    }

    @Override
    public void run() {
        storageManager.addToCollection(createRandomList());
    }

    private List<Integer> createRandomList() {
        return new Random()
                .ints(CAPACITY_TO_INCREASE, 0, 1000)
                .boxed()
                .collect(Collectors.toList());
    }
}
