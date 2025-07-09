package co.yixiang.yshop.module.pay.controller.yeepay.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

/**
 * 易宝退款-请求参数
 */
@Data
public class YeepayRefundReqVO {
    @Schema(description = "原收款商户编号", required = true)
    @NotBlank(message = "merchantNo不能为空")
    private String merchantNo;

    @Schema(description = "原收款交易对应的商户收款请求号", required = true)
    @NotBlank(message = "orderId不能为空")
    private String orderId;

    @Schema(description = "商户退款请求号（唯一）", required = true)
    @NotBlank(message = "refundRequestId不能为空")
    private String refundRequestId;

    @Schema(description = "退款申请金额，单位元，最多两位小数", required = true)
    @NotNull(message = "refundAmount不能为空")
    private BigDecimal refundAmount;

    @Schema(description = "退款原因")
    private String description;

    @Schema(description = "对账备注")
    private String memo;

    @Schema(description = "退款资金来源，FUND_ACCOUNT等")
    private String refundAccountType;

    @Schema(description = "退款结果回调url")
    private String notifyUrl;

    // 可扩展其他必要参数
} 