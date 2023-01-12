package com.example.ldap.model

data class AdUser(
    val userId: String,
    val name: String,
    val emails: List<String>
)
