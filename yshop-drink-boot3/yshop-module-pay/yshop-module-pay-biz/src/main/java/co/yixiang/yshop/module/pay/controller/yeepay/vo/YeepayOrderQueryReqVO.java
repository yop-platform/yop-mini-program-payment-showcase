package co.yixiang.yshop.module.pay.controller.yeepay.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import jakarta.validation.constraints.NotBlank;

/**
 * 易宝聚合支付订单查询-请求参数
 */
@Data
public class YeepayOrderQueryReqVO {
    @Schema(description = "订单号", required = true)
    @NotBlank(message = "订单号不能为空")
    private String orderId;
} 