package com.projects;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.MockitoAnnotations.*;

class ProducerTest {

    private Producer producer;
    @Mock
    private ExecutorService mockExecutorService;
    private StorageManager storageManager;

    @BeforeEach
    void setUp() {
        initMocks(this);

        storageManager = new StorageManager(new ArrayList<>(), new ReentrantReadWriteLock(), mockExecutorService, 10);
        producer = new Producer(storageManager);
    }

    @Test
    void run_shouldAddNewDataToStorage() {
        int expectedSize = 100;

        producer.run();

        assertEquals(expectedSize, storageManager.getStorage().size());
    }
}
