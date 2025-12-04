package com.codewithmosh.store.controller;

import com.codewithmosh.store.dtos.ProductDto;
import com.codewithmosh.store.entities.Product;
import com.codewithmosh.store.mappers.ProductsMappers;
import com.codewithmosh.store.mappers.UserMappers;
import com.codewithmosh.store.repositories.CategoryRepository;
import com.codewithmosh.store.repositories.ProductRepository;
import com.codewithmosh.store.repositories.UserRepository;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;


@RestController
@RequestMapping("/products")
@AllArgsConstructor
@Tag(name = "Product-Controller")

public class ProductController {


    private final ProductRepository productRepository;
    private final ProductsMappers productsMappers;
    private final CategoryRepository categoryRepository;

    @GetMapping
    public List<ProductDto> getAllProducts(
            @RequestParam(name = "categoryId", required = false) Byte categoryId
    ) {
        List<Product> products;

        if (categoryId != null) {
            products = productRepository.findByCategoryId(categoryId);
        }
        else {
            products = productRepository.findAllWithCategory();
        }

       return  products .stream()
               .map(productsMappers::toDto)
               .toList();
    }

  @PostMapping()
    public ResponseEntity<ProductDto> createProduct(
            @RequestBody ProductDto request,
            UriComponentsBuilder uriBuilder){
        var category = categoryRepository.findById(request.getCategoryId()).orElse(null);
        if (category == null){
            return ResponseEntity.badRequest().build();

        }

       var product = productsMappers.toEntity(request);
       product.setCategory(category);
       productRepository.save(product);
       request.setId(product.getId());
       var uri = uriBuilder.path("/products/{id}").buildAndExpand(product.getId()).toUri();


       return ResponseEntity.ok(request);
  }

    @PutMapping("/{id}")
  public ResponseEntity<ProductDto> createProduct(
          @PathVariable Long id,
          @RequestBody ProductDto request,
          UriComponentsBuilder uriBuilder
  ){
        var category = categoryRepository.findById(request.getCategoryId()).orElse(null);
        if (category == null){
            return ResponseEntity.badRequest().build();
        }

        var product = productRepository.findById(id).orElse(null);
        if(product == null){
            return ResponseEntity.notFound().build();
        }
        productsMappers.update(request,product);
        product.setCategory(category);
        productRepository.save(product);
        request.setId(product.getId());


        var uri =   uriBuilder.path("/products/{id}").buildAndExpand(product.getId()).toUri();


        return ResponseEntity.ok(request);

  }
  @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(
            @PathVariable Long id
  ){
        var product = productRepository.findById(id).orElse(null);
        if (product == null){
            return ResponseEntity.notFound().build();
        }
        productRepository.delete(product);
        return ResponseEntity.noContent().build();

  }










}