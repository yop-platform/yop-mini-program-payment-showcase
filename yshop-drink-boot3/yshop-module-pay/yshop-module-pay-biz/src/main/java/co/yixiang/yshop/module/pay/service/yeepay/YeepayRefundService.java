package co.yixiang.yshop.module.pay.service.yeepay;

import co.yixiang.yshop.module.pay.controller.yeepay.vo.YeepayRefundReqVO;
import co.yixiang.yshop.module.pay.controller.yeepay.vo.YeepayRefundRespVO;

/**
 * 易宝退款Service
 * 负责组装参数、调用SDK、处理响应、日志、异常
 */
public interface YeepayRefundService {
    /**
     * 申请退款
     * @param reqVO 退款请求参数
     * @return 退款响应
     */
    YeepayRefundRespVO refund(YeepayRefundReqVO reqVO);

    /**
     * 查询退款状态
     * @param merchantNo 商户号
     * @param orderId 原订单号
     * @param refundRequestId 退款请求号
     * @return 退款查询响应
     */
    YeepayRefundRespVO queryRefund(String merchantNo, String orderId, String refundRequestId);
} 