package com.island.simulation.entity.creatures.animals.herbivores;

import com.island.simulation.entity.creatures.animals.Animal;

import com.island.simulation.entity.map.Cell;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString

public class Butterfly extends Animal {
    public Butterfly(String name, double weight, int maxSpeed, String icon, double satiety, Cell cell) {
        super(name, weight, maxSpeed, icon, satiety, cell);
    }

    @Override
    public void reproduce(Cell cell) {
        //появляются в процессе жизненного цикла из гусениц
        }
    }

