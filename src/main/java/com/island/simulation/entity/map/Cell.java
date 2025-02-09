package com.island.simulation.entity.map;

import com.island.simulation.entity.creatures.Creature;
import com.island.simulation.entity.creatures.Worm;
import com.island.simulation.entity.map.structures.Sand;
import com.island.simulation.entity.map.structures.Structure;
import lombok.Getter;
import lombok.ToString;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Getter
@ToString
public class Cell {

    final private int x;
    final private int y;
    private Structure structure;
    private final Map<Class<? extends Creature>, Integer> creaturePopulation;
    // Т.к. я обхожусь лишь травой в растениях, мне достаточно одной общей мапы

   Map<Class<? extends Creature>, Integer> births = new ConcurrentHashMap<>();
    Map<Class<? extends Creature>, Integer> deaths = new ConcurrentHashMap<>();

    public Cell(int x, int y, Structure structure) {
        this.x = x;
        this.y = y;
        this.creaturePopulation = new ConcurrentHashMap<>();
        this.structure = structure;
    }

    public void addCreatureForBirth(Class<? extends Creature> creatureType, int quantity) {
        creaturePopulation.merge(creatureType, quantity, Integer::sum);
        births.merge(creatureType, quantity, Integer::sum);
    }

    public void addCreatureForMove(Class<? extends Creature> creatureType, int quantity) {
        creaturePopulation.merge(creatureType, quantity, Integer::sum);
    }

    public void removeCreatureForMove(Class<? extends Creature> creatureType, int quantity) {
        creaturePopulation.merge(creatureType, -quantity, Integer::sum);
        int newCount = creaturePopulation.getOrDefault(creatureType, 0);
        if (newCount < 0) {
            creaturePopulation.put(creatureType, 0);
        }
    }
    public void removeCreatureForDeath(Class<? extends Creature> creatureType, int quantity) {
        creaturePopulation.merge(creatureType, -quantity, Integer::sum);
        int newCount = creaturePopulation.getOrDefault(creatureType, 0);
        if (newCount < 0) {
            creaturePopulation.put(creatureType, 0);
        }
        deaths.merge(creatureType, quantity, Integer::sum);
    }


    public int getCreatureQuantity(Class<? extends Creature> creatureType) {
        return creaturePopulation.getOrDefault(creatureType, 0);
    }

    public void setStructure(Structure structure) {
        if (this.structure instanceof Sand) {
            return;
        }
        this.structure = structure;
        if (structure instanceof Sand) {
            creaturePopulation.merge(Worm.class, 1, Integer::sum);
           // LoggingService.log(String.format("На клетке [%d, %d] появился Червь!%n", x, y));
        }
    }

    public void resetStats() {
        births.clear();
        deaths.clear();
    }

}
