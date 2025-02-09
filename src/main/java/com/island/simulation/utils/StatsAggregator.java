package com.island.simulation.utils;

import com.island.simulation.entity.creatures.Creature;
import com.island.simulation.entity.map.Cell;
import com.island.simulation.entity.map.Island;

import java.util.HashMap;
import java.util.Map;


public class StatsAggregator {

    public static Map<String, Integer> aggregateBirths(Island island) {
        Map<String, Integer> aggregatedBirths = new HashMap<>();
        Cell[][] map = island.getMap();
        for (Cell[] row : map) {
            for (Cell cell : row) {

                for (Map.Entry<Class<? extends Creature>, Integer> entry : cell.getBirths().entrySet()) {
                    String creatureName = entry.getKey().getSimpleName();
                    aggregatedBirths.merge(creatureName, entry.getValue(), Integer::sum);
                }
            }
        }
        return aggregatedBirths;
    }

    public static Map<String, Integer> aggregateDeaths(Island island) {
        Map<String, Integer> aggregatedDeaths = new HashMap<>();
        Cell[][] map = island.getMap();
        for (Cell[] row : map) {
            for (Cell cell : row) {
                for (Map.Entry<Class<? extends Creature>, Integer> entry : cell.getDeaths().entrySet()) {
                    String creatureName = entry.getKey().getSimpleName();
                    aggregatedDeaths.merge(creatureName, entry.getValue(), Integer::sum);
                }
            }
        }
        return aggregatedDeaths;
    }

    public static Map<String, Integer> aggregateCurrentPopulation(Island island) {
        Map<String, Integer> population = new HashMap<>();
        Cell[][] map = island.getMap();
        for (Cell[] row : map) {
            for (Cell cell : row) {
                for (Map.Entry<Class<? extends Creature>, Integer> entry : cell.getCreaturePopulation().entrySet()) {
                    String creatureName = entry.getKey().getSimpleName();
                    population.merge(creatureName, entry.getValue(), Integer::sum);
                }
            }
        }
        return population;
    }

    public static int aggregateSpecificPopulation(Island island, String creatureName) {
        return aggregateCurrentPopulation(island).getOrDefault(creatureName, 0);
    }
}

