package com.projects;

import lombok.Getter;

import java.util.List;

public class Performer extends Thread {
    private final StorageManager storageManager;
    private final int searchNumber;

    @Getter
    private volatile boolean startSearch = false;
    private volatile boolean threadStop = false;

    public Performer(StorageManager storageManager, int searchNumber) {
        this.storageManager = storageManager;
        this.searchNumber = searchNumber;
    }

    @Override
    public void run() {
        while (!threadStop) {
            doWait();
            List<Integer> indexes = storageManager.findAllIndexes(searchNumber);
            System.out.println("Count of indexes of numbers which contain number \"" + searchNumber + "\" is " + indexes.size() + "\n");
        }
    }

    private synchronized void doWait() {
        while (!startSearch) {
            try {
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        startSearch = false;
    }

    public synchronized void startSearch() {
        startSearch = true;
        notify();
    }

    public void stopPerformer() {
        threadStop = true;
    }
}
