package org.zqs.common.entity;

public enum OrderStatus {
    PENDING_PAYMENT(0, "待付款"),
    PAID(1, "已付款"),
    SHIPPED(2, "已发货"),
    COMPLETED(3, "已完成"),
    CANCELLED(4, "已取消"),
    TIMEOUT(5, "超时关闭");

    private final int code;
    private final String desc;

    OrderStatus(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public int getCode() { return code; }
    public String getDesc() { return desc; }

    public static OrderStatus of(int code) {
        for (OrderStatus status : values()) {
            if (status.code == code) return status;
        }
        return null;
    }
}