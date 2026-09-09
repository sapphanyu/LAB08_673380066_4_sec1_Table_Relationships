package com.example.demo.strategy;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class DiscountStrategyTest {

    private DiscountContext discountContext;

    @BeforeEach
    void setUp() {
        List<DiscountStrategy> strategies = List.of(
                new NoDiscountStrategy(),
                new MemberDiscountStrategy(),
                new SeasonalSaleStrategy()
        );
        discountContext = new DiscountContext(strategies);
    }

    @Test
    void testNoDiscount() {
        double result = discountContext.calculateDiscountedPrice("NONE", 1000.0);
        assertEquals(1000.0, result, 0.001);
    }

    @Test
    void testMemberDiscount() {
        double result = discountContext.calculateDiscountedPrice("MEMBER", 1000.0);
        assertEquals(900.0, result, 0.001); // 10% off
    }

    @Test
    void testSeasonalDiscount() {
        double result = discountContext.calculateDiscountedPrice("SEASONAL", 1000.0);
        assertEquals(800.0, result, 0.001); // 20% off
    }

    @Test
    void testUnknownDiscountReturnsOriginalPrice() {
        double result = discountContext.calculateDiscountedPrice("UNKNOWN", 1000.0);
        assertEquals(1000.0, result, 0.001);
    }
}
