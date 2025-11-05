package com.example.eletricarlos

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class LocalActivity : AppCompatActivity() {
    
    private lateinit var localName: String
    private var canEdit: Boolean = false
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_local)
        
        supportActionBar?.hide()
        
        localName = intent.getStringExtra("LOCAL_NAME") ?: ""
        canEdit = intent.getBooleanExtra("CAN_EDIT", false)
        
        val tvLocalName = findViewById<TextView>(R.id.tvLocalName)
        tvLocalName.text = localName.uppercase()
        
        val btnManutencaoPreventiva = findViewById<Button>(R.id.btnManutencaoPreventiva)
        val btnManutencaoCorretiva = findViewById<Button>(R.id.btnManutencaoCorretiva)
        val btnObservacao = findViewById<Button>(R.id.btnObservacao)
        
        btnManutencaoPreventiva.setOnClickListener {
            openFormActivity(getString(R.string.manutencao_preventiva))
        }
        
        btnManutencaoCorretiva.setOnClickListener {
            openFormActivity(getString(R.string.manutencao_corretiva))
        }
        
        btnObservacao.setOnClickListener {
            openFormActivity(getString(R.string.observacao))
        }
    }
    
    private fun openFormActivity(type: String) {
        val intent = Intent(this, FormActivity::class.java)
        intent.putExtra("LOCAL_NAME", localName)
        intent.putExtra("TYPE", type)
        intent.putExtra("CAN_EDIT", canEdit)
        startActivity(intent)
    }
}

