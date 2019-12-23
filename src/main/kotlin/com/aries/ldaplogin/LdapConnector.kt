package com.aries.ldaplogin

import com.aries.extension.util.LogUtil
import java.lang.NullPointerException
import java.util.*
import javax.naming.AuthenticationException
import javax.naming.Context
import javax.naming.NamingException
import javax.naming.directory.DirContext
import javax.naming.directory.SearchControls
import javax.naming.directory.SearchResult
import javax.naming.ldap.InitialLdapContext


object LdapConnector {
    fun connect(usrId: String, usrPw: String,
                url: String="",  baseRdn: String="",
                ntUserId: String="", ntPasswd: String=""): Boolean {
        try {
            val env = Hashtable<String, String>()
            env[Context.INITIAL_CONTEXT_FACTORY] = "com.sun.jndi.ldap.LdapCtxFactory"
            env[Context.PROVIDER_URL] = url
            env[Context.SECURITY_AUTHENTICATION] = "simple"
            env[Context.SECURITY_PRINCIPAL] = ntUserId
            env[Context.SECURITY_CREDENTIALS] = ntPasswd

            val ctx = InitialLdapContext(env, null)
            LogUtil.info("Active Directory Connection: CONNECTED")

            val results = findAccountByAccountName(ctx, baseRdn, usrId)
            val user = results!!.nameInNamespace

            val usrEnv = Hashtable<String, String>()
            usrEnv[Context.INITIAL_CONTEXT_FACTORY] = "com.sun.jndi.ldap.LdapCtxFactory"
            usrEnv[Context.PROVIDER_URL] = url
            usrEnv[Context.SECURITY_AUTHENTICATION] = "simple"
            usrEnv[Context.SECURITY_PRINCIPAL] = user
            usrEnv[Context.SECURITY_CREDENTIALS] = usrPw

            InitialLdapContext(usrEnv, null)
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
        } catch (e: NullPointerException) {
            LogUtil.error("User not found in namespace")
            return false
        }

        return true
    }

    @Throws(NamingException::class)
    fun findAccountByAccountName(ctx: DirContext, ldapSearchBase: String?, accountName: String): SearchResult? {
        val searchFilter = "(&(objectClass=user)(sAMAccountName=$accountName))"
        val searchControls = SearchControls()
        searchControls.searchScope = SearchControls.SUBTREE_SCOPE
        val results = ctx.search(ldapSearchBase, searchFilter, searchControls)
        var searchResult: SearchResult? = null
        if (results.hasMoreElements()) {
            searchResult = results.nextElement() as SearchResult
            //make sure there is not another item available, there should be only 1 match
            if (results.hasMoreElements()) {
                LogUtil.error("Matched multiple users for the accountName: $accountName")
                return null
            }
        }
        return searchResult
    }
}
