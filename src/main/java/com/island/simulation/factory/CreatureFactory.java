package com.island.simulation.factory;

import com.island.simulation.config.AnimalStats;
import com.island.simulation.config.EatingProbabilities;
import com.island.simulation.entity.creatures.animals.Animal;
import com.island.simulation.entity.map.Cell;

import java.lang.reflect.Constructor;
import java.util.Map;


public class CreatureFactory {
    private static Map<String, AnimalStats> animalStats;
    private static Map<String, Map<String, Integer>> eatingProbabilities;

    public CreatureFactory(Map<String, AnimalStats> loadedAnimalStats, EatingProbabilities loadedEatingProbabilities) {
        animalStats = loadedAnimalStats;
        eatingProbabilities = loadedEatingProbabilities.getProbabilities();
    }

    public static Map<String, Map<String, Integer>> getEatingProbabilities() {
        return eatingProbabilities;
    }

    public static Map<String, AnimalStats> getAnimalStats() {
        return animalStats;
    }


    public Animal createAnimal(String name, Cell cell) {
        AnimalStats stats = animalStats.get(name);
        if (stats == null) {
            throw new IllegalArgumentException("Животное " + name + " не найдено");
        }
        try {
            Class<?> animalClass = Class.forName(stats.getAnimalClass());
            Constructor<?> constructor = animalClass.getDeclaredConstructor(String.class, double.class, int.class,
                     String.class, double.class, Cell.class);
            constructor.setAccessible(true);
            return (Animal) constructor.newInstance(
                    name,
                    stats.getWeight(),
                    stats.getMaxSpeed(),
                    stats.getIcon(),
                    stats.getFoodNeeded(),
                    cell
            );
        } catch (Exception e) {
            throw new RuntimeException("Ошибка при создании животного " + name, e);
        }
    }
}
