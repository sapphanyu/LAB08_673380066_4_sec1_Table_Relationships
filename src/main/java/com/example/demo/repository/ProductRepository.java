package com.example.demo.repository;

import com.example.demo.model.Product;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    @Override
    @EntityGraph(attributePaths = {"detail", "reviews"})
    List<Product> findAll();

    @Override
    @EntityGraph(attributePaths = {"detail", "reviews"})
    Optional<Product> findById(Long id);
}
