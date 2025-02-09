package com.island.simulation.config;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.NoArgsConstructor;

@Getter
@Setter
@AllArgsConstructor
@ToString
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)

public class IslandParameters {
    private int width;
    private int height;
    private int waterPercentage;
    private int landPercentage;
    private int initialPopulationPercentage;
    private double waterCoefficient;
    private double landCoefficient;
    private int maxTurns;
    private List<String> stopCondition;

}
