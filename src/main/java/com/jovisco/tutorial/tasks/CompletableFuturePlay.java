package com.jovisco.tutorial.tasks;

import com.jovisco.tutorial.CompletableApp;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousFileChannel;
import java.nio.channels.CompletionHandler;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.concurrent.CompletableFuture;

public class CompletableFuturePlay {

    public static CompletableFuture<String> handleResult(String result) {
        return CompletableFuture.supplyAsync(() -> {
            System.out.printf("%s: Handling Compose%n", Thread.currentThread().getName());
            return result + " :: Compose handled";
        });
    }

    public static CompletableFuture<String> readFileAsync(String fileName) throws IOException {

        // create a completable future
        var future = new CompletableFuture<String>();

        // create path to file
        var path = Paths.get(".").resolve(fileName);

        try (AsynchronousFileChannel channel = AsynchronousFileChannel.open(path, StandardOpenOption.READ)) {
            var buffer = ByteBuffer.allocate((int) path.toFile().length());
            channel.read(buffer, 0, buffer, new CompletionHandler<Integer, ByteBuffer>() {
                @Override
                public void completed(Integer result, ByteBuffer attachment) {
                    // extract the data from the attachment
                    attachment.flip();
                    byte[] data = new byte[attachment.limit()];
                    attachment.get(data);
                    attachment.clear();
                    // complete successfully
                    future.complete(new String(data));
                }

                @Override
                public void failed(Throwable exc, ByteBuffer attachment) {
                    // complete exceptionally
                    future.completeExceptionally(exc);
                }
            });
            return future;
        }
    }
}
