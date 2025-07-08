package co.yixiang.yshop.module.pay.service.yeepay;

import co.yixiang.yshop.module.pay.controller.yeepay.vo.YeepayOrderCreateReqVO;
import co.yixiang.yshop.module.pay.controller.yeepay.vo.YeepayOrderCreateRespVO;
import co.yixiang.yshop.module.pay.controller.yeepay.vo.YeepayOrderQueryReqVO;
import co.yixiang.yshop.module.pay.controller.yeepay.vo.YeepayOrderQueryRespVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import co.yixiang.yshop.module.pay.service.merchantdetails.MerchantDetailsService;
import co.yixiang.yshop.module.pay.dal.dataobject.merchantdetails.MerchantDetailsDO;
import com.yeepay.yop.sdk.service.common.YopClient;
import com.yeepay.yop.sdk.service.common.YopClientBuilder;
import com.yeepay.yop.sdk.service.common.request.YopRequest;
import com.yeepay.yop.sdk.service.common.response.YopResponse;
import org.springframework.beans.factory.annotation.Value;
import jakarta.annotation.Resource;
import java.util.HashMap;
import java.util.Map;
import com.alibaba.fastjson.JSONObject;
import java.util.concurrent.ConcurrentHashMap;
import com.alibaba.fastjson.JSONArray;

/**
 * 易宝支付-聚合支付统一下单Service实现
 * 负责组装参数、调用SDK、处理响应、日志、异常
 */
@Slf4j
@Service
public class YeepayOrderServiceImpl implements YeepayOrderService {
    private static final YopClient YOP_CLIENT = YopClientBuilder.builder().build();
    @Resource
    private MerchantDetailsService merchantDetailsService;

    @Value("${yop.payType:yeepay}")
    private String yeepayPayType;

    // 永久缓存微信配置校验结果，key=merchantNo+appId
    private static final ConcurrentHashMap<String, Boolean> WX_CONFIG_CACHE = new ConcurrentHashMap<>();

    @Override
    public YeepayOrderCreateRespVO createPrePayOrder(YeepayOrderCreateReqVO reqVO) {
        log.info("[易宝下单] 请求参数: {}", reqVO);
        YeepayOrderCreateRespVO respVO = new YeepayOrderCreateRespVO();
        try {
            // 1. 获取易宝商户配置
            MerchantDetailsDO merchant = merchantDetailsService.getMerchantDetailsList(
                java.util.Collections.singletonList(yeepayPayType)).stream().findFirst().orElse(null);
            if (merchant == null) {
                log.error("[易宝下单] 商户配置不存在，payType={}", yeepayPayType);
                respVO.setCode("FAIL");
                respVO.setMsg("易宝商户配置不存在");
                respVO.setOrderId(reqVO.getOrderId());
                return respVO;
            }
            // 1.1 校验微信配置（自动修正）
            String cacheKey = merchant.getMchId() + "#" + merchant.getAppid();
            if (!Boolean.TRUE.equals(WX_CONFIG_CACHE.get(cacheKey))) {
                try {
                    YopRequest queryConfigReq = new YopRequest("/rest/v2.0/aggpay/wechat-config/query", "GET");
                    queryConfigReq.addParameter("merchantNo", merchant.getMchId());
                    // queryConfigReq.addParameter("parentMerchantNo", merchant.getMchId());
                    queryConfigReq.addParameter("appIdType", "MINI_PROGRAM");
                    YopResponse queryConfigResp = YOP_CLIENT.request(queryConfigReq);
                    log.info("[易宝下单] 微信配置查询响应: {}", queryConfigResp);
                    boolean needSync = true;
                    Object configResult = queryConfigResp.getResult();
                    if (configResult instanceof Map) {
                        Map<String, Object> resultMap = (Map<String, Object>) configResult;
                        Object configResultStr = resultMap.get("configResult");
                        boolean found = false;
                        if (configResultStr != null) {
                            JSONArray arr = JSONArray.parseArray(configResultStr.toString());
                            for (int i = 0; i < arr.size(); i++) {
                                JSONObject obj = arr.getJSONObject(i);
                                JSONArray appIdList = obj.getJSONArray("appIdList");
                                if (appIdList != null) {
                                    for (int j = 0; j < appIdList.size(); j++) {
                                        JSONObject app = appIdList.getJSONObject(j);
                                        if (merchant.getAppid().equals(app.getString("appId")) && "SUCCESS".equals(app.getString("status"))) {
                                            found = true;
                                            break;
                                        }
                                    }
                                }
                            }
                        }
                        needSync = !found;
                    }
                    if (needSync) {
                        // 自动补充微信配置
                        YopRequest syncConfigReq = new YopRequest("/rest/v1.0/aggpay/wechat-config/add-sync", "POST");
                        Map<String, Object> syncBody = new HashMap<>();
                        syncBody.put("merchantNo", merchant.getMchId());
                        // syncBody.put("parentMerchantNo", merchant.getMchId());
                        syncBody.put("appIdList", "[{\"appId\":\"" + merchant.getAppid() + "\",\"appIdType\":\"MINI_PROGRAM\"}]");
                        // 可扩展tradeAuthDirList等参数
                        syncConfigReq.setContent(new com.alibaba.fastjson.JSONObject(syncBody).toJSONString());
                        YopResponse syncConfigResp = YOP_CLIENT.request(syncConfigReq);
                        log.info("[易宝下单] 微信配置同步响应: {}", syncConfigResp);
                    }
                    WX_CONFIG_CACHE.put(cacheKey, true);
                } catch (Exception ex) {
                    log.warn("[易宝下单] 微信配置自动校验/修正异常: {}", ex.getMessage(), ex);
                }
            }
            // 2. 构造YopRequest，按官方参数名
            YopRequest request = new YopRequest("/rest/v1.0/aggpay/pre-pay", "POST");
            request.addParameter("merchantNo", merchant.getMchId());
            request.addParameter("orderId", reqVO.getOrderId());
            request.addParameter("orderAmount", reqVO.getAmount().toPlainString());
            request.addParameter("notifyUrl", merchant.getNotifyUrl());
            request.addParameter("goodsName", reqVO.getSubject());
            request.addParameter("payWay", "MINI_PROGRAM"); // 小程序支付
            request.addParameter("channel", "WECHAT"); // 微信小程序
            request.addParameter("appId", merchant.getAppid());
            request.addParameter("userId", reqVO.getOpenid());
            request.addParameter("userIp", "127.0.0.1"); // TODO: 获取真实IP
            request.addParameter("scene", "OFFLINE"); // 微信渠道支付场景
            // 可扩展其他参数
            // 使用单例YopClient
            YopResponse yopResponse = YOP_CLIENT.request(request);
            log.info("[易宝下单] 响应: {}", yopResponse);
            Object resultObj = yopResponse.getResult();
            if (resultObj instanceof Map) {
                Map<String, Object> resultMap = (Map<String, Object>) resultObj;
                String code = resultMap.getOrDefault("code", "FAIL").toString();
                String msg = resultMap.getOrDefault("message", "无返回消息").toString();
                if ("00000".equals(code)) {
                    String prePayTn = resultMap.getOrDefault("prePayTn", "").toString();
                    respVO.setPrePayTn(prePayTn);
                    respVO.setCode("OPR00000");
                    respVO.setMsg("下单成功");
                } else {
                    respVO.setCode(code);
                    respVO.setMsg(msg);
                }
            } else {
                respVO.setCode("FAIL");
                respVO.setMsg("易宝返回结果为空或类型异常");
            }
            respVO.setOrderId(reqVO.getOrderId());
        } catch (Exception e) {
            log.error("[易宝下单] 异常: {}", e.getMessage(), e);
            respVO.setCode("FAIL");
            respVO.setMsg(e.getMessage());
            respVO.setOrderId(reqVO.getOrderId());
        }
        return respVO;
    }

