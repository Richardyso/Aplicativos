package com.example

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class MaterialTypeActivity : AppCompatActivity() {

    private lateinit var material: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_material_type)

        material = intent.getStringExtra("material") ?: ""
        
        initializeViews()
        setupClickListeners()
        updateUI()
    }

    private fun initializeViews() {
        // Views serão inicializadas no setupClickListeners
    }

    private fun setupClickListeners() {
        findViewById<Button>(R.id.btnVoltar).setOnClickListener {
            finish()
        }
    }

    private fun updateUI() {
        val tvTitle = findViewById<TextView>(R.id.tvTitle)
        val tvMaterial = findViewById<TextView>(R.id.tvMaterial)
        
        tvTitle.text = "Escolha o tipo de $material"
        tvMaterial.text = "Material: $material"

        when (material) {
            "Drywall" -> setupDrywallButtons()
            "Cimenticia" -> setupCimenticiaButtons()
            "PVC" -> setupPVCButtons()
            "Piso" -> setupPisoButtons()
        }
    }

    private fun setupDrywallButtons() {
        val btnParede = findViewById<Button>(R.id.btnParede)
        val btnTeto = findViewById<Button>(R.id.btnTeto)
        
        btnParede.text = "Parede"
        btnTeto.text = "Teto"
        
        btnParede.setOnClickListener {
            startActivity(Intent(this, MetragemActivity::class.java).apply {
                putExtra("material", material)
                putExtra("subtype", "Parede")
            })
        }
        
        btnTeto.setOnClickListener {
            startActivity(Intent(this, MetragemActivity::class.java).apply {
                putExtra("material", material)
                putExtra("subtype", "Teto")
            })
        }
    }

    private fun setupCimenticiaButtons() {
        val btnParede = findViewById<Button>(R.id.btnParede)
        val btnTeto = findViewById<Button>(R.id.btnTeto)
        
        btnParede.text = "Parede"
        btnTeto.text = "Teto"
        
        btnParede.setOnClickListener {
            startActivity(Intent(this, MetragemActivity::class.java).apply {
                putExtra("material", material)
                putExtra("subtype", "Parede")
            })
        }
        
        btnTeto.setOnClickListener {
            startActivity(Intent(this, MetragemActivity::class.java).apply {
                putExtra("material", material)
                putExtra("subtype", "Teto")
            })
        }
    }

    private fun setupPVCButtons() {
        val btnParede = findViewById<Button>(R.id.btnParede)
        val btnTeto = findViewById<Button>(R.id.btnTeto)
        
        btnParede.text = "Placa"
        btnTeto.text = "Régua"
        
        btnParede.setOnClickListener {
            startActivity(Intent(this, MetragemActivity::class.java).apply {
                putExtra("material", material)
                putExtra("subtype", "Placa")
            })
        }
        
        btnTeto.setOnClickListener {
            startActivity(Intent(this, MetragemActivity::class.java).apply {
                putExtra("material", material)
                putExtra("subtype", "Regua")
            })
        }
    }

    private fun setupPisoButtons() {
        val btnParede = findViewById<Button>(R.id.btnParede)
        val btnTeto = findViewById<Button>(R.id.btnTeto)
        
        btnParede.text = "Vinílico"
        btnTeto.text = "Laminado"
        
        btnParede.setOnClickListener {
            startActivity(Intent(this, MetragemActivity::class.java).apply {
                putExtra("material", material)
                putExtra("subtype", "Vinílico")
            })
        }
        
        btnTeto.setOnClickListener {
            startActivity(Intent(this, MetragemActivity::class.java).apply {
                putExtra("material", material)
                putExtra("subtype", "Laminado")
            })
        }
    }
}
