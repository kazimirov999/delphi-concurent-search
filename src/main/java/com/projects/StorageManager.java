package com.projects;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.tuple.Pair;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.locks.ReadWriteLock;

public class StorageManager {
    @Getter
    private final List<Integer> storage;
    private ReadWriteLock readWriteLock;
    private ExecutorService searchExecutor;
    private int countOfParts;

    @Getter
    private volatile Instant lastModification;
    private int possibleRemainder;

    public StorageManager(List<Integer> storage, ReadWriteLock readWriteLock, ExecutorService executorService, int countOfParts) {
        this.storage = storage;
        this.readWriteLock = readWriteLock;
        this.searchExecutor = executorService;
        this.countOfParts = countOfParts;

        lastModification = Instant.now();
    }

    public void addToCollection(List<Integer> newData) {
        readWriteLock.writeLock().lock();

        try {
            storage.addAll(newData);
            lastModification = Instant.now();

            System.out.println("New data added to collection!");
        } finally {
            readWriteLock.writeLock().unlock();
        }
    }

    public List<Integer> findAllIndexes(int number) {
        readWriteLock.readLock().lock();

        try {
            return splitListOnParts(number);
        } finally {
            readWriteLock.readLock().unlock();
        }
    }

    private List<Integer> splitListOnParts(int number) {
        List<Integer> allIndexes = new ArrayList<>();
        possibleRemainder = storage.size() % countOfParts;

        for (int partNumber = 0; partNumber < countOfParts; partNumber++) {
            Pair<Integer, Integer> range = getRange(partNumber);

            allIndexes.addAll(checkPartOfListInNewThread(number, range.getLeft(), range.getRight()));
        }

        return allIndexes;
    }

    private List<Integer> checkPartOfListInNewThread(int number, int start, int end) {
        try {
            return searchExecutor.invokeAny(Collections.singletonList(() -> searchForNumberIndex(number, start, end)));
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }

    private List<Integer> searchForNumberIndex(int number, int start, int end) {
        System.out.println(Thread.currentThread().getName() + " started to search \"" + number + "\" in range " + start + " - " + end);
        List<Integer> indexes = new ArrayList<>();

        for (int i = start; i < end; i++) {
            if (String.valueOf(storage.get(i)).contains(String.valueOf(number))) {
                indexes.add(i);
            }
        }

        return indexes;
    }

    private Pair<Integer, Integer> getRange(int partNumber) {
        int start, end;
        int elementOfRemainder = 1;
        int countOfNumsInRange = storage.size() / countOfParts;
        int initialRemainder = storage.size() % countOfParts;

        if (possibleRemainder > 0) {
            start = (countOfNumsInRange + elementOfRemainder) * partNumber;
            end = (countOfNumsInRange + elementOfRemainder) * (partNumber + 1);
            possibleRemainder--;
        } else {
            start = countOfNumsInRange * partNumber + initialRemainder;
            end = countOfNumsInRange * (partNumber + 1) + initialRemainder;
        }

        return Pair.of(start, end);
    }
}
