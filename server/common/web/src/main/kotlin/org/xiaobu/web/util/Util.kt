package org.xiaobu.web.util

import com.alibaba.fastjson2.toJSONString
import jakarta.servlet.http.HttpServletResponse
import org.xiaobu.core.entity.Response

fun HttpServletResponse.write(data: Response) {
    contentType = "application/json"
    characterEncoding = "UTF-8"
    writer.write(data.toJSONString())
    writer.flush()
}
