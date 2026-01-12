package org.xiaobu.user.entity

import com.baomidou.mybatisplus.annotation.IdType
import com.baomidou.mybatisplus.annotation.TableId
import com.baomidou.mybatisplus.annotation.TableName

@TableName("role_authority")
data class RoleAuthority(
    @TableId(value = "id", type = IdType.ASSIGN_ID) var id: String,
    var roleId: String,
    var authorityId: String
)
