package co.yixiang.yshop.module.pay.controller.yeepay.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 易宝聚合支付统一下单-响应参数
 */
@Data
public class YeepayOrderCreateRespVO {
    @Schema(description = "预支付标识prePayTn")
    private String prePayTn;

    @Schema(description = "订单号")
    private String orderId;

    @Schema(description = "易宝返回码")
    private String code;

    @Schema(description = "易宝返回消息")
    private String msg;

    /**
     * 是否下单成功
     */
    public boolean isSuccess() {
        return "OPR00000".equals(code);
    }
} 