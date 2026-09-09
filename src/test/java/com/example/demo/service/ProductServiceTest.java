package com.example.demo.service;

import com.example.demo.model.Product;
import com.example.demo.model.ProductDetail;
import com.example.demo.model.Review;
import com.example.demo.repository.ProductRepository;
import com.example.demo.strategy.DiscountContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class ProductServiceTest {

    @Autowired
    private ProductService productService;

    @Autowired
    private ProductRepository productRepository;

    @BeforeEach
    void cleanUp() {
        productRepository.deleteAll();
    }

    @Test
    void testSaveProductWith1to1And1toN() {
        Product product = new Product("iPhone 15 Pro", "Electronics", "Apple", 10, 42900.0, "MEMBER");

        ProductDetail detail = new ProductDetail("Titanium frame with A17 Pro chip", "1 Year", 0.187, "14.6x7.0x0.8 cm", "China");
        product.setDetail(detail);

        Review review = new Review("Somchai", 5, "Great build quality!", LocalDate.now());
        product.addReview(review);

        Product saved = productService.saveProduct(product);

        assertNotNull(saved.getId());
        assertNotNull(saved.getDetail());
        assertNotNull(saved.getDetail().getId());
        assertEquals(38610.0, saved.getDiscountedPrice(), 0.001); // 10% off
        assertEquals(1, saved.getReviews().size());
        assertEquals("Somchai", saved.getReviews().get(0).getReviewer());
        assertEquals(saved.getId(), saved.getReviews().get(0).getProduct().getId());
    }

    @Test
    void testGetAllProducts() {
        Product product1 = new Product("iPad Air", "Tablets", "Apple", 5, 23900.0, "NONE");
        productService.saveProduct(product1);

        Product product2 = new Product("Galaxy S24", "Smartphones", "Samsung", 8, 35900.0, "SEASONAL");
        productService.saveProduct(product2);

        List<Product> products = productService.getAllProducts();
        assertEquals(2, products.size());

        Product s24 = products.stream().filter(p -> p.getName().equals("Galaxy S24")).findFirst().orElseThrow();
        assertEquals(35900.0 * 0.80, s24.getDiscountedPrice(), 0.001);
    }

    @Test
    void testUpdateProductAndDetail() {
        Product product = new Product("MacBook Air", "Laptops", "Apple", 15, 39900.0, "NONE");
        ProductDetail detail = new ProductDetail("M2 chip, 8GB RAM", "1 Year", 1.24, "30.4x21.5x1.1 cm", "China");
        product.setDetail(detail);
        Product saved = productService.saveProduct(product);

        // Update product info and detail
        saved.setPrice(37900.0);
        saved.setDiscountType("MEMBER");
        saved.getDetail().setDescription("M2 chip, 16GB RAM upgraded");

        Product updated = productService.updateProduct(saved.getId(), saved);

        assertEquals(37900.0, updated.getPrice());
        assertEquals("MEMBER", updated.getDiscountType());
        assertEquals(37900.0 * 0.90, updated.getDiscountedPrice(), 0.001);
        assertEquals("M2 chip, 16GB RAM upgraded", updated.getDetail().getDescription());
    }

    @Test
    void testDeleteProductCascades() {
        Product product = new Product("Pixel 8", "Smartphones", "Google", 7, 29900.0, "NONE");
        ProductDetail detail = new ProductDetail("Google Tensor G3", "1 Year", 0.187, "15.0x7.0x0.8 cm", "Vietnam");
        product.setDetail(detail);
        product.addReview(new Review("John", 4, "Clean Android OS", LocalDate.now()));

        Product saved = productService.saveProduct(product);
        Long id = saved.getId();

        productService.deleteProduct(id);

        assertThrows(IllegalArgumentException.class, () -> productService.getProductById(id));
        assertEquals(0, productRepository.count());
    }
}
