package com.example.demo.controller;

import com.example.demo.model.Product;
import com.example.demo.model.ProductDetail;
import com.example.demo.model.Review;
import com.example.demo.service.ProductService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/products")
public class ProductController {

    private final ProductService productService;

    // Constructor Injection (adheres to DIP and SRP)
    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    // List all products
    @GetMapping
    public String listProducts(Model model) {
        model.addAttribute("products", productService.getAllProducts());
        return "products/list";
    }

    // Show add form
    @GetMapping("/add")
    public String showAddForm(Model model) {
        Product product = new Product();
        product.setDetail(new ProductDetail());
        // Initialize one empty review so add.html form binding (reviews[0]) succeeds
        product.getReviews().add(new Review());

        model.addAttribute("product", product);
        return "products/add";
    }

    // Save new product (+ ProductDetail and initial Review)
    @PostMapping("/save")
    public String saveProduct(@ModelAttribute Product product, RedirectAttributes redirectAttributes) {
        productService.saveProduct(product);
        redirectAttributes.addFlashAttribute("message", "บันทึกข้อมูลสินค้าเรียบร้อยแล้ว");
        return "redirect:/products";
    }

    // Show edit form
    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        Product product = productService.getProductById(id);
        if (product.getDetail() == null) {
            product.setDetail(new ProductDetail());
        }
        model.addAttribute("product", product);
        return "products/edit";
    }

    // Update product
    @PostMapping("/update/{id}")
    public String updateProduct(@PathVariable Long id, @ModelAttribute Product product, RedirectAttributes redirectAttributes) {
        productService.updateProduct(id, product);
        redirectAttributes.addFlashAttribute("message", "อัปเดตข้อมูลสินค้าเรียบร้อยแล้ว");
        return "redirect:/products";
    }

    // Show delete confirmation
    @GetMapping("/delete/{id}")
    public String showDeleteConfirm(@PathVariable Long id, Model model) {
        Product product = productService.getProductById(id);
        model.addAttribute("product", product);
        return "products/delete";
    }

    // Confirm delete product
    @PostMapping("/delete/{id}")
    public String deleteProduct(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        productService.deleteProduct(id);
        redirectAttributes.addFlashAttribute("message", "ลบสินค้าเรียบร้อยแล้ว");
        return "redirect:/products";
    }
}
