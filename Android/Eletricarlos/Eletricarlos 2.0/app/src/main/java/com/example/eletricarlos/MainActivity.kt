package com.example.eletricarlos

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.example.eletricarlos.utils.AuthManager

class MainActivity : AppCompatActivity() {
    
    private lateinit var authManager: AuthManager
    private var canEdit: Boolean = false
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        
        supportActionBar?.hide()
        
        authManager = AuthManager(this)
        
        // Check if user is logged in
        val session = authManager.getSession()
        if (session == null) {
            goToLogin()
            return
        }
        
        canEdit = session.canEdit
        
        val btnPousadaParaiso = findViewById<Button>(R.id.btnPousadaParaiso)
        val btnDorysPrime = findViewById<Button>(R.id.btnDorysPrime)
        val btnHotelJR = findViewById<Button>(R.id.btnHotelJR)
        val btnHotelGuarany = findViewById<Button>(R.id.btnHotelGuarany)
        val btnLogout = findViewById<Button>(R.id.btnLogout)
        
        // Filter buttons based on user permissions
        val allowedLocations = session.allowedLocations
        
        btnPousadaParaiso.visibility = if (allowedLocations.contains("Pousada Paraíso")) View.VISIBLE else View.GONE
        btnDorysPrime.visibility = if (allowedLocations.contains("Dorys Prime")) View.VISIBLE else View.GONE
        btnHotelJR.visibility = if (allowedLocations.contains("Hotel JR")) View.VISIBLE else View.GONE
        btnHotelGuarany.visibility = if (allowedLocations.contains("Hotel Guarany")) View.VISIBLE else View.GONE
        
        btnPousadaParaiso.setOnClickListener {
            openLocalActivity(getString(R.string.pousada_paraiso))
        }
        
        btnDorysPrime.setOnClickListener {
            openLocalActivity(getString(R.string.dorys_prime))
        }
        
        btnHotelJR.setOnClickListener {
            openLocalActivity(getString(R.string.hotel_jr))
        }
        
        btnHotelGuarany.setOnClickListener {
            openLocalActivity(getString(R.string.hotel_guarany))
        }
        
        btnLogout.setOnClickListener {
            logout()
        }
    }
    
    private fun logout() {
        authManager.clearSession()
        goToLogin()
    }
    
    private fun goToLogin() {
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }
    
    private fun openLocalActivity(localName: String) {
        val intent = Intent(this, LocalActivity::class.java)
        intent.putExtra("LOCAL_NAME", localName)
        intent.putExtra("CAN_EDIT", canEdit)
        startActivity(intent)
    }
}

