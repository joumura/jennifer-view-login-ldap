package com.aries.ldaplogin

fun main(args: Array<String>) {
    val groupPrefix = "JS_,jennifer_"

    println("-----")
    println(LdapConnector.connect(
            "Jennifer_user3",
            "Password123",
            "ldap://cpfb-acn.sg:389",
            "OU=CPF,DC=CPFB-ACN,DC=SG",
            "CN=jennifer_bindid,CN=Users,DC=CPFB-ACN,DC=SG",
            "Password123",
            groupPrefix.split(","),
            "",
            ""
    ))
    println("-----")
}