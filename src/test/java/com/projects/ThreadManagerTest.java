package com.projects;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.MockitoAnnotations.*;

class ThreadManagerTest {

    private ThreadManager threadManager;
    @Mock
    private StorageManager mockStorageManager;

    @BeforeEach
    void setUp() {
        initMocks(this);

        threadManager = new ThreadManager(mockStorageManager, 0);
    }

    @Test
    void start_shouldCheckAllThreadsWasStarted() {
        threadManager.start();

        assertTrue(threadManager.getListener().isAlive());
        assertTrue(threadManager.getPerformer().isAlive());
        assertNotNull(threadManager.getProducer());
        assertFalse(threadManager.getProducerService().isShutdown());
    }
}