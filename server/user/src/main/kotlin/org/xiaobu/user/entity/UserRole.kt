package org.xiaobu.user.entity

import com.baomidou.mybatisplus.annotation.IdType
import com.baomidou.mybatisplus.annotation.TableId
import com.baomidou.mybatisplus.annotation.TableName

@TableName("user_role")
data class UserRole(
    @TableId(value = "id", type = IdType.ASSIGN_ID) var id: String,
    var userId: String,
    var roleId: String
)
