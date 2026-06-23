package org.zqs.orderservice.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class OrderCreateDTO {
    private Integer productId;
    private BigDecimal amount;
    private Integer quantity = 1;
    private String remark;
}