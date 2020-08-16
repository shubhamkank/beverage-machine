package com.dunzo.bevaragemachine.config;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BeverageMachineConfig {

    MachineConfig machine;
}
