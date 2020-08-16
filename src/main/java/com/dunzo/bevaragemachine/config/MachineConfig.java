package com.dunzo.bevaragemachine.config;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Map;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MachineConfig {

    OutletConfig outlets;

    @JsonProperty("total_items_quantity")
    Map<String, Integer> ingredients;

    Map<String, Map<String, Integer>> beverages;
}
