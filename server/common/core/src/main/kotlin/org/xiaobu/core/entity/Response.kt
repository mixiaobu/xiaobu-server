package org.xiaobu.core.entity

import com.alibaba.fastjson2.toJSONString

/**
 * 响应数据类，用于封装API返回的各种信息
 * @param code 状态码，表示请求的处理结果
 * @param status 请求状态，true表示成功，false表示失败
 * @param message 状态消息，对请求结果的描述
 * @param data 返回的数据，可以是任意类型
 */
data class Response(
    val code: Int, val status: Boolean, val message: String = "", val data: Any? = null
) {
    companion object {
        /**
         * 创建一个表示成功的响应对象
         * @param message 成功消息（可选）
         * @param data 返回的数据（可选）
         * @return 成功的Response对象
         */
        fun success(message: String = "", data: Any? = null): Response {
            return Response(200, true, message, data)
        }

        /**
         * 创建一个表示错误的响应对象
         * @param message 错误消息（可选）
         * @return 错误的Response对象
         */
        fun error(message: String = ""): Response {
            return Response(500, false, message)
        }

        /**
         * 创建一个表示权限不足的响应对象
         * @param message 权限不足消息
         * @return 权限不足的Response对象
         */
        fun unauthorized(message: String): Response {
            return Response(403, false, message)
        }

        /**
         * 创建一个表示未登录的响应对象
         * @param message 未登录消息
         * @return 未登录的Response对象
         */
        fun expired(message: String): Response {
            return Response(401, false, message)
        }

        /**
         * 将Response对象转换为JSON字符串
         * @return JSON格式的字符串表示
         */
        fun Response.toJsonString(): String {
            return this.toJSONString()
        }

        /**
         * 将Response对象转换为字节数组
         * @return 对应的字节数组
         */
        fun Response.toByteArray(): ByteArray {
            return this.toJsonString().toByteArray()
        }
    }
}
