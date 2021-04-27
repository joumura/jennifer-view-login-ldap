package com.aries.ldaplogin

import com.aries.extension.data.UserData
import com.aries.extension.handler.LoginHandler
import com.aries.extension.util.LogUtil
import com.aries.extension.util.PropertyUtil
import java.util.Hashtable

class LoginAdapter12 : LoginHandler {
    override fun preHandle(id: String, password: String): UserData? {
        LogUtil.info("Starting authentication ($id)")

        val serverUrl = PropertyUtil.getValue(EXT_ID, "serverUrl", "")
        val baseRdn = PropertyUtil.getValue(EXT_ID, "baseRdn", "")
        val baseOu = PropertyUtil.getValue(EXT_ID, "baseOu", "")
        val groupPrefix = PropertyUtil.getValue(EXT_ID, "groupPrefix", "")
        val fixedGroup = PropertyUtil.getValue(EXT_ID, "fixedGroup", "")
        val adminId = PropertyUtil.getValue(EXT_ID, "adminId", "")
        val adminPwd = PropertyUtil.getValue(EXT_ID, "adminPwd", "")

        val attrKeys = Hashtable<String, String>()
        attrKeys["name"] = PropertyUtil.getValue(EXT_ID, "nameAttr", "")
        attrKeys["email"] = PropertyUtil.getValue(EXT_ID, "emailAttr", "")
        attrKeys["company"] = PropertyUtil.getValue(EXT_ID, "companyAttr", "")
        attrKeys["dept"] = PropertyUtil.getValue(EXT_ID, "deptAttr", "")
        attrKeys["jobTitle"] = PropertyUtil.getValue(EXT_ID, "jobTitleAttr", "")
        attrKeys["cellphone"] = PropertyUtil.getValue(EXT_ID, "cellphoneAttr", "")

        return LdapConnector12.connect(id, password, serverUrl, baseRdn, adminId, adminPwd,
                groupPrefix.split(","), baseOu, fixedGroup, attrKeys)
    }

    override fun redirect(id: String, password: String): String {
        return "/dashboard/realtimeAdmin"
    }

    companion object {
        const val EXT_ID = "ldap_adapter"
    }
}