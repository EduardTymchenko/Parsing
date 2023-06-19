package org.tymchenko;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class App {

    public static void main(String[] args) {
        ExecutorService executorService = Executors.newFixedThreadPool(3);
        String[] sports = {"Football", "Tennis", "Ice Hockey", "Basketball"};

        try {
            for (String sport : sports) {
                executorService.execute(() -> {
                    try {
                        ToolsParsing.reports(sport).forEach(report -> System.out.println(report.toString()));
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                });
            }
            executorService.shutdown();
            executorService.awaitTermination(Long.MAX_VALUE, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }
}

