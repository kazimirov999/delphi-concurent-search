package com.projects;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

class PerformerTest {

    private Performer performer;
    @Mock
    private StorageManager storageManager;

    @BeforeEach
    void setUp() {
        initMocks(this);

        performer = new Performer(storageManager, 0);
        performer.start();
    }

    @AfterEach
    void tearDown() {
        performer.stopPerformer();
    }

    @Test
    void run_shouldReturnListOfIndexesWhenThreadNotified() throws InterruptedException {
        when(storageManager.findAllIndexes(anyInt())).thenReturn(Collections.singletonList(0));

        performer.startSearch();
        Thread.sleep(10);

        assertFalse(performer.isStartSearch());
        verify(storageManager, times(1)).findAllIndexes(anyInt());
    }

    @Test
    void run_shouldWaitingWhenThreadInterrupted() throws InterruptedException {
        performer.interrupt();
        Thread.sleep(10);

        assertFalse(performer.isStartSearch());
        assertEquals(Thread.State.WAITING, performer.getState());
    }

    @Test
    void startSearch_shouldChangeStartSearchValueToTrue() {
        performer.startSearch();

        assertTrue(performer.isStartSearch());
    }

    @Test
    void startSearch_shouldNotifyPerformerWhenThreadWaiting() throws InterruptedException {
        assertEquals(Thread.State.WAITING, performer.getState());

        performer.startSearch();
        Thread.sleep(10);

        assertEquals(Thread.State.RUNNABLE, performer.getState());
    }
}
