package vz.concurrent_search.producer;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import vz.concurrent_search.exception.IllegalProducerParameterException;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;

public class IntegerProducerTaskTests {
    @Test
    void produce_Should_AddSpecificAmountOfNumbers() {
        List<Integer> numbers = new ArrayList<>();
        IntegerProducerTask task = new IntegerProducerTask(numbers, 100, 100);
        task.run();
        assertEquals(100, numbers.size());
        task.run();
        assertEquals(200, numbers.size());
    }

    @Test
    void produce_Should_AddNonNullValues() {
        List<Integer> numbers = new ArrayList<>();
        IntegerProducerTask task = new IntegerProducerTask(numbers, 100, 100);
        task.run();
        assertEquals(numbers.stream().filter(Objects::isNull).count(), 0);
    }

    @Test
    void produce_Should_ThrowIllegalProducerParameterException_When_MaxValueLessOrEquals0() {
        assertThrows(IllegalProducerParameterException.class,
                () -> new IntegerProducerTask(mock(List.class), 10, -10));
    }

    @Test
    void produce_Should_ThrowIllegalProducerParameterException_When_NumbersAmountLessOrEquals0(int maxValue) {
        assertThrows(IllegalProducerParameterException.class,
                () -> new IntegerProducerTask(mock(List.class), 10, -10));
    }
}
