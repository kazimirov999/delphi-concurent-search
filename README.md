# Multithreading and concurrency
Using multithreading and concurrency in a Maven project with applying the Picocli and JUnit tests.
* Producer - called by the SingleThreadScheduledExecutor() every 5 seconds and add some amount of items to the List.
* Listener - running in the background. Calls the Performer if there are any changes in the List. 
* Performer - called by the Listener. Uses the FixedThreadPool() to search template number in the List parallel. Gets and adds an indexes of the List items where number was found to new resulting List.   
