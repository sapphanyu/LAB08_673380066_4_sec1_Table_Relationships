package com.example.demo.strategy;

import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class DiscountContext {

    private final Map<String, DiscountStrategy> strategies = new HashMap<>();

    public DiscountContext(List<DiscountStrategy> strategyList) {
        for (DiscountStrategy strategy : strategyList) {
            strategies.put(strategy.getDiscountType().toUpperCase(), strategy);
        }
    }

    public double calculateDiscountedPrice(String discountType, double originalPrice) {
        if (discountType == null) {
            return originalPrice;
        }
        DiscountStrategy strategy = strategies.get(discountType.toUpperCase());
        if (strategy != null) {
            return strategy.applyDiscount(originalPrice);
        }
        return originalPrice;
    }
}
