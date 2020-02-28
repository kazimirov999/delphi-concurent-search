package vz.concurrent_search.listener;

import java.util.concurrent.locks.ReadWriteLock;
import java.util.function.Supplier;
import java.util.logging.Logger;

public class ValueChangeListenerThread extends Thread {
    private final static Logger LOGGER = Logger.getLogger(ValueChangeListenerThread.class.getName());

    private final ReadWriteLock locker;
    private final Supplier<?> valueSupplier;
    private final Runnable onSizeChangeAction;
    private Object oldValue;

    public ValueChangeListenerThread(ReadWriteLock locker, Supplier<?> valueSupplier, Runnable onSizeChangeAction) {
        this.locker = locker;
        this.valueSupplier = valueSupplier;
        this.onSizeChangeAction = onSizeChangeAction;
        oldValue = valueSupplier.get();
    }

    @Override
    public void run() {
        while (true) {
            locker.readLock().lock();

            if (isSizeChanged()) {
                LOGGER.info(String.format("%s: value has been changed. New value: %s",
                        Thread.currentThread().getName(),
                        valueSupplier.get()));

                onSizeChangeAction.run();
                oldValue = valueSupplier.get();
            }
            locker.readLock().unlock();
        }
    }

    private boolean isSizeChanged() {
        return !oldValue.equals(valueSupplier.get());
    }
}
