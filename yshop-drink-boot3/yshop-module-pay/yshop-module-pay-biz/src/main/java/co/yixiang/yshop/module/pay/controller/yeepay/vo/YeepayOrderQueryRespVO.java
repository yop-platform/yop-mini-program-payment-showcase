package co.yixiang.yshop.module.pay.controller.yeepay.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 易宝聚合支付订单查询-响应参数
 */
@Data
public class YeepayOrderQueryRespVO {
    @Schema(description = "订单号")
    private String orderId;

    @Schema(description = "支付状态，如SUCCESS、FAIL、PROCESSING等")
    private String payStatus;

    @Schema(description = "交易金额，单位元")
    private BigDecimal amount;

    @Schema(description = "支付渠道，如WECHAT、ALIPAY等")
    private String channel;

    @Schema(description = "易宝返回码")
    private String code;

    @Schema(description = "易宝返回信息")
    private String msg;

    /**
     * 是否支付成功
     */
    public boolean isSuccess() {
        return "SUCCESS".equalsIgnoreCase(payStatus);
    }
} 