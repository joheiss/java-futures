package com.jovisco.tutorial.tasks;

import java.util.concurrent.TimeUnit;

public class FuturePlay {

    public static void doSimpleTask() {

        System.out.printf("%s: Starting Simple Task%n", Thread.currentThread().getName());

        try {
            TimeUnit.SECONDS.sleep(3);
        }
        catch (InterruptedException e) {
            System.out.println("Task interrupted");
        }

        System.out.printf("%s: Ending Simple Task%n", Thread.currentThread().getName());
    }

    public static TaskResult doTask(String name, int seconds, boolean fail  ) {

        System.out.printf("%s: Starting Task %s%n", Thread.currentThread().getName(), name);

        try {
            TimeUnit.SECONDS.sleep(seconds  );
        }
        catch (InterruptedException e) {
            System.out.println("Task interrupted");
        }

        if (fail) throw new RuntimeException("Task failed");

        System.out.printf("%s: Ending Task %s%n", Thread.currentThread().getName(), name);

        return new TaskResult(name, seconds, "something".toUpperCase());
    }
}
