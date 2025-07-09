package co.yixiang.yshop.module.pay.controller.yeepay.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

/**
 * 易宝聚合支付统一下单-请求参数
 */
@Data
public class YeepayOrderCreateReqVO {
    @Schema(description = "微信/支付宝 openid", required = true)
    @NotBlank(message = "openid不能为空")
    private String openid;

    @Schema(description = "订单号", required = true)
    @NotBlank(message = "订单号不能为空")
    private String orderId;

    @Schema(description = "支付金额，单位元", required = true)
    @NotNull(message = "支付金额不能为空")
    private BigDecimal amount;

    @Schema(description = "商品描述", required = true)
    @NotBlank(message = "商品描述不能为空")
    private String subject;

    // 可扩展其他必要参数
} 