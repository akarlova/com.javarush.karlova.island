package com.island.simulation.entity.creatures.plants;

import com.island.simulation.entity.actions.Reproducible;
import com.island.simulation.entity.map.Cell;
import com.island.simulation.entity.map.structures.Sand;
import com.island.simulation.entity.map.structures.Water;
import java.util.concurrent.ThreadLocalRandom;

public class Grass extends Plant implements Reproducible {

    private final static int MAX_GRASS_PER_CELL = 200;
    private static final int MIN_LAND_GROWTH = 50;
    private static final int MAX_LAND_GROWTH = 100;
    private static final int MIN_WATER_GROWTH = 80;
    private static final int MAX_WATER_GROWTH = 100;

    public Grass(Cell cell) {
        super("Grass", 1.0, 0, "🌱", cell);
    }

    @Override
    public void reproduce(Cell cell) {
        if (cell.getStructure() instanceof Sand) {
            return; //на Песке растут лишь Червь и специя
        }
        int currentGrassQuantity = cell.getCreatureQuantity(Grass.class);
        if (currentGrassQuantity < MAX_GRASS_PER_CELL) {
            int growthAmount = (cell.getStructure() instanceof Water) ?
                    ThreadLocalRandom.current().nextInt(MIN_WATER_GROWTH, MAX_WATER_GROWTH + 1) :
                    ThreadLocalRandom.current().nextInt(MIN_LAND_GROWTH, MAX_LAND_GROWTH + 1);
            int newGrassQuantity = Math.min(MAX_GRASS_PER_CELL, currentGrassQuantity + growthAmount);
            cell.addCreatureForBirth(Grass.class, newGrassQuantity - currentGrassQuantity);
        }
    }
}

