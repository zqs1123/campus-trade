package org.zqs.campustrade.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.zqs.campustrade.dto.ProductAddDTO;
import org.zqs.campustrade.dto.ProductListDTO;
import org.zqs.campustrade.dto.ProductVO;
import org.zqs.campustrade.service.ProductService;
import org.zqs.campustrade.utils.JwtUtil;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/product")
public class ProductController {

    @Autowired
    private ProductService productService;

    @PostMapping("/add")
    public String add(@RequestBody ProductAddDTO dto, HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        if (token == null || token.isEmpty()) {
            return "请先登录";
        }
        if (token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        Integer userId = JwtUtil.getUserID(token);
        productService.addProduct(userId, dto);
        return "发布成功";
    }

    @GetMapping("/list")
    public Page<ProductVO> list(ProductListDTO dto) {
        return productService.listProducts(dto);
    }
}