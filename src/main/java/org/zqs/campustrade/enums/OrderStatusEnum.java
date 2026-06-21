package org.zqs.campustrade.enums;

public enum OrderStatusEnum {
    WAIT_PAY(0, "待付款"),
    PAID(1, "已付款"),
    SHIPPED(2, "已发货"),
    COMPLETED(3, "已完成"),
    CANCELLED(4, "已取消");

    private final int code;
    private final String desc;

    OrderStatusEnum(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public int getCode() { return code; }
    public String getDesc() { return desc; }

    public static OrderStatusEnum of(int code) {
        for (OrderStatusEnum status : values()) {
            if (status.code == code) return status;
        }
        return null;
    }
}