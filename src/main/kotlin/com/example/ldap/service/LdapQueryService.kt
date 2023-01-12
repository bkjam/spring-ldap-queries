package com.example.ldap.service

import com.example.ldap.model.AdUser
import org.slf4j.LoggerFactory
import org.springframework.ldap.core.AttributesMapper
import org.springframework.ldap.core.DirContextOperations
import org.springframework.ldap.core.LdapTemplate
import org.springframework.ldap.core.support.AbstractContextMapper
import org.springframework.ldap.core.support.DefaultIncrementalAttributesMapper
import org.springframework.stereotype.Service

@Service
class LdapQueryService(
    private val ldapTemplate: LdapTemplate
) {
    companion object {
        private val logger = LoggerFactory.getLogger(LdapQueryService::class.java)
    }

    private fun getMultiValuedAttributesWithDefaultIncrementAttributesMapper(dn: String, attr: String): List<String> {
        val attributes = DefaultIncrementalAttributesMapper.lookupAttributes(ldapTemplate, dn, attr)
        val results = ArrayList<String>()
        for (i in 0 until attributes.get(attr).size()) {
            results.add(attributes.get(attr)[i].toString())
        }
        return results
    }

    private fun queryWithAttributeMapper(userId: String): AdUser? {
        val usersFound = ldapTemplate.search("ou=Users", "uid=$userId", AttributesMapper { attributes ->
            val id = attributes.get("uid").get().toString()
            val name = attributes.get("displayname").get().toString()
            val emails = getMultiValuedAttributesWithDefaultIncrementAttributesMapper("cn=U0,ou=Users", "mail")
            AdUser(userId = id, name = name, emails = emails)
        })
        return if (usersFound.isNotEmpty()) usersFound[0] else null
    }

    private fun queryWithContextMapper(userId: String): AdUser? {
        val usersFound = ldapTemplate.search("ou=Users", "uid=$userId", object: AbstractContextMapper<AdUser>() {
            override fun doMapFromContext(ctx: DirContextOperations): AdUser {
                val dn = ctx.dn
                val id = ctx.getStringAttribute("uid")
                val name = ctx.getStringAttribute("displayname")
                //val emails = ctx.getStringAttributes("mail")
                val emails = getMultiValuedAttributesWithDefaultIncrementAttributesMapper(dn.toString(), "mail")
                return AdUser(id, name, emails)
            }
        })
        return if (usersFound.isNotEmpty()) usersFound[0] else null
    }

    fun queryLdap(userId: String): AdUser? {
        logger.info("Searching for user ==> $userId")
        val adUser = queryWithContextMapper(userId)
        if (adUser == null) {
            logger.info("$userId is not found")
        }
        return adUser
    }
}
