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

        return if (LdapConnector2.connect(id, password, serverUrl, baseRdn, baseOu)) {
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

//fun main(args: Array<String>) {
//
//    //172.18.220.139
////yong.sang.kwon
////Naya180500
//
//// jennifer_bindid
//// Password123456
//
////    LdapConnector.connect(
////            "jennifer_bindid",
////            "Password123456",
////            "LDAP://WIN-AD1.CPFB-ACN.SG",
////            "CN=Users,DC=CPFB-ACN,DC=SG",
////            "CN=jennifer_bindid,CN=Users,DC=CPFB-ACN,DC=SG",
////            "Password123456")
//
//    LdapConnector2.connect(
//            "yong.sang.kwon",
//            "Naya180500",
//            "LDAP://WIN-AD1.CPFB-ACN.SG",
//            "CN=Users,DC=CPFB-ACN,DC=SG",
//            "JenniferSoft")
//}
