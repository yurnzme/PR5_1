package com.naruto;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ThreadLocalRandom;

public class AsyncTasksBooking {

    public static void main(String[] args) throws ExecutionException, InterruptedException {

        CompletableFuture<String> task1 = CompletableFuture.supplyAsync(() -> fetchData("Source 1"));
        CompletableFuture<String> task2 = CompletableFuture.supplyAsync(() -> fetchData("Source 2"));
        CompletableFuture<String> task3 = CompletableFuture.supplyAsync(() -> fetchData("Source 3"));

        CompletableFuture<Object> firstCompleted = CompletableFuture.anyOf(task1, task2, task3);
        System.out.println("First completed result: " + firstCompleted.get());


        CompletableFuture<Boolean> seatAvailability = CompletableFuture.supplyAsync(() -> checkSeatAvailability());
        CompletableFuture<Integer> bestPrice = CompletableFuture.supplyAsync(() -> findBestPrice());

        CompletableFuture<Void> bookingFlow = seatAvailability.thenCombine(bestPrice, (available, price) -> {
            if (available) {
                bookTicket(price);
                return true;
            } else {
                throw new RuntimeException("No seats available");
            }
        }).thenAccept(result -> {
            System.out.println("Booking completed successfully.");
        }).exceptionally(ex -> {
            System.err.println("Booking failed: " + ex.getMessage());
            return null;
        });


        bookingFlow.thenRun(() -> System.out.println("Booking completed successfully."))
                   .exceptionally(ex -> {
                       System.err.println("Booking failed: " + ex.getMessage());
                       return null;
                   });
    }

    private static String fetchData(String source) {
        try {
            Thread.sleep(ThreadLocalRandom.current().nextInt(1000, 2000));
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        return source + " data";
    }

    private static boolean checkSeatAvailability() {
        try {
            Thread.sleep(ThreadLocalRandom.current().nextInt(1000, 2000));
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        return ThreadLocalRandom.current().nextBoolean();
    }

    private static int findBestPrice() {
        try {
            Thread.sleep(ThreadLocalRandom.current().nextInt(1000, 2000));
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        return ThreadLocalRandom.current().nextInt(50, 500);
    }

    private static boolean bookTicket(int price) {
        try {
            Thread.sleep(ThreadLocalRandom.current().nextInt(500, 1000));
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        System.out.println("Ticket booked at price: $" + price);
        return true;
    }
}
