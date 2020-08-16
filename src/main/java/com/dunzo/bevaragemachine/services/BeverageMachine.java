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

    /**
     * Refill the provided ingredient
     *
     * @param ingredientName the ingredient name
     * @param refillQuantity the quantity to refill
     */
    public void refillIngredient(String ingredientName, int refillQuantity) {
        ingredientDao.refill(ingredientName, refillQuantity);
    }

    /**
     * Add beverage
     *
     * @param beverageName the beverage name
     * @param ingredients a map of ingredient and the corresponding quantity required to prepare the beverage
     */
    public void addBeverage(String beverageName, Map<String, Integer> ingredients) {
        beverageDao.save(beverageName, ingredients);
    }

    /**
     * Serve beverage via any of the available outlet.
     * Using executor service to restrict the number of beverage served at a same time (additional requests are queued)
     *
     * @param beverageName the beverage name
     * @return the future object to track the progress of beverage preparation
     * @throws BeverageNotFoundException when the beverage name does not exist
     */
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

    /**
     * Gracefully shutdown the beverage machine
     *
     * @throws InterruptedException if interrupted while waiting
     */
    public void shutdown() throws InterruptedException {
        outletExecutor.shutdown();
        if (!outletExecutor.awaitTermination(10, TimeUnit.SECONDS)) {
            outletExecutor.shutdownNow();
        }
    }
}
