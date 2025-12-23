package scheduling;

import java.util.concurrent.PriorityBlockingQueue;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class TiredExecutor {

    private final TiredThread[] workers;
    private final PriorityBlockingQueue<TiredThread> idleMinHeap = new PriorityBlockingQueue<>();
    private final AtomicInteger inFlight = new AtomicInteger(0);

    public TiredExecutor(int numThreads) {
        workers = new TiredThread[numThreads];
        for(int i = 0; i < numThreads; i++){   
            workers[i] = new TiredThread(i, Math.random() + 0.5 );
            workers[i].start(); //while alive the worker will run
            idleMinHeap.add(workers[i]);
        }
    }

    public void submit(Runnable task) {

        if (task == null) throw new IllegalArgumentException("task is null");
        try {
            TiredThread worker = idleMinHeap.take();
            inFlight.incrementAndGet();
            Runnable wrapped = () -> {
                try {
                    task.run();
                } 
                catch(Exception e) {
                    System.out.println(e.getMessage());
                }
                finally {
                    idleMinHeap.offer(worker);

                    int left = inFlight.decrementAndGet();
                    if (left == 0) {
                        synchronized (inFlight) {
                            inFlight.notifyAll();
                        }
                    }
                }
            };
            
            worker.newTask(wrapped); 
            
        } catch (Exception e) {
            Thread.currentThread().interrupt();
        }
        
    }

    public void submitAll(Iterable<Runnable> tasks) {
        // TODO: submit tasks one by one and wait until all finish
        for (Runnable task : tasks) {
            submit(task);
        }

        synchronized(inFlight){
            while(inFlight.get()>0){
                try{
                    inFlight.wait();
                }
                catch(InterruptedException e){
                    Thread.currentThread().interrupt();
                    return;
                }
            }
        }
    }

    public void shutdown() throws InterruptedException {
        synchronized (inFlight) {
            while (inFlight.get() > 0) {
                inFlight.wait();
            }
        }

        for (TiredThread w : workers) {
            w.shutdown();
        }
        for (TiredThread w : workers) {
            w.join();
        }
    }

    public synchronized String getWorkerReport() {
        // TODO: return readable statistics for each worker
        String result="";
        for(int i=0;i<workers.length;i++){
            result=result+"Worker ID: "+workers[i].getWorkerId()+", Fatigue: "+workers[i].getFatigue()
            +", Time Used:"+workers[i].getTimeUsed()+",Time Idle:"+workers[i].getTimeIdle()+"\n";
        }
        return result;
    }
}
