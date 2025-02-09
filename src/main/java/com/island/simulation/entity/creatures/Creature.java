package com.island.simulation.entity.creatures;

import com.island.simulation.entity.actions.Reproducible;
import com.island.simulation.entity.map.Cell;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.AllArgsConstructor;
import lombok.experimental.SuperBuilder;


@Getter
@Setter
@AllArgsConstructor
@ToString
@SuperBuilder

public abstract class Creature implements Reproducible {

    protected String name;
    protected double weight;
    protected int maxSpeed;
    protected String icon;
    protected Cell cell;


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Creature creature = (Creature) o;
        return name.equals(creature.name) && icon.equals(creature.icon);
    }

    @Override
    public int hashCode() {
       int result = name.hashCode();
       result = 31 * result + icon.hashCode();
       return result;
    }
}
