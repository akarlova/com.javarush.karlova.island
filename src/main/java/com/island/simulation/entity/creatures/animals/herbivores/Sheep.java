package com.island.simulation.entity.creatures.animals.herbivores;

import com.island.simulation.entity.creatures.animals.Animal;
import com.island.simulation.entity.map.Cell;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString

public class Sheep extends Animal {

    public Sheep(String name, double weight, int maxSpeed, String icon, double satiety, Cell cell) {
        super(name, weight, maxSpeed, icon, satiety, cell);
    }
}
