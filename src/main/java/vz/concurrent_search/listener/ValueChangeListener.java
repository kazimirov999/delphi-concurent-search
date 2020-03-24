package vz.concurrent_search.listener;

import java.util.Optional;
import java.util.concurrent.locks.Lock;
import java.util.function.Supplier;
import java.util.logging.Logger;

public class ValueChangeListener implements Runnable {
    private final static Logger LOGGER = Logger.getLogger(ValueChangeListener.class.getName());

    private Lock lock;
    private final Supplier<?> valueSupplier;
    private final Runnable onSizeChangeAction;
    private Object oldValue;

    public ValueChangeListener(Supplier<?> valueSupplier, Runnable onSizeChangeAction) {
        this.valueSupplier = valueSupplier;
        this.onSizeChangeAction = onSizeChangeAction;
        oldValue = valueSupplier.get();
    }

    public ValueChangeListener(Lock lock, Supplier<?> valueSupplier, Runnable onSizeChangeAction) {
        this.lock = lock;
        this.valueSupplier = valueSupplier;
        this.onSizeChangeAction = onSizeChangeAction;
        oldValue = valueSupplier.get();
    }

    @Override
    public void run() {
        Optional<Lock> lockOptional = Optional.ofNullable(lock);
        lockOptional.ifPresent(Lock::lock);

        if (isSizeChanged()) {
            LOGGER.info(String.format("%s: value has been changed. New value: %s",
                    Thread.currentThread().getName(),
                    valueSupplier.get()));

            onSizeChangeAction.run();
            oldValue = valueSupplier.get();
        }

        lockOptional.ifPresent(Lock::unlock);
    }

    private boolean isSizeChanged() {
        return !oldValue.equals(valueSupplier.get());
    }
}
