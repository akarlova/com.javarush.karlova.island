package com.island.simulation.entity.creatures.plants;

import com.island.simulation.entity.creatures.Creature;
import com.island.simulation.entity.map.Cell;

public abstract class Plant extends Creature {
    public Plant(String name, double weight, int maxSpeed, String icon, Cell cell) {
        super(name, weight, maxSpeed, icon, cell);
    }
}
