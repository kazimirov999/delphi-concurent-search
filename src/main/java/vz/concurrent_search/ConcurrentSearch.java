package vz.concurrent_search;

import vz.concurrent_search.listener.ValueChangeListenerThread;
import vz.concurrent_search.producer.IntegerProducerTask;
import vz.concurrent_search.searcher.ConcurrentFindAndReplaceService;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import static picocli.CommandLine.Option;

public class ConcurrentSearch implements Runnable {
    @Option(names = {"-s", "--searched"}, description = "Searched number", interactive = true)
    private int searchedNumber = 50;

    @Option(names = {"-r", "--replace"}, description = "Replace number", interactive = true)
    private int replaceNumber = 0;

    @Option(names = {"-p", "--pool"}, description = "Pool size", interactive = true)
    private int poolSize = 10;

    @Option(names = {"-n", "--numbers"}, description = "Amount of produced numbers", interactive = true)
    private int numbersAmount = 5_000;

    @Option(names = {"-m", "--max"}, description = "Max value of produced numbers", interactive = true)
    private int maxValue = 100;

    private final ReadWriteLock locker = new ReentrantReadWriteLock();
    private List<Integer> numbersList = new ArrayList<>();

    @Override
    public void run() {
        ScheduledExecutorService producerExecutorService = Executors.newSingleThreadScheduledExecutor();
        producerExecutorService.scheduleAtFixedRate(new IntegerProducerTask(locker, numbersList, numbersAmount, maxValue),
                0, 5, TimeUnit.SECONDS);

        ConcurrentFindAndReplaceService<Integer> searcher = new ConcurrentFindAndReplaceService<>(locker);

        ValueChangeListenerThread listenerThread = new ValueChangeListenerThread(locker,
                () -> numbersList.size(),
                () -> searcher.findAndReplace(searchedNumber, replaceNumber, numbersList, poolSize));
        listenerThread.start();
    }
}
