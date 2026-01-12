package org.xiaobu.core.util

import cn.hutool.http.ContentType
import cn.hutool.http.HttpRequest
import cn.hutool.http.HttpResponse

/**
 * 网络请求相关配置和工具方法
 */
class Net {
    companion object {
        /**
         * 默认的请求超时时间（毫秒）
         */
        const val DEFAULT_TIMEOUT = 30000

        /**
         * 默认的请求和响应字符集
         */
        const val DEFAULT_CHARSET = "UTF-8"

        /**
         * 默认的内容类型，这里使用JSON
         */
        val DEFAULT_CONTENT_TYPE: String = ContentType.JSON.value

        /**
         * 发起GET请求
         * @param url 请求的URL
         * @param block 请求构建器的配置块
         * @return 解析后的响应数据，类型为T
         */
        inline fun <reified T> Get(url: String, block: RequestBuilder.() -> Unit = {}): T? {
            val requestBuilder = RequestBuilder(HttpRequest.get(url))
            requestBuilder.apply(block)
            return requestBuilder.execute<T>()
        }

        /**
         * 发起POST请求
         * @param url 请求的URL
         * @param block 请求构建器的配置块
         * @return 解析后的响应数据，类型为T
         */
        inline fun <reified T> Post(url: String, block: RequestBuilder.() -> Unit = {}): T? {
            val requestBuilder = RequestBuilder(HttpRequest.post(url))
            requestBuilder.apply(block)
            return requestBuilder.execute<T>()
        }

        /**
         * 发起表单方式的 POST 请求（application/x-www-form-urlencoded）
         * @param url 请求的URL
         * @param block 请求构建器的配置块
         * @return 解析后的响应数据，类型为T
         */
        inline fun <reified T> PostForm(url: String, block: RequestBuilder.() -> Unit = {}): T? {
            val requestBuilder = RequestBuilder(HttpRequest.post(url), isForm = true)
            requestBuilder.apply(block)
            return requestBuilder.execute<T>()
        }

        /**
         * 发起PUT请求
         * @param url 请求的URL
         * @param block 请求构建器的配置块
         * @return 解析后的响应数据，类型为T
         */
        inline fun <reified T> Put(url: String, block: RequestBuilder.() -> Unit = {}): T? {
            val requestBuilder = RequestBuilder(HttpRequest.put(url))
            requestBuilder.apply(block)
            return requestBuilder.execute<T>()
        }

        /**
         * 发起DELETE请求
         * @param url 请求的URL
         * @param block 请求构建器的配置块
         * @return 解析后的响应数据，类型为T
         */
        inline fun <reified T> Delete(url: String, block: RequestBuilder.() -> Unit = {}): T? {
            val requestBuilder = RequestBuilder(HttpRequest.delete(url))
            requestBuilder.apply(block)
            return requestBuilder.execute<T>()
        }
    }
}

/**
 * HTTP请求的构建器，用于配置和执行HTTP请求
 */
class RequestBuilder(val request: HttpRequest, isForm: Boolean = false) {

    init {
        // 设置默认的内容类型、字符集和超时时间
        val contentType = if (isForm) ContentType.FORM_URLENCODED.value else Net.DEFAULT_CONTENT_TYPE
        request.header("Content-Type", contentType)
        request.charset(Net.DEFAULT_CHARSET)
        request.timeout(Net.DEFAULT_TIMEOUT)
        request.setFollowRedirects(true)
    }

    /**
     * 添加HTTP请求头
     * @param key 头部键
     * @param value 头部值
     * @return RequestBuilder实例，支持链式调用
     */
    fun addHeader(key: String, value: String) = apply {
        request.header(key, value)
    }

    /**
     * 添加HTTP请求参数
     * @param key 参数键
     * @param value 参数值
     * @return RequestBuilder实例，支持链式调用
     */
    fun addParam(key: String, value: Any) = apply {
        request.form(key, value)
    }

    /**
     * 设置HTTP请求超时时间
     * @param milliseconds 超时时间（毫秒）
     * @return RequestBuilder实例，支持链式调用
     */
    fun setTimeout(milliseconds: Int) = apply {
        request.timeout(milliseconds)
    }

    /**
     * 设置HTTP请求体
     * @param body 请求体内容
     * @return RequestBuilder实例，支持链式调用
     */
    fun setBody(body: String) = apply {
        request.body(body)
    }

    /**
     * 设置HTTP代理
     * @param proxyHost 代理主机地址
     * @param proxyPort 代理端口
     * @return RequestBuilder实例，支持链式调用
     */
    fun setProxy(proxyHost: String, proxyPort: Int) = apply {
        request.setHttpProxy(proxyHost, proxyPort)
    }

    /**
     * 执行HTTP请求并解析响应
     * @param T 响应数据的期望类型
     * @return 解析后的响应数据，类型为T
     */
    inline fun <reified T> execute(): T? {
        val response: HttpResponse = request.execute()
        return if (response.isOk) {
            parseResponse<T>(response)
        } else {
            // 如果响应错误，打印错误信息并返回null
            log.error("Error: ${request.url}, ${response.status}, ${response.body()}")
            null
        }
    }

    /**
     * 解析HTTP响应
     * @param T 响应数据的期望类型
     * @param response HTTP响应对象
     * @return 解析后的响应数据，类型为T
     */
    inline fun <reified T> parseResponse(response: HttpResponse): T? {
        return when (T::class) {
            String::class -> response.body() as? T
            else -> null
        }
    }
}
