package co.yixiang.yshop.module.pay.controller.yeepay.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 易宝支付结果通知-请求参数
 */
@Data
public class YeepayPayResultNotifyReqVO {
    @Schema(description = "订单号")
    private String orderId;

    @Schema(description = "支付状态，如SUCCESS、FAIL等")
    private String status;

    @Schema(description = "交易金额，单位元")
    private BigDecimal amount;

    @Schema(description = "支付渠道，如WECHAT、ALIPAY等")
    private String channel;

    @Schema(description = "签名")
    private String sign;

    // 可扩展其他必要字段
} 