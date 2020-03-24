package vz.concurrent_search.searcher;

import com.google.common.collect.Lists;
import vz.concurrent_search.exception.IllegalPoolSizeException;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.*;
import java.util.concurrent.locks.Lock;
import java.util.logging.Logger;

public class ConcurrentFindAndReplaceService<T> {
    private final static Logger LOGGER = Logger.getLogger(ConcurrentFindAndReplaceService.class.getName());

    private Lock locker;

    public ConcurrentFindAndReplaceService() {
    }

    public ConcurrentFindAndReplaceService(Lock locker) {
        this.locker = locker;
    }

    public long findAndReplace(T searched, T replacer, List<T> list, int threadPoolSize) {
        Optional<Lock> lockOptional = Optional.ofNullable(locker);
        lockOptional.ifPresent(Lock::lock);

        ExecutorService executor = Executors.newFixedThreadPool(threadPoolSize);
        CompletionService<Long> completionService = new ExecutorCompletionService<>(executor);

        int subListSize = Optional.of(list.size() / threadPoolSize)
                .filter(size -> size >= 1)
                .orElseThrow(() -> new IllegalPoolSizeException("Pool size is bigger than list size"));

        Lists.partition(list, subListSize)
                .forEach(subList -> completionService.submit(new FindAndReplaceTask<>(searched, replacer, subList)));

        int receivedResults = 0;
        long itemsFound = 0;

        while (receivedResults < threadPoolSize) {
            itemsFound += receiveCompletionServiceResult(completionService);
            receivedResults++;

            LOGGER.info(String.format("%s: %s tasks finished",
                    Thread.currentThread().getName(), receivedResults));
        }

        lockOptional.ifPresent(Lock::unlock);
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
