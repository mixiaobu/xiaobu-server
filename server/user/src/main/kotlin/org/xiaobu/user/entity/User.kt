package org.xiaobu.user.entity

import com.baomidou.mybatisplus.annotation.IdType
import com.baomidou.mybatisplus.annotation.TableId
import com.baomidou.mybatisplus.annotation.TableName
import java.util.*

@TableName("user")
data class User(
    @TableId(value = "id", type = IdType.ASSIGN_ID) var id: String,
    var username: String,
    var password: String,
    var nickname: String,
    var email: String,
    var avatarUrl: String,
    var gender: String,
    var signature: String,
    var address: String,
    var createTime: Date,
    var updateTime: Date,
    var onlineTime: Date,
    var sourceFrom: String,
    var deleted: Boolean
)
