package com.aries.ldaplogin

import javax.naming.AuthenticationException
import javax.naming.Context
import javax.naming.NamingException
import javax.naming.directory.SearchControls
import javax.naming.ldap.InitialLdapContext
import java.util.Hashtable
import com.aries.extension.util.LogUtil
import javax.naming.directory.SearchResult

object LdapConnector2 {
    fun connect(usrId: String, usrPw: String, url: String="", baseRdn: String="", baseOu: String=""): Boolean {
        var result = false

        val env = Hashtable<String, String>()
        env[Context.INITIAL_CONTEXT_FACTORY] = "com.sun.jndi.ldap.LdapCtxFactory"
        env[Context.PROVIDER_URL] = url
        env[Context.SECURITY_AUTHENTICATION] = "simple"
        env[Context.SECURITY_PRINCIPAL] = usrId
        env[Context.SECURITY_CREDENTIALS] = usrPw

        try {
            val ctx = InitialLdapContext(env, null)
            LogUtil.info("Active Directory Connection: CONNECTED")

            val ctls = SearchControls()
            ctls.searchScope = SearchControls.SUBTREE_SCOPE

            val answer = ctx.search(baseRdn, String.format("(cn=%s)", usrId), ctls)

            while (answer.hasMore()) {
                val searchResult = answer.next() as SearchResult
                val attributes = searchResult.attributes
                val memberOf = attributes["memberOf"]

                if (memberOf != null) {
                    for (i in 0 until memberOf.size()) {
                        val member = takeValues(memberOf[i].toString(), "OU")
                        if (member.contains(baseOu)) {
                            result = true
                        }
                    }
                }
            }

            ctx.close()
        } catch (e: AuthenticationException) {
            val msg = e.message!!
            when {
                msg.indexOf("data 525") > 0 -> LogUtil.warn("User not found (525)")
                msg.indexOf("data 773") > 0 -> LogUtil.warn("The user must reset the password (773)")
                msg.indexOf("data 52e") > 0 -> LogUtil.warn("ID and password do not match (52e)")
                msg.indexOf("data 533") > 0 -> LogUtil.warn("The ID you entered is inactive (533)")
                msg.indexOf("data 532") > 0 -> LogUtil.warn("Password has expired (532)")
                msg.indexOf("data 701") > 0 -> LogUtil.warn("The account has expired in AD (701)")
                else -> LogUtil.error(msg)
            }
            return false
        } catch (e: NamingException) {
            LogUtil.error(e.message)
            return false
        } catch (e: Exception) {
            LogUtil.error(e.message)
            return false
        }

        return result
    }

    private fun takeValues(args: String, key: String): Set<String> {
        val result = HashSet<String>()
        val tokens = args.split(",")
        for (token in tokens) {
            val keyAndValue = token.split("=")
            if (key == keyAndValue[0]) {
                result.add(keyAndValue[1])
            }
        }
        return result
    }
}
