package vz.concurrent_search.searcher;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class FindAndReplaceTaskTests {
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

    @Test
    void callShouldReturnCountOfSearchedElements() {
        assertEquals(4, task.call());
    }

    @Test
    void callShouldReplaceSearchedElements() {
        List<Integer> expectedList = new ArrayList<>(Arrays.asList(0, 5, 5, 0, 5, 5, 5, 0, 0, 5, 5, 0));
        task.call();
        assertEquals(expectedList, list);
    }
}
