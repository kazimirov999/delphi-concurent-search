package com.projects;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.concurrent.ThreadPoolExecutor;

import static org.junit.jupiter.api.Assertions.*;

class StartFinderTest {

    private StartFinder startFinder;

    @BeforeEach
    void setUp() {
        startFinder = new StartFinder();
    }

    @Test
    void run_shouldStartAllObjectsAndExecutorService() {
        startFinder.setSearchNumber(5);
        startFinder.setPoolSize(10);

        startFinder.run();

        assertFalse(startFinder.getSearchExecutor().isShutdown());
        assertEquals(10, ((ThreadPoolExecutor) startFinder.getSearchExecutor()).getCorePoolSize());
        assertNotNull(startFinder.getStorageManager());
        assertEquals(100000, startFinder.getStorageManager().getStorage().size());
        assertNotNull(startFinder.getThreadManager());
    }
}