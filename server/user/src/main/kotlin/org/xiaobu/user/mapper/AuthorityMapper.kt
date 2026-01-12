package org.xiaobu.user.mapper

import com.baomidou.mybatisplus.core.mapper.BaseMapper
import org.apache.ibatis.annotations.Mapper
import org.xiaobu.user.entity.Authority

@Mapper
interface AuthorityMapper : BaseMapper<Authority>
