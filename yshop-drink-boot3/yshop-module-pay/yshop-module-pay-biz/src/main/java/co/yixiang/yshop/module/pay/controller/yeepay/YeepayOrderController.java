package co.yixiang.yshop.module.pay.controller.yeepay;

import co.yixiang.yshop.module.pay.controller.yeepay.vo.YeepayOrderCreateReqVO;
import co.yixiang.yshop.module.pay.controller.yeepay.vo.YeepayOrderCreateRespVO;
import co.yixiang.yshop.module.pay.controller.yeepay.vo.YeepayOrderQueryReqVO;
import co.yixiang.yshop.module.pay.controller.yeepay.vo.YeepayOrderQueryRespVO;
import co.yixiang.yshop.module.pay.service.yeepay.YeepayOrderService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import jakarta.annotation.Resource;

/**
 * 易宝支付-聚合支付统一下单Controller
 * 暴露下单API，返回prePayTn
 */
@RestController
@RequestMapping("/pay/yeepay/order")
@Validated
public class YeepayOrderController {
    @Resource
    private YeepayOrderService yeepayOrderService;

    @PostMapping("/pre-pay")
    public YeepayOrderCreateRespVO createPrePayOrder(@RequestBody YeepayOrderCreateReqVO reqVO) {
        return yeepayOrderService.createPrePayOrder(reqVO);
    }

    @PostMapping("/query")
    public YeepayOrderQueryRespVO queryOrderStatus(@RequestBody YeepayOrderQueryReqVO reqVO) {
        return yeepayOrderService.queryOrderStatus(reqVO);
    }
} 