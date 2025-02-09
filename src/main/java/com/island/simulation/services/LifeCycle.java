package com.island.simulation.services;

import com.island.simulation.entity.creatures.Worm;
import com.island.simulation.entity.creatures.animals.Animal;
import com.island.simulation.entity.creatures.animals.herbivores.Butterfly;
import com.island.simulation.entity.creatures.animals.herbivores.Caterpillar;
import com.island.simulation.entity.map.Cell;
import com.island.simulation.entity.map.Island;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.stream.Collectors;

public class LifeCycle {
    private final Island island;
    private final IslandStatistics statistics;
    private final ScheduledExecutorService scheduler;
    private final ExecutorService animalPool;
    private final int tickIntervalMillis;


    public LifeCycle(Island island, int tickIntervalMillis) {
        this.island = island;
        this.tickIntervalMillis = tickIntervalMillis;
        this.statistics = new IslandStatistics(island);
        this.scheduler = Executors.newSingleThreadScheduledExecutor();
        int nThreads = Runtime.getRuntime().availableProcessors();
        this.animalPool = Executors.newFixedThreadPool(nThreads);
    }

    public void startSimulation() {
        scheduler.scheduleAtFixedRate(() -> {
            try {
                runTick();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }, 0, tickIntervalMillis, TimeUnit.MILLISECONDS);
    }

    private void runTick() throws InterruptedException {
        LoggingService.log("Начало такта. Текущий такт: " + island.getCurrentTurn().get());
        //растим траву
        island.growGrass();
        //запускаем животных
        List<Animal> animals = island.getAllAnimals().stream()
                .filter(c -> c instanceof Animal)
                .map(c -> (Animal) c)
                .collect(Collectors.toList());
        List<Callable<Void>> animalTasks = new ArrayList<>();

        for (Animal animal : animals) {
            animalTasks.add(() -> {
                animal.move(island);
                animal.eat();
                animal.reproduce(animal.getCell());
                animal.updateState();
                return null;
            });
        }
        List<Future<Void>> futures = animalPool.invokeAll(animalTasks);
        for (Future<Void> future : futures) {
            try {
                future.get();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        }
        //трансформация гусеничек
        processTransformations();
        //деятельность Червя
        List<Worm> worms = island.getAllWorms();
        List<Callable<Void>> wormTasks = new ArrayList<>();
        for (Worm worm : worms) {
            wormTasks.add(() -> {
                worm.move(island);
                return null;
            });
        }
        futures = animalPool.invokeAll(wormTasks);
        for (Future<Void> future : futures) {
            try {
                future.get();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        }

        // обновляем структуры
        island.updateCellStructure();
        //инкрементируем такт
        island.incrementTurn();
        LoggingService.log("После инкремента такта: " + island.getCurrentTurn().get());
        //выводим статистику
        statistics.printStatistics();
        island.resetCellStatistics();
        //проверяем условия для остановки симуляции
        if (island.checkStopCondition()) {
            LoggingService.log("Симуляция завершена");
            shutdown();
        }
    }

    private void shutdown() {
        scheduler.shutdown();
        animalPool.shutdown();
        LoggingService.shutdown();
    }

    private void processTransformations() {
        for (int y = 0; y < island.getHeight(); y++) {
            for (int x = 0; x < island.getWidth(); x++) {
                Cell cell = island.getCell(x, y);
                // преобразование гусениц (Caterpillar) в бабочек (Butterfly)
                int caterpillarCount = cell.getCreatureQuantity(Caterpillar.class);
                if (caterpillarCount > 0) {
                    int transformCount = (int) (caterpillarCount * 0.3);
                    if (transformCount > 0) {
                        cell.addCreatureForBirth(Butterfly.class, transformCount);
                        cell.removeCreatureForDeath(Caterpillar.class, transformCount);
                    }
                }
            }
        }
    }

}
