package org.xiaobu.core.entity

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonFormat
import com.fasterxml.jackson.annotation.JsonProperty
import java.io.Serializable
import java.util.*

data class User @JsonCreator constructor(
    @JsonProperty("id") var id: String,
    @JsonProperty("username") var username: String,
    @JsonProperty("password") var password: String,
    @JsonProperty("nickname") var nickname: String,
    @JsonProperty("email") var email: String,
    @JsonProperty("avatarUrl") var avatarUrl: String,
    @JsonProperty("gender") var gender: String,
    @JsonProperty("signature") var signature: String,
    @JsonProperty("address") var address: String,
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss") @JsonProperty("createTime") var createTime: Date,
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss") @JsonProperty("updateTime") var updateTime: Date,
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss") @JsonProperty("onlineTime") var onlineTime: Date,
    @JsonProperty("sourceFrom") var sourceFrom: String,
    @JsonProperty("deleted") var deleted: Boolean
) : Serializable {
    constructor() : this(
        id = UUID.randomUUID().toString(),
        username = "",
        password = "",
        nickname = "",
        email = "",
        avatarUrl = "",
        gender = "",
        signature = "",
        address = "",
        createTime = Date(),
        updateTime = Date(),
        onlineTime = Date(),
        sourceFrom = "",
        deleted = false
    )
}
