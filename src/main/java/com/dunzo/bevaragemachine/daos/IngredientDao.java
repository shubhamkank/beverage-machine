package com.dunzo.bevaragemachine.daos;

import com.dunzo.bevaragemachine.entities.Ingredient;
import com.dunzo.bevaragemachine.exceptions.InsufficientIngredientException;

import java.util.*;

public class IngredientDao {

    private static IngredientDao ingredientDao = new IngredientDao();
    private Map<String, Ingredient> ingredientMap = new HashMap<>();

    private IngredientDao() {

    }

    public static IngredientDao getInstance() {
        return ingredientDao;
    }

    public Ingredient findByName(String name) {
        return ingredientMap.get(name);
    }

    public synchronized void refill(String name, int quantity) {
        Ingredient ingredient = findByName(name);
        if (!Objects.isNull(ingredient)) {
            ingredient.setQuantity(ingredient.getQuantity() + quantity);
        } else {
            ingredientMap.put(name, new Ingredient(name, quantity));
        }
    }

    public synchronized void bulkConsume(Map<String, Integer> ingredients) throws InsufficientIngredientException {
        for (Map.Entry<String, Integer> entry : ingredients.entrySet()) {
            checkAvailability(entry.getKey(), entry.getValue());
        }

        for (Map.Entry<String, Integer> entry : ingredients.entrySet()) {
            consume(entry.getKey(), entry.getValue());
        }
    }

    public synchronized void consume(String name, int requiredQuantity) throws InsufficientIngredientException {
        checkAvailability(name, requiredQuantity);
        Ingredient ingredient = findByName(name);
        ingredient.setQuantity(ingredient.getQuantity() - requiredQuantity);
    }

    public void checkAvailability(String name, int requiredQuantity) throws InsufficientIngredientException {
        Ingredient ingredient = findByName(name);
        if (ingredient == null) {
            throw new InsufficientIngredientException(String.format("%s is not available", name));
        }
        if (ingredient.getQuantity() < requiredQuantity) {
            throw new InsufficientIngredientException(String.format("%s is not sufficient", ingredient.getName()));
        }
    }
}
