package com.projects;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.*;

class ListenerTest {

    private Listener listener;
    @Mock
    private StorageManager storageManager;
    @Mock
    private Performer performer;

    @BeforeEach
    void setUp() {
        initMocks(this);

        listener = new Listener(storageManager, performer);
    }

    @AfterEach
    void tearDown() {
        listener.stopListener();
    }

    @Test
    void run_shouldBeNullLastModificationWhenValidationFailed() throws InterruptedException {
        when(storageManager.getLastModification()).thenReturn(null);

        listener.start();
        Thread.sleep(10);

        assertNull(listener.getLastModification());
        verify(storageManager, atLeastOnce()).getLastModification();
        verify(performer, never()).startSearch();
    }

    @Test
    void run_shouldInitLastModificationAndStartPerformerWhenValidationSucceeded() throws InterruptedException {
        Instant expected = Instant.ofEpochMilli(1000);

        when(storageManager.getLastModification()).thenReturn(expected);

        listener.start();
        Thread.sleep(10);

        assertEquals(expected, listener.getLastModification());
        verify(storageManager, atLeastOnce()).getLastModification();
        verify(performer, times(1)).startSearch();
    }
}
