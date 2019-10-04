package com.aries.ldaplogin

import com.aries.extension.data.UserData
import com.aries.extension.handler.LoginHandler
import com.aries.extension.util.LogUtil
import com.aries.extension.util.PropertyUtil

class LoginAdapter : LoginHandler {
    override fun preHandle(id: String, password: String): UserData? {
        LogUtil.info("Starting authentication ($id,$password)")

        val serverUrl = PropertyUtil.getValue(EXT_ID, "serverUrl", "")
        val baseRdn = PropertyUtil.getValue(EXT_ID, "baseRdn", "")
        val adminId = PropertyUtil.getValue(EXT_ID, "adminId", "")
        val adminPwd = PropertyUtil.getValue(EXT_ID, "adminPwd", "")

        return if (LdapConnector.connect(id, password, serverUrl, baseRdn, adminId, adminPwd)) {
            UserData(id, password, "admin", "Tester")
        } else null

    }

    override fun redirect(id: String, password: String): String {
        return "/dashboard/realtimeAdmin"
    }

    companion object {
        const val EXT_ID = "ldap_adapter"
    }
}
