package org.xiaobu.core.util

import org.xiaobu.core.entity.Response

object RegexUtil {

    fun validUsernamePassword(username: String, password: String): Response {
        if (!username.matches(Regex("^[0-9]{6,12}\$"))) {
            return Response.error("账号只能是 6 到 12 位的数字")
        }
        if (!password.matches(Regex("^[a-zA-Z0-9!@#\$%^&*()-_+=<>?]+$"))) {
            return Response.error("密码只能包含字母、数字和符号，不能包含中文、空格和其他特殊字符")
        }
        return Response.success()
    }

    fun validUsername(username: String): Response {
        if (!username.matches(Regex("^[0-9]{6,12}\$"))) {
            return Response.error("账号只能是 6 到 12 位的数字")
        }
        return Response.success()
    }

    fun validPassword(password: String): Response {
        if (!password.matches(Regex("^[a-zA-Z0-9!@#\$%^&*()-_+=<>?]+$"))) {
            return Response.error("密码只能包含字母、数字和符号，不能包含中文、空格和其他特殊字符")
        }
        return Response.success()
    }

    fun validNickname(nickname: String): Response {
        if (!nickname.matches(Regex("^[a-zA-Z0-9\u4e00-\u9fa5]{1,12}\$"))) {
            return Response.error("昵称只能是 1 到 12 位的字母、数字或汉字")
        }
        return Response.success()
    }

    fun validEmail(email: String): Boolean {
        return email.matches(Regex("^[a-zA-Z0-9_-]+@[a-zA-Z0-9_-]+(\\.[a-zA-Z0-9_-]+)+\$"))
    }
}
