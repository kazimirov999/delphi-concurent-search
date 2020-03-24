package vz.concurrent_search.searcher;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.logging.Logger;

public class FindAndReplaceTask<T> implements Callable<Long> {
    private final static Logger LOGGER = Logger.getLogger(FindAndReplaceTask.class.getName());

    private final T searched;
    private final T replacer;
    private final List<T> list;

    FindAndReplaceTask(T searched, T replacer, List<T> list) {
        this.searched = searched;
        this.replacer = replacer;
        this.list = list;
    }

    @Override
    public Long call() {
        LOGGER.info(String.format("%s: start find and replace task",
                Thread.currentThread().getName()));

        long count = Collections.frequency(list, searched);
        Collections.replaceAll(list, searched, replacer);
        return count;
    }
}
