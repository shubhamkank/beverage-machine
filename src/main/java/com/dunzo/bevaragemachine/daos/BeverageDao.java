package com.dunzo.bevaragemachine.daos;

import com.dunzo.bevaragemachine.entities.Beverage;

import java.util.*;

public class BeverageDao {

    private static BeverageDao beverageDao = new BeverageDao();
    private Map<String, Beverage> beverageMap = new HashMap<>();

    private BeverageDao() {

    }

    public static BeverageDao getInstance() {
        return beverageDao;
    }

    public void save(String name, Map<String, Integer> ingredients) {
        beverageMap.put(name, new Beverage(name, ingredients));
    }

    public Optional<Beverage> findByName(String name) {
        return Optional.ofNullable(beverageMap.get(name));
    }
}
