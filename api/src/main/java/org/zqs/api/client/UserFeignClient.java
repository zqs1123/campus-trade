package org.zqs.api.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.zqs.common.entity.Result;

@FeignClient(name = "user-service")
public interface UserFeignClient {

    @GetMapping("/api/user/{id}")
    Result<?> getUserById(@PathVariable("id") Integer id);
}