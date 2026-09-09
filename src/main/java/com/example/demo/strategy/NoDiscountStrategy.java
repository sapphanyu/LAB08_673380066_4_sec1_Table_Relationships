package com.example.demo.strategy;

import org.springframework.stereotype.Component;

@Component
public class NoDiscountStrategy implements DiscountStrategy {

    @Override
    public double applyDiscount(double originalPrice) {
        return originalPrice;
    }

    @Override
    public String getDiscountType() {
        return "NONE";
    }
}
