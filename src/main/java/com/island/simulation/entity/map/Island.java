package com.island.simulation.entity.map;

import com.island.simulation.config.IslandParameters;
import com.island.simulation.entity.creatures.Creature;
import com.island.simulation.entity.creatures.Worm;
import com.island.simulation.entity.creatures.animals.Animal;
import com.island.simulation.entity.creatures.plants.Grass;
import com.island.simulation.entity.map.structures.Land;
import com.island.simulation.entity.map.structures.Sand;
import com.island.simulation.entity.map.structures.Structure;
import com.island.simulation.entity.map.structures.Water;
import com.island.simulation.factory.CreatureFactory;
import com.island.simulation.services.DunaBanner;
import com.island.simulation.services.LoggingService;
import lombok.Getter;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

@Getter
public class Island {

    private final Cell[][] map;
    private final IslandParameters parameters;
    private final CreatureFactory factory;
    private final AtomicInteger currentTurn = new AtomicInteger(0); //текущий так симуляции
    private final Random random = new Random();

    public Island(IslandParameters parameters, CreatureFactory factory) {
        this.parameters = parameters;
        this.factory = factory;
        this.map = new Cell[parameters.getHeight()][parameters.getWidth()];
        initIsland();
        resetCellStatistics();
    }

    public AtomicInteger getCurrentTurn() {
        return currentTurn;
    }

    public int getWidth() {
        return parameters.getWidth();
    }

    public int getHeight() {
        return parameters.getHeight();
    }

    private void initIsland() {

        for (int y = 0; y < parameters.getHeight(); y++) {
            for (int x = 0; x < parameters.getWidth(); x++) {
                Structure structure = random.nextInt(100) < parameters.getWaterPercentage() ? new Water() : new Land();
                map[y][x] = new Cell(x, y, structure);
                initCellPopulation(map[y][x]);
            }
        }
    }

    private void initCellPopulation(Cell cell) {
        Structure structure = cell.getStructure();
        if (structure instanceof Water) {
            //На клетке с водой изначальная популяция животных составит 70% от максимальной
            //Травы же будет 50% от максимальной, т.к. половина клетки условно занята водоемом
            int grassCount = parameters.getInitialPopulationPercentage();
            addGrassToCell(cell, grassCount);
            addAnimalsToCell(cell, parameters.getWaterCoefficient());


        } else if (structure instanceof Land) {
            //На клетке с землёй изначальная популяция животных составит 50% от максимальной
            //Травы же будет 100% изначально
            int grassCount = parameters.getInitialPopulationPercentage() * 2;
            addGrassToCell(cell, grassCount);
            addAnimalsToCell(cell, parameters.getLandCoefficient());
        }
    }

    private void addAnimalsToCell(Cell cell, double structureCoefficient) {
        factory.getAnimalStats().forEach((name, stats) -> {
            Animal animal = factory.createAnimal(name, cell);
            int maxOnCell = stats.getMaxOnCell();
            int addOnCell = (int) (maxOnCell * parameters.getInitialPopulationPercentage() / 100 * structureCoefficient);
            cell.addCreatureForBirth(animal.getClass(), addOnCell);
        });
    }

    private void addGrassToCell(Cell cell, int count) {
        cell.addCreatureForBirth(Grass.class, count);
    }

    public Cell getCell(int x, int y) {
        if (x < 0 || y < 0 || x >= parameters.getWidth() || y >= parameters.getHeight()) {
            throw new IndexOutOfBoundsException("Выход за пределы Острова");
        }
        return map[y][x];
    }

    //проверка условий остановки симуляции:
    public boolean checkStopCondition() {
        //1. Песок покрыл 80% острова:
        long sandCount = 0;
        for (int y = 0; y < parameters.getHeight(); y++) {
            for (int x = 0; x < parameters.getWidth(); x++) {
                if (map[y][x].getStructure() instanceof Sand) {
                    sandCount++;
                }
            }
        }
        double sandPercentage = (double) sandCount / (parameters.getHeight() * parameters.getWidth()) * 100.0;
        if (sandPercentage >= 80) {
            DunaBanner.displayBanner();
            return true;
        }
        //2. Все животинки издохли (Червь не животное!!!)
        long totalAnimals = 0;
        for (int y = 0; y < parameters.getHeight(); y++) {
            for (int x = 0; x < parameters.getWidth(); x++) {
                for (Map.Entry<Class<? extends Creature>, Integer> entry : map[y][x].getCreaturePopulation().entrySet()) {
                    if (Animal.class.isAssignableFrom(entry.getKey())) {
                        totalAnimals += entry.getValue();
                    }
                }
            }
        }
        if (totalAnimals == 0) {
            LoggingService.log("THEY ALL ARE DEAD");
            return true;
        }
        //3. Завершился период симуляции острова, ему удалось не превратиться в Дюну, или животинки не успели издохнуть

        if (currentTurn.get() > parameters.getMaxTurns()) {
            LoggingService.log("Период симуляции исчерпан");
            return true;
        }
        return false;
    }

