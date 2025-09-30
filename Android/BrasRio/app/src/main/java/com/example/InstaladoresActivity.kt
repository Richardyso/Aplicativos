package com.example

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class InstaladoresActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_instaladores)

        initializeViews()
        setupClickListeners()
        loadInstaladoresData()
    }

    private fun initializeViews() {
        // Views serão inicializadas no setupClickListeners
    }

    private fun setupClickListeners() {
        findViewById<Button>(R.id.btnVoltar).setOnClickListener {
            finish()
        }
        
        findViewById<Button>(R.id.btnContatarWhatsApp).setOnClickListener {
            contatarWhatsApp()
        }
    }

    private fun loadInstaladoresData() {
        val tvInstaladoresContent = findViewById<TextView>(R.id.tvInstaladoresContent)
        val instaladores = ServiceManager.getAllInstaladores()
        
        val html = buildString {
            append("Nossos Parceiros:\n\n")
            instaladores.forEachIndexed { index, instalador ->
                append("${index + 1}. ${instalador.nome}\n")
                append("   Especialização: ${instalador.especializacao}\n")
                append("   Telefone: ${instalador.telefone}\n")
                append("   [Contratar]\n\n")
            }
        }
        
        tvInstaladoresContent.text = html
        
        // Criar botões individuais para cada instalador
        createInstaladorButtons()
    }
    
    private fun createInstaladorButtons() {
        val instaladores = ServiceManager.getAllInstaladores()
        val buttonContainer = findViewById<LinearLayout>(R.id.buttonContainer)
        
        // Limpar botões existentes (exceto os fixos)
        val buttonsToRemove = mutableListOf<View>()
        for (i in 0 until buttonContainer.childCount) {
            val child = buttonContainer.getChildAt(i)
            if (child is Button && child.id != R.id.btnVoltar) {
                buttonsToRemove.add(child)
            }
        }
        buttonsToRemove.forEach { buttonContainer.removeView(it) }
        
        // Criar botão para cada instalador
        instaladores.forEach { instalador ->
            val button = Button(this).apply {
                text = "Contratar ${instalador.nome}"
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                ).apply {
                    setMargins(0, 0, 0, 16)
                }
                setBackgroundResource(R.drawable.button_primary)
                setTextColor(resources.getColor(android.R.color.white, null))
                setOnClickListener {
                    contatarInstaladorWhatsApp(instalador.numeroWhatsApp)
                }
            }
            buttonContainer.addView(button, buttonContainer.childCount - 1) // Adicionar antes do botão Voltar
        }
    }

    private fun contatarWhatsApp() {
        // Esta função não será mais usada, pois cada instalador terá seu próprio botão
        // Mantida apenas para compatibilidade
    }
    
    fun contatarInstaladorWhatsApp(numeroWhatsApp: String) {
        val url = "https://wa.me/55$numeroWhatsApp"
        
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        startActivity(intent)
    }
}
