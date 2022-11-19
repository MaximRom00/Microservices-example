package by.rom.inventoryservice.service;

import by.rom.inventoryservice.dto.InventoryResponse;
import by.rom.inventoryservice.dto.ProductRequest;
import by.rom.inventoryservice.dto.ProductResponse;
import by.rom.inventoryservice.model.Product;
import by.rom.inventoryservice.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductService {

    private final ProductRepository productRepository;

    @Transactional
    public ProductResponse createProduct(ProductRequest productRequest){
        Product product = Product.builder()
                .name(productRequest.getName())
                .price(productRequest.getPrice())
                .description(productRequest.getDescription())
                .build();

        productRepository.save(product);
        log.info("Product {} is saved", product.getId());

        return mapToProductResponse(product);
    }

    @Transactional(readOnly = true)
    public List<ProductResponse> getAllProducts() {
        List<Product> products = productRepository.findAll();

        return products.stream()
                .map(this::mapToProductResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<InventoryResponse> isInStock(String name){
        return productRepository.findByNameIn(name).stream()
                .map(product ->
                        InventoryResponse.builder()
                                .nameProduct(product.getName())
                                .price(product.getPrice())
                                .isInStock(product.getQuantity() > 0)
                                .build())
                .collect(Collectors.toList());
    }

    private ProductResponse mapToProductResponse(Product product) {
        return ProductResponse.builder()
                .id(product.getId())
                .description(product.getDescription())
                .name(product.getName())
                .price(product.getPrice())
                .build();
    }
}