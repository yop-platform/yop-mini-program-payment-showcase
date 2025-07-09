package co.yixiang.yshop.module.pay.service.yeepay;

import co.yixiang.yshop.module.pay.controller.yeepay.vo.YeepayRefundReqVO;
import co.yixiang.yshop.module.pay.controller.yeepay.vo.YeepayRefundRespVO;
import co.yixiang.yshop.module.pay.service.merchantdetails.MerchantDetailsService;
import co.yixiang.yshop.module.pay.dal.dataobject.merchantdetails.MerchantDetailsDO;
import com.yeepay.yop.sdk.service.common.YopClient;
import com.yeepay.yop.sdk.service.common.YopClientBuilder;
import com.yeepay.yop.sdk.service.common.request.YopRequest;
import com.yeepay.yop.sdk.service.common.response.YopResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import jakarta.annotation.Resource;
import java.util.Map;

/**
 * 易宝退款Service实现
 */
@Slf4j
@Service
public class YeepayRefundServiceImpl implements YeepayRefundService {
    private static final YopClient YOP_CLIENT = YopClientBuilder.builder().build();
    @Resource
    private MerchantDetailsService merchantDetailsService;

    @Value("${yop.payType:yeepay}")
    private String yeepayPayType;

    @Override
    public YeepayRefundRespVO refund(YeepayRefundReqVO reqVO) {
        log.info("[易宝退款] 请求参数: {}", reqVO);
        YeepayRefundRespVO respVO = new YeepayRefundRespVO();
        try {
            MerchantDetailsDO merchant = merchantDetailsService.getMerchantDetailsList(
                java.util.Collections.singletonList(yeepayPayType)).stream().findFirst().orElse(null);
            if (merchant == null) {
                log.error("[易宝退款] 商户配置不存在，payType={}", yeepayPayType);
                respVO.setCode("FAIL");
                respVO.setMsg("易宝商户配置不存在");
                respVO.setOrderId(reqVO.getOrderId());
                return respVO;
            }
            YopRequest request = new YopRequest("/rest/v1.0/trade/refund", "POST");
            request.addParameter("merchantNo", reqVO.getMerchantNo());
            request.addParameter("orderId", reqVO.getOrderId());
            request.addParameter("refundRequestId", reqVO.getRefundRequestId());
            request.addParameter("refundAmount", reqVO.getRefundAmount().toPlainString());
            if (reqVO.getDescription() != null) request.addParameter("description", reqVO.getDescription());
            if (reqVO.getMemo() != null) request.addParameter("memo", reqVO.getMemo());
            if (reqVO.getRefundAccountType() != null) request.addParameter("refundAccountType", reqVO.getRefundAccountType());
            if (reqVO.getNotifyUrl() != null) request.addParameter("notifyUrl", reqVO.getNotifyUrl());
            // 可扩展其他参数
            YopResponse yopResponse = YOP_CLIENT.request(request);
            log.info("[易宝退款] 响应: {}", yopResponse);
            Object resultObj = yopResponse.getResult();
            if (resultObj instanceof Map) {
                Map<String, Object> resultMap = (Map<String, Object>) resultObj;
                respVO.setCode(resultMap.getOrDefault("code", "FAIL").toString());
                respVO.setMsg(resultMap.getOrDefault("message", "无返回消息").toString());
                respVO.setOrderId(resultMap.getOrDefault("orderId", "").toString());
                respVO.setRefundRequestId(resultMap.getOrDefault("refundRequestId", "").toString());
                respVO.setUniqueRefundNo(resultMap.getOrDefault("uniqueRefundNo", "").toString());
                respVO.setStatus(resultMap.getOrDefault("status", "").toString());
                respVO.setRefundAmount(resultMap.getOrDefault("refundAmount", "").toString());
                respVO.setRefundRequestDate(resultMap.getOrDefault("refundRequestDate", "").toString());
            } else {
                respVO.setCode("FAIL");
                respVO.setMsg("易宝返回结果为空或类型异常");
            }
        } catch (Exception e) {
            log.error("[易宝退款] 异常: {}", e.getMessage(), e);
            respVO.setCode("FAIL");
            respVO.setMsg(e.getMessage());
        }
        return respVO;
    }

    @Override
    public YeepayRefundRespVO queryRefund(String merchantNo, String orderId, String refundRequestId) {
        log.info("[易宝退款查询] merchantNo={}, orderId={}, refundRequestId={}", merchantNo, orderId, refundRequestId);
        YeepayRefundRespVO respVO = new YeepayRefundRespVO();
        try {
            YopRequest request = new YopRequest("/rest/v1.0/trade/refund/query", "GET");
            request.addParameter("merchantNo", merchantNo);
            request.addParameter("orderId", orderId);
            request.addParameter("refundRequestId", refundRequestId);
            YopResponse yopResponse = YOP_CLIENT.request(request);
            log.info("[易宝退款查询] 响应: {}", yopResponse);
            Object resultObj = yopResponse.getResult();
            if (resultObj instanceof Map) {
                Map<String, Object> resultMap = (Map<String, Object>) resultObj;
                respVO.setCode(resultMap.getOrDefault("code", "FAIL").toString());
                respVO.setMsg(resultMap.getOrDefault("message", "无返回消息").toString());
                respVO.setOrderId(resultMap.getOrDefault("orderId", "").toString());
                respVO.setRefundRequestId(resultMap.getOrDefault("refundRequestId", "").toString());
                respVO.setUniqueRefundNo(resultMap.getOrDefault("uniqueRefundNo", "").toString());
                respVO.setStatus(resultMap.getOrDefault("status", "").toString());
                respVO.setRefundAmount(resultMap.getOrDefault("refundAmount", "").toString());
                respVO.setRefundRequestDate(resultMap.getOrDefault("refundRequestDate", "").toString());
            } else {
                respVO.setCode("FAIL");
                respVO.setMsg("易宝返回结果为空或类型异常");
            }
        } catch (Exception e) {
            log.error("[易宝退款查询] 异常: {}", e.getMessage(), e);
            respVO.setCode("FAIL");
            respVO.setMsg(e.getMessage());
        }
        return respVO;
    }
} 