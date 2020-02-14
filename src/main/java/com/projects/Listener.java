package com.projects;

import lombok.Getter;

import java.time.Instant;
import java.util.Objects;

public class Listener extends Thread {

    private final StorageManager storageManager;
    private final Performer performer;

    @Getter
    private Instant lastModification;
    private volatile boolean threadStop = false;

    public Listener(StorageManager storageManager, Performer performer) {
        this.storageManager = storageManager;
        this.performer = performer;
    }

    @Override
    public void run() {
        while (!threadStop) {
            if (validationStorage(storageManager)) {
                runPerformerIfListModified();
            }
        }
    }

    private boolean validationStorage(StorageManager storageManager) {
        return Objects.nonNull(storageManager) &&
                Objects.nonNull(storageManager.getLastModification());
    }

    private void runPerformerIfListModified() {
        if (!storageManager.getLastModification().equals(lastModification)) {
            lastModification = storageManager.getLastModification();
            performer.startSearch();
        }
    }

    public void stopListener() {
        threadStop = true;
    }
}
