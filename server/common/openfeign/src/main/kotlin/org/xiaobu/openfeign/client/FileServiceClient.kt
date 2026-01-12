package org.xiaobu.openfeign.client

import org.springframework.cloud.openfeign.FeignClient
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.multipart.MultipartFile
import org.xiaobu.core.entity.Response

@FeignClient("file")
interface FileServiceClient {

    @PostMapping("/upload")
    fun upload(
        @RequestParam bucketName: String, @RequestParam("file") files: MutableList<MultipartFile>
    ): Response

    @GetMapping("/remove")
    fun remove(@RequestParam url: String): Response
}
