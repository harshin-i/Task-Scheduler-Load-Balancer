package com.scheduler;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

public class Worker extends Thread {

    public final String name;
    private final BlockingQueue<Task> inbox = new LinkedBlockingQueue<>();
    private final Scheduler scheduler;
    private final AtomicBoolean processing = new AtomicBoolean(false);

    public Worker(String name, Scheduler scheduler) {
        this.name = name;
        this.scheduler = scheduler;
        setName(name);
    }

    /**
     * Returns current load = number of tasks waiting in this worker + 1 if currently processing.
     * This is what the dashboard will display.
     */
    public int getLoad() {
        return inbox.size() + (processing.get() ? 1 : 0);
    }

    /**
     * Called by Scheduler to assign a task to this worker.
     */
    public void assign(Task t) {
        inbox.add(t);
    }

    @Override
    public void run() {
        while (true) {
            try {
                // take next task from this worker's inbox (blocks if none)
                Task task = inbox.take();

                // mark processing
                processing.set(true);

                System.out.println("[" + name + "] Started " + task);
                Database.updateStatus(task.id, "RUNNING");

                // simulate work
                Thread.sleep(task.processingTime);

                System.out.println("[" + name + "] Completed " + task);
                Database.updateStatus(task.id, "COMPLETED");

                // finished
                processing.set(false);

            } catch (InterruptedException ie) {
                // restore interrupt flag and continue/exit as appropriate
                Thread.currentThread().interrupt();
                break;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}

