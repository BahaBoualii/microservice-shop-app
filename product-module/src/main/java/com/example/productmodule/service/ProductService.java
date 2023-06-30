package com.example.productmodule.service;

import com.example.productmodule.dto.ProductRequest;
import com.example.productmodule.dto.ProductResponse;
import com.example.productmodule.model.Product;
import com.example.productmodule.repository.ProductRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class ProductService {
    @Autowired
    ProductRepository productRepository;

    public void createProduct(ProductRequest productRequest){
        Product product = Product.builder()
                .name(productRequest.getName())
                .description(productRequest.getDescription())
                .price(productRequest.getPrice())
                .build();
        productRepository.save(product);
        log.info("Product {} has been saved", product.getId());
    }

    public List<ProductResponse> getAllProducts(){
        List<Product> products = productRepository.findAll();

        return products.stream().map(this::productToProductResponse).toList();

    }

    public ProductResponse getProductById(String id) {
        Optional<Product> product = productRepository.findById(id);
        return productToProductResponse(product.get());
    }

    public ProductResponse productToProductResponse(Product product){
        ProductResponse productResponse = ProductResponse.builder()
                .id(product.getId())
                .name(product.getName())
                .description(product.getDescription())
                .price(product.getPrice())
                .build();
        return productResponse;
    }
}
