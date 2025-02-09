package com.island.simulation.entity.creatures;

import com.island.simulation.entity.actions.Fightable;
import com.island.simulation.entity.actions.Moveable;
import com.island.simulation.entity.creatures.animals.predators.Tyrannosaurus;
import com.island.simulation.entity.map.Cell;
import com.island.simulation.entity.map.Island;
import com.island.simulation.entity.map.structures.Sand;
import com.island.simulation.factory.CreatureFactory;
import com.island.simulation.services.LoggingService;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class Worm extends Creature implements Fightable, Moveable {

    public Worm(Cell cell) {
        super("WORM", 120000, 10, "֎ջ", cell);
    }

    @Override
    public void reproduce(Cell cell) {
        //Червь не размножается, он появляется на клетке вместе с Песком
    }

    @Override
    public void fight(Cell cell) {
        int tRexCount = cell.getCreatureQuantity(Tyrannosaurus.class);
        if (tRexCount > 0) {
            String tRexIcon = CreatureFactory.getAnimalStats().get("Tyrannosaurus").getIcon();
            String wormIcon = this.getIcon();

            int x = cell.getX();
            int y = cell.getY();

          LoggingService.log("Шаи-Хулуд, Червь Дюны " + wormIcon + " сражается с Тираннозавром " + tRexIcon +
                    " на клетке (" + x + ", " + y + "). Тираннозавр проиграл!");
            cell.removeCreatureForDeath(Tyrannosaurus.class, 1);

        }

    }

    @Override
    public void move(Island island) {

        List<Cell> possibleCells = new ArrayList<>();
        int currentX = cell.getX();
        int currentY = cell.getY();
        int islandWidth = island.getWidth();
        int islandHeight = island.getHeight();

        int[][] directions = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}};
        for (int[] dir : directions) {
            int newX = currentX + dir[1];
            int newY = currentY + dir[0];

            if (newX >= 0 && newX < islandWidth && newY < islandHeight && newY >= 0) {
                Cell targetCell = island.getCell(newX, newY);
                if (targetCell.getStructure() instanceof Sand) {
                    possibleCells.add(targetCell);
                }
            }
        }
        if (!possibleCells.isEmpty()) {
            Cell target = possibleCells.get(ThreadLocalRandom.current().nextInt(possibleCells.size()));
            cell.removeCreatureForMove(Worm.class, 1);
            target.addCreatureForMove(Worm.class, 1);
            setCell(target);

            if (target.getCreatureQuantity(Tyrannosaurus.class) > 0) {
                fight(target);
            }
        }


    }
}

