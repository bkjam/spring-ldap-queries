package com.example.ldap

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class LdapApplication

fun main(args: Array<String>) {
	runApplication<LdapApplication>(*args)
}
