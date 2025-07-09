package co.yixiang.yshop.module.pay.controller.yeepay;

import co.yixiang.yshop.module.pay.controller.yeepay.vo.YeepayRefundReqVO;
import co.yixiang.yshop.module.pay.controller.yeepay.vo.YeepayRefundRespVO;
import co.yixiang.yshop.module.pay.service.yeepay.YeepayRefundService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import jakarta.annotation.Resource;

/**
 * 易宝退款Controller
 * 暴露退款和退款查询API
 */
@RestController
@RequestMapping("/pay/yeepay/refund")
@Validated
public class YeepayRefundController {
    @Resource
    private YeepayRefundService yeepayRefundService;

    @PostMapping("")
    public YeepayRefundRespVO refund(@RequestBody @Validated YeepayRefundReqVO reqVO) {
        return yeepayRefundService.refund(reqVO);
    }

    @GetMapping("/query")
    public YeepayRefundRespVO queryRefund(@RequestParam String merchantNo,
                                          @RequestParam String orderId,
                                          @RequestParam String refundRequestId) {
        return yeepayRefundService.queryRefund(merchantNo, orderId, refundRequestId);
    }
} 