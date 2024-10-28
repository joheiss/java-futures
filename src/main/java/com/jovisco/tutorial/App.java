package com.jovisco.tutorial;

import com.jovisco.tutorial.tasks.FuturePlay;
import com.jovisco.tutorial.tasks.TaskResult;

import java.util.concurrent.*;

public class App
{
    public static void main( String[] args )
    {
        // submit task using single thread executor
        try(ExecutorService executor = Executors.newSingleThreadExecutor()) {
            var future = executor.submit(FuturePlay::doSimpleTask);

            future.get();
        } catch (ExecutionException | InterruptedException e) {
            throw new RuntimeException(e);
        }

        // submit task using fixed thread
        try(ExecutorService executor = Executors.newFixedThreadPool(3)) {
            var future = executor.submit(() -> FuturePlay.doTask("JoTask1",2,false));
            // here is usually some other stuff done ...
            TaskResult result = future.get();
            System.out.println("Task result: " + result);
        } catch (ExecutionException | InterruptedException e) {
            throw new RuntimeException(e);
        }

        // submit tasks using execution completion service
       try (ExecutorService executor = Executors.newFixedThreadPool(3)) {
           var completionService = new ExecutorCompletionService<TaskResult>(executor);

           Callable<TaskResult> callable1 = () -> FuturePlay.doTask("JoTask1",5,false);
           Callable<TaskResult> callable2 = () -> FuturePlay.doTask("JoTask2",3,false);
           Callable<TaskResult> callable3 = () -> FuturePlay.doTask("JoTask3",1,false);

           var task1Future = completionService.submit(callable1);
           var task2Future = completionService.submit(callable2);
           var task3Future = completionService.submit(callable3);

           for (int i = 0; i < 3; i++) {
               var future = completionService.take();
               if (future == task1Future) {
                   var result = future.get();
                   System.out.println("Task #1 result: " + result);
               } else if (future == task2Future) {
                   var result = future.get();
                   System.out.println("Task #2 result: " + result);
               } else if (future == task3Future) {
                   var result = future.get();
                   System.out.println("Task #3 result: " + result);
               }
           }
       } catch (InterruptedException | ExecutionException e) {
           throw new RuntimeException(e);
       }
    }
}
