package org.zqs.productservice.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.zqs.common.exception.BusinessException;
import org.zqs.productservice.dto.ProductAddDTO;
import org.zqs.productservice.dto.ProductListDTO;
import org.zqs.productservice.dto.ProductVO;
import org.zqs.productservice.entity.Category;
import org.zqs.productservice.entity.Product;
import org.zqs.productservice.mapper.CategoryMapper;
import org.zqs.productservice.mapper.ProductMapper;
import org.zqs.productservice.service.ProductService;

import java.util.stream.Collectors;

@Service
public class ProductServiceImpl implements ProductService {

    @Autowired
    private ProductMapper productMapper;

    @Autowired
    private CategoryMapper categoryMapper;

    @Override
    @CacheEvict(value = "products", allEntries = true)
    public void addProduct(Integer userId, ProductAddDTO dto) {
        Product product = new Product();
        BeanUtils.copyProperties(dto, product);
        product.setUserId(userId);
        product.setStatus(0);
        product.setViewCount(0);
        productMapper.insert(product);
    }

    @Override
    @Cacheable(value = "products", key = "#dto.categoryId + '_' + (#dto.keyword != null ? #dto.keyword : '') + '_' + #dto.page + '_' + #dto.size")
    public Page<ProductVO> listProducts(ProductListDTO dto) {
        QueryWrapper<Product> wrapper = new QueryWrapper<>();
        if (dto.getCategoryId() != null) {
            wrapper.eq("category_id", dto.getCategoryId());
        }
        if (dto.getKeyword() != null && !dto.getKeyword().isEmpty()) {
            wrapper.like("title", dto.getKeyword());
        }
        wrapper.orderByDesc("create_time");

        Page<Product> page = new Page<>(dto.getPage(), dto.getSize());
        productMapper.selectPage(page, wrapper);

        Page<ProductVO> voPage = new Page<>(page.getCurrent(), page.getSize(), page.getTotal());
        voPage.setRecords(page.getRecords().stream().map(product -> {
            ProductVO vo = new ProductVO();
            BeanUtils.copyProperties(product, vo);
            Category category = categoryMapper.selectById(product.getCategoryId());
            vo.setCategoryName(category != null ? category.getName() : "未分类");
            return vo;
        }).collect(Collectors.toList()));
        return voPage;
    }

    @Override
    public ProductVO getProductById(Integer id) {
        Product product = productMapper.selectById(id);
        if (product == null) {
            throw new BusinessException("商品不存在");
        }
        ProductVO vo = new ProductVO();
        BeanUtils.copyProperties(product, vo);
        return vo;
    }

    @Override
    @Transactional
    public void deductStock(Integer productId, Integer quantity) {
        int rows = productMapper.deductStock(productId, quantity);
        if (rows == 0) {
            throw new BusinessException("库存不足或商品不存在");
        }
    }
}