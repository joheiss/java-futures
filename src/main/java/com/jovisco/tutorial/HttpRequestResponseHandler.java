package com.jovisco.tutorial;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class HttpRequestResponseHandler {

    public static void main(String[] args) {
        try (HttpClient client = HttpClient.newHttpClient()) {
            HttpRequest request = HttpRequest.newBuilder().GET()
                    .uri(new URI("https://httpbin.org/delay/10"))
                    .build();
            // send an Http request asynchronously
            var future = client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                    .whenComplete((response, throwable) -> {
                        if (throwable != null) {
                            if (response.statusCode() <= 400) {
                                throw new RuntimeException("Error " + response.statusCode());
                            }
                        }
                    })
                    .thenApply(HttpResponse::body)
                    .thenAccept(System.out::println);
            future.join();
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }
}
