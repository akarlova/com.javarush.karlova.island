package com.island.simulation.services;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class LoggingService {

    //создадим однопоточный пул для последовательного вывода логов, чтобы не перемешать сообщения некоторых
    //методов симуляции с выводом статистик
    private static final ExecutorService loggerExecutor = Executors.newSingleThreadExecutor();

    public static void log(String message) {
        loggerExecutor.submit(() -> System.out.println(message));
    }

    public static void shutdown() {
        loggerExecutor.shutdown();
    }
}
