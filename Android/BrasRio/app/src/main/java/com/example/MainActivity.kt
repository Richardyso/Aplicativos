package com.example

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private lateinit var prefs: SharedPreferences
    private lateinit var tvWelcome: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        prefs = getSharedPreferences("BRASRIO_PREFS", MODE_PRIVATE)
        
        initializeViews()
        setupClickListeners()
        updateWelcomeMessage()
    }

    private fun initializeViews() {
        tvWelcome = findViewById(R.id.tvWelcome)
    }

    private fun setupClickListeners() {
        // Botões de materiais
        findViewById<Button>(R.id.btnDrywall).setOnClickListener {
            startActivity(Intent(this, MaterialTypeActivity::class.java).apply {
                putExtra("material", "Drywall")
            })
        }
        
        findViewById<Button>(R.id.btnCimenticia).setOnClickListener {
            startActivity(Intent(this, MaterialTypeActivity::class.java).apply {
                putExtra("material", "Cimenticia")
            })
        }
        
        findViewById<Button>(R.id.btnPVC).setOnClickListener {
            startActivity(Intent(this, MaterialTypeActivity::class.java).apply {
                putExtra("material", "PVC")
            })
        }
        
        findViewById<Button>(R.id.btnIsopor).setOnClickListener {
            startActivity(Intent(this, MetragemActivity::class.java).apply {
                putExtra("material", "Isopor")
            })
        }
        
        findViewById<Button>(R.id.btnForroMineral).setOnClickListener {
            startActivity(Intent(this, MetragemActivity::class.java).apply {
                putExtra("material", "Forro Mineral")
                putExtra("subtype", "Teto")
            })
        }
        
        findViewById<Button>(R.id.btnForroBoreal).setOnClickListener {
            startActivity(Intent(this, MetragemActivity::class.java).apply {
                putExtra("material", "Forro Boreal")
                putExtra("subtype", "Teto")
            })
        }
        
        findViewById<Button>(R.id.btnPainel).setOnClickListener {
            startActivity(Intent(this, MetragemActivity::class.java).apply {
                putExtra("material", "Painel")
            })
        }
        
        findViewById<Button>(R.id.btnPiso).setOnClickListener {
            startActivity(Intent(this, MaterialTypeActivity::class.java).apply {
                putExtra("material", "Piso")
            })
        }
        
        // Botões de indicação
        findViewById<Button>(R.id.btnInstaladores).setOnClickListener {
            startActivity(Intent(this, InstaladoresActivity::class.java))
        }
        
        findViewById<Button>(R.id.btnFretes).setOnClickListener {
            startActivity(Intent(this, FretesActivity::class.java))
        }
    }

    private fun updateWelcomeMessage() {
        val nome = prefs.getString("nome", "")
        if (nome?.isNotEmpty() == true) {
            tvWelcome.text = "Bem-vindo, $nome!"
        } else {
            tvWelcome.text = "Bem-vindo ao Sistema de Orçamento!"
        }
    }
}