    @Override
    public YeepayOrderQueryRespVO queryOrderStatus(YeepayOrderQueryReqVO reqVO) {
        log.info("[易宝订单查询] 请求参数: {}", reqVO);
        YeepayOrderQueryRespVO respVO = new YeepayOrderQueryRespVO();
        try {
            MerchantDetailsDO merchant = merchantDetailsService.getMerchantDetailsList(
                java.util.Collections.singletonList(yeepayPayType)).stream().findFirst().orElse(null);
            if (merchant == null) {
                log.error("[易宝订单查询] 商户配置不存在，payType={}", yeepayPayType);
                respVO.setCode("FAIL");
                respVO.setMsg("易宝商户配置不存在");
                respVO.setOrderId(reqVO.getOrderId());
                return respVO;
            }
            YopRequest request = new YopRequest("/rest/v1.0/trade/order/query", "POST");
            request.addParameter("merchantNo", merchant.getMchId());
            request.addParameter("orderId", reqVO.getOrderId());
            YopResponse yopResponse = YOP_CLIENT.request(request);
            log.info("[易宝订单查询] 响应: {}", yopResponse);
            Object resultObj = yopResponse.getResult();
            if (resultObj instanceof Map) {
                Map<String, Object> resultMap = (Map<String, Object>) resultObj;
                String code = resultMap.getOrDefault("code", "FAIL").toString();
                String msg = resultMap.getOrDefault("message", "无返回消息").toString();
                respVO.setCode(code);
                respVO.setMsg(msg);
                respVO.setOrderId(reqVO.getOrderId());
                respVO.setPayStatus(resultMap.getOrDefault("orderStatus", "").toString());
                respVO.setChannel(resultMap.getOrDefault("channel", "").toString());
                try {
                    String amountStr = resultMap.getOrDefault("orderAmount", "0").toString();
                    respVO.setAmount(new java.math.BigDecimal(amountStr));
                } catch (Exception ignore) {}
            } else {
                respVO.setCode("FAIL");
                respVO.setMsg("易宝返回结果为空或类型异常");
                respVO.setOrderId(reqVO.getOrderId());
            }
        } catch (Exception e) {
            log.error("[易宝订单查询] 异常: {}", e.getMessage(), e);
            respVO.setCode("FAIL");
            respVO.setMsg(e.getMessage());
            respVO.setOrderId(reqVO.getOrderId());
        }
        return respVO;
    }
} 