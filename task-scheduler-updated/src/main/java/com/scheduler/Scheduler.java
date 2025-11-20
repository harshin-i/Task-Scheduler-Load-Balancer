package com.scheduler;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class Scheduler {

    public final List<Worker> workers = new ArrayList<>();

    public Scheduler() {

        // create N workers (you can change count)
        int workerCount = 3;
        for (int i = 1; i <= workerCount; i++) {
            Worker w = new Worker("Worker-" + i, this);
            workers.add(w);
            w.start();
        }

        System.out.println("[Scheduler] Started " + workerCount + " workers.");
    }

    /**
     * Submit a task -> assign to the worker with the lowest load.
     */
    public void submit(Task t) {
        // choose worker with smallest load
        Worker target = workers.stream()
                .min(Comparator.comparingInt(Worker::getLoad))
                .orElse(workers.get(0));

        target.assign(t);

        System.out.println("[Scheduler] Assigned " + t + " to " + target.name + " (load=" + target.getLoad() + ")");
    }
}


