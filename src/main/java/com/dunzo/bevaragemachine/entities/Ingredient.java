package com.dunzo.bevaragemachine.entities;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Ingredient {

    String name;

    int quantity;


}
