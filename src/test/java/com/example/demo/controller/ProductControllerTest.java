package com.example.demo.controller;

import com.example.demo.model.Product;
import com.example.demo.model.ProductDetail;
import com.example.demo.repository.ProductRepository;
import com.example.demo.service.ProductService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ProductService productService;

    @Autowired
    private ProductRepository productRepository;

    @BeforeEach
    void setUp() {
        productRepository.deleteAll();
    }

    @Test
    void testRootRedirect() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/products"));
    }

    @Test
    void testListProducts() throws Exception {
        mockMvc.perform(get("/products"))
                .andExpect(status().isOk())
                .andExpect(view().name("products/list"))
                .andExpect(model().attributeExists("products"));
    }

    @Test
    void testShowAddForm() throws Exception {
        mockMvc.perform(get("/products/add"))
                .andExpect(status().isOk())
                .andExpect(view().name("products/add"))
                .andExpect(model().attributeExists("product"));
    }

    @Test
    void testSaveProduct() throws Exception {
        mockMvc.perform(post("/products/save")
                        .param("name", "iPad Pro")
                        .param("category", "Tablets")
                        .param("brand", "Apple")
                        .param("stock", "10")
                        .param("price", "39900.0")
                        .param("discountType", "NONE")
                        .param("detail.description", "M4 iPad Pro 11-inch")
                        .param("detail.warranty", "1 Year")
                        .param("detail.weight", "0.444")
                        .param("detail.dimensions", "24.9x17.7x0.5 cm")
                        .param("detail.manufacturedCountry", "China")
                        .param("reviews[0].reviewer", "Somchai")
                        .param("reviews[0].rating", "5")
                        .param("reviews[0].comment", "Super thin and fast!"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/products"))
                .andExpect(flash().attributeExists("message"));

        assertEquals(1, productRepository.count());
        Product saved = productRepository.findAll().get(0);
        assertEquals("iPad Pro", saved.getName());
        assertEquals("M4 iPad Pro 11-inch", saved.getDetail().getDescription());
        assertEquals(1, saved.getReviews().size());
        assertEquals("Somchai", saved.getReviews().get(0).getReviewer());
    }

    @Test
    void testShowEditForm() throws Exception {
        Product product = new Product("iPad Pro", "Tablets", "Apple", 10, 39900.0, "NONE");
        product.setDetail(new ProductDetail("Description", "1 Year", 0.5, "10x10x1", "USA"));
        Product saved = productService.saveProduct(product);

        mockMvc.perform(get("/products/edit/" + saved.getId()))
                .andExpect(status().isOk())
                .andExpect(view().name("products/edit"))
                .andExpect(model().attributeExists("product"));
    }

    @Test
    void testUpdateProduct() throws Exception {
        Product product = new Product("iPad Pro", "Tablets", "Apple", 10, 39900.0, "NONE");
        product.setDetail(new ProductDetail("Old description", "1 Year", 0.5, "10x10x1", "USA"));
        Product saved = productService.saveProduct(product);

        mockMvc.perform(post("/products/update/" + saved.getId())
                        .param("name", "iPad Pro M4")
                        .param("category", "Tablets")
                        .param("brand", "Apple")
                        .param("stock", "8")
                        .param("price", "42900.0")
                        .param("discountType", "MEMBER")
                        .param("detail.description", "Updated M4 description")
                        .param("detail.warranty", "2 Years")
                        .param("detail.weight", "0.45")
                        .param("detail.dimensions", "24.9x17.7x0.5 cm")
                        .param("detail.manufacturedCountry", "China"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/products"))
                .andExpect(flash().attributeExists("message"));

        Product updated = productRepository.findById(saved.getId()).orElseThrow();
        assertEquals("iPad Pro M4", updated.getName());
        assertEquals("MEMBER", updated.getDiscountType());
        assertEquals("Updated M4 description", updated.getDetail().getDescription());
    }

    @Test
    void testShowDeleteConfirm() throws Exception {
        Product product = new Product("iPad Pro", "Tablets", "Apple", 10, 39900.0, "NONE");
        Product saved = productService.saveProduct(product);

        mockMvc.perform(get("/products/delete/" + saved.getId()))
                .andExpect(status().isOk())
                .andExpect(view().name("products/delete"))
                .andExpect(model().attributeExists("product"));
    }

    @Test
    void testDeleteProduct() throws Exception {
        Product product = new Product("iPad Pro", "Tablets", "Apple", 10, 39900.0, "NONE");
        Product saved = productService.saveProduct(product);

        mockMvc.perform(post("/products/delete/" + saved.getId()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/products"))
                .andExpect(flash().attributeExists("message"));

        assertEquals(0, productRepository.count());
    }
}
