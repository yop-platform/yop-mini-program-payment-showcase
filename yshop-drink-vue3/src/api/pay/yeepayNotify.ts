import request from '@/config/axios'

// 模拟易宝支付回调 - SM2密钥(json)
export const postYeepayNotifyJson = async (data: any, headers: any) => {
  return await request.postOriginal({
    url: '/pay/yeepay/notify',
    data,
    headersType: 'application/json',
    headers
  })
}

// 模拟易宝支付回调 - RSA密钥(form)
export const postYeepayNotifyForm = async (data: any) => {
  return await request.postOriginal({
    url: '/pay/yeepay/notify',
    data,
    headersType: 'application/x-www-form-urlencoded'
  })
} 