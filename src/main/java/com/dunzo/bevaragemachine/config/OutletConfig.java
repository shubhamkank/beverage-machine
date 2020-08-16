package com.dunzo.bevaragemachine.config;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class OutletConfig {

    @JsonProperty("count_n")
    int numberOfOutlets;
}
