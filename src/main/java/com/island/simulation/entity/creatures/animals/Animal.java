package com.island.simulation.entity.creatures.animals;

import com.island.simulation.config.AnimalStats;
import com.island.simulation.entity.actions.Eatable;
import com.island.simulation.entity.actions.Moveable;
import com.island.simulation.entity.creatures.Creature;
import com.island.simulation.entity.creatures.plants.Grass;
import com.island.simulation.entity.map.Cell;
import com.island.simulation.entity.map.Island;
import com.island.simulation.entity.map.structures.Water;
import com.island.simulation.factory.CreatureFactory;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

@Getter
@ToString
@SuperBuilder

public abstract class Animal extends Creature implements Eatable, Moveable {
    private static final int MAX_EXISTING_WITHOUT_WATER = 5;//одинаково для всех
    @Builder.Default
    private int existingWithoutWater = 0;//счётчик, сколько тактов животинка прожила без воды
    private double satiety;
    private double maxSatiety;
    private final boolean isHerbivore;

    private static final double SATIETY_REDUCTION_FACTOR = 0.5;
    private static final double SATIETY_THRESHOLD = 0.2;

    public Animal(String name, double weight, int maxSpeed, String icon, double satiety,
                  Cell cell) {
        super(name, weight, maxSpeed, icon, cell);
        AnimalStats stats = CreatureFactory.getAnimalStats().get(this.getClass().getSimpleName());
        this.maxSatiety = (stats != null) ? stats.getFoodNeeded() : satiety;
        this.satiety = this.maxSatiety;
        this.isHerbivore = determineIfHerbivore();
    }

    private boolean determineIfHerbivore() {
        return CreatureFactory.getEatingProbabilities()
                .getOrDefault(this.getClass().getSimpleName(), Map.of())
                .containsKey(Grass.class.getSimpleName());
    }

    public void reduceSatiety() {
        satiety *= SATIETY_REDUCTION_FACTOR; // животное теряет 50% текущей сытости за такт
    }

    public boolean isHungry() {
        return satiety < maxSatiety;
    }

    public void setSatiety(double satiety) {
        this.satiety = Math.min(satiety, maxSatiety);
    }

    @Override
    public void reproduce(Cell cell) {
        AnimalStats stats = CreatureFactory.getAnimalStats().get(getClass().getSimpleName());
        if (stats == null) {
            return;
        }
        double reproductionProbability = stats.getReproductionProbability();
        int offspringCount = stats.getOffspringCount();
        int maxOnCell = getMaxOnCell();
        boolean selfReproducing = stats.isSelfReproducing();

        int currentCount = cell.getCreatureQuantity(getClass());
        if (!selfReproducing && currentCount < 2) {
            return;
        }
        double randomReproductionProbability = ThreadLocalRandom.current().nextDouble(100);
        if (randomReproductionProbability < reproductionProbability) {
            int possibleOffspring = Math.min(offspringCount, maxOnCell - currentCount);
            if (possibleOffspring > 0) {
                cell.addCreatureForBirth(getClass(), possibleOffspring);

            }
        }
    }

    @Override
    public void eat() {
        if (!isHungry()) {
            return;
        }
        Map<Class<? extends Creature>, Integer> creaturesOnCell = cell.getCreaturePopulation();

        if (isHerbivore) {
            int grassAmount = creaturesOnCell.getOrDefault(Grass.class, 0);
            if (grassAmount > 0) {
                double foodToEat = Math.min(maxSatiety - satiety, grassAmount);
                satiety += foodToEat;
                int grassUnitsToRemove = (int)Math.ceil(foodToEat);
                cell.removeCreatureForDeath(Grass.class, grassUnitsToRemove);
                return; // травоядное наелось
            }
        }
        // Если травы нет или животное хищник — ищем других существ для обеда
        Map<String, Integer> preyChances = CreatureFactory.getEatingProbabilities().get(this.getClass().getSimpleName());

        for (Class<? extends Creature> preyClass : creaturesOnCell.keySet()) {
            if (preyClass.equals(Grass.class)) continue;

            Integer chanceToEat = 0;
            if (preyChances != null && preyChances.containsKey(preyClass.getSimpleName())) {
                chanceToEat = preyChances.get(preyClass.getSimpleName());
            }

            if (chanceToEat > 0 && ThreadLocalRandom.current().nextInt(100) < chanceToEat) {

                double preyWeight = CreatureFactory.getAnimalStats().get(preyClass.getSimpleName()).getWeight();
                double foodToEat = Math.min(maxSatiety - satiety, preyWeight);
                satiety += foodToEat;
                cell.removeCreatureForDeath(preyClass, 1);
                return; // Животное съело добычу и насытилось, больше никого не ищем, ест только одного за раз
            }
        }
    }

    @Override
    public void move(Island island) {

        if (maxSpeed <= 0) {
            return;
        }
        int speed = ThreadLocalRandom.current().nextInt(1, maxSpeed + 1);

        int[][] directions = new int[][]{
                {-speed, 0}, //вверх
                {speed, 0}, // вниз
                {0, -speed}, // влево
                {0, speed}}; // вправо

        for (int[] dir : directions) {
            int targetY = cell.getY() + dir[0];
            int targetX = cell.getX() + dir[1];
            //проверим границы острова, чтоб не вывалиться за карту:
            if (targetY < 0 || targetY >= island.getHeight() || targetX < 0 || targetX >= island.getWidth()) {
                continue;
            }

            Cell targetCell = island.getCell(targetX, targetY);
            int currentQuantity = targetCell.getCreatureQuantity(this.getClass());

            if (currentQuantity < getMaxOnCell()) {
                cell.removeCreatureForMove(this.getClass(), 1);
                targetCell.addCreatureForMove(this.getClass(), 1);
                setCell(targetCell);
                return;
            }
        }
    }

    protected int getMaxOnCell() {
        return CreatureFactory.getAnimalStats().get(this.getClass().getSimpleName()).getMaxOnCell();
    }

    public void updateWaterStatus() {
        if (cell.getStructure() instanceof Water) {
            resetExistingWithoutWater();
        } else {
            incrementExistingWithoutWater();
        }
    }

    private void incrementExistingWithoutWater() {
        existingWithoutWater++;
    }

    private void resetExistingWithoutWater() { //сбрасываем счётчик Жажды, если животинка успела найти клетку с водой
        existingWithoutWater = 0;
    }

    public void checkSurvival() {
        double starvationThreshold = maxSatiety * SATIETY_THRESHOLD;
        if (satiety < starvationThreshold || existingWithoutWater > MAX_EXISTING_WITHOUT_WATER) {
            cell.removeCreatureForDeath(this.getClass(), 1);
        }
    }

    public void updateState() {
        reduceSatiety();
        updateWaterStatus();
        checkSurvival();
    }
}
