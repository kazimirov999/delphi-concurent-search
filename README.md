# delphi-concurent-search
* FindAndReplaceTask<T> - searches for specific item (searched) in list and replaces it with another item (replacer).
* ConcurrentFindAndReplaceService<T> - starts fixed-size pool thread of FindAndReplaceTask.
* IntegerProducerTask - produces fixed amount of random int numbers in provided list.
* ValueChangeListenerThread - checks for value change of Supplier and calls Runnable when it happens.
