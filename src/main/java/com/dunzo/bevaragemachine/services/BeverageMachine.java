package com.dunzo.bevaragemachine.services;

import com.dunzo.bevaragemachine.daos.BeverageDao;
import com.dunzo.bevaragemachine.daos.IngredientDao;
import com.dunzo.bevaragemachine.entities.Beverage;
import com.dunzo.bevaragemachine.exceptions.BeverageNotFoundException;
import com.dunzo.bevaragemachine.exceptions.InsufficientIngredientException;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.*;

@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class BeverageMachine {

    ExecutorService outletExecutor;
    IngredientDao ingredientDao;
    BeverageDao beverageDao;

    public BeverageMachine(int numberOfOutlets) {
        this.outletExecutor = Executors.newFixedThreadPool(numberOfOutlets);
        this.ingredientDao = IngredientDao.getInstance();
        this.beverageDao = BeverageDao.getInstance();
    }

    public void refillIngredient(String ingredientName, int refillQuantity) {
        ingredientDao.refill(ingredientName, refillQuantity);
    }

    public void addBeverage(String beverageName, Map<String, Integer> ingredients) {
        beverageDao.save(beverageName, ingredients);
    }

    public Future<Void> serveBeverage(String beverageName) throws BeverageNotFoundException {
        Optional<Beverage> beverageOptional = beverageDao.findByName(beverageName);
        if (!beverageOptional.isPresent()) {
            throw new BeverageNotFoundException(String.format("Beverage %s not found", beverageName));
        }

        Beverage beverage = beverageOptional.get();
        return outletExecutor.submit(() -> {
            try {
                ingredientDao.bulkConsume(beverage.getIngredients());
                Thread.sleep(1000);
                log.info("{} is prepared", beverage.getName());
            } catch (InsufficientIngredientException e) {
                log.error("{} cannot be prepared because {}", beverage.getName(), e.getMessage());
            } catch (InterruptedException e) {
                log.error("InterruptedException", e);
                throw e;
            }
            return null;
        });
    }

    public void shutdown() throws InterruptedException {
        outletExecutor.shutdown();
        if (!outletExecutor.awaitTermination(10, TimeUnit.SECONDS)) {
            outletExecutor.shutdownNow();
        }
    }
}
