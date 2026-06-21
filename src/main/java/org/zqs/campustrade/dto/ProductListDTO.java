package org.zqs.campustrade.dto;

import lombok.Data;

@Data
public class ProductListDTO {
    private Integer categoryId;
    private String keyword;
    private Integer page = 1;
    private Integer size = 10;
}