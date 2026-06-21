package org.zqs.campustrade.dto;

import lombok.Data;

@Data
public class OrderCreateDTO {
    private Integer productId;
    private String remark;
}