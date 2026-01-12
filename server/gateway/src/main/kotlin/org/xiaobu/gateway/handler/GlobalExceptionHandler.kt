package org.xiaobu.gateway.handler

import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.client.HttpServerErrorException
import org.springframework.web.servlet.resource.NoResourceFoundException
import org.xiaobu.core.entity.Response
import org.xiaobu.core.util.log

@RestControllerAdvice
class GlobalExceptionHandler {

    @ExceptionHandler(NoResourceFoundException::class)
    fun exception(e: NoResourceFoundException): Response {
        log.error("地址无法到达：${e.message}")
        return Response.error("地址无法到达")
    }

    @ExceptionHandler(HttpServerErrorException::class)
    fun exception(e: HttpServerErrorException): Response {
        log.error("服务无法到达：${e.message}")
        return Response.error("服务无法到达")
    }

    @ExceptionHandler(value = [Exception::class])
    fun exceptionHandler(e: Exception): Response {
        log.error("未知错误：${e.message}")
        return Response.error("未知错误")
    }
}
