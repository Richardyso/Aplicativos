package com.example.eletricarlos.models

data class UserSession(
    val username: String,
    val isAdmin: Boolean,
    val allowedLocations: List<String>,
    val canEdit: Boolean
)

