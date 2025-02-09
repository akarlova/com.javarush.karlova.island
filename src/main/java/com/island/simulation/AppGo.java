package com.island.simulation;

import com.island.simulation.config.AnimalStats;
import com.island.simulation.config.ConfigLoader;
import com.island.simulation.config.EatingProbabilities;
import com.island.simulation.config.IslandParameters;
import com.island.simulation.entity.map.Island;
import com.island.simulation.factory.CreatureFactory;
import com.island.simulation.services.IslandStatistics;
import com.island.simulation.services.LifeCycle;

import java.util.Map;


public class AppGo {
    public static void main(String[] args) {
        ConfigLoader configLoader = new ConfigLoader();
        IslandParameters parameters = configLoader.loadSettings();
        Map<String, AnimalStats> animalStats= configLoader.loadAnimalStats();
        EatingProbabilities eatingProbabilities= configLoader.loadEatingProbabilities();

        CreatureFactory factory = new CreatureFactory(animalStats, eatingProbabilities);
        Island island = new Island(parameters, factory);
        IslandStatistics statistics = new IslandStatistics(island);
        statistics.printStatistics();
        LifeCycle lifeCycle = new LifeCycle(island, 1000);//такт каждую 1с
        lifeCycle.startSimulation();

    }
}

