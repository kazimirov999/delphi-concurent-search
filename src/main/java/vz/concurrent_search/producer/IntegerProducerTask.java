package vz.concurrent_search.producer;

import vz.concurrent_search.exception.IllegalProducerParameterException;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.concurrent.locks.Lock;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class IntegerProducerTask implements Runnable {
    private final static Logger LOGGER = Logger.getLogger(IntegerProducerTask.class.getName());

    private Lock locker;
    private final List<Integer> numbersList;

    private final int numbersAmount;
    private final int maxValue;

    public IntegerProducerTask(List<Integer> numbersList, int numbersAmount, int maxValue) {
        this.numbersList = numbersList;
        this.numbersAmount = numbersAmount;
        this.maxValue = maxValue;
        checkParameters();
    }

    public IntegerProducerTask(Lock locker, List<Integer> numbersList, int numbersAmount, int maxValue) {
        this.locker = locker;
        this.numbersList = numbersList;
        this.numbersAmount = numbersAmount;
        this.maxValue = maxValue;
        checkParameters();
    }

    @Override
    public void run() {
        produce(numbersAmount);
    }

    public void produce(int numbersToProduce) {
        Optional<Lock> lockOptional = Optional.ofNullable(locker);
        lockOptional.ifPresent(Lock::lock);

        Random random = new Random();

        LOGGER.info(String.format("%s: start integer producer task",
                Thread.currentThread().getName()));

        numbersList.addAll(Stream.generate(() -> random.nextInt(maxValue))
                .limit(numbersToProduce)
                .collect(Collectors.toList()));
        Collections.shuffle(numbersList);

        LOGGER.info(String.format("%s: finished generating %s numbers",
                Thread.currentThread().getName(), numbersAmount));

        lockOptional.ifPresent(Lock::unlock);
    }

    private void checkParameters() {
        Optional.of(numbersAmount)
                .filter(numbers -> numbers <= 0)
                .orElseThrow(() -> new IllegalProducerParameterException("Numbers amount should be > 0"));
        Optional.of(maxValue)
                .filter(max -> max <= 0)
                .orElseThrow(() -> new IllegalProducerParameterException("Max value should be >= 0"));
    }
}
