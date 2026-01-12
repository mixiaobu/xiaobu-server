package org.xiaobu.mybatis.plus.config

import com.alibaba.fastjson2.JSON
import com.alibaba.fastjson2.toJSONString
import org.apache.ibatis.type.BaseTypeHandler
import org.apache.ibatis.type.JdbcType
import java.sql.CallableStatement
import java.sql.PreparedStatement
import java.sql.ResultSet

class JsonTypeHandler : BaseTypeHandler<MutableList<String>>() {

    override fun setNonNullParameter(
        ps: PreparedStatement, i: Int, parameter: MutableList<String>, jdbcType: JdbcType
    ) {
        ps.setString(i, parameter.toJSONString())
    }

    override fun getNullableResult(rs: ResultSet, columnName: String): MutableList<String> {
        return parse(rs.getString(columnName))
    }

    override fun getNullableResult(rs: ResultSet, columnIndex: Int): MutableList<String> {
        return parse(rs.getString(columnIndex))
    }

    override fun getNullableResult(cs: CallableStatement, columnIndex: Int): MutableList<String> {
        return parse(cs.getString(columnIndex))
    }

    private fun parse(value: String?): MutableList<String> {
        return JSON.parseArray(value, String::class.java).toMutableList()
    }
}
