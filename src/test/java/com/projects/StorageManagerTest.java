package com.projects;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

class StorageManagerTest {

    private StorageManager storageManager;
    @Mock
    private ExecutorService mockExecutorService;
    private List<Integer> initStorage = new ArrayList<>(Arrays.asList(1, 5, 2, 3, 4, 15, 7, 8, 9, 5));
    private int countOfParts = 10;

    @BeforeEach
    void setUp() {
        initMocks(this);

        storageManager = new StorageManager(initStorage, new ReentrantReadWriteLock(), mockExecutorService, countOfParts);
    }

    @Test
    void addToCollection_shouldAddNewListToStorageIfListNotNull() {
        List<Integer> sourceList = Arrays.asList(1, 2, 3, 5, 7);
        int expectedSize = initStorage.size() + sourceList.size();

        storageManager.addToCollection(sourceList);
        int actualSize = storageManager.getStorage().size();
        List<Integer> actualList = storageManager.getStorage().subList(10, 15);

        assertEquals(expectedSize, actualSize);
        assertIterableEquals(sourceList, actualList);
    }

    @Test
    void addToCollection_shouldThrowExceptionIfNewListIsNull() {
        Executable actual = () -> storageManager.addToCollection(null);
        assertThrows(NullPointerException.class, actual);
    }

    @Test
    void addToCollection_shouldChangeLastModificationWhenNewListAdded() throws InterruptedException {
        List<Integer> source = new ArrayList<>();
        Instant expected = storageManager.getLastModification();
        Thread.sleep(10);

        storageManager.addToCollection(source);
        Instant actual = storageManager.getLastModification();

        assertNotEquals(expected, actual);
    }

    @ParameterizedTest
    @ValueSource(strings = {"7", "10"})
    void findAllIndexes_shouldSplitStorageOnParts(int newCountOfParts) throws ExecutionException, InterruptedException {
        storageManager = new StorageManager(initStorage, new ReentrantReadWriteLock(), mockExecutorService, newCountOfParts);

        when(mockExecutorService.invokeAny(any())).thenReturn(Collections.singletonList(anyInt()));

        int actual = storageManager.findAllIndexes(0).size();

        assertEquals(newCountOfParts, actual);
        verify(mockExecutorService, times(newCountOfParts)).invokeAny(any());
    }

    @Test
    void findAllIndexes_shouldThrowExceptionWhenSplitListOnZeroParts() {
        storageManager = new StorageManager(initStorage, new ReentrantReadWriteLock(), mockExecutorService, 0);

        Executable actual = () -> storageManager.findAllIndexes(0);

        Exception exception = assertThrows(ArithmeticException.class, actual);
        assertEquals("/ by zero", exception.getMessage());
    }

    @Test
    void findAllIndexes_shouldReturnEmptyListWhenSplitListOnNegativeNumberOfParts() {
        int expectedSize = 0;
        storageManager = new StorageManager(initStorage, new ReentrantReadWriteLock(), mockExecutorService, -10);

        int actual = storageManager.findAllIndexes(0).size();

        assertEquals(expectedSize, actual);
    }

    @Test
    void findAllIndexes_shouldReturnEmptyListWhenExceptionThrown() throws ExecutionException, InterruptedException {
        int expectedSize = 0;

        when(mockExecutorService.invokeAny(any())).thenThrow(new InterruptedException());

        int actual = storageManager.findAllIndexes(0).size();

        assertEquals(expectedSize, actual);
        verify(mockExecutorService, times(countOfParts)).invokeAny(any());
    }

    @Test
    void findAllIndexes_shouldReturnListOfIndexesWhenStorageHasNumber() {
        int sourceToFind = 5;
        int expectedSize = 3;
        ExecutorService tempExecutorService = Executors.newFixedThreadPool(countOfParts);
        storageManager = new StorageManager(initStorage, new ReentrantReadWriteLock(), tempExecutorService, countOfParts);

        List<Integer> actual = storageManager.findAllIndexes(sourceToFind);

        assertEquals(expectedSize, actual.size());
        for (Integer index : actual) {
            assertTrue(checkSourceNumberContainedInStorage(index, sourceToFind));
        }

        tempExecutorService.shutdown();
    }

    private boolean checkSourceNumberContainedInStorage(Integer index, int source) {
        return String.valueOf(storageManager.getStorage().get(index)).contains(String.valueOf(source));
    }
}
