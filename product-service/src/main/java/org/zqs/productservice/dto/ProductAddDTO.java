package org.zqs.productservice.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class ProductAddDTO {
    private Integer categoryId;
    private String title;
    private String description;
    private BigDecimal price;
    private BigDecimal originalPrice;
    private String imageUrls;
    private Integer stock;
}