package com.scheduler;

public class Main {
    public static void main(String[] args) {
        Database.init();
        Database.clearTasks();

        Scheduler scheduler = new Scheduler();
        try {
            ApiServer.start(scheduler);
        } catch (Exception e) {
            e.printStackTrace();
        }

        System.out.println("Task Scheduler Started!");
    }
}



