package com.example.eletricarlos.models

data class User(
    val username: String,
    val password: String,
    val isAdmin: Boolean = false,
    val allowedLocations: List<String> = emptyList(),
    val canEdit: Boolean = false
)

