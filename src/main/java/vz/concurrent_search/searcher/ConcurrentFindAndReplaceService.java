package vz.concurrent_search.searcher;

import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.logging.Logger;
import java.util.stream.IntStream;

public class ConcurrentFindAndReplaceService<T> {
    private final static Logger LOGGER = Logger.getLogger(ConcurrentFindAndReplaceService.class.getName());

    private final ReadWriteLock locker;

    public ConcurrentFindAndReplaceService(ReadWriteLock locker) {
        this.locker = locker;
    }

    public long findAndReplace(T searched, T replacer, List<T> list, int threadPoolSize) {
        locker.readLock().lock();

        ExecutorService executor = Executors.newFixedThreadPool(threadPoolSize);
        CompletionService<Long> searchCompletionService = new ExecutorCompletionService<>(executor);

        int subListSize = list.size() / threadPoolSize;

        IntStream.range(0, threadPoolSize)
                .forEach(index -> searchCompletionService.submit(new FindAndReplaceTask<>(searched, replacer,
                        list.subList(index * subListSize, (index + 1) * subListSize))));

        int receivedResults = 0;
        long itemsFound = 0;

        while (receivedResults < threadPoolSize) {
            itemsFound += receiveCompletionServiceResult(searchCompletionService);
            receivedResults++;

            LOGGER.info(String.format("%s: %s tasks finished",
                    Thread.currentThread().getName(), receivedResults));
        }

        locker.readLock().unlock();
        executor.shutdown();

        LOGGER.info(String.format("%s: %s items have been found and replaced",
                Thread.currentThread().getName(), itemsFound));

        return itemsFound;
    }

    private long receiveCompletionServiceResult(CompletionService<Long> service) {
        long result = 0;
        try {
            Future<Long> future = service.take();
            result = future.get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        return result;
    }
}
