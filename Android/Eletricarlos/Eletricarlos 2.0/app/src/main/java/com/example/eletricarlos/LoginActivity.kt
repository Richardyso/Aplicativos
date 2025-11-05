package com.example.eletricarlos

import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.eletricarlos.utils.AuthManager
import com.example.eletricarlos.utils.LegacyDataMigration

class LoginActivity : AppCompatActivity() {
    
    private lateinit var authManager: AuthManager
    private lateinit var legacyMigration: LegacyDataMigration
    private lateinit var spinnerUser: Spinner
    private lateinit var etPassword: EditText
    private lateinit var btnLogin: Button
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        
        supportActionBar?.hide()
        
        authManager = AuthManager(this)
        legacyMigration = LegacyDataMigration(this)
        
        // Migrar dados automaticamente sem popup
        if (!legacyMigration.isMigrationCompleted()) {
            legacyMigration.migrateAllData()
        }
        
        // Check if user is already logged in
        val session = authManager.getSession()
        if (session != null) {
            goToMainActivity()
            return
        }
        
        spinnerUser = findViewById(R.id.spinnerUser)
        etPassword = findViewById(R.id.etPassword)
        btnLogin = findViewById(R.id.btnLogin)
        
        // Setup spinner with usernames
        val usernames = authManager.getUsernames()
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, usernames)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerUser.adapter = adapter
        
        btnLogin.setOnClickListener {
            login()
        }
    }
    
    private fun login() {
        val selectedUsername = spinnerUser.selectedItem?.toString()
        val password = etPassword.text.toString()
        
        if (selectedUsername == null) {
            Toast.makeText(this, getString(R.string.error_select_user), Toast.LENGTH_SHORT).show()
            return
        }
        
        if (password.isEmpty()) {
            Toast.makeText(this, getString(R.string.error_password), Toast.LENGTH_SHORT).show()
            return
        }
        
        val user = authManager.authenticate(selectedUsername, password)
        
        if (user != null) {
            authManager.saveSession(user)
            goToMainActivity()
        } else {
            Toast.makeText(this, getString(R.string.error_password), Toast.LENGTH_SHORT).show()
            etPassword.text.clear()
        }
    }
    
    private fun goToMainActivity() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }
}

