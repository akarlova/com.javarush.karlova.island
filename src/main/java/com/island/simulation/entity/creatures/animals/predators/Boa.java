package com.island.simulation.entity.creatures.animals.predators;

import com.island.simulation.entity.creatures.animals.Animal;
import com.island.simulation.entity.map.Cell;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString

public class Boa extends Animal {

    public Boa(String name, double weight, int maxSpeed, String icon, double satiety, Cell cell) {
        super(name, weight, maxSpeed, icon, satiety, cell);
    }
}
