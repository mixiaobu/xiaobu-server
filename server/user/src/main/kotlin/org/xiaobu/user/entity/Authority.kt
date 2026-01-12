package org.xiaobu.user.entity

import com.baomidou.mybatisplus.annotation.IdType
import com.baomidou.mybatisplus.annotation.TableId
import com.baomidou.mybatisplus.annotation.TableName

@TableName("authority")
data class Authority(
    @TableId(value = "id", type = IdType.ASSIGN_ID) var id: String,
    var name: String,
    var description: String,
    var type: String,
    var sort: Int
)
