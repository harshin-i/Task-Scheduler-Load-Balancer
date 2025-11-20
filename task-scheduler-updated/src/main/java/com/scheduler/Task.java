package com.scheduler;

public class Task {

    public int id;              // DB task id
    public int priority;
    public int processingTime;

    public Task(int priority, int processingTime) {
        this.priority = priority;
        this.processingTime = processingTime;
    }

    @Override
    public String toString() {
        return "Task-" + id + "(priority=" + priority + ", time=" + processingTime + "ms)";
    }
}



