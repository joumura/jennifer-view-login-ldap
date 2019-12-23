package com.aries.ldaplogin

fun main(args: Array<String>) {
    println(LdapConnector.connect(
            "jennifer",
            "rkflqhddur",
            "LDAP://192.168.0.86",
            "cn=Users,dc=testhvpc,dc=com",
            "cn=alvin,cn=Users,dc=testhvpc,dc=com",
            "1234"))
}