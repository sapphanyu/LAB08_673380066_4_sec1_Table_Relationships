package com.example.demo.strategy;

import org.springframework.stereotype.Component;

@Component
public class MemberDiscountStrategy implements DiscountStrategy {

    @Override
    public double applyDiscount(double originalPrice) {
        return originalPrice * 0.90;
    }

    @Override
    public String getDiscountType() {
        return "MEMBER";
    }
}
