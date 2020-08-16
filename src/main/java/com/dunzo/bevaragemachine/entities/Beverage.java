package com.dunzo.bevaragemachine.entities;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Map;

@Getter
@Setter
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Beverage {

    String name;

    Map<String, Integer> ingredients;
}
