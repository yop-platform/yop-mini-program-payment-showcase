package co.yixiang.yshop.module.pay.controller.yeepay.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 易宝退款-响应参数
 */
@Data
public class YeepayRefundRespVO {
    @Schema(description = "易宝返回码")
    private String code;

    @Schema(description = "易宝返回消息")
    private String msg;

    @Schema(description = "商户收款订单号")
    private String orderId;

    @Schema(description = "商户退款请求号")
    private String refundRequestId;

    @Schema(description = "易宝退款订单号")
    private String uniqueRefundNo;

    @Schema(description = "退款订单状态（PROCESSING/SUCCESS/FAILED等）")
    private String status;

    @Schema(description = "退款申请金额")
    private String refundAmount;

    @Schema(description = "退款受理时间")
    private String refundRequestDate;

    /**
     * 是否退款受理成功
     */
    public boolean isSuccess() {
        return "OPR00000".equals(code);
    }
} 