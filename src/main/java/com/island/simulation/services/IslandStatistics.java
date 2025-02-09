package com.island.simulation.services;

import com.island.simulation.config.AnimalStats;
import com.island.simulation.entity.creatures.Creature;
import com.island.simulation.entity.creatures.Worm;
import com.island.simulation.entity.creatures.plants.Grass;
import com.island.simulation.entity.map.Island;
import com.island.simulation.entity.map.structures.Land;
import com.island.simulation.entity.map.structures.Sand;
import com.island.simulation.entity.map.structures.Water;
import com.island.simulation.factory.CreatureFactory;
import com.island.simulation.utils.StatsAggregator;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

public class IslandStatistics {

    private final Island island;

    public IslandStatistics(Island island) {
        this.island = island;
    }

    //выводим статистику за текущий такт

    public void printStatistics() {
        Map<String, Integer> births = StatsAggregator.aggregateBirths(island);
        Map<String, Integer> deaths = StatsAggregator.aggregateDeaths(island);
        Map<String, Integer> population = StatsAggregator.aggregateCurrentPopulation(island);


        LoggingService.log("=== Статистика за такт № " + island.getCurrentTurn().get() + " ===");
        for (String creatureName : population.keySet()) {
            // Исключаем Grass и Worm (если у них отдельный вывод)
            if (creatureName.equals("Grass") || creatureName.equals("Worm")) {
                continue;
            }
            int available = population.getOrDefault(creatureName, 0);
            int dead = deaths.getOrDefault(creatureName, 0);
            int born = births.getOrDefault(creatureName, 0);

            String icon = CreatureFactory.getAnimalStats().containsKey(creatureName)
                    ? CreatureFactory.getAnimalStats().get(creatureName).getIcon()
                    : "";
            LoggingService.log(String.format("%s [%s] : dead - %d |were born - %d | available - %d |  ",
                    icon, creatureName, dead, born, available));
        }
        // Вывод для травы и червя отдельно
        int grassCount = StatsAggregator.aggregateSpecificPopulation(island, "Grass");
        LoggingService.log(String.format("🌱 [Grass] : %d", grassCount));
        int wormCount = StatsAggregator.aggregateSpecificPopulation(island, "Worm");
        LoggingService.log(String.format("֎ջ [WORM] : available - %d", wormCount));

        //статистика по структурному составу Острова:

        long landCount = Arrays.stream(island.getMap())
                .flatMap(Arrays::stream)
                .filter(cell -> cell.getStructure() instanceof Land)
                .count();
        long waterCount = Arrays.stream(island.getMap())
                .flatMap(Arrays::stream)
                .filter(cell -> cell.getStructure() instanceof Water)
                .count();
        long sandCount = Arrays.stream(island.getMap())
                .flatMap(Arrays::stream)
                .filter(cell -> cell.getStructure() instanceof Sand)
                .count();
        String landIcon = new Land().getIcon();
        String waterIcon = new Water().getIcon();
        String sandIcon = new Sand().getIcon();

        LoggingService.log(String.format("%s [Land] : %d", landIcon, landCount));
        LoggingService.log(String.format("%s [Water] : %d", waterIcon, waterCount));
        LoggingService.log(String.format("%s [Sand] : %d", sandIcon, sandCount));

        LoggingService.log("=======================================================");
    }
}

