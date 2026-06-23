package org.zqs.productservice.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("product")
public class Product {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private Integer userId;
    private Integer categoryId;
    private String title;
    private String description;
    private BigDecimal price;
    private BigDecimal originalPrice;
    private Integer stock;
    private Integer status;
    private Integer viewCount;
    private String imageUrls;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}