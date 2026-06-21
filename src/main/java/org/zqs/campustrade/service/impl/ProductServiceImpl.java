package org.zqs.campustrade.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.zqs.campustrade.dto.ProductAddDTO;
import org.zqs.campustrade.dto.ProductListDTO;
import org.zqs.campustrade.dto.ProductVO;
import org.zqs.campustrade.entity.Category;
import org.zqs.campustrade.entity.Product;
import org.zqs.campustrade.entity.User;
import org.zqs.campustrade.mapper.CategoryMapper;
import org.zqs.campustrade.mapper.ProductMapper;
import org.zqs.campustrade.mapper.UserMapper;
import org.zqs.campustrade.service.ProductService;

import java.util.stream.Collectors;

@Service
public class ProductServiceImpl implements ProductService {

    @Autowired
    private ProductMapper productMapper;

    @Autowired
    private UserMapper userMapper;

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
    @Cacheable(value = "products", key = "#dto.categoryId + '_' + #dto.keyword + '_' + #dto.page + '_' + #dto.size")
    public Page<ProductVO> listProducts(ProductListDTO dto) {
        // 1. 构建查询条件
        QueryWrapper<Product> wrapper = new QueryWrapper<>();
        if (dto.getCategoryId() != null) {
            wrapper.eq("category_id", dto.getCategoryId());
        }
        if (dto.getKeyword() != null && !dto.getKeyword().isEmpty()) {
            wrapper.like("title", dto.getKeyword());
        }
        wrapper.orderByDesc("create_time");

        // 2. 分页查询
        Page<Product> page = new Page<>(dto.getPage(), dto.getSize());
        productMapper.selectPage(page, wrapper);

        // 3. 转换为 VO（填充用户名和分类名）
        Page<ProductVO> voPage = new Page<>(page.getCurrent(), page.getSize(), page.getTotal());
        voPage.setRecords(page.getRecords().stream().map(product -> {
            ProductVO vo = new ProductVO();
            BeanUtils.copyProperties(product, vo);

            User user = userMapper.selectById(product.getUserId());
            if (user != null) {
                vo.setUserNickname(user.getNickname());
            }

            Category category = categoryMapper.selectById(product.getCategoryId());
            if (category != null) {
                vo.setCategoryName(category.getName());
            }

            return vo;
        }).collect(Collectors.toList()));

        return voPage;
    }
}