package com.island.simulation.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

public class ConfigLoader {
    private static final ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
    private static final String ISLAND_PARAMETERS_FILE = "islandParameters.yaml";
    private static final String ANIMAL_STATS_FILE = "AnimalStats.yaml";
    private static final String EATING_PROBABILITIES_FILE = "eatingProbabilities.yaml";

    public IslandParameters loadSettings() {
        try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream(ISLAND_PARAMETERS_FILE)) {
            if (inputStream == null) {
                throw new IllegalArgumentException("File not found: " + ISLAND_PARAMETERS_FILE);
            }
            return mapper.readValue(inputStream, IslandParameters.class);
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Ошибка при загрузке " + ISLAND_PARAMETERS_FILE, e);
        }
    }

    public Map<String, AnimalStats> loadAnimalStats() {
        try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream(ANIMAL_STATS_FILE)) {
            if (inputStream == null) {
                throw new IllegalArgumentException("File not found: " + ANIMAL_STATS_FILE);
            }
            return mapper.readValue(inputStream, mapper.getTypeFactory().constructMapType(Map.class, String.class, AnimalStats.class));
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Ошибка при загрузке " + ANIMAL_STATS_FILE, e);
        }
    }

    public EatingProbabilities loadEatingProbabilities() {
        try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream(EATING_PROBABILITIES_FILE)) {
            if (inputStream == null) {
                throw new IllegalArgumentException("Файл не найден: " + EATING_PROBABILITIES_FILE);
            }
            return mapper.readValue(inputStream, EatingProbabilities.class);
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Ошибка при загрузке " + EATING_PROBABILITIES_FILE, e);
        }
    }

}
