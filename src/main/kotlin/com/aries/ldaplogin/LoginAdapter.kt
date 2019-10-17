package com.aries.ldaplogin

import com.aries.extension.data.UserData
import com.aries.extension.handler.LoginHandler
import com.aries.extension.util.LogUtil
import com.aries.extension.util.PropertyUtil

class LoginAdapter : LoginHandler {
    override fun preHandle(id: String, password: String): UserData? {
        LogUtil.info("Starting authentication ($id)")

        val serverUrl = PropertyUtil.getValue(EXT_ID, "serverUrl", "")
        val baseRdn = PropertyUtil.getValue(EXT_ID, "baseRdn", "")
        val baseOu = PropertyUtil.getValue(EXT_ID, "baseOu", "")
        val userGroup = PropertyUtil.getValue(EXT_ID, "userGroup", "guest")

        return if (LdapConnector2.connect(id, password, serverUrl, baseRdn, baseOu)) {
            UserData(id, password, userGroup, id)
        } else null
    }

    override fun redirect(id: String, password: String): String {
        return "/dashboard/realtimeAdmin"
    }

    companion object {
        const val EXT_ID = "ldap_adapter"
    }
}