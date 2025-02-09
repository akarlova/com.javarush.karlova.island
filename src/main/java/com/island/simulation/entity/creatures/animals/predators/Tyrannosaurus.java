package com.island.simulation.entity.creatures.animals.predators;

import com.island.simulation.entity.creatures.Creature;
import com.island.simulation.entity.creatures.animals.Animal;
import com.island.simulation.entity.map.Cell;
import com.island.simulation.factory.CreatureFactory;
import lombok.Getter;
import lombok.ToString;
import java.util.Map;

@Getter
@ToString

public class Tyrannosaurus extends Animal {

    public Tyrannosaurus(String name, double weight, int maxSpeed, String icon, double satiety, Cell cell) {
        super(name, weight, maxSpeed, icon, satiety, cell);
    }

    @Override
    public void eat() {
        String predatorName = getClass().getSimpleName();
        Map<String, Integer> allowedPrey = CreatureFactory.getEatingProbabilities().get(predatorName);
        if (allowedPrey == null || allowedPrey.isEmpty()) {
            return;
        }
        while (isHungry()) {
            boolean ateSomething = false;
            Map<Class<? extends Creature>, Integer> cellPopulation = cell.getCreaturePopulation();
            for (Map.Entry<String, Integer> preyEntry : allowedPrey.entrySet()) {
                String preyName = preyEntry.getKey();
                for (Map.Entry<Class<? extends Creature>, Integer> cellEntry : cellPopulation.entrySet()) {
                    Class<? extends Creature> preyClass = cellEntry.getKey();
                    if (!preyClass.getSimpleName().equals(preyName)) {
                        continue;
                    }
                    int preyCount = cellEntry.getValue();
                    if (preyCount <= 0) {
                        continue;
                    }
                    double foodNeeded = getMaxSatiety() - getSatiety();
                    double preyFoodValue = CreatureFactory.getAnimalStats().get(preyName).getWeight();
                    double foodConsumed = Math.min(foodNeeded, preyFoodValue);
                    setSatiety(getSatiety() + foodConsumed);
                    cell.removeCreatureForDeath(preyClass, 1);
                    ateSomething = true;
                    if (!isHungry()) {
                        break;
                    }
                }
                if (!isHungry()) {
                    break;
                }
            }
            if (!ateSomething) {
                break;
            }
        }
    }
}



