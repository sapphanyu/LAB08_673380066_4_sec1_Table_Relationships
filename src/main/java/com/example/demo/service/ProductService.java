package com.example.demo.service;

import com.example.demo.model.Product;
import com.example.demo.model.ProductDetail;
import com.example.demo.model.Review;
import com.example.demo.repository.ProductRepository;
import com.example.demo.strategy.DiscountContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class ProductService {

    private final ProductRepository productRepository;
    private final DiscountContext discountContext;

    // Constructor Injection (adheres to DIP and avoids field injection)
    public ProductService(ProductRepository productRepository, DiscountContext discountContext) {
        this.productRepository = productRepository;
        this.discountContext = discountContext;
    }

    @Transactional(readOnly = true)
    public List<Product> getAllProducts() {
        List<Product> products = productRepository.findAll();
        for (Product product : products) {
            calculateAndSetDiscountedPrice(product);
        }
        return products;
    }

    @Transactional(readOnly = true)
    public Product getProductById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Product not found with id: " + id));
        calculateAndSetDiscountedPrice(product);
        return product;
    }

    public Product saveProduct(Product product) {
        // Manage 1:1 bidirectional relationship
        if (product.getDetail() != null) {
            product.getDetail().setProduct(product);
        }

        // Manage 1:N relationship with Review
        if (product.getReviews() != null) {
            List<Review> validReviews = new ArrayList<>();
            for (Review review : product.getReviews()) {
                // If the user filled in reviewer name, attach review
                if (review != null && review.getReviewer() != null && !review.getReviewer().trim().isEmpty()) {
                    review.setProduct(product);
                    if (review.getReviewDate() == null) {
                        review.setReviewDate(LocalDate.now());
                    }
                    validReviews.add(review);
                }
            }
            product.setReviews(validReviews);
        }

        Product savedProduct = productRepository.save(product);
        calculateAndSetDiscountedPrice(savedProduct);
        return savedProduct;
    }

    public Product updateProduct(Long id, Product updatedProduct) {
        Product existingProduct = productRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Product not found with id: " + id));

        // Update product fields
        existingProduct.setName(updatedProduct.getName());
        existingProduct.setCategory(updatedProduct.getCategory());
        existingProduct.setBrand(updatedProduct.getBrand());
        existingProduct.setStock(updatedProduct.getStock());
        existingProduct.setPrice(updatedProduct.getPrice());
        existingProduct.setDiscountType(updatedProduct.getDiscountType());

        // Update 1:1 ProductDetail
        if (updatedProduct.getDetail() != null) {
            if (existingProduct.getDetail() == null) {
                ProductDetail newDetail = updatedProduct.getDetail();
                newDetail.setProduct(existingProduct);
                existingProduct.setDetail(newDetail);
            } else {
                ProductDetail existingDetail = existingProduct.getDetail();
                ProductDetail formDetail = updatedProduct.getDetail();
                existingDetail.setDescription(formDetail.getDescription());
                existingDetail.setWarranty(formDetail.getWarranty());
                existingDetail.setWeight(formDetail.getWeight());
                existingDetail.setDimensions(formDetail.getDimensions());
                existingDetail.setManufacturedCountry(formDetail.getManufacturedCountry());
            }
        }

        Product saved = productRepository.save(existingProduct);
        calculateAndSetDiscountedPrice(saved);
        return saved;
    }

    public void deleteProduct(Long id) {
        if (!productRepository.existsById(id)) {
            throw new IllegalArgumentException("Product not found with id: " + id);
        }
        productRepository.deleteById(id);
    }

    private void calculateAndSetDiscountedPrice(Product product) {
        if (product.getPrice() != null) {
            double discounted = discountContext.calculateDiscountedPrice(
                    product.getDiscountType(),
                    product.getPrice()
            );
            product.setDiscountedPrice(discounted);
        }
    }
}
