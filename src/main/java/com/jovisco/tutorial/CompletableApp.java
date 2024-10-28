package com.jovisco.tutorial;

import com.jovisco.tutorial.tasks.CompletableFuturePlay;
import com.jovisco.tutorial.tasks.FuturePlay;
import com.jovisco.tutorial.tasks.TaskResult;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;

public class CompletableApp {

    public static void main(String[] args) {

        // prepare tasks to run in parallel
        Supplier<TaskResult> task1 = () -> FuturePlay.doTask("JoTask1", 5, false);
        Supplier<TaskResult> task2 = () -> FuturePlay.doTask("JoTask2", 3, false);

        // supplyAsync will start the task in a separate thread
        // thenCombine will combine the results of task1 and task2
        // thenCompose will handle a completable future returned from a previous
        // thenApply will handle the combined result and generate a new result
        // thenAccept will handle the final result
        var future = CompletableFuture.supplyAsync(task1)
                .exceptionally(e -> new TaskResult("error", 0, "error"))
                .thenCombine(
                        CompletableFuture.supplyAsync(task2)
                                .exceptionally(e -> new TaskResult("error2", 0, "error2")),
                        (result1, result2) -> fuze(result1.name(), result2.name())
                )
                .thenCompose(CompletableFuturePlay::handleResult)
                .thenApply(combined -> combined + " :: Apply handled")
                .thenAccept(applied -> System.out.println(applied + " :: Accept handled"));
        future.join();

        // prepare tasks to run in parallel
        Supplier<TaskResult> task11 = () -> FuturePlay.doTask("JoTask11", 5, false);
        Supplier<TaskResult> task12 = () -> FuturePlay.doTask("JoTask12", 4, false);
        Supplier<TaskResult> task13 = () -> FuturePlay.doTask("JoTask13", 3, false);
        Supplier<TaskResult> task14 = () -> FuturePlay.doTask("JoTask14", 2, false);

        // run 4 tasks in parallel
        var future11 = CompletableFuture.supplyAsync(task11);
        var future12 = CompletableFuture.supplyAsync(task12);
        var future13 = CompletableFuture.supplyAsync(task13);
        var future14 = CompletableFuture.supplyAsync(task14);

        // chain task execution
        var pipeline = future11
                .thenCombine(future12, (r11, r12) -> fuze(r11.name(), r12.name()))
                .thenCombine(future13, (s, r13) -> fuze(s, r13.name()))
                .thenCombine(future14, (s, r14) -> fuze(s, r14.name()))
                .thenApply(s -> s + " :: Apply handled")
                .thenAccept(s -> System.out.println(s + " :: Accept handled"));

        pipeline.join();

        // prepare tasks to run in parallel
        Supplier<TaskResult> task21 = () -> FuturePlay.doTask("JoTask21", 5, false);
        Supplier<TaskResult> task22 = () -> FuturePlay.doTask("JoTask22", 4, false);
        Supplier<TaskResult> task23 = () -> FuturePlay.doTask("JoTask23", 3, false);
        Supplier<TaskResult> task24 = () -> FuturePlay.doTask("JoTask24", 2, false);

        // run tasks 21 & 22 in parallel
        var future21 = CompletableFuture.supplyAsync(task21);
        var future22 = CompletableFuture.supplyAsync(task22);

        // chain task execution
        var pipeline2 = future21
                .thenCombine(future22, (r21, r22) -> fuze(r21.name(), r22.name()))
                .thenApply(s -> s + " :: Glue ")
                .thenCompose(s -> {
                    // run tasks 23 & 24 in parallel - after tasks 21 & 22 have completed
                    var future23 = CompletableFuture.supplyAsync(task23);
                    var future24 = CompletableFuture.supplyAsync(task24);
                    return future23
                            .thenCombine(future24, (r23, r24) -> s + " :: " + fuze(r23.name(),r24.name()));
                })
                .thenAccept(s -> System.out.println(s + " :: Accept handled"));

        pipeline2.join();

        // allOf ...
        Supplier<TaskResult> task31 = () -> FuturePlay.doTask("JoTask31", 5, false);
        Supplier<TaskResult> task32 = () -> FuturePlay.doTask("JoTask32", 4, false);
        Supplier<TaskResult> task33 = () -> FuturePlay.doTask("JoTask33", 3, false);
        Supplier<TaskResult> task34 = () -> FuturePlay.doTask("JoTask34", 2, false);

        // run all 4 in parallel
        var future31 = CompletableFuture.supplyAsync(task31);
        var future32 = CompletableFuture.supplyAsync(task32);
        var future33 = CompletableFuture.supplyAsync(task33);
        var future34 = CompletableFuture.supplyAsync(task34);

        // allOf returns a completable future which completes when all 4 tasks complete
        // BUT note: allOf(...) does NOT WAIT for the tasks to complete it simply returns a completable future
        var futureAll = CompletableFuture.allOf(future31, future32, future33, future34);

        // chain task execution
        var pipeline3 = futureAll
                .thenAccept(unused -> {
                    System.out.println(List.of(future31.join(), future32.join(), future33.join(), future34.join()));
                })
                .exceptionally(CompletableApp::handleError);

        futureAll.join();
    }

    private static String fuze(String s1, String s2) {
        return String.format("Combined result: (%s : %s)",s1, s2);
    }

    private static Void handleError(Throwable t) {
        t.printStackTrace();
        return null;
    }
}
