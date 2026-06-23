package org.zqs.productservice.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.zqs.common.entity.Result;
import org.zqs.productservice.dto.ProductAddDTO;
import org.zqs.productservice.dto.ProductListDTO;
import org.zqs.productservice.dto.ProductVO;
import org.zqs.productservice.service.ProductService;

@RestController
@RequestMapping("/api/product")
public class ProductController {

    @Autowired
    private ProductService productService;

    @PostMapping
    public Result<String> add(@RequestBody ProductAddDTO dto,
                              @RequestHeader(value = "X-User-Id", required = false) Integer userId) {
        if (userId == null) {
            return Result.error("未登录");
        }
        productService.addProduct(userId, dto);
        return Result.success("发布成功");
    }

    @GetMapping("/list")
    public Result<Page<ProductVO>> list(ProductListDTO dto) {
        Page<ProductVO> page = productService.listProducts(dto);
        return Result.success(page);
    }

    @GetMapping("/{id}")
    public Result<ProductVO> getById(@PathVariable Integer id) {
        ProductVO vo = productService.getProductById(id);
        return Result.success(vo);
    }

    @PutMapping("/{id}/stock")
    public Result<String> deductStock(@PathVariable Integer id,
                                      @RequestParam Integer quantity) {
        productService.deductStock(id, quantity);
        return Result.success("扣库存成功");
    }
}