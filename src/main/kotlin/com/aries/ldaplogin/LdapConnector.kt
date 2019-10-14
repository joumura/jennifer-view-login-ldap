package com.aries.ldaplogin

import javax.naming.AuthenticationException
import javax.naming.Context
import javax.naming.NamingException
import javax.naming.directory.SearchControls
import javax.naming.ldap.InitialLdapContext
import java.util.Hashtable
import com.aries.extension.util.LogUtil

object LdapConnector {
    fun connect(usrId: String, usrPw: String,
                url: String="",  baseRdn: String="",
                ntUserId: String="", ntPasswd: String=""): Boolean {
        val initialLdapContext: InitialLdapContext = try {
            val env = Hashtable<String, String>()
            env[Context.INITIAL_CONTEXT_FACTORY] = "com.sun.jndi.ldap.LdapCtxFactory"
            env[Context.PROVIDER_URL] = url
            env[Context.SECURITY_AUTHENTICATION] = "simple"
            env[Context.SECURITY_PRINCIPAL] = ntUserId
            env[Context.SECURITY_CREDENTIALS] = ntPasswd

            val ctx = InitialLdapContext(env, null)
            LogUtil.info("Active Directory Connection: CONNECTED")

            // Hashtable 부터 LdapContext까지 LDAP 접속의 대한 인증을 합니다. ntUserId, ntPasswd, url 세가지로 연결 확인을 합니다.
            // 정상적인 연결이 되면 "CONNECTED"가 출력됩니다.
            val ctls = SearchControls()
            ctls.searchScope = SearchControls.SUBTREE_SCOPE
            ctls.returningAttributes = arrayOf("cn")

            // 인증이 확인 됬다면 usrId, usrPw, baseRdn(유저가 등록된 위치)으로 Admin에서 등록한 유저를 찾아봅시다!
            val searchFilter = String.format("(cn=%s)", usrId)
            val results = ctx.search(baseRdn, searchFilter, ctls)

            val usrEnv = Hashtable<String, String>()
            usrEnv[Context.INITIAL_CONTEXT_FACTORY] = "com.sun.jndi.ldap.LdapCtxFactory"
            usrEnv[Context.PROVIDER_URL] = url
            usrEnv[Context.SECURITY_AUTHENTICATION] = "simple"
            usrEnv[Context.SECURITY_PRINCIPAL] = String.format("%s=%s,%s", "cn", usrId, baseRdn)
            usrEnv[Context.SECURITY_CREDENTIALS] = usrPw

            InitialLdapContext(usrEnv, null)
            // 이 부분도 마찬가지로 ID, PW, 유저가 등록된 위치로 유저를 찾습니다.
            // 아래는 Active Directory에서 발생한 에러처리 입니다.
        } catch (e: AuthenticationException) {
            val msg = e.message!!
            when {
                msg.indexOf("data 525") > 0 -> LogUtil.warn("User not found (525)")
                msg.indexOf("data 773") > 0 -> LogUtil.warn("The user must reset the password (773)")
                msg.indexOf("data 52e") > 0 -> LogUtil.warn("ID and password do not match (52e)")
                msg.indexOf("data 533") > 0 -> LogUtil.warn("The ID you entered is inactive (533)")
                msg.indexOf("data 532") > 0 -> LogUtil.warn("Password has expired (532)")
                msg.indexOf("data 701") > 0 -> LogUtil.warn("The account has expired in AD (701)")
                else -> LogUtil.info("The login was successful")
            }
            return false
        } catch (e: NamingException) {
            LogUtil.error(e.message)
            return false
        }

        return true
    }
}
