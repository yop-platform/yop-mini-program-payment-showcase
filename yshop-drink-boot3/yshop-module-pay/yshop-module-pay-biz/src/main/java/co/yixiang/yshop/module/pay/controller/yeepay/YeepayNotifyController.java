package co.yixiang.yshop.module.pay.controller.yeepay;

import co.yixiang.yshop.module.pay.controller.yeepay.vo.YeepayPayResultNotifyReqVO;
import co.yixiang.yshop.module.pay.controller.yeepay.vo.YeepayPayResultNotifyRespVO;
import co.yixiang.yshop.module.pay.service.yeepay.YeepayOrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMethod;
import jakarta.annotation.Resource;
import com.yeepay.yop.sdk.utils.DigitalEnvelopeUtils;
import org.springframework.web.bind.annotation.RestController;
import com.alibaba.fastjson.JSON;
import co.yixiang.yshop.module.pay.mq.producer.PayNoticeProducer;

/**
 * 易宝支付结果通知回调Controller
 */
@Slf4j
@RestController
@RequestMapping({"/pay/yeepay/notify", "/admin-api/pay/yeepay/notify"})
public class YeepayNotifyController {
    @Resource
    private YeepayOrderService yeepayOrderService;

    @Resource
    private PayNoticeProducer payNoticeProducer;

    @PostMapping(consumes = {"application/x-www-form-urlencoded", "application/json"})
    public String payResultNotify(@RequestParam(required = false) String response,
                                 @RequestParam(required = false) String customerIdentification,
                                 @RequestBody(required = false) String body) {
        log.info("[易宝支付结果通知] response={}, customerIdentification={}, body={}", response, customerIdentification, body);
        // 1. 验签与解析（建议用SDK，简化处理）
        // 2. 解析body为Map或VO
        // 3. 幂等处理、订单状态更新
        // 4. 日志与响应
        try {
            // 2. 验签与解密
            if (response == null || response.isEmpty()) {
                log.error("[易宝支付结果通知] response参数为空");
                return "FAIL";
            }
            String plainText;
            try {
                plainText = DigitalEnvelopeUtils.decrypt(response, "RSA2048");
            } catch (Exception ex) {
                log.error("[易宝支付结果通知] 解密失败: {}", ex.getMessage(), ex);
                return "FAIL";
            }
            log.info("[易宝支付结果通知] 解密明文: {}", plainText);
            // 3. 业务参数映射与订单状态更新
            YeepayPayResultNotifyReqVO notifyVO = null;
            try {
                notifyVO = JSON.parseObject(plainText, YeepayPayResultNotifyReqVO.class);
            } catch (Exception ex) {
                log.error("[易宝支付结果通知] 明文解析失败: {}", ex.getMessage(), ex);
                return "FAIL";
            }
            log.info("[易宝支付结果通知] 业务参数: {}", notifyVO);
            if (notifyVO != null && "SUCCESS".equalsIgnoreCase(notifyVO.getStatus())) {
                try {
                    payNoticeProducer.sendPayNoticeMessage(notifyVO.getOrderId(), "weixin");
                    log.info("[易宝支付结果通知] 已发送支付成功MQ消息，orderId={}", notifyVO.getOrderId());
                } catch (Exception ex) {
                    log.error("[易宝支付结果通知] MQ消息发送异常: {}", ex.getMessage(), ex);
                    return "FAIL";
                }
            }
            return "SUCCESS";
        } catch (Exception e) {
            log.error("[易宝支付结果通知] 处理异常", e);
            return "FAIL";
        }
    }
} 