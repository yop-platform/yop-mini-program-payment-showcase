package co.yixiang.yshop.module.pay.controller.yeepay.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 易宝支付结果通知-响应参数
 */
@Data
public class YeepayPayResultNotifyRespVO {
    @Schema(description = "响应码，如SUCCESS、FAIL")
    private String code;

    @Schema(description = "响应信息")
    private String msg;
} 