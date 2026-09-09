package com.example.demo.strategy;

import org.springframework.stereotype.Component;

@Component
public class SeasonalSaleStrategy implements DiscountStrategy {

    @Override
    public double applyDiscount(double originalPrice) {
        return originalPrice * 0.80;
    }

    @Override
    public String getDiscountType() {
        return "SEASONAL";
    }
}
