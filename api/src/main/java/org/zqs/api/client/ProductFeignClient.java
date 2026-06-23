package org.zqs.api.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.zqs.common.entity.Result;

@FeignClient(name = "product-service")
public interface ProductFeignClient {

    @GetMapping("/api/product/{id}")
    Result<?> getProductById(@PathVariable("id") Integer id);

    @PutMapping("/api/product/{id}/stock")
    Result<?> deductStock(@PathVariable("id") Integer id, @RequestParam("quantity") Integer quantity);
}