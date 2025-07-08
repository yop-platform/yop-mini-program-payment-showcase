<template>
  <Dialog v-model="visible" title="模拟易宝支付回调" :fullscreen="false" width="600px">
    <el-form label-width="100px">
      <el-form-item label="回调报文">
        <el-input
          type="textarea"
          v-model="rawMsg"
          :rows="10"
          placeholder="请粘贴易宝支付回调报文（JSON格式）"
        />
      </el-form-item>
    </el-form>
    <template #footer>
      <el-button @click="visible = false">取消</el-button>
      <el-button type="primary" :loading="loading" @click="handleSend">发送模拟回调</el-button>
    </template>
    <el-alert v-if="resultMsg" :title="resultMsg" type="success" show-icon class="mt-10px" />
    <el-alert v-if="errorMsg" :title="errorMsg" type="error" show-icon class="mt-10px" />
  </Dialog>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import Dialog from '@/components/Dialog/src/Dialog.vue'
import { postYeepayNotifyJson, postYeepayNotifyForm } from '@/api/pay/yeepayNotify'

const visible = defineModel<boolean>({ default: false })
const rawMsg = ref('')
const loading = ref(false)
const resultMsg = ref('')
const errorMsg = ref('')

// 识别报文类型并组装请求
const handleSend = async () => {
  resultMsg.value = ''
  errorMsg.value = ''
  let msgObj
  try {
    msgObj = JSON.parse(rawMsg.value)
  } catch {
    errorMsg.value = '报文格式错误，请粘贴标准JSON字符串！'
    return
  }
  loading.value = true
  try {
    // SM2密钥格式
    if (msgObj.headers && msgObj.data && msgObj.data.data) {
      const headers = msgObj.headers
      const data = JSON.parse(msgObj.data.data)
      const resp = await postYeepayNotifyJson(data, headers)
      resultMsg.value = '回调成功，响应：' + (resp?.data ? JSON.stringify(resp.data) : '无')
    } 
    // RSA密钥格式
    else if (msgObj.data && msgObj.data.response && msgObj.data.customerIdentification) {
      const formData = {
        response: msgObj.data.response,
        customerIdentification: msgObj.data.customerIdentification
      }
      const resp = await postYeepayNotifyForm(formData)
      resultMsg.value = '回调成功，响应：' + (resp?.data ? JSON.stringify(resp.data) : '无')
    } else {
      errorMsg.value = '无法识别报文类型，请检查格式！'
    }
  } catch (e: any) {
    errorMsg.value = '发送失败：' + (e?.message || e)
  } finally {
    loading.value = false
  }
}
</script> 