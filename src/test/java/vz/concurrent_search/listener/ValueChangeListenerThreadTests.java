package vz.concurrent_search.listener;

import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.*;

public class ValueChangeListenerThreadTests {

    @Test
    void run_Should_ExecuteRunnable_When_ValueChanges() {
        Runnable runnable = mock(Runnable.class);
        ValueChangeListener listener = new ValueChangeListener(Object::new, runnable);
        listener.run();
        verify(runnable, atLeastOnce()).run();
    }
}
