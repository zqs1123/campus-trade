package org.zqs.orderservice.dto;

import lombok.Data;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class OrderVO implements Serializable {
    private static final long serialVersionUID = 1L;
    private Integer id;
    private String orderNo;
    private Integer productId;
    private String productTitle;
    private Integer buyerId;
    private String buyerNickname;
    private Integer sellerId;
    private String sellerNickname;
    private BigDecimal amount;
    private Integer quantity;
    private Integer status;
    private String statusDesc;
    private String remark;
    private LocalDateTime createTime;
}