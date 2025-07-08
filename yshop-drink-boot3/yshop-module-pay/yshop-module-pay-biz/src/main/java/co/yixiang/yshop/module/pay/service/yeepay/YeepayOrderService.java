package co.yixiang.yshop.module.pay.service.yeepay;

import co.yixiang.yshop.module.pay.controller.yeepay.vo.YeepayOrderCreateReqVO;
import co.yixiang.yshop.module.pay.controller.yeepay.vo.YeepayOrderCreateRespVO;
import co.yixiang.yshop.module.pay.controller.yeepay.vo.YeepayOrderQueryReqVO;
import co.yixiang.yshop.module.pay.controller.yeepay.vo.YeepayOrderQueryRespVO;

/**
 * 易宝支付-聚合支付统一下单Service
 * 负责组装参数、调用SDK、处理响应、日志、异常
 */
public interface YeepayOrderService {
    /**
     * 创建易宝聚合支付预支付订单
     * @param reqVO 下单请求参数
     * @return 预支付响应（含prePayTn）
     */
    YeepayOrderCreateRespVO createPrePayOrder(YeepayOrderCreateReqVO reqVO);

    /**
     * 查询易宝聚合支付订单状态
     * @param reqVO 查询请求参数
     * @return 查询响应（含订单状态等）
     */
    YeepayOrderQueryRespVO queryOrderStatus(YeepayOrderQueryReqVO reqVO);
} 