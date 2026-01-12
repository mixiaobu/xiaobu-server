package org.xiaobu.core.util

import java.util.*
import javax.mail.*
import javax.mail.internet.InternetAddress
import javax.mail.internet.MimeMessage

object EmailUtil {

    fun sendEmailBy163(email: String, subject: String, text: String) {
        val username = "xiaobuorg@163.com"
        val authCode = "ZEdAkBJNXbGts2t5"
        val props = Properties()
        props["mail.smtp.auth"] = "true"
        props["mail.smtp.starttls.enable"] = "true"
        props["mail.smtp.host"] = "smtp.163.com"
        props["mail.smtp.port"] = "25"
        val session = Session.getInstance(props, object : Authenticator() {
            override fun getPasswordAuthentication(): PasswordAuthentication {
                return PasswordAuthentication(username, authCode)
            }
        })
        val message: Message = MimeMessage(session)
        message.setFrom(InternetAddress(username))
        message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(email))
        message.subject = subject
        message.setText(text)
        Transport.send(message)
    }
}
