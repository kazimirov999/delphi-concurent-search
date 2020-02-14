package com.projects;

import lombok.Getter;
import lombok.Setter;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.stream.Collectors;

import static lombok.experimental.FieldNameConstants.Exclude;

@Command(name = "Index finder", description = "Finds indexes of List items where a number is present.")
public class StartFinder implements Runnable {
    private static final int STORAGE_CAPACITY = 100000;

    @Setter
    @Option(names = {"-s", "--search"}, defaultValue = "0", description = "A number which need to search.", interactive = true)
    private int searchNumber;
    @Setter
    @Option(names = {"-p", "--poolSize"}, defaultValue = "10", description = "Count of threads in pool.", interactive = true)
    private int poolSize;

    @Getter
    private ExecutorService searchExecutor;
    @Getter
    private StorageManager storageManager;
    @Getter
    private ThreadManager threadManager;

    @Override
    public void run() {
        searchExecutor = Executors.newFixedThreadPool(poolSize);
        storageManager = new StorageManager(createRandomList(), new ReentrantReadWriteLock(), searchExecutor, poolSize);
        threadManager = new ThreadManager(storageManager, searchNumber);

        threadManager.start();
    }

    private List<Integer> createRandomList() {
        return new Random()
                .ints(STORAGE_CAPACITY, 0, 1000)
                .boxed()
                .collect(Collectors.toList());
    }
}