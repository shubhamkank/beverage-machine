package com.dunzo.beveragemachine.services;

import com.dunzo.bevaragemachine.config.BeverageMachineConfig;
import com.dunzo.bevaragemachine.exceptions.BeverageNotFoundException;
import com.dunzo.bevaragemachine.services.BeverageMachine;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.*;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class BeverageMachineTest {

    BeverageMachine beverageMachine;

    @Before
    public void setUp() throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        BeverageMachineConfig config = objectMapper.readValue(
                getClass().getClassLoader().getResource("fixtures/input.json"), BeverageMachineConfig.class);

        beverageMachine = new BeverageMachine(config.getMachine().getOutlets().getNumberOfOutlets());

        config.getMachine().getIngredients().forEach((ingredientName, quantity) ->
                beverageMachine.refillIngredient(ingredientName, quantity));
        config.getMachine().getBeverages().forEach((beverageName, ingredients) ->
                beverageMachine.addBeverage(beverageName, ingredients));
    }

    @After
    public void tearDown() throws InterruptedException {
        beverageMachine.shutdown();
    }

    @Test
    public void testServeMultipleBeverages() throws BeverageNotFoundException, ExecutionException, InterruptedException {
        List<String> beverages = Arrays.asList("hot_tea", "hot_coffee", "black_tea", "green_tea");

        List<Future<Void>> futures = new ArrayList<>();
        for (String beverage : beverages) {
            futures.add(beverageMachine.serveBeverage(beverage));
        }

        for (Future<Void> future : futures) {
            future.get();
        }
    }
}
