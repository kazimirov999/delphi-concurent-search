package vz.concurrent_search.producer;

import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class IntegerProducerTask implements Runnable {
    private final static Logger LOGGER = Logger.getLogger(IntegerProducerTask.class.getName());

    private final ReadWriteLock locker;
    private final List<Integer> numbersList;

    private final int numbersAmount;
    private final int maxValue;

    public IntegerProducerTask(ReadWriteLock locker, List<Integer> numbersList, int numbersAmount, int maxValue) {
        this.locker = locker;
        this.numbersList = numbersList;
        this.numbersAmount = numbersAmount;
        this.maxValue = maxValue;
    }

    @Override
    public void run() {
        produce(numbersAmount);
    }

    public void produce(int numbersToProduce) {
        locker.writeLock().lock();

        Random random = new Random();

        LOGGER.info(String.format("%s: start integer producer task",
                Thread.currentThread().getName()));

        numbersList.addAll(Stream.generate(() -> random.nextInt(maxValue))
                .limit(numbersToProduce)
                .collect(Collectors.toList()));
        Collections.shuffle(numbersList);

        LOGGER.info(String.format("%s: finished generating %s numbers",
                Thread.currentThread().getName(), numbersAmount));

        locker.writeLock().unlock();
    }
}
