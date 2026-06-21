package org.zqs.campustrade.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.zqs.campustrade.dto.ProductAddDTO;
import org.zqs.campustrade.dto.ProductListDTO;
import org.zqs.campustrade.dto.ProductVO;

public interface ProductService {
    void addProduct(Integer userId, ProductAddDTO dto);
    Page<ProductVO> listProducts(ProductListDTO dto);
}