    public void incrementTurn() {
        currentTurn.incrementAndGet();
    }

    public void growGrass() {
        for (int y = 0; y < parameters.getHeight(); y++) {
            for (int x = 0; x < parameters.getWidth(); x++) {
                Cell cell = map[y][x];
                Grass grass = new Grass(cell);
                grass.reproduce(cell);
            }
        }
    }

    public List<Creature> getAllAnimals() {
        List<Creature> animals = new ArrayList<>();
        for (int y = 0; y < parameters.getHeight(); y++) {
            for (int x = 0; x < parameters.getWidth(); x++) {
                Cell cell = map[y][x];
                for (Map.Entry<Class<? extends Creature>, Integer> entry : cell.getCreaturePopulation().entrySet()) {

                    if (Animal.class.isAssignableFrom(entry.getKey())) {
                        int count = entry.getValue();
                        Animal demo = factory.createAnimal(entry.getKey().getSimpleName(), cell);
                        for (int i = 0; i < count; i++) {
                            animals.add(demo);
                        }
                    }
                }
            }
        }
        return animals;
    }

    public List<Worm> getAllWorms() {
        List<Worm> worms = new ArrayList<>();
        for (int y = 0; y < parameters.getHeight(); y++) {
            for (int x = 0; x < parameters.getWidth(); x++) {
                Cell cell = map[y][x];
                int count = cell.getCreatureQuantity(Worm.class);
                if (count > 0) {
                    for (int i = 0; i < count; i++) {
                        worms.add(new Worm(cell));
                    }
                }
            }
        }
        return worms;
    }

    public void resetCellStatistics() {
        for (int y = 0; y < parameters.getHeight(); y++) {
            for (int x = 0; x < parameters.getWidth(); x++) {
                map[y][x].resetStats();
            }
        }
    }

    public void updateCellStructure() {
        //получим стрим всех клеток Острова, не являющихся Песком.
        Stream<Cell> validCells = Arrays.stream(map)
                .flatMap(Arrays::stream)
                .filter(cell -> !(cell.getStructure() instanceof Sand));
        //находим клетку с максимальным числом смертей:
        Optional<Cell> cellWithMaxDeaths = validCells
                .max(Comparator.comparingInt(cell ->
                        cell.getDeaths().values().stream().mapToInt(Integer::intValue).sum()
                ));
        //находим клетку с минимальной рождаемостью:
        Optional<Cell> cellWithMinBirths = Arrays.stream(map)
                .flatMap(Arrays::stream)
                .filter(cell -> !(cell.getStructure() instanceof Sand))
                .min(Comparator.comparingInt(cell
                        -> cell.getBirths().values().stream().mapToInt(Integer::intValue).sum()
                ));
        //поменяем структуру в найденных клетках:
        cellWithMaxDeaths.ifPresent(cell -> cell.setStructure(new Sand()));
        //если клетка с мин рождаемостью - это не та же, что и с макс смертностью,
        //ее структуру тоже преобразуем
        if (cellWithMinBirths.isPresent()) {
            if (!cellWithMaxDeaths.isPresent() || !cellWithMinBirths.get().equals(cellWithMaxDeaths.get())) {
                cellWithMinBirths.get().setStructure(new Sand());
            }
        }
        //рандомное добавление клеток Песка для ускорения симуляции Дюны:
        double conversionProbability = 0.15; // 15%
        Arrays.stream(map)
                .flatMap(Arrays::stream)
                .filter(cell -> !(cell.getStructure() instanceof Sand))
                .forEach(cell -> {
                    if(ThreadLocalRandom.current().nextDouble() < conversionProbability) {
                        cell.setStructure(new Sand());
                    }
                });
    }
}
