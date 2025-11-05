package com.example.eletricarlos.utils

import android.content.Context
import com.example.eletricarlos.models.User
import com.example.eletricarlos.models.UserSession

class AuthManager(private val context: Context) {
    
    private val prefs = context.getSharedPreferences("eletricarlos_auth", Context.MODE_PRIVATE)
    
    // Lista de usuários do sistema
    private val users = listOf(
        User(
            username = "Carlos",
            password = "olavo3043",
            isAdmin = true,
            allowedLocations = listOf("Pousada Paraíso", "Dorys Prime", "Hotel JR", "Hotel Guarany"),
            canEdit = true
        ),
        User(
            username = "Dorian",
            password = "dorys",
            isAdmin = false,
            allowedLocations = listOf("Dorys Prime"),
            canEdit = false
        ),
        User(
            username = "Romario",
            password = "paraiso",
            isAdmin = false,
            allowedLocations = listOf("Pousada Paraíso"),
            canEdit = false
        ),
        User(
            username = "Hotel",
            password = "hotel",
            isAdmin = false,
            allowedLocations = listOf("Hotel JR", "Hotel Guarany"),
            canEdit = false
        )
    )
    
    fun authenticate(username: String, password: String): User? {
        return users.find { it.username == username && it.password == password }
    }
    
    fun saveSession(user: User) {
        prefs.edit().apply {
            putString("username", user.username)
            putBoolean("isAdmin", user.isAdmin)
            putBoolean("canEdit", user.canEdit)
            putString("allowedLocations", user.allowedLocations.joinToString(","))
            apply()
        }
    }
    
    fun getSession(): UserSession? {
        val username = prefs.getString("username", null) ?: return null
        val isAdmin = prefs.getBoolean("isAdmin", false)
        val canEdit = prefs.getBoolean("canEdit", false)
        val locationsString = prefs.getString("allowedLocations", "") ?: ""
        val allowedLocations = if (locationsString.isNotEmpty()) {
            locationsString.split(",")
        } else {
            emptyList()
        }
        
        return UserSession(username, isAdmin, allowedLocations, canEdit)
    }
    
    fun clearSession() {
        prefs.edit().clear().apply()
    }
    
    fun getUsernames(): List<String> {
        return users.map { it.username }
    }
}

