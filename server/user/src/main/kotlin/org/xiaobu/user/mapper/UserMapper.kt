package org.xiaobu.user.mapper

import com.baomidou.mybatisplus.core.mapper.BaseMapper
import org.apache.ibatis.annotations.Mapper
import org.xiaobu.user.entity.User

@Mapper
interface UserMapper : BaseMapper<User>
