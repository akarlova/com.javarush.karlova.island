package com.island.simulation.config;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
//@JsonIgnoreProperties(ignoreUnknown = true)

public class AnimalStats {
    @JsonProperty("class")
    private String animalClass;
    private double weight;
    private int maxSpeed;
    private int maxOnCell;
    private double foodNeeded;
    private String icon;
    private double reproductionProbability;
    private int offspringCount;
    private boolean selfReproducing;

}
