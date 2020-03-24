package vz.concurrent_search.searcher;

import org.junit.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import vz.concurrent_search.exception.IllegalPoolSizeException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class ConcurrentFindAndReplaceServiceTests {
    List<Integer> list;
    FindAndReplaceTask<Integer> task;
    int searched = 1;
    int replacer = 0;

    @BeforeEach
    private void init() {
        int s = searched;
        list = new ArrayList<>();
        list.addAll(Arrays.asList(
                0, 5, 5, s,
                5, 5, 5, s,
                s, 5, 5, s));
        task = new FindAndReplaceTask<>(searched, replacer, list);
    }

    @ParameterizedTest
    @ValueSource(ints = {1, 2, 3, 4, 10})
    void findAndReplace_Should_ReturnCountOfSearchedElements(int poolSize) {
        var findAndReplaceService = new ConcurrentFindAndReplaceService<Integer>();
        long count = findAndReplaceService.findAndReplace(searched, replacer, list, poolSize);
        assertEquals(4, count);
    }

    @ParameterizedTest
    @ValueSource(ints = {1, 2, 3, 4, 10})
    void findAndReplace_Should_ReplaceSearchedElements(int poolSize) {
        var findAndReplaceService = new ConcurrentFindAndReplaceService<Integer>();
        findAndReplaceService.findAndReplace(searched, replacer, list, poolSize);
        List<Integer> expectedList = new ArrayList<>(Arrays.asList(0, 5, 5, 0, 5, 5, 5, 0, 0, 5, 5, 0));
        assertEquals(expectedList, list);
    }

    @Test
    public void findAndReplace_Should_ThrowIllegalPoolSizeException_When_PoolSizeBiggerThanListSize() {
        var findAndReplaceService = new ConcurrentFindAndReplaceService<Integer>();
        assertThrows(IllegalPoolSizeException.class,
                () -> findAndReplaceService.findAndReplace(searched, replacer, list, list.size() + 10));
    }

}
