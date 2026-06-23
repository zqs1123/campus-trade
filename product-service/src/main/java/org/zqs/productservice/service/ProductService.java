package org.zqs.productservice.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.zqs.productservice.dto.ProductAddDTO;
import org.zqs.productservice.dto.ProductListDTO;
import org.zqs.productservice.dto.ProductVO;

public interface ProductService {
    void addProduct(Integer userId, ProductAddDTO dto);
    Page<ProductVO> listProducts(ProductListDTO dto);
    ProductVO getProductById(Integer id);
    void deductStock(Integer productId, Integer quantity);
}